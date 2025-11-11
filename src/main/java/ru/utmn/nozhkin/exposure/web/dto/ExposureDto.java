package ru.utmn.nozhkin.exposure.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ExposureDto(
        @NotBlank @Size(max = 128) String country,
        @Size(max = 64) String ghrp,
        @Size(max = 64) String incomeClassification,
        @Digits(integer = 6, fraction = 4) @PositiveOrZero BigDecimal economicExposureIndex
) {}
