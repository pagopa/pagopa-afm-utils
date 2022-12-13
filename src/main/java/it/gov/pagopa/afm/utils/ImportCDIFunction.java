package it.gov.pagopa.afm.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import feign.FeignException;
import it.gov.pagopa.afm.utils.model.bundle.BundleRequest;
import it.gov.pagopa.afm.utils.model.bundle.BundleResponse;
import it.gov.pagopa.afm.utils.model.bundle.BundleWrapper;
import it.gov.pagopa.afm.utils.service.MarketPlaceClient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ImportCDIFunction  implements Function<Mono<BundleWrapper>, Mono<List<BundleResponse>>>{

	@Autowired
    private MarketPlaceClient marketPlaceClient;

	
	@Override
	public Mono<List<BundleResponse>> apply(Mono<BundleWrapper> input) {	
		List<BundleResponse> bundleResponses = new ArrayList<>();
		return input.map(wrapper -> {			
            for (BundleRequest bundleRequest: wrapper.getBundleRequests()) {
            	bundleResponses.add(this.createBundle(wrapper.getIdPsp(), bundleRequest));
            }
            return bundleResponses;
        });
		
	}
	
	
	private BundleResponse createBundle(String idPsp, BundleRequest bundleRequest) {
		BundleResponse response = BundleResponse.builder().build();
		try {
			response = marketPlaceClient.createBundle(idPsp, bundleRequest);
        } catch (FeignException.BadRequest e) {
            log.error("Creation of the Bundle on Markeplace Bad Request Error [idPsp={}]", idPsp, e);
            //throw new AppException(AppError.BUNDLE_REQUEST_DATA_ERROR, "[idPsp= "+idPsp+"]");
        } catch (FeignException.Conflict e) {
            log.error("Creation of the Bundle on Markeplace Conflict Error [idPsp={}]", idPsp, e);
            //throw new AppException(AppError.BUNDLE_REQUEST_DATA_ERROR, "[idPsp= "+idPsp+"]");
        } catch (Exception e) {
            log.error("Creation of the Bundle on Markeplace Error [idPsp={}]", idPsp, e);
            //throw new AppException(AppError.INTERNAL_SERVER_ERROR);
        }
		 return response;
	}

	

}
