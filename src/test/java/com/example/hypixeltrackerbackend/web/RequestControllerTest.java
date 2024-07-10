package com.example.hypixeltrackerbackend.web;

import com.example.hypixeltrackerbackend.constant.TimeConstant;
import com.example.hypixeltrackerbackend.repository.ItemPricingRepository;
import com.example.hypixeltrackerbackend.services.DataProcessorService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
class RequestControllerTest {
    @Autowired
    ItemPricingRepository itemPricingRepository;
    @Autowired
    DataProcessorService dataProcessorService;
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new RequestController(dataProcessorService)).build();
    }

    @Test
    @DisplayName("GET /bazaar")
    void testGetItems() throws Exception {
        // wait that a request have been proceeded
        Awaitility.waitAtMost(TimeConstant.CALL_FREQUENCY_IN_SECOND, TimeUnit.SECONDS)
                .until(()-> dataProcessorService.getLastData()!=null);

        mockMvc.perform(get("/bazaar").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> assertThat(result.getResponse().getContentAsString())
                        .hasSizeGreaterThan(1500)
                        .contains("INK_SACK:3")
                        .startsWith("{")
                        .endsWith("}")

                );
    }
    @Test
    @DisplayName("GET /sql")
    void shouldSqlEndpointWorkProperly() throws Exception {
        // wait that a request have been proceeded
        Awaitility.waitAtMost(TimeConstant.CALL_FREQUENCY_IN_SECOND, TimeUnit.SECONDS)
                .until(()-> dataProcessorService.getLastData()!=null);

        mockMvc.perform(get("/sql").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
