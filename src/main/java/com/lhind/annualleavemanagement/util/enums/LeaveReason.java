package com.lhind.annualleavemanagement.util.enums;

public enum LeaveReason {
    ANNUAL_LEAVE("Annual Leave"), //
    SICK_LEAVE("Sick Leave"), //
    PERSONAL_LEAVE("Personal Leave");

    private final String value;

    LeaveReason(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LeaveReason fromValue(String leaveReason) {
        for (LeaveReason l : values()) {
            if (l.value.equals(leaveReason)) {
                return l;
            }
        }

        return null;
    }
}
