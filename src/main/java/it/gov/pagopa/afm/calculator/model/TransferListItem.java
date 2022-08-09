package it.gov.pagopa.afm.calculator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransferListItem {
    private String creditorInstitution;
    private String transferCategory;
}
