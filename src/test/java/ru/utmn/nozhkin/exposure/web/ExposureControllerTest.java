package ru.utmn.nozhkin.exposure.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.utmn.nozhkin.exposure.domain.Exposure;
import ru.utmn.nozhkin.exposure.service.ExposureService;

import java.math.BigDecimal;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ExposureController.class)
class ExposureControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ExposureService service;

    @Test
    void getById_ok() throws Exception {
        Exposure e = Exposure.builder()
                .id(1L).country("Italy").ghrp("X")
                .incomeClassification("High income")
                .economicExposureIndex(new BigDecimal("12.34")).build();

        when(service.get(1L)).thenReturn(e);

        mvc.perform(get("/api/exposures/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.country").value("Italy"));
    }

    @Test
    void getById_notFound() throws Exception {
        when(service.get(999L))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "not found"));

        mvc.perform(get("/api/exposures/999"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", org.hamcrest.Matchers.containsString("application/problem+json")));
    }
}
