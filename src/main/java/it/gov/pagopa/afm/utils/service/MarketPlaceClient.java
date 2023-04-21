package it.gov.pagopa.afm.utils.service;

import feign.FeignException;
import io.swagger.v3.oas.annotations.Parameter;
import it.gov.pagopa.afm.utils.config.FeignConfig;
import it.gov.pagopa.afm.utils.model.bundle.BundleRequest;
import it.gov.pagopa.afm.utils.model.bundle.BundleResponse;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    value = "marketplace",
    url = "${service.marketplace.host}",
    configuration = FeignConfig.class)
public interface MarketPlaceClient {

  @Retryable(
      exclude = FeignException.FeignClientException.class,
      maxAttemptsExpression = "${retry.marketplace.maxAttempts}",
      backoff = @Backoff(delayExpression = "${retry.marketplace.maxDelay}"))
  @GetMapping(value = "/configuration")
  void getConfiguration();

  @Retryable(
      exclude = FeignException.FeignClientException.class,
      maxAttemptsExpression = "${retry.marketplace.maxAttempts}",
      backoff = @Backoff(delayExpression = "${retry.marketplace.maxDelay}"))
  @PostMapping(value = "/psps/{idPSP}/bundles/massive", consumes = MediaType.APPLICATION_JSON_VALUE)
  List<BundleResponse> createBundleByList(
      @PathVariable("idPSP") String idPSP, @RequestBody List<BundleRequest> body);
  
  @Retryable(
      exclude = FeignException.FeignClientException.class,
      maxAttemptsExpression = "${retry.marketplace.maxAttempts}",
      backoff = @Backoff(delayExpression = "${retry.marketplace.maxDelay}"))
  @PostMapping(value = "/cdis/{idcdi}", consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> deleteCdi(
      @PathVariable("idcdi") String idCdi,
      @NotBlank
      @Pattern(regexp = "[A-Z0-9_]{6,14}")
      @Parameter(description = "PSP code", required = true)
      @RequestParam("pspcode") String pspCode);
}
