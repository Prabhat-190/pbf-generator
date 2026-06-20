package com.pbf.pbf_generator.dto;

public class JourneyDto {

    private String txnRef;
    private String reversedTxnRef;
    private String businessDate;
    private String crn;
    private String txnType;
    private String beId;
    private String beName;
    private Double txnValue;

    public JourneyDto(
            String txnRef,
            String reversedTxnRef,
            String businessDate,
            String crn,
            String txnType,
            String beId,
            String beName,
            Double txnValue) {

        this.txnRef = txnRef;
        this.reversedTxnRef = reversedTxnRef;
        this.businessDate = businessDate;
        this.crn = crn;
        this.txnType = txnType;
        this.beId = beId;
        this.beName = beName;
        this.txnValue = txnValue;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public String getReversedTxnRef() {
        return reversedTxnRef;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public String getCrn() {
        return crn;
    }

    public String getTxnType() {
        return txnType;
    }

    public String getBeId() {
        return beId;
    }

    public String getBeName() {
        return beName;
    }

    public Double getTxnValue() {
        return txnValue;
    }
}