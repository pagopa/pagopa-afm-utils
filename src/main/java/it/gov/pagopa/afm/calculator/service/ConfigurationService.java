package it.gov.pagopa.afm.calculator.service;

import it.gov.pagopa.afm.calculator.model.configuration.Configuration;
import it.gov.pagopa.afm.calculator.repository.BundleRepository;
import it.gov.pagopa.afm.calculator.repository.CiBundleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigurationService {

    @Autowired
    BundleRepository bundleRepository;

    @Autowired
    CiBundleRepository ciBundleRepository;

    @Transactional
    public void save(Configuration configuration) {
        // erase tables
        bundleRepository.deleteAll();
        ciBundleRepository.deleteAll();

        // save
        bundleRepository.saveAll(configuration.getBundles());
        ciBundleRepository.saveAll(configuration.getCiBundles());
    }
}
