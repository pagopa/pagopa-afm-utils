package it.gov.pagopa.afm.calculator.service;

import it.gov.pagopa.afm.calculator.entity.CiBundle;
import it.gov.pagopa.afm.calculator.model.configuration.Configuration;
import it.gov.pagopa.afm.calculator.repository.BundleRepository;
import it.gov.pagopa.afm.calculator.repository.CiBundleRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConfigurationService {

    @Autowired
    BundleRepository bundleRepository;

    @Autowired
    CiBundleRepository ciBundleRepository;

    @Autowired
    ModelMapper modelMapper;

    @Transactional
    public void save(Configuration configuration) {
        // erase tables
        bundleRepository.deleteAll();
        ciBundleRepository.deleteAll();

        // save
        bundleRepository.saveAllAndFlush(configuration.getBundles());

        List<CiBundle> ciBundleList = configuration.getCiBundles().parallelStream().map(ciBundleM -> {
            CiBundle ciBundleE = modelMapper.map(ciBundleM, CiBundle.class);
            ciBundleE.getBundle().getCiBundles().add(ciBundleE);
            return ciBundleE;
        }).collect(Collectors.toList());
        ciBundleRepository.saveAll(ciBundleList);

    }

    public Configuration get() {
        return Configuration.builder()
                .bundles(bundleRepository.findAll())
                .ciBundles(ciBundleRepository.findAll().parallelStream().map(ciBundle -> modelMapper.map(ciBundle, it.gov.pagopa.afm.calculator.model.configuration.CiBundle.class)).collect(Collectors.toList()))
                .build();
    }
}
