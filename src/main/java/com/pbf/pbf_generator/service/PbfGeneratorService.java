package com.pbf.pbf_generator.service;

import com.pbf.pbf_generator.dto.JourneyDto;
import com.pbf.pbf_generator.dto.response.FileStatusResponse;
import com.pbf.pbf_generator.entity.FileMetadata;
import com.pbf.pbf_generator.repository.FileMetadataRepository;
import com.pbf.pbf_generator.validation.PbfValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PbfGeneratorService {

    private static final Logger logger =
            LoggerFactory.getLogger(PbfGeneratorService.class);

    private static final AtomicInteger FILE_SEQUENCE = new AtomicInteger(1);

    private final FileMetadataRepository repository;

    public PbfGeneratorService(FileMetadataRepository repository) {
        this.repository = repository;
    }

    public FileStatusResponse generatePbf() throws IOException {
        try {
            logger.info("PBF generation started");

            List<JourneyDto> journeys = sampleJourneys();
            logger.debug("Loaded {} journey records for PBF generation", journeys.size());

            PbfValidator.validate(journeys);
            logger.info("Validation completed successfully for {} records", journeys.size());

            LocalDate now = LocalDate.now();
            LocalDateTime exportTime = LocalDateTime.now();
            String sequence = nextSequence();
            String filename =
                    "pbf_"
                            + exportTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                            + "_"
                            + sequence
                            + ".pbf";

            Path folderPath =
                    Paths.get(
                            "uploads",
                            String.valueOf(now.getYear()),
                            now.getMonth().toString().toLowerCase(Locale.ROOT));
            Files.createDirectories(folderPath);
            logger.debug("PBF output directory is {}", folderPath);

            Path pbfPath = folderPath.resolve(filename);
            writePbfFile(pbfPath, journeys, exportTime, sequence);
            logger.info("PBF file written to {}", pbfPath);

            FileMetadata metadata =
                    new FileMetadata(
                            filename,
                            pbfPath.toString(),
                            journeys.size(),
                            "PBF",
                            "IN_PROGRESS",
                            false);
            metadata.setRequestId(UUID.randomUUID().toString());
            metadata.setCallbackUrl("http://localhost:8080/callback");
            metadata.setResponseStatus("PENDING");
            metadata.setResponseMessage("Waiting for loyalty response");

            repository.save(metadata);
            logger.info(
                    "Metadata saved with id {} and requestId {}",
                    metadata.getId(),
                    metadata.getRequestId());

            Path zipPath = zipFile(pbfPath);
            logger.info("ZIP file created successfully at {}", zipPath);

            moveToSftp(zipPath);

            metadata.setStatus("GENERATED");
            metadata.setSftpSent(true);

            repository.save(metadata);
            logger.info(
                    "PBF generation completed for metadata id {} with status {}",
                    metadata.getId(),
                    metadata.getStatus());

            return toResponse(metadata);
        } catch (Exception e) {
            logger.error("PBF generation failed", e);
            throw new RuntimeException(e);
        }
    }

    public void moveToSftp(Path zipPath) throws IOException {
        Files.createDirectories(Paths.get("sftp"));

        Path sftpPath = Paths.get("sftp").resolve(zipPath.getFileName());
        logger.debug("Moving ZIP from {} to mock SFTP path {}", zipPath, sftpPath);

        Files.move(
                zipPath,
                sftpPath,
                StandardCopyOption.REPLACE_EXISTING);

        logger.info("ZIP moved to mock SFTP folder at {}", sftpPath);
    }

    public Path zipFile(Path pbfPath) throws IOException {
        Path zipPath = Paths.get(pbfPath.toString() + ".zip");

        try (InputStream fis = Files.newInputStream(pbfPath);
             OutputStream fos = Files.newOutputStream(zipPath);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            ZipEntry zipEntry = new ZipEntry(pbfPath.getFileName().toString());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;

            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }

        return zipPath;
    }

    public FileStatusResponse getStatus(Long id) {
        logger.debug("Fetching PBF metadata status for id {}", id);

        FileMetadata metadata =
                repository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("Record not found"));

        logger.info("Status fetched for id {} as {}", id, metadata.getStatus());
        return toResponse(metadata);
    }

    public String callback(Long id) {
        logger.info("Callback received for id {}", id);

        FileMetadata metadata =
                repository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("Record not found"));
        metadata.setStatus("VALIDATED");
        metadata.setResponseStatus("SUCCESS");
        metadata.setResponseMessage("Response received from loyalty system");

        repository.save(metadata);
        logger.info("Record {} marked as VALIDATED", id);
        logger.info("Callback processing completed for request {}", metadata.getRequestId());

        return "Callback received successfully";
    }

    private void writePbfFile(
            Path pbfPath,
            List<JourneyDto> journeys,
            LocalDateTime exportTime,
            String sequence)
            throws IOException {

        String header =
                "H;PBF;"
                        + exportTime.format(DateTimeFormatter.BASIC_ISO_DATE)
                        + ";"
                        + sequence
                        + ";"
                        + exportTime.format(DateTimeFormatter.ofPattern("HH.mm.ss"));

        try (BufferedWriter writer =
                     Files.newBufferedWriter(pbfPath, StandardCharsets.UTF_8)) {
            writer.write(header);
            writer.newLine();

            for (JourneyDto dto : journeys) {
                writer.write(toPbfRecord(dto));
                writer.newLine();
            }

            writer.write("T;" + String.format("%05d", journeys.size()));
        }
    }

    private List<JourneyDto> sampleJourneys() {
        return List.of(
                new JourneyDto(
                        "TXN001",
                        "",
                        "20260514",
                        "1009999505",
                        "NOL CARD JOURNEY",
                        "12345",
                        "NA",
                        10.00),
                new JourneyDto(
                        "TXN002",
                        "",
                        "20260514",
                        "1009999506",
                        "NOL CARD TOP UP",
                        "12345",
                        "NA",
                        20.00),
                new JourneyDto(
                        "TXN003",
                        "",
                        "20260514",
                        "1009999501",
                        "NOL CARD JOURNEY",
                        "12345",
                        "NA",
                        10.00),
                new JourneyDto(
                        "TXN004",
                        "",
                        "20260514",
                        "1009999502",
                        "NOL CARD TOP UP",
                        "12345",
                        "NA",
                        20.00),
                new JourneyDto(
                        "TXN005",
                        "",
                        "20260514",
                        "1009999503",
                        "NOL CARD PRODUCT SALE",
                        "12345",
                        "NA",
                        15.50),
                new JourneyDto(
                        "TXN006",
                        "",
                        "20260514",
                        "1009999504",
                        "NOL CARD REFUND",
                        "12345",
                        "NA",
                        5.00));
    }

    private String toPbfRecord(JourneyDto dto) {
        return "R;"
                + dto.getTxnRef() + ";"
                + nullToEmpty(dto.getReversedTxnRef()) + ";"
                + dto.getBusinessDate() + ";"
                + dto.getCrn() + ";"
                + dto.getTxnType() + ";"
                + dto.getBeId() + ";"
                + dto.getBeName() + ";"
                + String.format(Locale.ROOT, "%.2f", dto.getTxnValue());
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String nextSequence() {
        int sequence = FILE_SEQUENCE.getAndUpdate(value -> value == 9999 ? 1 : value + 1);
        return String.format("%04d", sequence);
    }

    private FileStatusResponse toResponse(FileMetadata metadata) {
        return new FileStatusResponse(
                metadata.getId(),
                metadata.getFilename(),
                metadata.getFilepath(),
                metadata.getNoOfRecords(),
                metadata.getStatus(),
                metadata.isSftpSent(),
                metadata.getRequestId(),
                metadata.getResponseStatus(),
                metadata.getResponseMessage());
    }
}
