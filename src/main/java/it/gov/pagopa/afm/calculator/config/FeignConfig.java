package it.gov.pagopa.afm.calculator.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@Slf4j
public class FeignConfig {

	static final String HEADER_REQUEST_ID = "X-Request-Id";
	static final String HEADER_SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key";
    @Value("${service.marketplace.subscriptionKey}")
    private String subscriptionKey;

    @Bean
    public RequestInterceptor requestIdInterceptor() {
        return requestTemplate -> requestTemplate.header(HEADER_REQUEST_ID, MDC.get("requestId"));
    }

    @Bean
    public RequestInterceptor subscriptionKeyInterceptor() {
        return requestTemplate -> requestTemplate.header(HEADER_SUBSCRIPTION_KEY, subscriptionKey);
    }
}
