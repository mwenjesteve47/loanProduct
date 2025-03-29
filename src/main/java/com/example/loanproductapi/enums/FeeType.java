package com.example.loanproductapi.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FeeType {
    SERVICE_FEE("Service_Fee"),
    DAILY_FEE("Daily_Fee"),
    LATE_FEE("Late_Fee");

    private final String value;

    FeeType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FeeType fromValue(String value) {
        for (FeeType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid FeeType: " + value);
    }}
