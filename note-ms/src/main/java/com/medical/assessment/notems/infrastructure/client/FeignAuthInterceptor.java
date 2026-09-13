package com.medical.assessment.notems.infrastructure.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    public static final String AUTHORIZATION = "Authorization";

    /**
     * Propagates the HTTP {@code Authorization} header from the incoming request
     * to an outgoing Feign request.
     *
     * <p>This allows the JWT bearer token received by the current service to be
     * forwarded when communicating with another microservice through Feign.</p>
     *
     * @param template the Feign {@link RequestTemplate} to which the authorization
     *                 header is added
     */
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
