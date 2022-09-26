package it.gov.pagopa.afm.calculator.task;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.afm.calculator.entity.Bundle;
import it.gov.pagopa.afm.calculator.entity.CiBundle;
import it.gov.pagopa.afm.calculator.model.PaymentMethod;
import it.gov.pagopa.afm.calculator.model.Touchpoint;
import it.gov.pagopa.afm.calculator.model.configuration.Configuration;
import it.gov.pagopa.afm.calculator.repository.BundleRepository;
import it.gov.pagopa.afm.calculator.repository.CiBundleRepository;
import it.gov.pagopa.afm.calculator.util.AzuriteStorageUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ConfigurationTask implements Runnable {

    private BundleRepository bundleRepository;
    private CiBundleRepository ciBundleRepository;

    private ModelMapper modelMapper;

    private String storageConnectionString;

    private String containerBlob;

    public ConfigurationTask(BundleRepository bundleRepository, CiBundleRepository ciBundleRepository, ModelMapper modelMapper, String storageConnectionString, String containerBlob) {
        this.bundleRepository = bundleRepository;
        this.ciBundleRepository = ciBundleRepository;
        this.modelMapper = modelMapper;
        this.storageConnectionString = storageConnectionString;
        this.containerBlob = containerBlob;
    }

    @Override
    public void run() {
        Configuration configuration = getConfiguration();

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

    Configuration getConfiguration() {
        // try to create blob container
        AzuriteStorageUtil azuriteStorageUtil = new AzuriteStorageUtil(storageConnectionString, null,null, containerBlob);
        azuriteStorageUtil.createBlob();

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(storageConnectionString).buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerBlob);
        LocalDate now = LocalDate.now();
        String filename = String.format("configuration_%s_%s_%s.json", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        BlobClient blobClient = containerClient.getBlobClient(filename);

        //creating an object of output stream to receive the file's content from azure blob.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Configuration configuration = new Configuration();
        try {
            blobClient.downloadStream(outputStream);
            ObjectMapper objectMapper = new ObjectMapper();
            configuration = objectMapper.readValue(outputStream.toString(), Configuration.class);
        } catch (BlobStorageException | JsonProcessingException e) {
            log.error("Problem to deserialize configuration: ", e);
            throw new RuntimeException(e);
        }
        return configuration;
    }
}
