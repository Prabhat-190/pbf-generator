# PBF Generator

## Project Overview

This project simulates the ABT-to-NLP transaction settlement workflow using Spring Boot. It generates PBF transaction files, validates records, creates ZIP archives, stores processing metadata in H2 Database, performs mock SFTP transfers, and handles callback-based status updates.

The implementation follows the PBF file specifications described in the ABT-TIBCO Integration Document and demonstrates an end-to-end file processing workflow.

---

## Architecture Workflow

```text
Generate API
      ↓
Load Transaction Data
      ↓
Validation
      ↓
PBF Generation
      ↓
Metadata Persistence
      ↓
ZIP Creation
      ↓
Mock SFTP Transfer
      ↓
Status = GENERATED
      ↓
Callback Processing
      ↓
Status = VALIDATED
```

---

## Features Implemented

* PBF file generation using Header, Record, and Trailer structure
* Dynamic file naming to prevent overwriting
* Transaction validation layer
* ZIP file generation
* Year/month-based file storage
* Metadata persistence using H2 database
* Unique RequestId generation
* Mock SFTP file transfer
* Callback processing
* Status tracking API
* Health check API
* SLF4J logging
* Exception handling

---

## PBF File Structure

### Header

```text
H;PBF;YYYYMMDD;1234;HH.mm.ss
```

### Record

```text
R;txnRef;reversedTxnRef;businessDate;crn;txnType;beId;beName;txnValue
```

### Trailer

```text
T;00006
```

---

## API Endpoints

### Generate PBF

```http
GET /generate
```

Example:

```bash
curl http://localhost:8080/generate
```

---

### Check Status

```http
GET /status/{id}
```

Example:

```bash
curl http://localhost:8080/status/1
```

---

### Callback Processing

```http
POST /callback/{id}
```

Example:

```bash
curl -X POST http://localhost:8080/callback/1
```

---

### Health Check

```http
GET /health
```

Example:

```bash
curl http://localhost:8080/health
```

---

## Sample Status Response

### Before Callback

```json
{
  "id": 1,
  "filename": "pbf_20260620204157_0001.pbf",
  "filepath": "uploads/2026/june/pbf_20260620204157_0001.pbf",
  "noOfRecords": 6,
  "status": "GENERATED",
  "sftpSent": true,
  "requestId": "deb8b2ea-d67a-426c-adce-89ff23b24388",
  "responseStatus": "PENDING",
  "responseMessage": "Waiting for loyalty response"
}
```

### After Callback

```json
{
  "id": 1,
  "filename": "pbf_20260620204157_0001.pbf",
  "filepath": "uploads/2026/june/pbf_20260620204157_0001.pbf",
  "noOfRecords": 6,
  "status": "VALIDATED",
  "sftpSent": true,
  "requestId": "deb8b2ea-d67a-426c-adce-89ff23b24388",
  "responseStatus": "SUCCESS",
  "responseMessage": "Response received from loyalty system"
}
```

---

## Database Metadata

The FILE_METADATA table stores:

* ID
* Filename
* Filepath
* File Type
* Number Of Records
* RequestId
* Callback URL
* Status
* Response Status
* Response Message
* SFTP Sent Flag

Example:

```text
STATUS = VALIDATED
RESPONSE_STATUS = SUCCESS
SFTP_SENT = TRUE
```

---

## Logging

The application uses SLF4J with Spring Boot Logback.

Examples:

```java
logger.info("PBF generation started");
logger.info("Validation completed successfully");
logger.info("Metadata saved with requestId {}", requestId);
logger.info("ZIP file created successfully");
logger.info("ZIP moved to mock SFTP folder");
logger.info("Callback processing completed");
```

Log Levels:

* INFO – Normal application flow
* DEBUG – Technical details
* WARN – Validation warnings
* ERROR – Exceptions and failures

---

## Generated Output Locations

Generated PBF Files:

```text
uploads/<year>/<month>/
```

Example:

```text
uploads/2026/june/pbf_20260620204157_0001.pbf
```

Mock SFTP Files:

```text
sftp/
```

Example:

```text
sftp/pbf_20260620204157_0001.pbf.zip
```

---

## How To Run

Start the application:

```bash
./gradlew bootRun
```

Open H2 Console:

```text
http://localhost:8080/h2-console
```

Database Details:

```text
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password:
```

---

## Technologies Used

* Java 25
* Spring Boot 3
* Spring Data JPA
* H2 Database
* Gradle
* SLF4J / Logback

---

## Future Enhancements

* Replace hardcoded sample data with CSV/API input
* PostgreSQL integration
* Real SFTP integration
* Swagger/OpenAPI documentation
* Docker deployment
* Global exception handling
* File download API
* Checksum and reconciliation support
* LPBF/response file processing

---

## Author

Prabhat Kumar

Software Engineering Intern

Indian Institute of Technology Kharagpur (Mathematics and Computing)
