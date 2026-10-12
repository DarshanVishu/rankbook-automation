package com.rankbook.framework.enums;

public enum LeadStatus {
    NEW("New"),
    CONTACTED("Contacted"),
    QUALIFIED("Qualified"),
    ENROLLED("Enrolled"),
    LOST("Lost");

    private final String value;

    LeadStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}