package it.gov.pagopa.afm.utils.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceAmount {
  private Long paymentAmount;
  private Long minPaymentAmount;
  private Long maxPaymentAmount;
}
