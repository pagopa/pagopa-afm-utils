package it.gov.pagopa.afm.utils;


import it.gov.pagopa.afm.utils.repository.CDICollectionRepository;
import it.gov.pagopa.afm.utils.service.CDIService;
import it.gov.pagopa.afm.utils.service.MarketPlaceClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImportCDIFunctionTest {

  @Mock private MarketPlaceClient marketPlaceClient;

  @Mock private CDICollectionRepository cdisRepository;

  @Spy private CDIService cdiService;

  /*
  @Test
  void applyTest() {
    // precondition
    cdiService.setCdisRepository(cdisRepository);
    cdiService.setMarketPlaceClient(marketPlaceClient);
    BundleResponse response = BundleResponse.builder().idBundle("12345").build();
    Mockito.when(marketPlaceClient.createBundleByList(eq("201"), any()))
        .thenReturn(Arrays.asList(response));
    EasyRandom generator = new EasyRandom();
    CDI cdi = generator.nextObject(CDI.class);
    cdi.setIdPsp("201");
    cdi.setValidityDateFrom("2022-12-15");
    cdi.setCdiStatus(StatusType.NEW);
    Mockito.when(cdisRepository.getWorkableCDIs()).thenReturn(Arrays.asList(new CDI[] {cdi}));

    CDIWrapper input = CDIWrapper.builder().cdiItems(Arrays.asList(new CDI[] {cdi})).build();
    Mono<List<BundleResponse>> responses =
        new ImportCDIFunction(cdiService).apply(Mono.just(input));

    assertTrue(responses.block().size() > 0);
    assertThat(responses.block().get(0).getIdBundle()).isEqualTo("12345");
  }

  @Test
  void applyTest_400() {
    // precondition
    cdiService.setCdisRepository(cdisRepository);
    cdiService.setMarketPlaceClient(marketPlaceClient);
    EasyRandom generator = new EasyRandom();
    CDI cdi = generator.nextObject(CDI.class);
    cdi.setIdPsp("400");
    cdi.setValidityDateFrom("2022-12-15");
    cdi.setCdiStatus(StatusType.NEW);
    Mockito.when(cdisRepository.getWorkableCDIs()).thenReturn(Arrays.asList(new CDI[] {cdi}));
    lenient()
        .when(marketPlaceClient.createBundleByList(eq("400"), any()))
        .thenThrow(FeignException.BadRequest.class);

    CDIWrapper input = CDIWrapper.builder().cdiItems(Arrays.asList(new CDI[] {cdi})).build();
    Mono<List<BundleResponse>> responses =
        new ImportCDIFunction(cdiService).apply(Mono.just(input));

    assertEquals(0, responses.block().size());
  }

  @Test
  void applyTest_409() {
    // precondition
    cdiService.setCdisRepository(cdisRepository);
    cdiService.setMarketPlaceClient(marketPlaceClient);
    EasyRandom generator = new EasyRandom();
    CDI cdi = generator.nextObject(CDI.class);
    cdi.setIdPsp("409");
    cdi.setValidityDateFrom("2022-12-15");
    cdi.setCdiStatus(StatusType.NEW);
    Mockito.when(cdisRepository.getWorkableCDIs()).thenReturn(Arrays.asList(new CDI[] {cdi}));
    lenient()
        .when(marketPlaceClient.createBundleByList(eq("409"), any()))
        .thenThrow(FeignException.Conflict.class);

    CDIWrapper input = CDIWrapper.builder().cdiItems(Arrays.asList(new CDI[] {cdi})).build();
    Mono<List<BundleResponse>> responses =
        new ImportCDIFunction(cdiService).apply(Mono.just(input));

    assertEquals(0, responses.block().size());
  }

  @Test
  void applyTest_404() {
    // precondition
    cdiService.setCdisRepository(cdisRepository);
    cdiService.setMarketPlaceClient(marketPlaceClient);
    EasyRandom generator = new EasyRandom();
    CDI cdi = generator.nextObject(CDI.class);
    cdi.setIdPsp("404");
    cdi.setValidityDateFrom("2022-12-15");
    cdi.setCdiStatus(StatusType.NEW);
    Mockito.when(cdisRepository.getWorkableCDIs()).thenReturn(Arrays.asList(new CDI[] {cdi}));
    lenient()
        .when(marketPlaceClient.createBundleByList(eq("404"), any()))
        .thenThrow(FeignException.NotFound.class);

    CDIWrapper input = CDIWrapper.builder().cdiItems(Arrays.asList(new CDI[] {cdi})).build();
    Mono<List<BundleResponse>> responses =
        new ImportCDIFunction(cdiService).apply(Mono.just(input));

    assertEquals(0, responses.block().size());
  }
   */
}
