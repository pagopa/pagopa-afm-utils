package it.gov.pagopa.afm.utils.service;

import feign.FeignException;
import it.gov.pagopa.afm.utils.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "marketplace", url = "${service.marketplace.host}", configuration = FeignConfig.class)
public interface MarketPlaceClient {

    @Retryable(exclude = FeignException.FeignClientException.class, maxAttemptsExpression = "${retry.marketplace.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.marketplace.maxDelay}"))
    @GetMapping(value = "/configuration")
    void getConfiguration();
}
