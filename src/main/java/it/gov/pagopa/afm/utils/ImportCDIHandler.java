package it.gov.pagopa.afm.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.cloud.function.adapter.azure.FunctionInvoker;
import org.springframework.util.CollectionUtils;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.CosmosDBTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import it.gov.pagopa.afm.utils.entity.CDI;
import it.gov.pagopa.afm.utils.entity.Detail;
import it.gov.pagopa.afm.utils.entity.ServiceAmount;
import it.gov.pagopa.afm.utils.model.bundle.BundleRequest;
import it.gov.pagopa.afm.utils.model.bundle.BundleResponse;
import it.gov.pagopa.afm.utils.model.bundle.BundleType;
import it.gov.pagopa.afm.utils.model.bundle.BundleWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportCDIHandler extends FunctionInvoker<BundleWrapper, List<BundleResponse>> {
		
	@FunctionName("importCDIFunction")
	public List<BundleResponse> execute(
			/*
			@CosmosDBTrigger(
					name = "CDIDatastoreTrigger",
					databaseName = "cdi",
				    containerName = "cdi-collection",
				    leaseContainerName = "cdi-collection-leases",
				    createLeaseContainerIfNotExists = true,
					connection = "COSMOS_CONN_STRING"
					) 
			List<CDI> items,*/
			@CosmosDBTrigger(
					name = "CDIDatastoreTrigger",
					databaseName = "cdi",
					collectionName = "cdi-collection",
					leaseCollectionName = "cdi-collection-leases",
					createLeaseCollectionIfNotExists = true,
					connectionStringSetting = "COSMOS_CONN_STRING") 
			List<CDI> items,
			/*@HttpTrigger(
					name = "GeneratePackageTrigger",
					methods = {HttpMethod.GET},
					authLevel = AuthorizationLevel.ANONYMOUS
					) HttpRequestMessage<Optional<CDI>> request,*/
			ExecutionContext context) {
		
		log.info("Processing the trigger.");
		
		BundleWrapper bundleWrapper = new BundleWrapper();
		
		for (CDI cdi: items) {
			log.info("Generate Package function executed at: " + LocalDateTime.now() + " for CDI with idPsp: " + cdi.getIdPsp());
			if (!CollectionUtils.isEmpty(cdi.getDetails())) {
				DateTimeFormatter  dfDate     = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				bundleWrapper.setIdPsp(cdi.getIdPsp());
				BundleRequest bundleRequest = new BundleRequest();
				//TODO check for not existing field
				//bundleRequest.setIdCdi(cdi.getIdCdi());
				bundleRequest.setDigitalStamp(cdi.getDigitalStamp());
				bundleRequest.setDigitalStampRestriction(Boolean.FALSE);
				bundleRequest.setType(BundleType.GLOBAL);
				bundleRequest.setTransferCategoryList(null);
				bundleRequest.setValidityDateFrom(LocalDate.parse(cdi.getValidityDateFrom(), dfDate));
				bundleRequest.setValidityDateTo(null);
				for (Detail d: cdi.getDetails()) {
					bundleRequest.setIdChannel(d.getIdChannel());
					bundleRequest.setIdBrokerPsp(d.getIdBrokerPsp());
					bundleRequest.setName(d.getName());
					bundleRequest.setDescription(d.getDescription());
					bundleRequest.setPaymentType(d.getPaymentMethod());
					// TODO calculate value
					bundleRequest.setOnUs(null);
					// TODO calculate value
					bundleRequest.setTouchpoint(null);
					for (ServiceAmount sa: d.getServiceAmount()) {
						bundleRequest.setPaymentAmount(sa.getPaymentAmount());
						bundleRequest.setMinPaymentAmount(sa.getMinPaymentAmount());
						bundleRequest.setMaxPaymentAmount(sa.getMaxPaymentAmount());
						bundleWrapper.getBundleRequests().add(bundleRequest);
					}
				}
			}
			
		}
		
		return handleRequest(bundleWrapper, context);
	}

}
