package it.gov.pagopa.afm.calculator.service;

import it.gov.pagopa.afm.calculator.entity.Bundle;
import it.gov.pagopa.afm.calculator.entity.CiBundle;
import it.gov.pagopa.afm.calculator.model.BundleType;
import it.gov.pagopa.afm.calculator.model.PaymentOption;
import it.gov.pagopa.afm.calculator.model.calculator.CalculatorElem;
import it.gov.pagopa.afm.calculator.model.calculator.CalculatorResponse;
import it.gov.pagopa.afm.calculator.model.calculator.Transfer;
import it.gov.pagopa.afm.calculator.repository.BundleRepository;
import it.gov.pagopa.afm.calculator.util.BundleSpecification;
import it.gov.pagopa.afm.calculator.util.SearchCriteria;
import it.gov.pagopa.afm.calculator.util.SearchOperation;
import it.gov.pagopa.afm.calculator.util.TaxBundleSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CalculatorService {

    @Autowired
    BundleRepository bundleRepository;

    @Autowired
    UtilityComponent utilityComponent;


    @Cacheable(value = "calculate")
    public CalculatorResponse calculate(PaymentOption paymentOption) {
        // create filters
        var touchpointFilter = new BundleSpecification(new SearchCriteria("touchpoint", SearchOperation.NULL_OR_EQUAL, paymentOption.getTouchpoint()));
        var paymentMethodFilter = new BundleSpecification(new SearchCriteria("paymentMethod", SearchOperation.NULL_OR_EQUAL, paymentOption.getPaymentMethod()));
        var pspFilter = new BundleSpecification(new SearchCriteria("idPsp", SearchOperation.IN, paymentOption.getIdPspList()));
        var ciFilter = new BundleSpecification(new SearchCriteria("ciBundles.ciFiscalCode", SearchOperation.EQUAL, paymentOption.getPrimaryCreditorInstitution()));
        var globalFilter = new BundleSpecification(new SearchCriteria("type", SearchOperation.EQUAL, BundleType.GLOBAL));
        // the payment amount should be in range [minPaymentAmount, maxPaymentAmount]
        var minPriceRangeFilter = new BundleSpecification(new SearchCriteria("minPaymentAmount", SearchOperation.LESS_THAN_EQUAL, paymentOption.getPaymentAmount()));
        var maxPriceRangeFilter = new BundleSpecification(new SearchCriteria("maxPaymentAmount", SearchOperation.GREATER_THAN_EQUAL, paymentOption.getPaymentAmount()));
        var taxonomyFilter = new TaxBundleSpecification(utilityComponent.getTaxonomyList(paymentOption));

        var specifications = Specification.where(touchpointFilter)
                .and(paymentMethodFilter)
                .and(pspFilter)
                .and(maxPriceRangeFilter)
                .and(minPriceRangeFilter)
                .and(ciFilter.or(globalFilter))
                .and(taxonomyFilter);

        // do the query
        var bundles = bundleRepository.findAll(specifications);

        // calculate the taxPayerFee
        List<CalculatorElem> response = new ArrayList<>();
        for (Bundle bundle : bundles) {
            List<Transfer> transfers = new ArrayList<>();
            for (CiBundle cibundle : bundle.getCiBundles()) {
                transfers = cibundle.getAttributes().parallelStream()
                        .map(attribute -> {
                            Long fee = 0L;
                            if (attribute.getTransferCategory() == null
                                    || utilityComponent.getPrimaryTaxonomyList(paymentOption, paymentOption.getPrimaryCreditorInstitution())
                                    .contains(attribute.getTransferCategory())) {
                                fee = bundle.getPaymentAmount() > attribute.getMaxPaymentAmount() ?
                                        attribute.getMaxPaymentAmount() :
                                        bundle.getPaymentAmount();
                            }

                            return Transfer.builder()
                                    .creditorInstitution(cibundle.getCiFiscalCode())
                                    .incurredFee(fee)
                                    .build();
                        })
                        .collect(Collectors.toList());
            }

            var sum = transfers.parallelStream()
                    .map(Transfer::getIncurredFee)
                    .filter(incurredFee -> incurredFee != 0L)
                    .reduce(Long::sum)
                    .orElse(0L);

            Long taxPayerFee = bundle.getPaymentAmount() - sum;
            var elem = CalculatorElem.builder()
                    .taxPayerFee(taxPayerFee)
                    .idPsp(bundle.getIdPsp())
                    .transfer(transfers)
                    .build();
            response.add(elem);
        }


        return CalculatorResponse.builder()
                .list(response)
                .build();
    }

}
