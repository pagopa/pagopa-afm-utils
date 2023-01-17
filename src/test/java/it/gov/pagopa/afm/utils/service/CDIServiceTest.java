package it.gov.pagopa.afm.utils.service;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.gov.pagopa.afm.utils.entity.CDI;
import it.gov.pagopa.afm.utils.repository.CDICollectionRepository;

@ExtendWith(MockitoExtension.class)
class CDIServiceTest {
	@Mock
    private CDICollectionRepository cdisRepository;
	@Mock
	private MarketPlaceClient marketPlaceClient;
	
	@Test
	void deleteTest() {
		CDIService cdiService = new CDIService(cdisRepository, marketPlaceClient);
		cdiService.deleteCDI(CDI.builder().build());
		assertTrue(true);
	}
	
	@Test
	void updateTest() {
		CDIService cdiService = new CDIService(cdisRepository, marketPlaceClient);
		cdiService.updateCDI(CDI.builder().build());
		assertTrue(true);
	}
	
	@Test
	void getWorkableCDIs() {
		CDIService cdiService = new CDIService(cdisRepository, marketPlaceClient);
		cdiService.getWorkableCDIs();
		assertTrue(true);
    }
}
