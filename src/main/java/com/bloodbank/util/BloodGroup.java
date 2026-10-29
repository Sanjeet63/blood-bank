package com.bloodbank.util;

import java.util.List;
import java.util.Map;

/** Blood group list and compatibility lookup map (patient group -> donor groups it can receive from). */
public class BloodGroup {

    public static final List<String> ALL = List.of("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+");

    public static final int CRITICAL_LEVEL = 5;   // O- below this is flagged as critical

    private static final Map<String, List<String>> CAN_RECEIVE_FROM = Map.of(
        "O-",  List.of("O-"),
        "O+",  List.of("O-", "O+"),
        "A-",  List.of("O-", "A-"),
        "A+",  List.of("O-", "O+", "A-", "A+"),
        "B-",  List.of("O-", "B-"),
        "B+",  List.of("O-", "O+", "B-", "B+"),
        "AB-", List.of("O-", "A-", "B-", "AB-"),
        "AB+", ALL
    );

    public static boolean isValid(String group) {
        return group != null && ALL.contains(group);
    }

    public static List<String> compatibleDonors(String patientGroup) {
        return CAN_RECEIVE_FROM.getOrDefault(patientGroup, List.of());
    }
}
