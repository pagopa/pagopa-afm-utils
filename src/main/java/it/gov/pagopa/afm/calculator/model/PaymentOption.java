package it.gov.pagopa.afm.calculator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentOption {
    private Integer paymentAmount;
    private String primaryCreditorInstitution;
    private PaymentMethod paymentMethod;
    private Touchpoint touchPoint;
    private List<String> idPspList;
    private ArrayList<TransferList> transferList;
}



