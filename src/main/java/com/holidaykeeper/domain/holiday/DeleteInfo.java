package com.holidaykeeper.domain.holiday;

public record DeleteInfo(
        String countryCode,
        int year,
        int deletedCount
) {
}
