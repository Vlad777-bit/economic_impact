package ru.utmn.nozhkin.exposure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.utmn.nozhkin.exposure.domain.Exposure;
import ru.utmn.nozhkin.exposure.repo.ExposureRepository;
import ru.utmn.nozhkin.exposure.web.dto.ExposureDto;

import java.math.BigDecimal;

import static ru.utmn.nozhkin.exposure.repo.ExposureSpecifications.*;

@Service
@RequiredArgsConstructor
public class ExposureService {

    private final ExposureRepository repository;

    @Transactional(readOnly = true)
    public Page<Exposure> list(String country, String income,
                               BigDecimal econMin, BigDecimal econMax,
                               Pageable pageable) {
        Specification<Exposure> spec = Specification
                .where(countryContains(country))
                .and(incomeEquals(income))
                .and(econIndexBetween(econMin, econMax));

        return repository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Exposure get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exposure with id " + id + " not found")
                );
    }

    @Transactional
    public Exposure create(ExposureDto dto) {
        Exposure e = Exposure.builder()
                .country(dto.country())
                .ghrp(dto.ghrp())
                .incomeClassification(dto.incomeClassification())
                .economicExposureIndex(dto.economicExposureIndex())
                .build();
        return repository.save(e);
    }

    @Transactional
    public Exposure update(Long id, ExposureDto dto) {
        Exposure e = get(id);
        e.setCountry(dto.country());
        e.setGhrp(dto.ghrp());
        e.setIncomeClassification(dto.incomeClassification());
        e.setEconomicExposureIndex(dto.economicExposureIndex());
        return repository.save(e);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
