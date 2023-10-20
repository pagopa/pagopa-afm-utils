package it.gov.pagopa.afm.utils.entity;

import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Detail {

  private String idBrokerPsp;
  private String idChannel;
  private String name;
  private String description;
  private String paymentType;
  private Boolean channelApp;
  private Boolean channelCardsCart;
  private List<ServiceAmount> serviceAmount;
}
