package com.rankbook.crm.models;

public class LeadCreateRequest {

    private String name;
    private String email;
    private String phone;
    private String course;
    private String source;

    public LeadCreateRequest() {
    }

    public LeadCreateRequest(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public LeadCreateRequest(String name, String email, String phone, String course, String source) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.course = course;
        this.source = source;
    }

    /**
     * Factory helper to generate unique test lead data per execution run.
     * Generates a 10-digit number ending with "3210" to satisfy deterministic password assertions.
     */
    public static LeadCreateRequest createDefaultUnique(String firstName, String lastName) {
        long timestamp = System.currentTimeMillis();
        String fullName = firstName + " " + lastName;
        String middleDigits = String.valueOf(timestamp).substring(8); // 5 digits
        String uniquePhone = "9" + middleDigits + "3210";
        String uniqueEmail = firstName.toLowerCase() + "." + timestamp + "@test.com";

        return new LeadCreateRequest(fullName, uniqueEmail, uniquePhone, "Full Stack Web Development", "Website");
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "LeadCreateRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", course='" + course + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}