package it.gov.pagopa.afm.utils.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.afm.utils.Application;
import it.gov.pagopa.afm.utils.entity.Bundle;
import it.gov.pagopa.afm.utils.entity.CDI;
import it.gov.pagopa.afm.utils.model.bundle.BundleResponse;
import it.gov.pagopa.afm.utils.repository.BundleRepository;
import it.gov.pagopa.afm.utils.repository.CDICollectionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@SpringBootTest(classes = Application.class)
class CDIServiceTest {

  public static final String MOCK_IDCDI = "testcdi";

  @MockBean
  private CDICollectionRepository cdisRepository;

  @MockBean
  private BundleRepository bundleRepository;

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

  @Test
  void deleteCDIBySync_OK_main() {
    Bundle bundle = Bundle.builder().id(UUID.randomUUID().toString()).idCdi(MOCK_IDCDI).build();
    CDI cdi = CDI.builder().id(UUID.randomUUID().toString()).idCdi(MOCK_IDCDI).build();
    when(bundleRepository.findByIdCdiIsNotNull()).thenReturn(List.of(bundle));
    when(cdisRepository.findByIdCdi(MOCK_IDCDI)).thenReturn(List.of(cdi));
    cdiService.deleteCDIs();
    verify(bundleRepository, times(1)).deleteAll(anyCollection());
    verify(cdisRepository, times(1)).deleteAll(anyCollection());
  }

  @Test
  void deleteCDIBySync_OK_noBundleAndCDIDeleted() {
    CDI cdi = CDI.builder().id(UUID.randomUUID().toString()).idCdi(MOCK_IDCDI).build();
    when(bundleRepository.findByIdCdiIsNotNull()).thenReturn(List.of());
    when(cdisRepository.findByIdCdi(MOCK_IDCDI)).thenReturn(List.of(cdi));
    cdiService.deleteCDIs();
    verify(bundleRepository, times(1)).deleteAll(anyCollection());
    verify(cdisRepository, times(0)).deleteAll(anyCollection());
  }

  @Test
  void deleteCDIBySync_OK_noCDIDeleted() {
    Bundle bundle = Bundle.builder().id(UUID.randomUUID().toString()).idCdi(MOCK_IDCDI).build();
    when(bundleRepository.findByIdCdiIsNotNull()).thenReturn(List.of(bundle));
    when(cdisRepository.findByIdCdi(MOCK_IDCDI)).thenReturn(List.of());
    cdiService.deleteCDIs();
    verify(bundleRepository, times(1)).deleteAll(anyCollection());
    verify(cdisRepository, times(1)).deleteAll(List.of());
  }

  @Test
  void deleteCDIBySync_OK_throwOnDeleteAll() {
    Bundle bundle = Bundle.builder().id(UUID.randomUUID().toString()).idCdi(MOCK_IDCDI).build();
    when(bundleRepository.findByIdCdiIsNotNull()).thenReturn(List.of(bundle));
    when(cdisRepository.findByIdCdi(MOCK_IDCDI)).thenReturn(List.of());
    doThrow(IllegalArgumentException.class).when(cdisRepository).deleteAll(anyCollection()); // very rare case, added for the sake of test coverage
    cdiService.deleteCDIs();
    verify(bundleRepository, times(1)).deleteAll(anyCollection());
    verify(cdisRepository, times(1)).deleteAll(List.of());
  }
}
