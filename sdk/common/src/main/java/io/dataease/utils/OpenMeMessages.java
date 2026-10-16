package io.dataease.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 上游失败文案只保留简短业务原因。token、appSecret 不进接口，也不进日志原文。
 */
public final class OpenMeMessages {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int MAX_CLIENT = 800;
    private static final int MIN_SECRET = 6;
    private static final int OPAQUE_LENGTH = 24;

    private static final Pattern JWT = Pattern.compile("eyJ[A-Za-z0-9_-]{8,}\\.[A-Za-z0-9_-]{8,}\\.[A-Za-z0-9_-]{8,}");
    /** Bearer 后面跟的字段名（appAccessToken / teamAccessToken）是提示文案，不是凭据。 */
    private static final Pattern BEARER = Pattern.compile("(?i)\\bBearer\\s+(?!(?:app|team)AccessToken\\b)\\S+");
    /** 键名里含 token / secret / password，或就是 authorization。 */
    private static final String SECRET_KEY = "(?:[A-Za-z0-9_]*(?:token|secret|password|passwd)[A-Za-z0-9_]*|authorization)";
    private static final Pattern QUOTED_SECRET = Pattern.compile(
            "(?i)[\"']" + SECRET_KEY + "[\"']\\s*:\\s*[\"'](?:\\\\.|[^\"'\\\\])*[\"']?");
    private static final Pattern PLAIN_SECRET = Pattern.compile(
            "(?i)\\b[A-Za-z0-9_]*(?:token|secret|password|passwd)\\s*[:=]\\s*\\S{6,}");
    private static final Pattern APP_OR_TEAM = Pattern.compile("(?i)\\b(?:app|team)-[0-9a-fA-F]{8,}\\b");
    private static final Pattern OPAQUE = Pattern.compile("[A-Za-z0-9_-]{" + OPAQUE_LENGTH + ",}");
    private static final Pattern UUID = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
    private static final Pattern HTTP_STATUS = Pattern.compile("StatusCode:\\s*(\\d{3})|\\bHTTP(?:/\\d(?:\\.\\d)?)?\\s+(\\d{3})\\b");
    private static final Pattern EXCEPTION_NAME = Pattern.compile("\\b([A-Z][A-Za-z0-9]{0,40}Exception)\\b");

    private OpenMeMessages() {
    }

    /**
     * 全局异常处理器用：只有明显来自上游 HTTP 原文、或带凭据特征的消息才需要整条替换。
     * 普通业务错误（含 SQL / 校验信息里的花括号）原样返回，不能因为有 { 就误杀。
     */
    public static boolean needsSanitize(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        return text.contains("HttpClient查询失败")
                || looksLikeHtml(text)
                || JWT.matcher(text).find()
                || BEARER.matcher(text).find()
                || QUOTED_SECRET.matcher(text).find()
                || PLAIN_SECRET.matcher(text).find()
                || APP_OR_TEAM.matcher(text).find();
    }

    public static String clientMessage(String fallback, String detail, String... secrets) {
        if (StringUtils.isBlank(detail)) {
            return fallback;
        }
        String hint = hint(detail);
        JsonNode root = payload(detail);
        if (root != null) {
            String msg = text(root.get("msg"));
            if (isClientSafe(msg, secrets)) {
                return limit(msg);
            }
            String code = text(root.get("code"));
            return compose(fallback, isShortCode(code, secrets) ? "code=" + code : null, hint);
        }
        if (detail.contains("HttpClient查询失败") || looksLikeHtml(detail) || !isClientSafe(detail, secrets)) {
            return compose(fallback, null, hint);
        }
        return limit(detail);
    }

