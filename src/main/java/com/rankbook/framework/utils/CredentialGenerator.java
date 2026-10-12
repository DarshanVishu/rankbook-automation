package com.rankbook.framework.utils;

public class CredentialGenerator {

    /**
     * LMS Deterministic Password Rule:
     * Format: [FirstName]@[Last 4 digits of phone number]
     */
    public static String generateStudentPassword(String firstName, String phoneNumber) {
        if (firstName == null || firstName.isBlank() || phoneNumber == null) {
            throw new IllegalArgumentException("First name and phone cannot be null or empty");
        }

        String digitsOnly = phoneNumber.replaceAll("[^0-9]", "");
        if (digitsOnly.length() < 4) {
            throw new IllegalArgumentException("Phone number must have at least 4 digits");
        }

        String lastFourDigits = digitsOnly.substring(digitsOnly.length() - 4);
        String formattedFirstName = firstName.trim().substring(0, 1).toUpperCase() 
                                  + firstName.trim().substring(1).toLowerCase();

        return formattedFirstName + "@" + lastFourDigits;
    }
}