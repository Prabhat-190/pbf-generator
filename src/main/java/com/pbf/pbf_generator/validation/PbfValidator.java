package com.pbf.pbf_generator.validation;

import com.pbf.pbf_generator.dto.JourneyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class PbfValidator {

    private static final Logger logger =
            LoggerFactory.getLogger(PbfValidator.class);

    private static final Set<String> ALLOWED_TXN_TYPES = Set.of(
            "NOL CARD PRODUCT SALE",
            "NOL CARD PRODUCT RENEWAL",
            "NOL CARD TOP UP",
            "NOL CARD JOURNEY",
            "NOL CARD REVERSAL",
            "NOL CARD REFUND",
            "NOL Card MICROPAYMENT");

    public static void validate(List<JourneyDto> journeys) {

        if (journeys == null || journeys.isEmpty()) {
            logger.warn("PBF validation failed because no records were provided");
            throw new RuntimeException("No records found");
        }

        logger.debug("Starting PBF validation for {} records", journeys.size());

        for (JourneyDto dto : journeys) {

            if (dto.getTxnRef() == null ||
               dto.getTxnRef().isEmpty()) {

                logger.warn("PBF validation failed: TxnRef missing");
                throw new RuntimeException(
                        "TxnRef missing");
            }

            if (dto.getCrn() == null ||
               dto.getCrn().isEmpty()) {

                logger.warn("PBF validation failed for txnRef {}: CRN missing", dto.getTxnRef());
                throw new RuntimeException(
                        "CRN missing");
            }

            if (!dto.getCrn().matches("\\d{10,16}")) {
                logger.warn(
                        "PBF validation failed for txnRef {}: invalid CRN {}",
                        dto.getTxnRef(),
                        dto.getCrn());
                throw new RuntimeException(
                        "CRN must be 10 to 16 digits");
            }

            if (dto.getBusinessDate() == null ||
               !dto.getBusinessDate().matches("\\d{8}")) {
                logger.warn(
                        "PBF validation failed for txnRef {}: invalid business date {}",
                        dto.getTxnRef(),
                        dto.getBusinessDate());
                throw new RuntimeException(
                        "Business date must be YYYYMMDD");
            }

            if (!ALLOWED_TXN_TYPES.contains(dto.getTxnType())) {
                logger.warn(
                        "PBF validation failed for txnRef {}: invalid transaction type {}",
                        dto.getTxnRef(),
                        dto.getTxnType());
                throw new RuntimeException(
                        "Invalid transaction type");
            }

            if (dto.getBeId() == null ||
               !dto.getBeId().matches("\\d{5}")) {
                logger.warn(
                        "PBF validation failed for txnRef {}: invalid BE ID {}",
                        dto.getTxnRef(),
                        dto.getBeId());
                throw new RuntimeException(
                        "BE ID must be 5 digits");
            }

            if (dto.getTxnValue() == null ||
               dto.getTxnValue() <= 0) {

                logger.warn(
                        "PBF validation failed for txnRef {}: invalid amount {}",
                        dto.getTxnRef(),
                        dto.getTxnValue());
                throw new RuntimeException(
                        "Invalid amount");
            }
        }

        logger.debug("PBF validation completed successfully");
    }
}
