package com.bloodbank.util;

import com.bloodbank.model.Donor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Server-side validation and donor eligibility rules. */
public class Validator {

    public static final int MIN_GAP_DAYS = 90;

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static int toInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return -1;
        }
    }

    /** Returns an error message, or null if the donor data is valid. */
    public static String validateDonor(String name, int age, String group, String phone) {
        if (isBlank(name)) return "Name is required.";
        if (age < 18 || age > 65) return "Age must be between 18 and 65.";
        if (!BloodGroup.isValid(group)) return "Invalid blood group.";
        if (phone == null || !phone.matches("\\d{10}")) return "Phone must be 10 digits.";
        return null;
    }

    /** Returns a reason the donor cannot donate today, or null if eligible. */
    public static String checkEligibility(Donor d) {
        if (d.getAge() < 18 || d.getAge() > 65) return "Donor age must be between 18 and 65.";
        if (d.getLastDonation() != null) {
            long days = ChronoUnit.DAYS.between(d.getLastDonation().toLocalDate(), LocalDate.now());
            if (days < MIN_GAP_DAYS) {
                return "Donor last donated " + days + " days ago. Minimum gap is " + MIN_GAP_DAYS + " days.";
            }
        }
        return null;
    }
}
