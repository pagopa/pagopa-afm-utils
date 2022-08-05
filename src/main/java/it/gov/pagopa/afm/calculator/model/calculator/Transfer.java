package it.gov.pagopa.afm.calculator.model.calculator;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Transfer {
    private Long incurredFee;
    private String creditorInstitution;
}

