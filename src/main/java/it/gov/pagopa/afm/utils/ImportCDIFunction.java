package it.gov.pagopa.afm.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.azure.cosmos.implementation.apachecommons.lang.StringUtils;

import feign.FeignException;
import it.gov.pagopa.afm.utils.entity.CDI;
import it.gov.pagopa.afm.utils.entity.Detail;
import it.gov.pagopa.afm.utils.entity.ServiceAmount;
import it.gov.pagopa.afm.utils.entity.StatusType;
import it.gov.pagopa.afm.utils.exception.AppError;
import it.gov.pagopa.afm.utils.exception.AppException;
import it.gov.pagopa.afm.utils.model.bundle.BundleRequest;
import it.gov.pagopa.afm.utils.model.bundle.BundleResponse;
import it.gov.pagopa.afm.utils.model.bundle.BundleType;
import it.gov.pagopa.afm.utils.model.bundle.CDIWrapper;
import it.gov.pagopa.afm.utils.model.bundle.PaymentMethodType;
import it.gov.pagopa.afm.utils.model.bundle.TouchpointType;
import it.gov.pagopa.afm.utils.service.CDIService;
import it.gov.pagopa.afm.utils.service.MarketPlaceClient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ImportCDIFunction  implements Function<Mono<CDIWrapper>, Mono<List<BundleResponse>>>{

	@Autowired(required=false)
	private MarketPlaceClient marketPlaceClient;

	@Autowired(required=false)
	private CDIService cdiService;


	@Override
	public Mono<List<BundleResponse>> apply(Mono<CDIWrapper> input) {	
		
		List<BundleResponse> bundleResponses = new ArrayList<>();
		
		return input.map(wrapper -> {			
			
			// the status of the items is put on PROCESSING to identify that these records are not in a final state
			// *** @CosmosDBTrigger doesn't need of this step -> remove when change to this kind of function ***
			this.setCDIToProcessingStatus(wrapper.getCdiItems());
			
			for (CDI cdi: wrapper.getCdiItems()) {
				// processed only cdis not in FAILED status
				if (null != cdi.getCdiStatus() && !cdi.getCdiStatus().equals(StatusType.FAILED)) {
					String idPsp = cdi.getIdPsp();
					List<BundleRequest> bundleRequestList = this.createBundlesByCDI(cdi);
					try {
						// create marketplace bundle
						Optional.ofNullable(this.createBundleByList(idPsp, bundleRequestList)).ifPresent(bundleResponses::addAll);
						// success -> delete the CDI
						Optional.ofNullable(cdiService).ifPresent(result -> 
						{
							cdiService.deleteCDI(cdi);
							log.info(String.format("SUCCESS Bundles creation -> CDI deleted [idCdi=%s]", cdi.getIdCdi()));
						});
					}
					catch (AppException e) {
						log.error("Error during the creation of the MarketPlace Bundles [idPsp= "+idPsp+", idCdi="+cdi.getIdCdi()+"]", e);
						// error -> set CDI status to failed
						cdi.setCdiStatus(StatusType.FAILED);
						cdi.setCdiErrorDesc(e.getMessage());
						CDI updated = Optional.ofNullable(cdiService).map(result -> cdiService.updateCDI(cdi)).orElseGet(() -> CDI.builder().build());
						log.info(String.format("CDI status updated [idCdi=%s, status=%s, errorDesc=%s]", updated.getIdCdi(), updated.getCdiStatus(), updated.getCdiErrorDesc()));
					}

				}
			}
			return bundleResponses;
		});

	}

	public List<BundleRequest> createBundlesByCDI(CDI cdi) {
		List<BundleRequest> bundleRequestList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(cdi.getDetails())) {
			DateTimeFormatter  dfDate     = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			BundleRequest bundleRequest = new BundleRequest();
			bundleRequest.setIdCdi(cdi.getIdCdi());
			bundleRequest.setDigitalStamp(cdi.getDigitalStamp());
			bundleRequest.setDigitalStampRestriction(Boolean.FALSE);
			bundleRequest.setType(BundleType.GLOBAL);
			bundleRequest.setTransferCategoryList(null);
			bundleRequest.setValidityDateFrom(!StringUtils.isEmpty(cdi.getValidityDateFrom()) ? LocalDate.parse(cdi.getValidityDateFrom(), dfDate) : null);
			bundleRequest.setValidityDateTo(null);
			for (Detail d: cdi.getDetails()) {
				bundleRequest.setIdChannel(d.getIdChannel());
				bundleRequest.setIdBrokerPsp(d.getIdBrokerPsp());
				bundleRequest.setName(d.getName());
				bundleRequest.setDescription(d.getDescription());
				bundleRequest.setPaymentType(d.getPaymentMethod());
				for (ServiceAmount sa: d.getServiceAmount()) {
					bundleRequest.setPaymentAmount(sa.getPaymentAmount());
					bundleRequest.setMinPaymentAmount(sa.getMinPaymentAmount());
					bundleRequest.setMaxPaymentAmount(sa.getMaxPaymentAmount());
					this.addBundleByTouchpoint(d, bundleRequestList, bundleRequest);
				}
			}
		}
		return bundleRequestList;
	}

	private void addBundleByTouchpoint(Detail d, List<BundleRequest> bundleRequestList, BundleRequest bundleRequest) {

		boolean isNullTouchPoint = true;

		if (d.getPaymentMethod().equalsIgnoreCase(PaymentMethodType.PO.name())) {
			isNullTouchPoint = false;
			BundleRequest bundleRequestClone = SerializationUtils.clone(bundleRequest);
			bundleRequestClone.setTouchpoint(TouchpointType.PSP.name());
			bundleRequestList.add(bundleRequestClone);
		}
		if ((d.getPaymentMethod().equalsIgnoreCase(PaymentMethodType.CP.name()) && d.getChannelCardsCart() && d.getChannelApp().equals(Boolean.FALSE)) || 
				(d.getPaymentMethod().matches("(?i)"+PaymentMethodType.BBT+"|"+PaymentMethodType.BP+"|"+PaymentMethodType.MYBK+"|"+PaymentMethodType.AD) && d.getChannelApp().equals(Boolean.FALSE)) ||
				(!d.getPaymentMethod().equalsIgnoreCase(PaymentMethodType.PPAL.name()) && d.getChannelApp())) {
			isNullTouchPoint = false;
			BundleRequest bundleRequestClone = SerializationUtils.clone(bundleRequest);
			bundleRequestClone.setTouchpoint(TouchpointType.WISP.name());
			bundleRequestList.add(bundleRequestClone);
		}
		if ((d.getPaymentMethod().equalsIgnoreCase(PaymentMethodType.CP.name()) && d.getChannelCardsCart() && d.getChannelApp().equals(Boolean.FALSE)) ||
				(d.getPaymentMethod().equalsIgnoreCase(PaymentMethodType.PPAL.name()) && d.getChannelApp())) {
			isNullTouchPoint = false;
			BundleRequest bundleRequestClone = SerializationUtils.clone(bundleRequest);
			bundleRequestClone.setTouchpoint(TouchpointType.IO.name());
			bundleRequestList.add(bundleRequestClone);
		}
		if ((d.getPaymentMethod().equalsIgnoreCase(PaymentMethodType.CP.name()) && d.getChannelCardsCart() && d.getChannelApp().equals(Boolean.FALSE))) {
			isNullTouchPoint = false;
			BundleRequest bundleRequestClone = SerializationUtils.clone(bundleRequest);
			bundleRequestClone.setTouchpoint(TouchpointType.CHECKOUT.name());
			bundleRequestList.add(bundleRequestClone);
		}
		if (isNullTouchPoint) {
			// default bundle with null touchpoint value
			bundleRequestList.add(bundleRequest);
		}

	}


	private List<BundleResponse> createBundleByList(String idPsp, List<BundleRequest> bundleRequestList) throws AppException{
		List<BundleResponse> response = null;
		try {
			response = Optional.ofNullable(marketPlaceClient).map(result -> marketPlaceClient.createBundleByList(idPsp, bundleRequestList)).orElseGet(() -> null);
		} catch (FeignException.BadRequest e) {
			throw new AppException(AppError.BUNDLE_REQUEST_DATA_ERROR, e.getMessage());
		} catch (FeignException.NotFound e) {
			throw new AppException(AppError.BUNDLE_NOT_FOUND_ERROR, e.getMessage());
		} catch (FeignException.Conflict e) {
			throw new AppException(AppError.BUNDLE_CONFLICT_ERROR, e.getMessage());
		} catch (FeignException.InternalServerError e) {
			throw new AppException(AppError.INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (Exception e) {
			throw new AppException(AppError.UNKNOWN, e.getMessage());
		}
		return response;
	}
	
	// remove when @CosmosDBTrigger function will be used
	public void setCDIToProcessingStatus(List<CDI> items) {
		for (CDI cdi: items) {
			cdi.setCdiStatus(StatusType.PROCESSING);
			CDI updated = Optional.ofNullable(cdiService).map(result -> cdiService.updateCDI(cdi)).orElseGet(() -> CDI.builder().build());
			log.info(String.format("CDI status updated [idCdi=%s, status=%s]", updated.getIdCdi(), updated.getCdiStatus()));
		}
	}



}
