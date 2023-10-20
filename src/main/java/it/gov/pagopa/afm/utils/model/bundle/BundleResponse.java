package it.gov.pagopa.afm.utils.model.bundle;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class BundleResponse {
  private String idBundle;
}
