package ru.utmn.nozhkin.exposure.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.utmn.nozhkin.exposure.domain.Exposure;
import ru.utmn.nozhkin.exposure.service.ExposureService;
import ru.utmn.nozhkin.exposure.web.dto.ExposureDto;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/exposures")
@RequiredArgsConstructor
public class ExposureController {

    private final ExposureService service;

    @GetMapping
    public Page<Exposure> list(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String income,
            @RequestParam(required = false) BigDecimal econMin,
            @RequestParam(required = false) BigDecimal econMax,
            Pageable pageable
    ) {
        return service.list(country, income, econMin, econMax, pageable);
    }

    @GetMapping("/{id}")
    public Exposure get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public Exposure create(@RequestBody @Valid ExposureDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public Exposure update(@PathVariable Long id, @RequestBody @Valid ExposureDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
