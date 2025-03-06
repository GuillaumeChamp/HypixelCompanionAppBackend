package com.example.hypixeltrackerbackend.data.mapper;


import com.example.hypixeltrackerbackend.data.bazaar.CompleteItem;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class StaticItemMapperTest {
    @Test
    void assertStaticDataMapper() throws IOException {
        Map<String, CompleteItem> items = StaticItemMapper.generate();
        assertThat(items).hasSizeLessThan(1349);
    }

}