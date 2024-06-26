package it.gov.pagopa.afm.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.gov.pagopa.afm.utils.common.TestUtil;
import it.gov.pagopa.afm.utils.entity.CDI;
import it.gov.pagopa.afm.utils.model.bundle.BundleRequest;
import it.gov.pagopa.afm.utils.service.CDIService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImportCDIHandlerTest {

  @Spy CDIService cdiService;

  /*
  @Test
  void execute()
      throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
          IllegalAccessException {
    // precondition
    CDI cdi = TestUtil.readModelFromFile("cdi/cdi.json", CDI.class);
    List<CDI> items = new ArrayList<>();
    items.add(cdi);

    List<BundleRequest> requests = cdiService.createBundlesByCDI(cdi);

    // test execution
    FunctionInvoker<CDIWrapper, List<BundleResponse>> handler =
        new FunctionInvoker<>(ImportCDIFunction.class);
    CDIWrapper wrapper = new CDIWrapper();
    wrapper.setCdiItems(items);

    handler.handleRequest(
        wrapper,
        new ExecutionContext() {
          @Override
          public Logger getLogger() {
            return Logger.getLogger(ImportCDIHandlerTest.class.getName());
          }

          @Override
          public String getInvocationId() {
            return "id1";
          }

          @Override
          public String getFunctionName() {
            return "importCDIFunction";
          }
        });

    handler.close();
    assertEquals(1, requests.size());
  }

  @Test
  void executeMultipleTouchPoint() throws IOException {
    // precondition
    CDI cdi = TestUtil.readModelFromFile("cdi/cdi_wisp_io_checkout.json", CDI.class);
    List<CDI> items = new ArrayList<>();
    items.add(cdi);

    List<BundleRequest> requests = cdiService.createBundlesByCDI(cdi);

    // test execution
    FunctionInvoker<CDIWrapper, List<BundleResponse>> handler =
        new FunctionInvoker<>(ImportCDIFunction.class);
    CDIWrapper wrapper = new CDIWrapper();
    wrapper.setCdiItems(items);

    handler.handleRequest(
        wrapper,
        new ExecutionContext() {
          @Override
          public Logger getLogger() {
            return Logger.getLogger(ImportCDIHandlerTest.class.getName());
          }

          @Override
          public String getInvocationId() {
            return "id1";
          }

          @Override
          public String getFunctionName() {
            return "importCDIFunction";
          }
        });

    handler.close();
    assertEquals(3, requests.size());
  }

  @Test
  void executeMultipleServiceAmount() throws IOException {
    // precondition
    CDI cdi = TestUtil.readModelFromFile("cdi/cdi_service_amount.json", CDI.class);
    List<CDI> items = new ArrayList<>();
    items.add(cdi);

    List<BundleRequest> requests = cdiService.createBundlesByCDI(cdi);

    // test execution
    FunctionInvoker<CDIWrapper, List<BundleResponse>> handler =
        new FunctionInvoker<>(ImportCDIFunction.class);
    CDIWrapper wrapper = new CDIWrapper();
    wrapper.setCdiItems(items);

    handler.handleRequest(
        wrapper,
        new ExecutionContext() {
          @Override
          public Logger getLogger() {
            return Logger.getLogger(ImportCDIHandlerTest.class.getName());
          }

          @Override
          public String getInvocationId() {
            return "id1";
          }

          @Override
          public String getFunctionName() {
            return "importCDIFunction";
          }
        });

    handler.close();
    assertEquals(2, requests.size());
  }
   */

  @Test
  void executePaymentMethodPO()
      throws IOException,
          NoSuchFieldException,
          SecurityException,
          IllegalArgumentException,
          IllegalAccessException {
    // precondition
    CDI cdi = TestUtil.readModelFromFile("cdi/cdi_PO.json", CDI.class);
    List<CDI> items = new ArrayList<>();
    items.add(cdi);

    List<BundleRequest> requests = cdiService.createBundlesByCDI(cdi);

    assertEquals(1, requests.size());
    assertEquals("PSP", requests.get(0).getTouchpoint());
  }

  @Test
  void verifyPSPBusinessName()
      throws IOException,
      SecurityException,
      IllegalArgumentException {
    // precondition
    CDI cdi = TestUtil.readModelFromFile("cdi/cdi_ragione_sociale.json", CDI.class);

    List<BundleRequest> requests = cdiService.createBundlesByCDI(cdi);

    assertEquals(1, requests.size());
    assertEquals("PSP_RAGIONE_SOCIALE", requests.get(0).getPspBusinessName());
  }

  /*
  @Test
  void executeFailed()
      throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
          IllegalAccessException {
    // precondition
    CDI cdi = TestUtil.readModelFromFile("cdi/cdi_status_FAILED.json", CDI.class);
    List<CDI> items = new ArrayList<>();
    items.add(cdi);

    // test execution
    FunctionInvoker<CDIWrapper, List<BundleResponse>> handler =
        new FunctionInvoker<>(ImportCDIFunction.class);
    CDIWrapper wrapper = new CDIWrapper();
    wrapper.setCdiItems(items);

    List<BundleResponse> result =
        handler.handleRequest(
            wrapper,
            new ExecutionContext() {
              @Override
              public Logger getLogger() {
                return Logger.getLogger(ImportCDIHandlerTest.class.getName());
              }

              @Override
              public String getInvocationId() {
                return "id1";
              }

              @Override
              public String getFunctionName() {
                return "importCDIFunction";
              }
            });

    handler.close();
    assertEquals(0, result.size());
  }
   */
}
