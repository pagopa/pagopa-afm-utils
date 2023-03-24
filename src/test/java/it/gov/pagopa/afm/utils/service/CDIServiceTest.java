package it.gov.pagopa.afm.utils.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import it.gov.pagopa.afm.utils.Application;
import it.gov.pagopa.afm.utils.entity.CDI;
import it.gov.pagopa.afm.utils.model.bundle.BundleResponse;
import it.gov.pagopa.afm.utils.repository.CDICollectionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.List;


@SpringBootTest(classes = Application.class)
class CDIServiceTest {
  @MockBean
  private CDICollectionRepository cdisRepository;
  @MockBean private MarketPlaceClient marketPlaceClient;

  @Autowired
  @InjectMocks
  private CDIService cdiService;

  @Test
  void saveCdis() {
    List<CDI> cdis = List.of(CDI.builder().build());
    cdiService.saveCDIs(cdis);
    Mockito.verify(cdisRepository, times(1)).saveAll(cdis);
  }

  @Test
  void updateCdi() {
    CDI cdi = CDI.builder().build();
    cdiService.updateCDI(cdi);
    Mockito.verify(cdisRepository, times(1)).save(cdi);
  }

  @Test
  void deleteCdis() {
    CDI cdi = CDI.builder().build();
    cdiService.deleteCDI(cdi);
    Mockito.verify(cdisRepository, times(1)).delete(cdi);
  }

  @Test
  void syncCdis() {
    List<CDI> cdis = List.of(CDI.builder().build());
    when(cdisRepository.getWorkableCDIs()).thenReturn(cdis);
    List<BundleResponse> result = cdiService.syncCDI();
    Assertions.assertEquals(0, result.size());
  }
}
