package com.bloodbank.model;

import java.sql.Date;

public class Donation {
    private int donationId;
    private int donorId;
    private int units;
    private Date donationDate;

    public int getDonationId() { return donationId; }
    public void setDonationId(int donationId) { this.donationId = donationId; }
    public int getDonorId() { return donorId; }
    public void setDonorId(int donorId) { this.donorId = donorId; }
    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }
    public Date getDonationDate() { return donationDate; }
    public void setDonationDate(Date donationDate) { this.donationDate = donationDate; }
}
