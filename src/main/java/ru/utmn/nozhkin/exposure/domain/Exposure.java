package ru.utmn.nozhkin.exposure.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "exposures",
        indexes = {
                @Index(name = "idx_exposure_country", columnList = "country"),
                @Index(name = "idx_exposure_income", columnList = "income_classification"),
                @Index(name = "idx_exposure_econ", columnList = "economic_exposure_index")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exposure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128, nullable = false)
    private String country;

    @Column(name = "ghrp", length = 64)
    private String ghrp;

    @Column(name = "income_classification", length = 64)
    private String incomeClassification;

    @Column(name = "economic_exposure_index", precision = 10, scale = 4)
    private BigDecimal economicExposureIndex;
}
