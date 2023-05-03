package it.gov.pagopa.afm.utils.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import it.gov.pagopa.afm.utils.service.CDIService;

@SpringBootTest
@AutoConfigureMockMvc
class SyncControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private CDIService cdiService;

  @Test
  void syncCDI() throws Exception {
    String url = "/cdis/sync";
    MvcResult result =
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    assertNotNull(result.getResponse().getContentAsString());
  }

  @Test
  void syncCDIDeletion() throws Exception {
    String url = "/cdis/sync";
    MvcResult result =
        mvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    assertNotNull(result.getResponse().getContentAsString());
  }
  
  @Test
  void syncDeleteBundlesByIdCDI() throws Exception {
    String url = "/psps/123456/cdis/7890";
    MvcResult result =
        mvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    assertNotNull(result.getResponse().getContentAsString());
  }
}
