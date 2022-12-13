package it.gov.pagopa.afm.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.ArrayList;
import java.util.List;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import feign.FeignException;
import it.gov.pagopa.afm.utils.model.bundle.BundleRequest;
import it.gov.pagopa.afm.utils.model.bundle.BundleResponse;
import it.gov.pagopa.afm.utils.model.bundle.BundleWrapper;
import it.gov.pagopa.afm.utils.service.MarketPlaceClient;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ImportCDIFunctionTest {
	
	@Mock
	private MarketPlaceClient marketPlaceClient;
	
	@Test
	void applyTest(){
		// precondition
		BundleResponse response = BundleResponse.builder().idBundle("12345").build();
		Mockito.when(marketPlaceClient.createBundle(eq("201"), any())).thenReturn(response);
		
		EasyRandom generator = new EasyRandom();
		BundleRequest request = generator.nextObject(BundleRequest.class);
		request.setTransferCategoryList(null);
		
		List<BundleRequest> requests = new ArrayList<>();
		requests.add(request);
		
		BundleWrapper input = BundleWrapper.builder().idPsp("201").bundleRequests(requests).build();
		Mono<List<BundleResponse>> responses = new ImportCDIFunction(marketPlaceClient).apply(Mono.just(input));
		
		assertEquals(1, responses.block().size());
		assertThat(responses.block().get(0).getIdBundle()).isEqualTo("12345");
	}
	
	@Test
	void applyTest_400(){
		// precondition
		Mockito.when(marketPlaceClient.createBundle(eq("400"), any())).thenThrow(FeignException.BadRequest.class);
		
		EasyRandom generator = new EasyRandom();
		BundleRequest request = generator.nextObject(BundleRequest.class);
		
		List<BundleRequest> requests = new ArrayList<>();
		requests.add(request);
		
		BundleWrapper input = BundleWrapper.builder().idPsp("400").bundleRequests(requests).build();
		Mono<List<BundleResponse>> responses = new ImportCDIFunction(marketPlaceClient).apply(Mono.just(input));
		
		assertEquals(1, responses.block().size());
		assertNull(responses.block().get(0).getIdBundle());
	}
	
	@Test
	void applyTest_409(){
		// precondition
		Mockito.when(marketPlaceClient.createBundle(eq("409"), any())).thenThrow(FeignException.Conflict.class);
		
		EasyRandom generator = new EasyRandom();
		BundleRequest request = generator.nextObject(BundleRequest.class);
		
		List<BundleRequest> requests = new ArrayList<>();
		requests.add(request);
		
		BundleWrapper input = BundleWrapper.builder().idPsp("409").bundleRequests(requests).build();
		Mono<List<BundleResponse>> responses = new ImportCDIFunction(marketPlaceClient).apply(Mono.just(input));
		
		assertEquals(1, responses.block().size());
		assertNull(responses.block().get(0).getIdBundle());
	}
}
