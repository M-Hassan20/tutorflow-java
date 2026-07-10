package com.tutorflow.tutorservice.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class RequestContext {

    private final HttpServletRequest request;

    public RequestContext(HttpServletRequest request) {
        this.request = request;
    }

    public String getUserEmail() {
        return request.getHeader("X-User-Email");
    }

    public String getUserRole() {
        return request.getHeader("X-User-Role");
    }

    public boolean isTutor() {
        return "TUTOR".equals(getUserRole());
    }

    public boolean isStudent() {
        return "STUDENT".equals(getUserRole());
    }

    public boolean isParent() {
        return "PARENT".equals(getUserRole());
    }
}