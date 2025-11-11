package ru.utmn.nozhkin.exposure.bootstrap;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.utmn.nozhkin.exposure.domain.Exposure;
import ru.utmn.nozhkin.exposure.repo.ExposureRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ExposureCsvLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ExposureCsvLoader.class);

    private final ExposureRepository repository;

    @Value("${app.csv.resource-name}")
    private String csvResourceName = "exposure.csv";

    private static final int BATCH_SIZE = 1000;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        long existing = repository.count();
        if (existing > 0) {
            log.info("ExposureCsvLoader: найдены существующие записи ({} шт.) — импорт пропущен.", existing);
            return;
        }

        var resource = new ClassPathResource(csvResourceName);
        if (!resource.exists()) {
            log.warn("ExposureCsvLoader: ресурс '{}' не найден — импорт пропущен.", csvResourceName);
            return;
        }

        try (var reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), Charset.forName("cp1251")))
        ) {
            CSVFormat format = CSVFormat.DEFAULT
                    .builder()
                    .setDelimiter(';')
                    .setIgnoreSurroundingSpaces(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            try (CSVParser parser = new CSVParser(reader, format)) {
                Map<String, Integer> headerMap = parser.getHeaderMap();
                log.info("ExposureCsvLoader: обнаружены заголовки CSV: {}", headerMap.keySet());

                String countryKey = resolveHeader(headerMap,
                        "country", "Country", "Country Name", "country_name");

                String ghrpKey = resolveHeader(headerMap,
                        "GHRP", "ghrp");

                String incomeKey = resolveHeader(headerMap,
                        "Income classification",
                        "Income classification according to...",
                        "income_classification",
                        "Income Classification");

                // Длинное имя индекса экономической уязвимости — добавь/подкорректируй алиасы по факту
                String economicIndexKey = resolveHeader(headerMap,
                        "covid_19_Economic_exposure_index_Ex_aid_and_FDI_and_food_import",
                        "economic_exposure_index",
                        "Economic exposure index");

                if (countryKey == null) {
                    log.error("ExposureCsvLoader: не удалось распознать столбец 'country' по заголовкам {}. Импорт прекращён.", headerMap.keySet());
                    return;
                }

                List<Exposure> buffer = new ArrayList<>(BATCH_SIZE);
                long processed = 0;
                long saved = 0;
                long skipped = 0;

                for (CSVRecord r : parser) {
                    processed++;

                    String country = getOrNull(r, countryKey);
                    if (isBlank(country)) {
                        skipped++;
                        continue; // не пишем пустые строки
                    }

                    String ghrp = getOrNull(r, ghrpKey);
                    String income = getOrNull(r, incomeKey);
                    BigDecimal econIdx = toDecimal(getOrNull(r, economicIndexKey));

                    Exposure e = Exposure.builder()
                            .country(country)
                            .ghrp(ghrp)
                            .incomeClassification(income)
                            .economicExposureIndex(econIdx)
                            .build();

                    buffer.add(e);
                    if (buffer.size() == BATCH_SIZE) {
                        repository.saveAll(buffer);
                        saved += buffer.size();
                        buffer.clear();
                    }

                    if (processed % 100_000 == 0) {
                        log.info("ExposureCsvLoader: обработано {} строк...", processed);
                    }
                }

                if (!buffer.isEmpty()) {
                    repository.saveAll(buffer);
                    saved += buffer.size();
                }

                log.info("ExposureCsvLoader: импорт завершён. Обработано: {}, сохранено: {}, пропущено: {}.",
                        processed, saved, skipped);
            }
        }
    }

    /**
     * Ищет первый подходящий ключ столбца в карте заголовков без учета регистра.
     * Возвращает фактическое имя столбца из файла (для обращения CSVRecord#get()).
     */
    private static String resolveHeader(Map<String, Integer> headerMap, String... aliases) {
        if (headerMap == null || headerMap.isEmpty()) return null;

        // Сформируем отображение lower-case -> реальный ключ
        Map<String, String> lc2real = new HashMap<>();
        for (String existing : headerMap.keySet()) {
            lc2real.put(existing.toLowerCase(Locale.ROOT).trim(), existing);
        }

        for (String alias : aliases) {
            if (alias == null) continue;
            String key = lc2real.get(alias.toLowerCase(Locale.ROOT).trim());
            if (key != null) return key;
        }

        // Дополнительно пробуем " startsWith " для очень длинных/обрезанных заголовков
        for (String alias : aliases) {
            if (alias == null) continue;
            String lcAlias = alias.toLowerCase(Locale.ROOT).trim();
            for (String lcExisting : lc2real.keySet()) {
                if (lcExisting.startsWith(lcAlias)) {
                    return lc2real.get(lcExisting);
                }
            }
        }
        return null;
    }

    private static String getOrNull(CSVRecord r, String key) {
        if (key == null) return null;
        if (!r.isMapped(key)) return null;
        String s = r.get(key);
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static BigDecimal toDecimal(String s) {
        if (isBlank(s)) return null;
        // Заменяем запятую на точку для десятичных значений
        String normalized = s.replace(',', '.');
        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
