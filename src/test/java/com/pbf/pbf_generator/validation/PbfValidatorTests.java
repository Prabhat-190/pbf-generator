package com.pbf.pbf_generator.validation;

import com.pbf.pbf_generator.dto.JourneyDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PbfValidatorTests {

    @Test
    void acceptsValidPbfJourney() {
        JourneyDto dto =
                new JourneyDto(
                        "TXN001",
                        "",
                        "20260514",
                        "1009999505",
                        "NOL CARD JOURNEY",
                        "12345",
                        "NA",
                        10.00);

        assertDoesNotThrow(() -> PbfValidator.validate(List.of(dto)));
    }

    @Test
    void rejectsInvalidCrn() {
        JourneyDto dto =
                new JourneyDto(
                        "TXN001",
                        "",
                        "20260514",
                        "ABC",
                        "NOL CARD JOURNEY",
                        "12345",
                        "NA",
                        10.00);

        assertThrows(RuntimeException.class, () -> PbfValidator.validate(List.of(dto)));
    }
}
