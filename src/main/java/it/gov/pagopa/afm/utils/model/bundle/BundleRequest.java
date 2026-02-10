package it.gov.pagopa.afm.utils.model.bundle;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BundleRequest implements Serializable {
  @NotNull
  private String idChannel;
  @NotNull
  private String idBrokerPsp;

  @Schema(description = "is the bundle valid for cart payments?",
          defaultValue = "true")
  @JsonSetter(nulls = Nulls.SKIP)
  private Boolean cart = true;

  private String idCdi;
  @NotNull
  private String abi;
  private String name;
  @NotNull
  private String pspBusinessName;
  private String urlPolicyPsp;
  private String description;
  private Long paymentAmount;
  private Long minPaymentAmount;
  private Long maxPaymentAmount;
  private String paymentType;
  private Boolean digitalStamp;
  private Boolean digitalStampRestriction;
  private String touchpoint;
  private BundleType type;
  private List<String> transferCategoryList;
  private LocalDate validityDateFrom;
  private LocalDate validityDateTo;
  @NotNull
  private Boolean onUs;
}