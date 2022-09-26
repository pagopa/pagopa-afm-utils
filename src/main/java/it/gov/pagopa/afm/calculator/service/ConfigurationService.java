package it.gov.pagopa.afm.calculator.service;

import it.gov.pagopa.afm.calculator.entity.Bundle;
import it.gov.pagopa.afm.calculator.entity.CiBundle;
import it.gov.pagopa.afm.calculator.model.PaymentMethod;
import it.gov.pagopa.afm.calculator.model.Touchpoint;
import it.gov.pagopa.afm.calculator.model.configuration.Configuration;
import it.gov.pagopa.afm.calculator.repository.BundleRepository;
import it.gov.pagopa.afm.calculator.repository.CiBundleRepository;
import it.gov.pagopa.afm.calculator.task.ConfigurationTask;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConfigurationService {
    @Value("${azure.storage.connectionString}")
    private String storageConnectionString;

    @Value("${azure.storage.blobName}")
    private String containerBlob;
    @Autowired
    BundleRepository bundleRepository;

    @Autowired
    CiBundleRepository ciBundleRepository;

    @Autowired
    ModelMapper modelMapper;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void save() {
        ConfigurationTask configurationTask = new ConfigurationTask(bundleRepository, ciBundleRepository, modelMapper, storageConnectionString, containerBlob);
        CompletableFuture.runAsync(configurationTask).thenRun(() -> log.debug("Configuration loaded " + LocalDateTime.now()));
    }
    public void save(Configuration configuration) {
        // erase tables
        bundleRepository.deleteAll();
        ciBundleRepository.deleteAll();

        List<Bundle> bundles = configuration.getBundles();
        // set any to null to simplify query during calculation
        bundles.parallelStream().forEach(bundle -> {
            if (Touchpoint.ANY.equals(bundle.getTouchpoint())) {
                bundle.setTouchpoint(null);
            }

            if (PaymentMethod.ANY.equals(bundle.getPaymentMethod())) {
                bundle.setPaymentMethod(null);
            }
        });

        // save
        List<Bundle> bundleList = bundleRepository.saveAllAndFlush(bundles);
        List<Bundle> bundleListToSave = new ArrayList<>();
        List<CiBundle> ciBundleList = configuration.getCiBundles().parallelStream().map(ciBundleM -> {
            CiBundle ciBundleE = modelMapper.map(ciBundleM, CiBundle.class);

            Optional<Bundle> optBundle = bundleList.parallelStream().filter(bundle ->
                    bundle.getId().equals(ciBundleE.getBundle().getId())
            ).findFirst();
            if (optBundle.isPresent()) {
                Bundle bundle = optBundle.get();
                bundle.getCiBundles().add(ciBundleE);
                bundleListToSave.add(bundle);
            }
            return ciBundleE;
        }).collect(Collectors.toList());
        ciBundleRepository.saveAllAndFlush(ciBundleList);
        bundleRepository.saveAll(bundleListToSave);
    }

    public Configuration get() {
        return Configuration.builder()
                .bundles(bundleRepository.findAll())
                .ciBundles(ciBundleRepository.findAll().parallelStream().map(ciBundle -> modelMapper.map(ciBundle, it.gov.pagopa.afm.calculator.model.configuration.CiBundle.class)).collect(Collectors.toList()))
                .build();
    }
}
