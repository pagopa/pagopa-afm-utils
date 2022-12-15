package it.gov.pagopa.afm.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Arrays;
import java.util.List;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import feign.FeignException;
import it.gov.pagopa.afm.utils.entity.CDI;
import it.gov.pagopa.afm.utils.model.bundle.BundleResponse;
import it.gov.pagopa.afm.utils.model.bundle.Wrapper;
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
		CDI cdi = generator.nextObject(CDI.class);
		cdi.setIdPsp("201");
		cdi.setValidityDateFrom("2022-12-15");
		
		Wrapper input = Wrapper.builder().cdiItems(Arrays.asList(new CDI[] {cdi})).build();
		Mono<List<BundleResponse>> responses = new ImportCDIFunction(marketPlaceClient).apply(Mono.just(input));
		
		assertTrue(responses.block().size() > 0);
		assertThat(responses.block().get(0).getIdBundle()).isEqualTo("12345");
	}
	
	@Test
	void applyTest_400(){
		// precondition
		Mockito.when(marketPlaceClient.createBundle(eq("400"), any())).thenThrow(FeignException.BadRequest.class);
		

		EasyRandom generator = new EasyRandom();
		CDI cdi = generator.nextObject(CDI.class);
		cdi.setIdPsp("400");
		cdi.setValidityDateFrom("2022-12-15");
		
		Wrapper input = Wrapper.builder().cdiItems(Arrays.asList(new CDI[] {cdi})).build();
		Mono<List<BundleResponse>> responses = new ImportCDIFunction(marketPlaceClient).apply(Mono.just(input));
		
		assertTrue(responses.block().size() > 0);
		assertNull(responses.block().get(0).getIdBundle());
	}
	
	@Test
	void applyTest_409(){
		// precondition
		Mockito.when(marketPlaceClient.createBundle(eq("409"), any())).thenThrow(FeignException.Conflict.class);
		
		EasyRandom generator = new EasyRandom();
		CDI cdi = generator.nextObject(CDI.class);
		cdi.setIdPsp("409");
		cdi.setValidityDateFrom("2022-12-15");
		
		Wrapper input = Wrapper.builder().cdiItems(Arrays.asList(new CDI[] {cdi})).build();
		Mono<List<BundleResponse>> responses = new ImportCDIFunction(marketPlaceClient).apply(Mono.just(input));
		
		assertTrue(responses.block().size() > 0);
		assertNull(responses.block().get(0).getIdBundle());
	}
}
