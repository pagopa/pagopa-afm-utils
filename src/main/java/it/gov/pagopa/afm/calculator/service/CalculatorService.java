package it.gov.pagopa.afm.calculator.service;

import it.gov.pagopa.afm.calculator.entity.Bundle;
import it.gov.pagopa.afm.calculator.model.BundleType;
import it.gov.pagopa.afm.calculator.model.PaymentOption;
import it.gov.pagopa.afm.calculator.repository.BundleRepository;
import it.gov.pagopa.afm.calculator.repository.CiBundleRepository;
import it.gov.pagopa.afm.calculator.util.BundleSpecification;
import it.gov.pagopa.afm.calculator.util.SearchCriteria;
import it.gov.pagopa.afm.calculator.util.SearchOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculatorService {

    @Autowired
    BundleRepository bundleRepository;

    @Autowired
    CiBundleRepository ciBundleRepository;


    public List<Bundle> calculate(PaymentOption paymentOption) {
        // create filters
        var touchpointFilter = new BundleSpecification(new SearchCriteria("touchpoint", SearchOperation.NULL_OR_EQUAL, paymentOption.getTouchPoint()));
        var paymentMethodFilter = new BundleSpecification(new SearchCriteria("paymentMethod", SearchOperation.NULL_OR_EQUAL, paymentOption.getPaymentMethod()));
        var pspFilter = new BundleSpecification(new SearchCriteria("idPsp", SearchOperation.IN, paymentOption.getIdPspList()));
        var ecFilter = new BundleSpecification(new SearchCriteria("ciBundles.ciFiscalCode", SearchOperation.EQUAL, paymentOption.getPrimaryCreditorInstitution()));
        var globalFilter = new BundleSpecification(new SearchCriteria("type", SearchOperation.EQUAL, BundleType.GLOBAL));
        var minPriceRangeFilter = new BundleSpecification(new SearchCriteria("minPaymentAmount", SearchOperation.LESS_THAN_EQUAL, paymentOption.getPaymentAmount()));
        var maxPriceRangeFilter = new BundleSpecification(new SearchCriteria("maxPaymentAmount", SearchOperation.GREATER_THAN, paymentOption.getPaymentAmount()));
        var taxonomyFilter = new BundleSpecification(new SearchCriteria("maxPaymentAmount", SearchOperation.GREATER_THAN, paymentOption.getPaymentAmount()));


        var specifications = Specification.where(touchpointFilter)
                .and(paymentMethodFilter)
                .and(pspFilter)
                .and(maxPriceRangeFilter)
                .and(minPriceRangeFilter)
                .and(globalFilter.or(ecFilter))
                .and(taxonomyFilter);

        return bundleRepository.findAll(specifications);
    }
}
