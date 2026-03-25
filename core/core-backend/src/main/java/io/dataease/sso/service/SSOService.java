package io.dataease.sso.service;

import com.alibaba.fastjson2.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import io.dataease.auth.bo.TokenUserBO;
import io.dataease.auth.vo.TokenVO;
import io.dataease.sso.client.SSOClient;
import io.dataease.sso.exception.SSOException;
import io.dataease.utils.Md5Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SSOService {

    private static final Logger log = LoggerFactory.getLogger(SSOService.class);

    @Autowired
    private SSOClient ssoClient;

    /**
     * SSO登录 - 兑换正式票据
     * @param ticket 前端传来的sso_service_ticket
     * @param request 原始请求（用于获取Header、IP等）
     * @param response HttpServletResponse（用于写Cookie，如果需要）
     * @return 登录结果，包含系统JWT token
     */
    public Map<String, Object> ssoLogin(String ticket, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 调用SSO的getTicket接口，换取正式票据
            String ssoTicket = ssoClient.getTicket(ticket, request);
            System.out.println("ssoservice:"+ ssoTicket);

//            Cookie ticketCookie = new Cookie("sso.jd.com", ssoTicket);
//            System.out.println("正式票据"+ ssoTicket);
//            ticketCookie.setDomain("sso.jd.com");
//            ticketCookie.setPath("/");
            // 设置票据有效期（应与 SSO 服务返回的票据有效期一致，例如 7 天）
//            ticketCookie.setMaxAge(7 * 24 * 60 * 60); // 7 天，单位秒
//            ticketCookie.setHttpOnly(true);   // 防止 XSS 读取
//            ticketCookie.setSecure(true);     // 仅在 HTTPS 下传输
//            response.addCookie(ticketCookie);

            // 2. 根据正式票据，获取用户信息（可选，也可以后续在verify时获取）
            //JSONObject userInfo = ssoClient.verifyTicket(ssoTicket, request);
            //String username = userInfo.getString("username");
//            String username = "admin";

            // 3. 生成系统自己的JWT token（与现有系统一致）
            TokenUserBO tokenUserBO = new TokenUserBO();
//            tokenUserBO.setUserId(getUserIdByUsername(username)); // 根据username查询或创建用户
//            tokenUserBO.setDefaultOid(getDefaultOid(username));
            tokenUserBO.setUserId(1L);
            tokenUserBO.setDefaultOid(1L);
            String secret = Md5Utils.md5("DataEase@123456");
            TokenVO tokenVO = generate(tokenUserBO, secret);

            // 4. （可选）将正式票据与JWT关联存储，便于后续登出或校验
//             redisTemplate.opsForValue().set("SSO:" + tokenVO.getToken(), ssoTicket, 7, TimeUnit.DAYS);

            // 5. 返回JWT给前端，存入localStorage
            result.put("success", true);
            result.put("token", tokenVO.getToken());
            System.out.println("token:"+tokenVO.getToken());
            result.put("exp", tokenVO.getExp());
            result.put("ticket",ssoTicket);

        } catch (SSOException e) {
            log.error("SSO登录失败", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        System.out.println(result);
        return result;
    }

    /**
     * 验证SSO票据 - 用于后续请求鉴权
     * @param ticket 当前请求携带的正式票据（这里假设前端会传回来，但实际更推荐通过拦截器自动处理）
     * @param request 原始请求
     * @return 用户信息
     */
    public Map<String, Object> verifyTicket(String ticket, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            JSONObject userInfo = ssoClient.verifyTicket(ticket, request);
            result.put("success", true);
            result.put("username", userInfo.getString("username"));
            // 可放入更多信息
        } catch (SSOException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * SSO登出
     * @param request 原始请求（用于获取JWT或票据）
     * @param response HttpServletResponse（用于清除Cookie）
     */
    public Map<String, Object> ssoLogout(String ticket,HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 从请求中获取系统JWT token（例如从Authorization头）
            String jwtToken = extractJwtFromRequest(request);
            System.out.println("退出逻辑开始"+ ticket);
            // 2. 根据JWT找到关联的SSO正式票据（如果之前存储过）
//            String ssoTicket = getSsoTicketByJwt(jwtToken); // 从Redis查询

            if (ticket != null) {
                // 3. 调用SSO登出接口

                ssoClient.logout(ticket, request);
                // 4. 删除本地存储的关联
                // redisTemplate.delete("SSO:" + jwtToken);
            }

            // 5. 清除本地Cookie（如果有）
//             CookieUtil.deleteCookie(response, "SSO_TICKET");

            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 生成JWT的方法（保持不变）
    private TokenVO generate(TokenUserBO bo, String secret) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Long userId = bo.getUserId();
        Long defaultOid = bo.getDefaultOid();
        JWTCreator.Builder builder = JWT.create();
        builder.withClaim("uid", userId).withClaim("oid", defaultOid);
        String token = builder.sign(algorithm);
        long exp = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000;
        return new TokenVO(token, exp);
    }

    // 以下为辅助方法，根据实际业务实现
    private Long getUserIdByUsername(String username) {
        // 查询数据库，若不存在可自动创建
        return 1L;
    }

    private Long getDefaultOid(String username) {
        return 1L;
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private String getSsoTicketByJwt(String jwt) {
        // 从Redis查询
        // return redisTemplate.opsForValue().get("SSO:" + jwt);
        return null; // 示例
    }
}
