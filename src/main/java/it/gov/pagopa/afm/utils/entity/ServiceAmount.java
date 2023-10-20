package it.gov.pagopa.afm.utils.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ServiceAmount {
  private Long paymentAmount;
  private Long minPaymentAmount;
  private Long maxPaymentAmount;
}
