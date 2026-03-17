package io.dataease.sso.client;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.dataease.sso.exception.SSOException;
import io.dataease.utils.Md5Utils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SSOClient {
    private static final Logger log = LoggerFactory.getLogger(SSOClient.class);

    @Value("${sso.appKey}")
    private String appKey;

    @Value("${sso.appToken}")
    private String appToken;

    @Value("${sso.api.getTicket}")
    private String getTicketUrl;

    @Value("${sso.api.verifyTicket}")
    private String verifyTicketUrl;

    @Value("${sso.api.logout}")
    private String logoutUrl;

    /**
     * 1. 换取正式登录态票据（/api/getTicket）
     * @param ssoServiceTicket 前端传来的临时票据
     * @param request 原始请求（用于获取IP、指纹、Header等）
     * @return 正式票据（REQ_DATA）
     */
    public String getTicket(String ssoServiceTicket, HttpServletRequest request) {
        long timestamp = System.currentTimeMillis();
        // 签名参数：token + timestamp + sso_service_ticket值
        String sign = generateSign(appToken, timestamp, ssoServiceTicket);

        // 准备请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("app", appKey);
        params.put("fp", getFpFromCookie(request));          // 从Cookie取指纹
        params.put("ip", getClientIp(request));              // 真实客户端IP
        params.put("url", request.getRequestURL().toString());
        params.put("time", String.valueOf(timestamp));
        params.put("sign", sign);
        params.put("sso_service_ticket", ssoServiceTicket);  // 注意参数名

        // 准备需要透传的Header
        Map<String, String> headers = buildHeaders(request);

        // 发起POST请求
        String responseBody = cn.hutool.http.HttpRequest.post(getTicketUrl)
                .form(params)
                .addHeaders(headers)
                .execute()
                .body();
        JSONObject respJson = JSON.parseObject(responseBody);

        int code = respJson.getIntValue("REQ_CODE");
        if (code == 1) {
            return respJson.getString("REQ_DATA");  // 正式票据
        } else {
            log.error("getTicket failed, code: {}, msg: {}", code, respJson.getString("REQ_MSG"));
            throw new SSOException("获取正式票据失败: " + respJson.getString("REQ_MSG"));
        }
    }

    /**
     * 2. 验证登录态票据（/api/verifyTicket）
     * @param ticket 当前请求携带的正式票据（可从Cookie中获取）
     * @param request 原始请求
     * @return 用户信息（包含username）
     */
    public JSONObject verifyTicket(String ticket, HttpServletRequest request) {
        long timestamp = System.currentTimeMillis();
        String sign = generateSign(appToken, timestamp, ticket);

        Map<String, Object> params = new HashMap<>();
        params.put("app", appKey);
        params.put("fp", getFpFromCookie(request));
        params.put("ip", getClientIp(request));
        params.put("url", request.getRequestURL().toString());
        params.put("time", String.valueOf(timestamp));
        params.put("sign", sign);
        params.put("ticket", ticket);  // 参数名是 ticket

        Map<String, String> headers = buildHeaders(request);

        String responseBody = cn.hutool.http.HttpRequest.post(verifyTicketUrl)
                .form(params)
                .addHeaders(headers)
                .execute()
                .body();
        JSONObject respJson = JSON.parseObject(responseBody);

        int code = respJson.getIntValue("REQ_CODE");
        if (code == 1) {
            return respJson.getJSONObject("REQ_DATA");
        } else {
            log.error("verifyTicket failed, code: {}, msg: {}", code, respJson.getString("REQ_MSG"));
            throw new SSOException("验证票据失败: " + respJson.getString("REQ_MSG"));
        }
    }

    /**
     * 3. 登出销毁票据（/api/logout）
     */
    public void logout(String ticket, HttpServletRequest request) {
        long timestamp = System.currentTimeMillis();
        String sign = generateSign(appToken, timestamp, ticket);

        Map<String, Object> params = new HashMap<>();
        params.put("app", appKey);
        params.put("fp", getFpFromCookie(request));
        params.put("ip", getClientIp(request));
        params.put("url", request.getRequestURL().toString());
        params.put("time", String.valueOf(timestamp));
        params.put("sign", sign);
        params.put("ticket", ticket);

        Map<String, String> headers = buildHeaders(request);

        String responseBody = cn.hutool.http.HttpRequest.post(logoutUrl)
                .form(params)
                .addHeaders(headers)
                .execute()
                .body();
        JSONObject respJson = JSON.parseObject(responseBody);

        int code = respJson.getIntValue("REQ_CODE");
        if (code != 1) {
            log.error("logout failed, code: {}, msg: {}", code, respJson.getString("REQ_MSG"));
            throw new SSOException("登出失败: " + respJson.getString("REQ_MSG"));
        }
    }

    // ---------- 辅助方法 ----------
    private String generateSign(String token, long timestamp, String... params) {
        StringBuilder sb = new StringBuilder(token);
        sb.append(timestamp);
        for (String p : params) {
            sb.append(p);
        }
        return Md5Utils.md5(sb.toString());  // 使用你现有的MD5工具
    }

    private String getFpFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jdd69fo72b8lfee".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return "";  // 若无指纹，传空字符串
    }

    private String getClientIp(HttpServletRequest request) {
        // 优先取 X-Forwarded-For，参考文档要求
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private Map<String, String> buildHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        // 必须设置的Header
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        // 透传客户端原始Header
        headers.put("User-Agent", request.getHeader("User-Agent"));
        headers.put("Origin", request.getHeader("Origin"));
        headers.put("Host", request.getHeader("Host"));
        headers.put("Referer", request.getHeader("Referer"));
        headers.put("X-Forwarded-For", request.getHeader("X-Forwarded-For"));
        headers.put("J-Forwarded-For", request.getHeader("J-Forwarded-For"));
        // 如果还有其他要求，继续添加
        return headers;
    }
}
