package it.gov.pagopa.afm.calculator.model.calculator;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CalculatorResponse {

    private List<CalculatorElem> list;
}