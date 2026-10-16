package io.dataease.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenMeMessagesTest {

    private static final String FALLBACK = "JoySpace 调用失败";
    private static final String TEAM_TOKEN = "team-3f2a9c1e5b7d4a6f8e0c2b4d6f8a0c1e";
    private static final String APP_TOKEN = "app-0123456789abcdef0123456789abcdef";

    // ---------- clientMessage：上游 msg ----------

    @Test
    void safeBusinessMessageIsKept() {
        assertEquals("页签不存在", OpenMeMessages.clientMessage(FALLBACK, "{\"code\":40004,\"msg\":\"页签不存在\"}"));
    }

    @Test
    void messageWithStandardUuidIsKept() {
        String msg = "页签不存在: 5f0c1a2b-3c4d-4e5f-8a9b-0c1d2e3f4a5b";
        assertEquals(msg, OpenMeMessages.clientMessage(FALLBACK, "{\"code\":40004,\"msg\":\"" + msg + "\"}"));
    }

    @Test
    void mockStyleTokenInMsgIsBlocked() {
        String raw = "{\"code\":401,\"msg\":\"invalid token " + TEAM_TOKEN + "\"}";
        assertEquals(FALLBACK + " code=401", OpenMeMessages.clientMessage(FALLBACK, raw));
        raw = "{\"code\":401,\"msg\":\"invalid " + APP_TOKEN + "\"}";
        assertEquals(FALLBACK + " code=401", OpenMeMessages.clientMessage(FALLBACK, raw));
    }

    @Test
    void bearerAndAssignmentFormsAreBlocked() {
        assertEquals(FALLBACK + " code=401",
                OpenMeMessages.clientMessage(FALLBACK, "{\"code\":401,\"msg\":\"Authorization: Bearer abc.def-ghi\"}"));
        assertEquals(FALLBACK + " code=401",
                OpenMeMessages.clientMessage(FALLBACK, "{\"code\":401,\"msg\":\"bad appSecret=Sup3rS3cret\"}"));
        assertEquals(FALLBACK + " code=401",
                OpenMeMessages.clientMessage(FALLBACK, "{\"code\":401,\"msg\":\"bad access_token: Sup3rS3cret\"}"));
        assertEquals(FALLBACK + " code=401",
                OpenMeMessages.clientMessage(FALLBACK, "{\"code\":401,\"msg\":\"{'appSecret': 'Sup3rS3cret'}\"}"));
    }

    @Test
    void knownShortSecretIsBlockedByExactMatch() {
        // 少于 24 位、没有任何前缀特征：只能靠精确值替换
        String raw = "{\"code\":401,\"msg\":\"invalid key abcd1234efgh\"}";
        assertEquals("invalid key abcd1234efgh", OpenMeMessages.clientMessage(FALLBACK, raw));
        assertEquals(FALLBACK + " code=401", OpenMeMessages.clientMessage(FALLBACK, raw, "abcd1234efgh"));
    }

    @Test
    void longOpaqueRunIsBlocked() {
        String raw = "{\"code\":401,\"msg\":\"bad AbCdEfGhIjKlMnOpQrStUvWx12\"}";
        assertEquals(FALLBACK + " code=401", OpenMeMessages.clientMessage(FALLBACK, raw));
    }

    @Test
    void bearerHintWordIsNotTreatedAsCredential() {
        String msg = "Authorization 需要 Bearer teamAccessToken";
        assertEquals(msg, OpenMeMessages.clientMessage(FALLBACK, "{\"code\":40001,\"msg\":\"" + msg + "\"}"));
    }

    @Test
    void longPlainMessageIsKeptUpTo800() {
        String msg = "字段类型错误".repeat(60);
        assertTrue(msg.length() > 200 && msg.length() < 800);
        assertEquals(msg, OpenMeMessages.clientMessage(FALLBACK, "{\"code\":1,\"msg\":\"" + msg + "\"}"));
    }

    @Test
    void missingMsgFallsBackToShortCode() {
        assertEquals(FALLBACK + " code=500", OpenMeMessages.clientMessage(FALLBACK, "{\"code\":500,\"data\":null}"));
    }

    // ---------- clientMessage：HTTP / 异常 ----------

    @Test
    void httpStatusIsReported() {
        assertEquals(FALLBACK + " HTTP 502",
                OpenMeMessages.clientMessage(FALLBACK, "HttpClient查询失败: StatusCode: 502"));
    }

    @Test
    void exceptionClassNameIsReported() {
        assertEquals(FALLBACK + " ConnectException",
                OpenMeMessages.clientMessage(FALLBACK,
                        "HttpClient查询失败: ConnectException: Connect to openme.jd.local:80 failed: Connection refused"));
        assertEquals(FALLBACK + " SocketTimeoutException",
                OpenMeMessages.clientMessage(FALLBACK, "HttpClient查询失败: SocketTimeoutException: Read timed out"));
    }

    @Test
    void htmlBodyIsNeverForwarded() {
        String detail = "<html><body>502 Bad Gateway " + TEAM_TOKEN + "</body></html>";
        assertEquals(FALLBACK, OpenMeMessages.clientMessage(FALLBACK, detail));
    }

    @Test
    void clientMessageIsIdempotent() {
        String[] inputs = {
                "{\"code\":401,\"msg\":\"invalid token " + TEAM_TOKEN + "\"}",
                "HttpClient查询失败: StatusCode: 502",
                "{\"code\":40004,\"msg\":\"页签不存在\"}",
                "HttpClient查询失败: ConnectException: refused"
        };
        for (String input : inputs) {
            String once = OpenMeMessages.clientMessage(FALLBACK, input);
            assertEquals(once, OpenMeMessages.clientMessage(FALLBACK, once), input);
        }
    }

    // ---------- redact（日志） ----------

    @Test
    void redactRemovesCredentialsFromRawResponse() {
        String raw = "{\"code\":0,\"data\":{\"appAccessToken\":\"" + APP_TOKEN + "\",\"expireIn\":7200}}";
        String redacted = OpenMeMessages.redact(raw);
        assertFalse(redacted.contains(APP_TOKEN), redacted);
        assertTrue(redacted.contains("[redacted]"), redacted);
    }

    @Test
    void redactRemovesKnownSecretsAndKeepsUuid() {
        String uuid = "5f0c1a2b-3c4d-4e5f-8a9b-0c1d2e3f4a5b";
        String redacted = OpenMeMessages.redact("secret=short1 key abcd1234efgh page " + uuid, "abcd1234efgh");
        assertFalse(redacted.contains("abcd1234efgh"), redacted);
        assertTrue(redacted.contains(uuid), redacted);
    }

    @Test
    void redactLimitedTruncatesAfterRedaction() {
        String body = ("x ".repeat(5000)) + TEAM_TOKEN;
        String out = OpenMeMessages.redactLimited(body, 100);
        assertTrue(out.length() <= 100 + "...(truncated)".length(), String.valueOf(out.length()));
        assertFalse(out.contains(TEAM_TOKEN));
    }

    @Test
    void redactedTraceDoesNotCarryThrowableMessage() {
        Exception ex = new RuntimeException("boom " + TEAM_TOKEN);
        String trace = OpenMeMessages.redactedTrace("failed " + ex.getMessage(), ex, 5);
        assertFalse(trace.contains(TEAM_TOKEN), trace);
        assertTrue(trace.contains("java.lang.RuntimeException"), trace);
        assertTrue(trace.contains("\tat "), trace);
    }

    // ---------- describe ----------

    @Test
    void describeDropsJacksonSourceSnippet() {
        JsonProcessingException ex = new JsonProcessingException("Unrecognized token 'Invalid " + TEAM_TOKEN + "'") {
        };
        assertEquals(ex.getClass().getSimpleName(), OpenMeMessages.describe(ex));
        assertFalse(OpenMeMessages.describe(ex).contains(TEAM_TOKEN));
    }

    @Test
    void describeKeepsClassNameAndReason() {
        assertEquals("IllegalStateException: boom", OpenMeMessages.describe(new IllegalStateException("boom")));
        assertEquals("IllegalStateException", OpenMeMessages.describe(new IllegalStateException()));
    }

    // ---------- needsSanitize（全局异常处理器） ----------

    @Test
    void ordinaryBusinessMessagesAreNotTouched() {
        assertFalse(OpenMeMessages.needsSanitize("页签不存在"));
        assertFalse(OpenMeMessages.needsSanitize("SQL 语法错误 near '{0}'"));
        assertFalse(OpenMeMessages.needsSanitize("参数校验失败: {name} 不能为空"));
        assertFalse(OpenMeMessages.needsSanitize("ds_a_very_long_table_name_for_report_2026_q2 不存在"));
        assertFalse(OpenMeMessages.needsSanitize(null));
    }

    @Test
    void upstreamAndCredentialMessagesAreSanitized() {
        assertTrue(OpenMeMessages.needsSanitize("HttpClient查询失败: StatusCode: 502"));
        assertTrue(OpenMeMessages.needsSanitize("<html><body>oops</body></html>"));
        assertTrue(OpenMeMessages.needsSanitize("Authorization: Bearer abc.def-ghi"));
        assertTrue(OpenMeMessages.needsSanitize("{\"access_token\":\"abc\"}"));
        assertTrue(OpenMeMessages.needsSanitize("failed appSecret=Sup3rS3cret"));
        assertTrue(OpenMeMessages.needsSanitize("failed " + TEAM_TOKEN));
    }
}