    public static String redact(String text, String... secrets) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String cleaned = replaceSecrets(text, secrets);
        cleaned = QUOTED_SECRET.matcher(cleaned).replaceAll("[redacted]");
        cleaned = PLAIN_SECRET.matcher(cleaned).replaceAll("[redacted]");
        cleaned = BEARER.matcher(cleaned).replaceAll("Bearer [redacted]");
        cleaned = JWT.matcher(cleaned).replaceAll("[redacted]");
        cleaned = APP_OR_TEAM.matcher(cleaned).replaceAll("[redacted]");
        Matcher matcher = OPAQUE.matcher(cleaned);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String token = matcher.group();
            String replacement = UUID.matcher(token).matches() ? token : "[redacted]";
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * 写日志用：先截断再脱敏，再按上限收尾，避免整页 HTML 错误页进日志。
     */
    public static String redactLimited(String text, int maxLength, String... secrets) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String clipped = text.length() > maxLength * 2 ? text.substring(0, maxLength * 2) : text;
        String cleaned = redact(clipped, secrets);
        if (cleaned.length() > maxLength) {
            return cleaned.substring(0, maxLength) + "...(truncated)";
        }
        return cleaned;
    }

    /**
     * 把异常压成「类名[: 原因]」。Jackson 解析异常的 message 可能带响应片段，只留类名。
     */
    public static String describe(Throwable ex) {
        if (ex == null) {
            return "";
        }
        String type = ex.getClass().getSimpleName();
        if (ex instanceof JsonProcessingException) {
            return type;
        }
        String message = ex.getMessage();
        if (StringUtils.isBlank(message)) {
            return type;
        }
        return message.contains(type) ? message : type + ": " + message;
    }

    /**
     * 写日志用：脱敏后的消息 + 堆栈帧（不含 Throwable.toString，因为它会带原始 message）。
     */
    public static String redactedTrace(String message, Throwable ex, int maxFrames, String... secrets) {
        StringBuilder sb = new StringBuilder(StringUtils.defaultString(redactLimited(message, 2000, secrets)));
        if (ex != null) {
            sb.append(" [").append(ex.getClass().getName()).append(']');
            StackTraceElement[] frames = ex.getStackTrace();
            for (int i = 0; i < frames.length && i < maxFrames; i++) {
                sb.append("\n\tat ").append(frames[i]);
            }
        }
        return sb.toString();
    }

    private static boolean isClientSafe(String text, String... secrets) {
        if (StringUtils.isBlank(text) || text.indexOf('{') >= 0 || text.indexOf('}') >= 0 || looksLikeHtml(text)) {
            return false;
        }
        if (containsKnownSecret(text, secrets) || JWT.matcher(text).find() || BEARER.matcher(text).find()
                || QUOTED_SECRET.matcher(text).find() || PLAIN_SECRET.matcher(text).find() || APP_OR_TEAM.matcher(text).find()) {
            return false;
        }
        Matcher matcher = OPAQUE.matcher(text);
        while (matcher.find()) {
            if (!UUID.matcher(matcher.group()).matches()) {
                return false;
            }
        }
        return true;
    }

    private static boolean isShortCode(String code, String... secrets) {
        return StringUtils.isNotBlank(code) && code.length() <= 32 && isClientSafe(code, secrets);
    }

    private static boolean containsKnownSecret(String text, String... secrets) {
        if (secrets == null) {
            return false;
        }
        for (String secret : secrets) {
            if (secret != null && secret.length() >= MIN_SECRET && text.contains(secret)) {
                return true;
            }
        }
        return false;
    }

    private static String replaceSecrets(String text, String... secrets) {
        if (secrets == null || secrets.length == 0) {
            return text;
        }
        List<String> values = new ArrayList<>();
        for (String secret : secrets) {
            if (secret != null && secret.length() >= MIN_SECRET) {
                values.add(secret);
            }
        }
        values.sort(Comparator.comparingInt(String::length).reversed());
        String cleaned = text;
        for (String secret : values) {
            cleaned = cleaned.replace(secret, "[redacted]");
        }
        return cleaned;
    }

    private static String compose(String fallback, String code, String hint) {
        StringBuilder message = new StringBuilder(fallback);
        if (StringUtils.isNotBlank(code)) {
            message.append(' ').append(code);
        }
        if (StringUtils.isNotBlank(hint)) {
            message.append(' ').append(hint);
        }
        return message.toString();
    }

    private static String hint(String detail) {
        StringBuilder hint = new StringBuilder();
        Matcher status = HTTP_STATUS.matcher(detail);
        if (status.find()) {
            String code = status.group(1) != null ? status.group(1) : status.group(2);
            hint.append("HTTP ").append(code);
        }
        Matcher exception = EXCEPTION_NAME.matcher(detail);
        if (exception.find()) {
            if (hint.length() > 0) {
                hint.append(' ');
            }
            hint.append(exception.group(1));
        }
        return hint.toString();
    }

    private static boolean looksLikeHtml(String text) {
        String lower = text.toLowerCase();
        return lower.contains("<html") || lower.contains("<!doctype") || lower.contains("<head") || lower.contains("<body");
    }

    private static String limit(String text) {
        if (text.length() <= MAX_CLIENT) {
            return text;
        }
        return text.substring(0, MAX_CLIENT) + "...";
    }

    private static JsonNode payload(String detail) {
        int start = detail.indexOf('{');
        int end = detail.lastIndexOf('}');
        if (start < 0 || end <= start) {
            return null;
        }
        try {
            return MAPPER.readTree(detail.substring(start, end + 1));
        } catch (Exception ex) {
            return null;
        }
    }

    private static String text(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return "";
        }
        if (node.isTextual() || node.isNumber() || node.isBoolean()) {
            return node.asText();
        }
        return "";
    }
}
