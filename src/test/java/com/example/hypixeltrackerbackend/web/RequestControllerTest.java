package com.example.hypixeltrackerbackend.web;

import com.example.hypixeltrackerbackend.constant.TimeConstant;
import com.example.hypixeltrackerbackend.data.bazaar.ItemPricing;
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

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
class RequestControllerTest {
    @Autowired
    DataProcessorService dataProcessorService;
    @Autowired
    RequestController requestController;
    @Autowired
    ItemPricingRepository itemPricingRepository;
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(requestController).build();
    }

    @Test
    @DisplayName("GET /bazaar")
    void shouldGetBazaarCurrentValueWorkProperly() throws Exception {
        // wait that a request have been proceeded
        Awaitility.waitAtMost(TimeConstant.CALL_FREQUENCY_IN_SECOND, TimeUnit.SECONDS)
                .until(() -> dataProcessorService.getLastData() != null);

        mockMvc.perform(get("/bazaar").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> assertThat(result.getResponse().getContentAsString())
                        .hasSizeGreaterThan(1500)
                        .contains("INK_SACK:3")
                        .startsWith("[")
                        .endsWith("]")
                );
    }

    @Test
    @DisplayName("GET /bazaar/{id} with wrong ID")
    void shouldGetAnInvalidItemHistoryReturn404() throws Exception {
        itemPricingRepository.save(new ItemPricing("WHEAT",4d,4d, LocalDateTime.now()));


        mockMvc.perform(get("/bazaar/{id}","toto").accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isNotFound())
                .andDo(result -> assertThat(result.getResponse().getErrorMessage()).isEqualTo("Item not found : toto"));
    }


    @Test
    @DisplayName("GET /bazaar/{id}")
    void shouldGetAParticularItemHistoryWorkProperly() throws Exception {
        // wait that a request have been proceeded
        itemPricingRepository.save(new ItemPricing("WHEAT",4d,4d, LocalDateTime.now()));

        mockMvc.perform(get("/bazaar/{id}/{window}","WHEAT","day").accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(result -> assertThat(result.getResponse().getContentAsString())
                        .contains("sellPrice")
                        .contains("buyPrice")
                        .contains("timestamp")
                        .startsWith("[{")
                        .endsWith("}]")
                );
    }

    @Test
    @DisplayName("GET /compress")
    void shouldCompressEndpointWorkProperly() throws Exception {
        // wait that a request have been proceeded
        Awaitility.waitAtMost(TimeConstant.CALL_FREQUENCY_IN_SECOND, TimeUnit.SECONDS)
                .until(() -> dataProcessorService.getLastData() != null);

        mockMvc.perform(get("/bazaar/compress").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
