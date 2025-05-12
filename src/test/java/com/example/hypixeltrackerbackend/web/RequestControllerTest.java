package com.example.hypixeltrackerbackend.web;

import com.example.hypixeltrackerbackend.data.constant.TimeConstant;
import com.example.hypixeltrackerbackend.data.bazaar.ItemPricing;
import com.example.hypixeltrackerbackend.data.repositories.ItemPricingRepository;
import com.example.hypixeltrackerbackend.services.BazaarDataProcessorService;
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
    BazaarDataProcessorService bazaarDataProcessorService;
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
                .until(() -> bazaarDataProcessorService.getLastData() != null);

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
    void shouldGetAWrongIDHistoryReturn404() throws Exception {
        String testString = "VALID_ID";
        itemPricingRepository.save(new ItemPricing(testString, 4d, 4d, LocalDateTime.now()));

        mockMvc.perform(get("/bazaar/{id}", "TOTO").accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isNotFound())
                .andDo(result -> assertThat(result.getResponse().getErrorMessage()).isEqualTo("Item not found : TOTO"));
    }

    @Test
    @DisplayName("GET /bazaar/{id} bad request")
    void shouldGetAnInvalidItemIDHistoryReturn400() throws Exception {
        String testString = "invalid ID";
        itemPricingRepository.save(new ItemPricing(testString, 4d, 4d, LocalDateTime.now()));

        mockMvc.perform(get("/bazaar/{id}", "invalid ID").accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest())
                .andDo(result -> assertThat(result.getResponse().getErrorMessage()).isEqualTo("Invalid item ID"));
    }

    @Test
    @DisplayName("GET /bazaar/{id}")
    void shouldGetAParticularItemHistoryWorkProperly() throws Exception {
        String testString = "VALID_ID";
        itemPricingRepository.save(new ItemPricing(testString, 4d, 4d, LocalDateTime.now()));

        mockMvc.perform(get("/bazaar/{id}/{window}", testString, TimeConstant.DAY_TIME_WINDOW).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
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
    void shouldCompressEndpointAnswerFirst() throws Exception {
        // wait that a request have been proceeded
        Awaitility.waitAtMost(TimeConstant.CALL_FREQUENCY_IN_SECOND, TimeUnit.SECONDS)
                .until(() -> bazaarDataProcessorService.getLastData() != null);

        mockMvc.perform(get("/bazaar/compress").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /museum")
    void shouldGetMuseumItemsEndpointWorkProperly() throws Exception {
        mockMvc.perform(get("/museum").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
