package ru.utmn.nozhkin.exposure.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "exposures")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
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
