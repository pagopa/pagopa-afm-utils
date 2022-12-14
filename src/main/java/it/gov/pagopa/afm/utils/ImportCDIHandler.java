package it.gov.pagopa.afm.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;
import org.springframework.util.CollectionUtils;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.CosmosDBTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;

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
			@CosmosDBTrigger(
					name = "CDIDatastoreTrigger",
					databaseName = "cdi",
					collectionName = "cdi-collection",
					leaseCollectionName = "cdi-collection-leases",
					createLeaseCollectionIfNotExists = true,
					connectionStringSetting = "COSMOS_CONN_STRING") 
			List<CDI> items,
			ExecutionContext context) {
		
		log.info("Processing the trigger.");
		
		BundleWrapper bundleWrapper = new BundleWrapper();
		
		for (CDI cdi: items) {
			log.info("Generate Package function executed at: " + LocalDateTime.now() + " for CDI with idPsp: " + cdi.getIdPsp());
			if (!CollectionUtils.isEmpty(cdi.getDetails())) {
				DateTimeFormatter  dfDate     = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				bundleWrapper.setIdPsp(cdi.getIdPsp());
				BundleRequest bundleRequest = new BundleRequest();
				bundleRequest.setIdCdi(cdi.getIdCdi());
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
					for (ServiceAmount sa: d.getServiceAmount()) {
						bundleRequest.setPaymentAmount(sa.getPaymentAmount());
						bundleRequest.setMinPaymentAmount(sa.getMinPaymentAmount());
						bundleRequest.setMaxPaymentAmount(sa.getMaxPaymentAmount());
						//bundleWrapper.getBundleRequests().add(bundleRequest);
						this.addBundleByTouchpoint(d, bundleWrapper, bundleRequest);
					}
				}
			}
			
		}
		
		return handleRequest(bundleWrapper, context);
	}
	
	private void addBundleByTouchpoint(Detail d, BundleWrapper bundleWrapper, BundleRequest bundleRequest) {
		
		boolean isNullTouchPoint = true;
		
		if (d.getPaymentMethod().equalsIgnoreCase("PO")) {
			isNullTouchPoint = false;
			BundleRequest bundleRequestClone = SerializationUtils.clone(bundleRequest);
			bundleRequestClone.setTouchpoint("PSP");
			bundleWrapper.getBundleRequests().add(bundleRequestClone);
		}
		if ((d.getPaymentMethod().equalsIgnoreCase("CP") && d.getChannelCardsCart() && d.getChannelApp().equals(Boolean.FALSE)) || 
				 (d.getPaymentMethod().matches("(?i)BBT|BP|MYBK|AD") && d.getChannelApp().equals(Boolean.FALSE)) ||
				 (!d.getPaymentMethod().equalsIgnoreCase("PPAL") && d.getChannelApp())) {
			isNullTouchPoint = false;
			BundleRequest bundleRequestClone = SerializationUtils.clone(bundleRequest);
			bundleRequestClone.setTouchpoint("WISP");
			bundleWrapper.getBundleRequests().add(bundleRequestClone);
		}
		if ((d.getPaymentMethod().equalsIgnoreCase("CP") && d.getChannelCardsCart() && d.getChannelApp().equals(Boolean.FALSE)) ||
				 (d.getPaymentMethod().equalsIgnoreCase("PPAL") && d.getChannelApp())) {
			isNullTouchPoint = false;
			BundleRequest bundleRequestClone = SerializationUtils.clone(bundleRequest);
			bundleRequestClone.setTouchpoint("IO");
			bundleWrapper.getBundleRequests().add(bundleRequestClone);
		}
		if ((d.getPaymentMethod().equalsIgnoreCase("CP") && d.getChannelCardsCart() && d.getChannelApp().equals(Boolean.FALSE))) {
			isNullTouchPoint = false;
			BundleRequest bundleRequestClone = SerializationUtils.clone(bundleRequest);
			bundleRequestClone.setTouchpoint("CHECKOUT");
			bundleWrapper.getBundleRequests().add(bundleRequestClone);
		}
		if (isNullTouchPoint) {
			// default bundle with null touchpoint value
			bundleWrapper.getBundleRequests().add(bundleRequest);
		}
		
	}

}
