package it.gov.pagopa.afm.calculator.controller;

import it.gov.pagopa.afm.calculator.model.PaymentOption;
import it.gov.pagopa.afm.calculator.model.calculator.CalculatorResponse;
import it.gov.pagopa.afm.calculator.service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class CalculatorController {
    @Autowired
    CalculatorService calculatorService;


    @PostMapping("/calculate")
    public CalculatorResponse calculate(@RequestBody PaymentOption paymentOption){
        return calculatorService.calculate(paymentOption);
    }
}
