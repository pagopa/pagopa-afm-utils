package it.gov.pagopa.afm.utils.config;

import static it.gov.pagopa.afm.utils.Constants.HEADER_REQUEST_ID;
import static it.gov.pagopa.afm.utils.Constants.HEADER_SUBSCRIPTION_KEY;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

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
