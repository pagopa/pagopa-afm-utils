package it.gov.pagopa.afm.utils;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.microsoft.azure.functions.ExecutionContext;

import it.gov.pagopa.afm.utils.common.TestUtil;
import it.gov.pagopa.afm.utils.entity.CDI;
import it.gov.pagopa.afm.utils.model.bundle.BundleResponse;

@ExtendWith(MockitoExtension.class)
public class ImportCDIHandlerTest {

	@Spy
	ImportCDIHandler handler;
	
	@Mock
    ExecutionContext context; 
	
	
	@Test
	void execute() throws IOException {
		// precondition
		CDI cdi = TestUtil.readModelFromFile("cdi/cdi.json", CDI.class);
		List<CDI> items = new ArrayList<>();
		items.add(cdi);
		
		BundleResponse response = BundleResponse.builder().idBundle("12345").build();
		
		WireMockServer wireMockServer = new WireMockServer(8585);
        wireMockServer.start();
        
        configureFor(wireMockServer.port());
        stubFor(post(urlEqualTo("/psps/"+cdi.getIdPsp()+"/bundles"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(TestUtil.toJson(response))));
		
		
		//test execution
		List<BundleResponse> responses = handler.execute(items, context);
		
		assertEquals(1, responses.size());
		assertThat(responses.get(0).getIdBundle()).isEqualTo("12345");
		
		wireMockServer.stop();
	}
	
	@Test
	void executeMultipleTouchPoint() throws IOException {
		// precondition
		CDI cdi = TestUtil.readModelFromFile("cdi/cdi_wisp_io_checkout.json", CDI.class);
		List<CDI> items = new ArrayList<>();
		items.add(cdi);
		
		BundleResponse response = BundleResponse.builder().idBundle("12345").build();
		
		WireMockServer wireMockServer = new WireMockServer(8585);
        wireMockServer.start();
        
        configureFor(wireMockServer.port());
        stubFor(post(urlEqualTo("/psps/"+cdi.getIdPsp()+"/bundles"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(TestUtil.toJson(response))));
		
		
		//test execution
		List<BundleResponse> responses = handler.execute(items, context);
		
		assertEquals(3, responses.size());
		assertThat(responses.get(0).getIdBundle()).isEqualTo("12345");
		
		wireMockServer.stop();
	}
	
	@Test
	void executeMultipleServiceAmount() throws IOException {
		// precondition
		CDI cdi = TestUtil.readModelFromFile("cdi/cdi_service_amount.json", CDI.class);
		List<CDI> items = new ArrayList<>();
		items.add(cdi);
		
		BundleResponse response = BundleResponse.builder().idBundle("12345").build();
		
		WireMockServer wireMockServer = new WireMockServer(8585);
        wireMockServer.start();
        
        configureFor(wireMockServer.port());
        stubFor(post(urlEqualTo("/psps/"+cdi.getIdPsp()+"/bundles"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(TestUtil.toJson(response))));
		
		
		//test execution
		List<BundleResponse> responses = handler.execute(items, context);
		
		assertEquals(2, responses.size());
		assertThat(responses.get(0).getIdBundle()).isEqualTo("12345");
		
		wireMockServer.stop();
	}
}
