package co.abcbank.service;
import co.abcbank.service.web.controller.AbcTradeServiceController;
import co.abcbank.service.web.service.TradeEnrichmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AbcTradeServiceController.class)
class AbcTradeServiceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TradeEnrichmentService enrichmentService;

    @Test
    void testEnrichTrades() throws Exception {
        String result = "enriched data";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "date,productId,currency,price\n20230101,1,USD,100.0".getBytes()
        );

        when(enrichmentService.enrichTrades(any(MockMultipartFile.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        var mvcResult = mockMvc.perform(multipart("/api/v1/enrich")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string(result));
    }
}