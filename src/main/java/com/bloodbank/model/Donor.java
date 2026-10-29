package com.bloodbank.model;

import java.sql.Date;

public class Donor {
    private int donorId;
    private int userId;
    private String name;
    private int age;
    private String bloodGroup;
    private String phone;
    private String city;
    private Date lastDonation;

    public int getDonorId() { return donorId; }
    public void setDonorId(int donorId) { this.donorId = donorId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Date getLastDonation() { return lastDonation; }
    public void setLastDonation(Date lastDonation) { this.lastDonation = lastDonation; }
}
