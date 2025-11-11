package ru.utmn.nozhkin.exposure.repo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.utmn.nozhkin.exposure.domain.Exposure;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ExposureRepositoryTest {

    @Autowired
    ExposureRepository repo;

    @Test
    void saveAndFind() {
        Exposure e = Exposure.builder()
                .country("Italy")
                .ghrp("X")
                .incomeClassification("High income")
                .economicExposureIndex(new BigDecimal("12.34"))
                .build();
        Exposure saved = repo.save(e);
        assertThat(saved.getId()).isNotNull();

        var found = repo.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCountry()).isEqualTo("Italy");
    }
}
