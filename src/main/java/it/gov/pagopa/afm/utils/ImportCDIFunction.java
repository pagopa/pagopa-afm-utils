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
import it.gov.pagopa.afm.utils.model.bundle.Wrapper;
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
public class ImportCDIFunction  implements Function<Mono<Wrapper>, Mono<List<BundleResponse>>>{

	@Autowired(required=false)
	private MarketPlaceClient marketPlaceClient;

	@Autowired(required=false)
	private CDIService cdiService;


	@Override
	public Mono<List<BundleResponse>> apply(Mono<Wrapper> input) {	
		List<BundleResponse> bundleResponses = new ArrayList<>();
		return input.map(wrapper -> {			

			for (CDI cdi: wrapper.getCdiItems()) {
				// processed only cdis not in FAILED status
				if (null != cdi.getCdiStatus() && !cdi.getCdiStatus().equals(StatusType.FAILED)) {
					String idPsp = cdi.getIdPsp();
					List<BundleRequest> bundleRequestList = this.createBundlesByCDI(cdi);
					// TODO attempting createBundle with list parameter
					for (BundleRequest bundleRequest: bundleRequestList) {
						try {
							bundleResponses.add(this.createBundle(idPsp, bundleRequest));
							// success -> delete the CDI
							Optional.ofNullable(cdiService).ifPresent(result -> cdiService.deleteCDI(cdi));
						}
						catch (AppException e) {
							// error -> set CDI status to failed
							cdi.setCdiStatus(StatusType.FAILED);
							cdi.setCdiErrorDesc(e.getMessage());
							CDI updated = Optional.ofNullable(cdiService).map(result -> cdiService.updateCDI(cdi)).orElseGet(() -> null);
							log.error(String.format("CDI status updated [%s, %s]", updated.getCdiStatus(), updated.getCdiErrorDesc()));
						}
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
			bundleRequest.setValidityDateFrom(LocalDate.parse(cdi.getValidityDateFrom(), dfDate));
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

		if (d.getPaymentMethod().equalsIgnoreCase("PO")) {
			isNullTouchPoint = false;
			BundleRequest bundleRequestClone = SerializationUtils.clone(bundleRequest);
			bundleRequestClone.setTouchpoint("PSP");
			bundleRequestList.add(bundleRequestClone);
		}
		if ((d.getPaymentMethod().equalsIgnoreCase("CP") && d.getChannelCardsCart() && d.getChannelApp().equals(Boolean.FALSE)) || 
				(d.getPaymentMethod().matches("(?i)BBT|BP|MYBK|AD") && d.getChannelApp().equals(Boolean.FALSE)) ||
				(!d.getPaymentMethod().equalsIgnoreCase("PPAL") && d.getChannelApp())) {
			isNullTouchPoint = false;
			BundleRequest bundleRequestClone = SerializationUtils.clone(bundleRequest);
			bundleRequestClone.setTouchpoint("WISP");
			bundleRequestList.add(bundleRequestClone);
		}
		if ((d.getPaymentMethod().equalsIgnoreCase("CP") && d.getChannelCardsCart() && d.getChannelApp().equals(Boolean.FALSE)) ||
				(d.getPaymentMethod().equalsIgnoreCase("PPAL") && d.getChannelApp())) {
			isNullTouchPoint = false;
			BundleRequest bundleRequestClone = SerializationUtils.clone(bundleRequest);
			bundleRequestClone.setTouchpoint("IO");
			bundleRequestList.add(bundleRequestClone);
		}
		if ((d.getPaymentMethod().equalsIgnoreCase("CP") && d.getChannelCardsCart() && d.getChannelApp().equals(Boolean.FALSE))) {
			isNullTouchPoint = false;
			BundleRequest bundleRequestClone = SerializationUtils.clone(bundleRequest);
			bundleRequestClone.setTouchpoint("CHECKOUT");
			bundleRequestList.add(bundleRequestClone);
		}
		if (isNullTouchPoint) {
			// default bundle with null touchpoint value
			bundleRequestList.add(bundleRequest);
		}

	}


	private BundleResponse createBundle(String idPsp, BundleRequest bundleRequest) throws AppException{
		final String detailErrorMsg = "[idPsp= "+idPsp+", idCdi= "+bundleRequest.getIdCdi()+", idBrokerPsp= "+bundleRequest.getIdBrokerPsp()+"]";
		BundleResponse response = null;
		try {
			response = Optional.ofNullable(marketPlaceClient).map(result -> marketPlaceClient.createBundle(idPsp, bundleRequest)).orElseGet(() -> null);
		} catch (FeignException.BadRequest e) {
			log.error("Creation of the Bundle on Markeplace Bad Request Error"+detailErrorMsg, e);
			throw new AppException(AppError.BUNDLE_REQUEST_DATA_ERROR, e.getMessage());
		} catch (FeignException.NotFound e) {
			log.error("Creation of the Bundle on Markeplace Not Found Error"+detailErrorMsg, e);
			throw new AppException(AppError.BUNDLE_NOT_FOUND_ERROR, e.getMessage());
		} catch (FeignException.Conflict e) {
			log.error("Creation of the Bundle on Markeplace Conflict Error"+detailErrorMsg, e);
			throw new AppException(AppError.BUNDLE_CONFLICT_ERROR, e.getMessage());
		} catch (FeignException.InternalServerError e) {
			log.error("Creation of the Bundle on Markeplace Internal Server Error"+detailErrorMsg, e);
			throw new AppException(AppError.INTERNAL_SERVER_ERROR, e.getMessage());
		} 
		catch (Exception e) {
			log.error("Creation of the Bundle on Markeplace Unexpected Error"+detailErrorMsg, e);
			throw new AppException(AppError.UNKNOWN, e.getMessage());
		}
		return response;
	}



}
