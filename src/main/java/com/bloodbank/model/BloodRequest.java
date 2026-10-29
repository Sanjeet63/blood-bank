package com.bloodbank.model;

import java.sql.Date;

public class BloodRequest {
    private int requestId;
    private int hospitalId;
    private String patientName;
    private String bloodGroup;
    private int units;
    private String status;
    private Date requestDate;

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }
    public int getHospitalId() { return hospitalId; }
    public void setHospitalId(int hospitalId) { this.hospitalId = hospitalId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }
}
