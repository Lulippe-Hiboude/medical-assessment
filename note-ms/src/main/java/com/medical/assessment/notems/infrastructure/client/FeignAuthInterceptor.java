package com.medical.assessment.notems.infrastructure.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    public static final String AUTHORIZATION = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        final String authorization = ((ServletRequestAttributes)RequestContextHolder
                .getRequestAttributes())
                .getRequest()
                .getHeader(AUTHORIZATION);
        if (authorization != null) {
            template.header(AUTHORIZATION, authorization);
        }
    }
}
