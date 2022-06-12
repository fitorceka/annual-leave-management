package com.lhind.annualleavemanagement.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;

public class DateUtils {
    public static ChronoLocalDate fetchDateAndTimeOfCurrentMachine() {
        return LocalDate.now();
    }

    public static LocalDateTime fetchDateOfCurrentMachine() {
        return LocalDate.now().atStartOfDay();
    }
}
