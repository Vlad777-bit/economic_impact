package ru.utmn.nozhkin.exposure.repo;

import org.springframework.data.jpa.domain.Specification;
import ru.utmn.nozhkin.exposure.domain.Exposure;

import java.math.BigDecimal;

public final class ExposureSpecifications {
    private ExposureSpecifications() {}

    public static Specification<Exposure> countryContains(String q) {
        return (root, cq, cb) -> (q == null || q.isBlank())
                ? null
                : cb.like(cb.lower(root.get("country")), "%" + q.toLowerCase() + "%");
    }

    public static Specification<Exposure> incomeEquals(String income) {
        return (root, cq, cb) -> (income == null || income.isBlank())
                ? null
                : cb.equal(cb.lower(root.get("incomeClassification")), income.toLowerCase());
    }

    public static Specification<Exposure> econIndexBetween(BigDecimal min, BigDecimal max) {
        return (root, cq, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) return cb.between(root.get("economicExposureIndex"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("economicExposureIndex"), min);
            return cb.lessThanOrEqualTo(root.get("economicExposureIndex"), max);
        };
    }
}
