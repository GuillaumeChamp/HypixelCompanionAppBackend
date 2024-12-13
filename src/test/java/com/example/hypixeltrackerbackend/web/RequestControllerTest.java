package com.example.hypixeltrackerbackend.web;

import com.example.hypixeltrackerbackend.constant.TimeConstant;
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
                .until(() -> dataProcessorService.getLastData() != null);

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
    @DisplayName("GET /bazaar/{id} with wrong ID")
    void testGetSpecificItemWithWrongId() throws Exception {
        // wait that a request have been proceeded
        Awaitility.waitAtMost(TimeConstant.CALL_FREQUENCY_IN_SECOND, TimeUnit.SECONDS)
                .pollDelay(500,TimeUnit.MILLISECONDS)
                .until(() -> dataProcessorService.getLastData() != null);

        mockMvc.perform(get("/bazaar/{id}","toto").accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isNotFound())
                .andDo(result -> assertThat(result.getResponse().getErrorMessage()).isEqualTo("Item not found : toto"));
    }


    @Test
    @DisplayName("GET /bazaar/{id}")
    void testGetSpecificItem() throws Exception {
        // wait that a request have been proceeded
        Awaitility.waitAtMost(TimeConstant.CALL_FREQUENCY_IN_SECOND, TimeUnit.SECONDS)
                .pollDelay(500,TimeUnit.MILLISECONDS)
                .until(() -> dataProcessorService.getLastData() != null);

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
