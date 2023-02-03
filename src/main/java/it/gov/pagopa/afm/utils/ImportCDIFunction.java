package it.gov.pagopa.afm.utils;

import it.gov.pagopa.afm.utils.model.bundle.BundleResponse;
import it.gov.pagopa.afm.utils.model.bundle.CDIWrapper;
import it.gov.pagopa.afm.utils.service.CDIService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class ImportCDIFunction implements Function<Mono<CDIWrapper>, Mono<List<BundleResponse>>> {

  @Autowired(required = false)
  private CDIService cdiService;

  @Override
  public Mono<List<BundleResponse>> apply(Mono<CDIWrapper> input) {

    return input.map(
        wrapper ->
            Optional.ofNullable(cdiService)
                .map(result -> cdiService.syncCDI())
                .orElse(new ArrayList<>()));
  }
}
