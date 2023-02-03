package it.gov.pagopa.afm.utils.service;

import static org.mockito.Mockito.times;

import it.gov.pagopa.afm.utils.entity.CDI;
import it.gov.pagopa.afm.utils.repository.CDICollectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CDIServiceTest {
  @Mock private CDICollectionRepository cdisRepository;
  @Mock private MarketPlaceClient marketPlaceClient;

  @Test
  void deleteTest() {
    CDIService cdiService = new CDIService(cdisRepository, marketPlaceClient);
    CDI cdi = CDI.builder().build();
    cdiService.deleteCDI(cdi);
    Mockito.verify(cdisRepository, times(1)).delete(cdi);
  }

  @Test
  void updateTest() {
    CDIService cdiService = new CDIService(cdisRepository, marketPlaceClient);
    CDI cdi = CDI.builder().build();
    cdiService.updateCDI(cdi);
    Mockito.verify(cdisRepository, times(1)).save(cdi);
  }
}
