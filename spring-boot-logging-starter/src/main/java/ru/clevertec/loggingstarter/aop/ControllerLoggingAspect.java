package ru.clevertec.loggingstarter.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.ObjectFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Slf4j
@RequiredArgsConstructor
public class ControllerLoggingAspect {

    public static final String LOG_MESSAGE_BEFORE = "Request -> HTTP Method: [{}] , URI: [{}] , Parameters: [{}]";

    public static final String LOG_MESSAGE_AFTER_RETURNING = "Response -> HTTP Method: [{}] , URI: [{}] , " +
                                                             "Parameters: [{}] ---> [{}]";

    private final ObjectFactory<HttpServletRequest> httpServletRequestFactory;

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) || " +
              "@within(org.springframework.stereotype.Controller)")
    public void isControllerLayer() {
    }

    @Before("isControllerLayer()")
    public void logBeforeRequest(JoinPoint joinPoint) {
        HttpServletRequest request = getHttpServletRequest();
        String httpMethod = request.getMethod();
        String requestUri = request.getRequestURI();
        String parameters = parametersToString(request.getParameterMap());

        log.info(LOG_MESSAGE_BEFORE, httpMethod, requestUri, parameters);
    }

    @AfterReturning(value = "isControllerLayer()", returning = "result")
    public void logAfterRequest(Object result) {
        HttpServletRequest request = getHttpServletRequest();
        String httpMethod = request.getMethod();
        String requestUri = request.getRequestURI();
        String parameters = parametersToString(request.getParameterMap());

        log.info(LOG_MESSAGE_AFTER_RETURNING, httpMethod, requestUri, parameters, result);
    }

    private HttpServletRequest getHttpServletRequest() {
        return httpServletRequestFactory.getObject();
    }

    private String parametersToString(Map<String, String[]> parameterMap) {
        return parameterMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + Arrays.toString(entry.getValue()))
                .collect(Collectors.joining(", "));
    }

}
