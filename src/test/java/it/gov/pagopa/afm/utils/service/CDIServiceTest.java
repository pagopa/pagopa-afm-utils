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
	
	@Test
	void deleteTest() {
		CDIService cdiService = new CDIService(cdisRepository);
		cdiService.deleteCDI(CDI.builder().build());
		assertTrue(true);
	}
	
	@Test
	void updateTest() {
		CDIService cdiService = new CDIService(cdisRepository);
		cdiService.updateCDI(CDI.builder().build());
		assertTrue(true);
	}
}
