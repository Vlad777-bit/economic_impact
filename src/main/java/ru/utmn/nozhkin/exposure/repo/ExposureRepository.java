package ru.utmn.nozhkin.exposure.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.utmn.nozhkin.exposure.domain.Exposure;

import java.util.List;

@Repository
public interface ExposureRepository extends JpaRepository<Exposure, Long> {

    List<Exposure> findByCountryContainingIgnoreCase(String country);

    List<Exposure> findByIncomeClassificationIgnoreCase(String incomeClassification);
}
