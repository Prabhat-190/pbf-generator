package com.pbf.pbf_generator.dto.response;

public class FileStatusResponse {

    private Long id;

    private String filename;

    private String filepath;

    private int noOfRecords;

    private String status;

    private boolean sftpSent;

    private String requestId;

    private String responseStatus;

    private String responseMessage;

    public FileStatusResponse(
            Long id,
            String filename,
            String filepath,
            int noOfRecords,
            String status,
            boolean sftpSent,
            String requestId,
            String responseStatus,
            String responseMessage) {

        this.id = id;
        this.filename = filename;
        this.filepath = filepath;
        this.noOfRecords = noOfRecords;
        this.status = status;
        this.sftpSent = sftpSent;
        this.requestId = requestId;
        this.responseStatus = responseStatus;
        this.responseMessage = responseMessage;
    }

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public int getNoOfRecords() {
        return noOfRecords;
    }

    public String getStatus() {
        return status;
    }

    public boolean isSftpSent() {
        return sftpSent;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}
