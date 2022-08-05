package it.gov.pagopa.afm.calculator.model.calculator;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CalculatorElem {

    private Long taxPayerFee;
    private String idPsp;
    private List<Transfer> transfer;
}