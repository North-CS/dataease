package io.dataease.share.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import io.dataease.auth.DeLinkPermit;
import io.dataease.constant.AuthConstant;
import io.dataease.exception.DEException;
import io.dataease.share.manage.XpackShareManage;
import io.dataease.share.util.LinkTokenUtil;
import io.dataease.utils.LogUtil;
import io.dataease.utils.ServletUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
public class DeLinkAop {

    private static final String PARAM_VARIABLE_PREFIX = "p";
    private static final String SPRING_EL_FLAG = "#";

    private final ExpressionParser parser = new SpelExpressionParser();

    @Resource
    private XpackShareManage xpackShareManage;

    @Around(value = "@annotation(io.dataease.auth.DeLinkPermit)")
    public Object logAround(ProceedingJoinPoint point) throws Throwable {
        Object[] params = point.getArgs();
        String linkToken = ServletUtils.getHead(AuthConstant.LINK_TOKEN_KEY);
        if (StringUtils.isNotBlank(linkToken)) {
            MethodSignature ms = (MethodSignature) point.getSignature();
            Method method = ms.getMethod();
            DeLinkPermit deLinkPermit = method.getAnnotation(DeLinkPermit.class);
            String value = deLinkPermit.value();
            if (StringUtils.isBlank(value)) {
                value = SPRING_EL_FLAG + PARAM_VARIABLE_PREFIX + "0";
            }
            Long id = getExpression(params, value);
            DecodedJWT jwt = JWT.decode(linkToken);
            Long resourceId = jwt.getClaim("resourceId").asLong();
            if (!id.equals(resourceId)) {
                DEException.throwException("link token invalid");
                return false;
            }

            Long uid = jwt.getClaim("uid").asLong();
            String secret = xpackShareManage.queryPwd(resourceId, uid);
            if (StringUtils.isBlank(secret)) {
                secret = LinkTokenUtil.defaultPwd;
            }
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Verification verification = JWT.require(algorithm);
            JWTVerifier verifier = verification.build();
            DecodedJWT decode = JWT.decode(linkToken);
            algorithm.verify(decode);
            verifier.verify(linkToken);
        }
        try {
            return point.proceed(params);
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            throw e;
        }
    }

    public Long getExpression(Object[] params, String expression) {
        StandardEvaluationContext context = buildContext(params);
        Object o = resolveValue(expression, context);
        if (ObjectUtils.isNotEmpty(o)) return Long.parseLong(o.toString());
        return null;
    }

    private StandardEvaluationContext buildContext(Object[] params) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (params != null && params.length == 1) {
            context.setRootObject(params[0]);
        }
        for (int i = 0; i < Objects.requireNonNull(params).length; i++) {
            Object paramValue = params[i];
            context.setVariable(PARAM_VARIABLE_PREFIX + i, paramValue);
        }
        return context;
    }

    private Object resolveValue(String exp, EvaluationContext context) {
        if (StringUtils.contains(exp, SPRING_EL_FLAG)) {
            Expression expression = parser.parseExpression(exp);
            return expression.getValue(context);
        }
        return exp;
    }
}
