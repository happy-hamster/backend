package de.sakpaas.backend.http;

import de.sakpaas.backend.model.Request;
import de.sakpaas.backend.service.RequestService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    final
    RequestService requestService;

    public RequestInterceptor(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) {
        Request requestObj = new Request();
        requestObj.setDate(ZonedDateTime.now());
        requestObj.setMethod(request.getMethod());
        requestObj.setRequestUri(request.getRequestURI());
        requestObj.setAddress(getClientIp(request));
        requestService.addRequest(requestObj);
        return true;
    }

    private String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}