package com.medical.assessment.riskms.infrastructure.client;

import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class FeignAuthInterceptorTest {

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("should forward authorization header to Feign request")
    void shouldForwardAuthorizationHeader() {
        // given
        final String token = "Bearer my-jwt-token";

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(FeignAuthInterceptor.AUTHORIZATION, token);

        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(request)
        );

        final RequestTemplate requestTemplate = new RequestTemplate();

        final FeignAuthInterceptor interceptor = new FeignAuthInterceptor();

        // when
        interceptor.apply(requestTemplate);

        // then
        assertThat(requestTemplate.headers())
                .containsKey(FeignAuthInterceptor.AUTHORIZATION);

        assertThat(requestTemplate.headers()
                .get(FeignAuthInterceptor.AUTHORIZATION))
                .containsExactly(token);
    }

    @Test
    @DisplayName("should not add authorization header when it is missing")
    void shouldNotAddAuthorizationHeaderWhenItIsMissing() {
        // given
        final MockHttpServletRequest request = new MockHttpServletRequest();

        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(request)
        );

        final RequestTemplate requestTemplate = new RequestTemplate();

        final FeignAuthInterceptor interceptor = new FeignAuthInterceptor();

        // when
        interceptor.apply(requestTemplate);

        // then
        assertThat(requestTemplate.headers())
                .doesNotContainKey(FeignAuthInterceptor.AUTHORIZATION);
    }
}