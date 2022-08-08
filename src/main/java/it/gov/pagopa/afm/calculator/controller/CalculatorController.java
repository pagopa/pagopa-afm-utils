package it.gov.pagopa.afm.calculator.controller;

import it.gov.pagopa.afm.calculator.entity.Bundle;
import it.gov.pagopa.afm.calculator.model.PaymentOption;
import it.gov.pagopa.afm.calculator.service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
public class CalculatorController {
    @Autowired
    CalculatorService calculatorService;


    @PostMapping("/calculate")
    public List<Bundle> calculate(@RequestBody PaymentOption paymentOption){
        return calculatorService.calculate(paymentOption);
    }
}
