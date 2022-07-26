package it.gov.pagopa.afm.calculator.model.bundle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CiBundleAttribute {

    private String id;
    private Long maxPaymentAmount;
    private String transferCategory;
    private TransferCategoryRelation transferCategoryRelation;
    private LocalDateTime insertedDate;
}
