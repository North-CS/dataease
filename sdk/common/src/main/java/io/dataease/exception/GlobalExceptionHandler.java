package io.dataease.exception;


import io.dataease.i18n.Translator;
import io.dataease.result.ResultCode;
import io.dataease.result.ResultMessage;
import io.dataease.utils.LogUtil;
import io.dataease.utils.OpenMeMessages;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String SANITIZED_FALLBACK = "调用失败";
    private static final int SANITIZED_TRACE_FRAMES = 30;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultMessage MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        String msg = objectError.getDefaultMessage();
        msg = Translator.get(msg);
        LogUtil.error(msg);
        return new ResultMessage(ResultCode.PARAM_IS_INVALID.code(), msg);
    }

    @ExceptionHandler(DEException.class)
    public ResultMessage deExceptionHandler(DEException e) {
        String message = e.getMessage();
        String safe = clientMessage(message);
        logError(message, safe, e);
        return new ResultMessage(e.getCode(), safe);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResultMessage noUserExceptionHandler(Exception e) {
        String message = e.getMessage();
        if (StringUtils.contains(message, "Cannot invoke \"io.dataease.auth.bo.TokenUserBO.getUserId()\" because \"user\" is null")) {
            LogUtil.error(message, e);
            return new ResultMessage(ResultCode.USER_NOT_LOGGED_IN.code(), ResultCode.USER_NOT_LOGGED_IN.message());
        }
        String safe = clientMessage(message);
        logError(message, safe, e);
        return new ResultMessage(ResultCode.PARAM_IS_BLANK.code(), safe);
    }

    /**
     * 只有上游 HTTP 原文 / 带凭据特征的消息才替换，普通业务错误原样返回。
     */
    private static String clientMessage(String message) {
        return OpenMeMessages.needsSanitize(message) ? OpenMeMessages.clientMessage(SANITIZED_FALLBACK, message) : message;
    }

    /**
     * 消息没被替换：照旧带完整堆栈。被替换了：原 message 可能含凭据，
     * 而 Throwable 的堆栈输出会带 message，所以只记脱敏后的 message 和堆栈帧。
     */
    private static void logError(String message, String safe, Throwable e) {
        if (java.util.Objects.equals(safe, message)) {
            LogUtil.error(message, e);
        } else {
            LogUtil.error(OpenMeMessages.redactedTrace(message, e, SANITIZED_TRACE_FRAMES));
        }
    }

}
