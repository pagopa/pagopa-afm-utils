package it.gov.pagopa.afm.calculator.service;

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
        log.info("Configuration loading " + LocalDateTime.now());
        ConfigurationTask configurationTask = new ConfigurationTask(bundleRepository, ciBundleRepository, modelMapper, storageConnectionString, containerBlob);
        CompletableFuture.runAsync(configurationTask)
                .whenComplete((msg, ex) -> {
                    LocalDateTime when = LocalDateTime.now();
                    if (ex != null) {
                        log.error("Configuration not loaded " + when, ex);
                    } else {
                        log.info("Configuration loaded " + when);
                    }
                });
    }

    public Configuration get() {
        return Configuration.builder()
                .bundles(bundleRepository.findAll())
                .ciBundles(ciBundleRepository.findAll().parallelStream().map(ciBundle -> modelMapper.map(ciBundle, it.gov.pagopa.afm.calculator.model.configuration.CiBundle.class)).collect(Collectors.toList()))
                .build();
    }
}
