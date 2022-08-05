package it.gov.pagopa.afm.calculator.service;

import it.gov.pagopa.afm.calculator.model.PaymentOption;
import it.gov.pagopa.afm.calculator.model.TransferList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link Cacheable} methods are ignored when called from within the same class
 */
@Component
@Slf4j
public class UtilityComponent {

    @Cacheable(value = "getTaxonomyList")
    public List<String> getTaxonomyList(PaymentOption paymentOption) {
        log.debug("getTaxonomyList");
        return paymentOption.getTransferList() != null ?
                paymentOption.getTransferList()
                        .parallelStream()
                        .map(TransferList::getTransferCategory)
                        .distinct()
                        .collect(Collectors.toList())
                : null;
    }

    @Cacheable(value = "getPrimaryTaxonomyList")
    public List<String> getPrimaryTaxonomyList(PaymentOption paymentOption, String primaryFiscalCode) {
        log.debug("getPrimaryTaxonomyList {} ", primaryFiscalCode);
        return paymentOption.getTransferList() != null ?
                paymentOption.getTransferList()
                        .parallelStream()
                        .filter(elem -> primaryFiscalCode.equals(elem.getCreditorInstitution()))
                        .map(TransferList::getTransferCategory)
                        .distinct()
                        .collect(Collectors.toList())
                : new ArrayList<>();
    }
}
