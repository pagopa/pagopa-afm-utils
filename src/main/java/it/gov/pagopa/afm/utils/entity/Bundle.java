package it.gov.pagopa.afm.utils.entity;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.GeneratedValue;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

@Container(containerName = "bundles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Bundle {

  @Id @GeneratedValue private String id;

  @PartitionKey
  @NotNull
  @Size(max = 35)
  private String idPsp;

  @NotNull private String idChannel;

  @NotNull private String idBrokerPsp;

  private String idCdi;

  @NotNull private String abi;

  private Boolean digitalStamp;

  // true if bundle must be used only for digital stamp
  private Boolean digitalStampRestriction;

  private String name;
  private String description;

  private Long paymentAmount;
  private Long minPaymentAmount;
  private Long maxPaymentAmount;

  private String paymentType;

  private String touchpoint;

  private String type;

  private List<String> transferCategoryList;

  private LocalDate validityDateFrom;

  private LocalDate validityDateTo;

  @CreatedDate private LocalDateTime insertedDate;

  @LastModifiedDate private LocalDateTime lastUpdatedDate;
}
