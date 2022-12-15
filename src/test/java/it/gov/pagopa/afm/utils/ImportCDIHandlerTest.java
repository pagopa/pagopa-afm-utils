package it.gov.pagopa.afm.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import com.microsoft.azure.functions.ExecutionContext;

import it.gov.pagopa.afm.utils.common.TestUtil;
import it.gov.pagopa.afm.utils.entity.CDI;
import it.gov.pagopa.afm.utils.model.bundle.BundleResponse;
import it.gov.pagopa.afm.utils.model.bundle.Wrapper;

@ExtendWith(MockitoExtension.class)
class ImportCDIHandlerTest {
	
	@Test
	void execute() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		// precondition
		CDI cdi = TestUtil.readModelFromFile("cdi/cdi.json", CDI.class);
		List<CDI> items = new ArrayList<>();
		items.add(cdi);
		
		
		//test execution
        FunctionInvoker<Wrapper, List<BundleResponse>> handler = new FunctionInvoker<>(ImportCDIFunction.class);
        Wrapper wrapper = new Wrapper();
        wrapper.setCdiItems(items);
        
        List<BundleResponse> responses = handler.handleRequest(wrapper, new ExecutionContext() {
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
		assertEquals(1, responses.size());
	}
	
	@Test
	void executeMultipleTouchPoint() throws IOException {
		// precondition
		CDI cdi = TestUtil.readModelFromFile("cdi/cdi_wisp_io_checkout.json", CDI.class);
		List<CDI> items = new ArrayList<>();
		items.add(cdi);
		
		//test execution
        FunctionInvoker<Wrapper, List<BundleResponse>> handler = new FunctionInvoker<>(ImportCDIFunction.class);
        Wrapper wrapper = new Wrapper();
        wrapper.setCdiItems(items);
        
        List<BundleResponse> responses = handler.handleRequest(wrapper, new ExecutionContext() {
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
		assertEquals(3, responses.size());
	}
	
	@Test
	void executeMultipleServiceAmount() throws IOException {
		// precondition
		CDI cdi = TestUtil.readModelFromFile("cdi/cdi_service_amount.json", CDI.class);
		List<CDI> items = new ArrayList<>();
		items.add(cdi);	
		
		//test execution
        FunctionInvoker<Wrapper, List<BundleResponse>> handler = new FunctionInvoker<>(ImportCDIFunction.class);
        Wrapper wrapper = new Wrapper();
        wrapper.setCdiItems(items);
        
        List<BundleResponse> responses = handler.handleRequest(wrapper, new ExecutionContext() {
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
		assertEquals(2, responses.size());
	}
}
