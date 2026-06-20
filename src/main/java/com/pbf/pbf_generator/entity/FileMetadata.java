package com.pbf.pbf_generator.entity;

import jakarta.persistence.*;

@Entity
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    private String filepath;

    private int noOfRecords;

    private String fileType;

    private String status;

    private boolean sftpSent;

    private String requestId;

    private String callbackUrl;

    private String responseStatus;

    private String responseMessage;

    public FileMetadata() {
    }

    public FileMetadata(
            String filename,
            String filepath,
            int noOfRecords,
            String fileType,
            String status,
            boolean sftpSent) {

        this.filename = filename;
        this.filepath = filepath;
        this.noOfRecords = noOfRecords;
        this.fileType = fileType;
        this.status = status;
        this.sftpSent = sftpSent;
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

    public String getFileType() {
        return fileType;
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

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSftpSent(boolean sftpSent) {
        this.sftpSent = sftpSent;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}