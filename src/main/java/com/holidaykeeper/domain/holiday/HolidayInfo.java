package com.holidaykeeper.domain.holiday;

import java.time.LocalDate;

public record HolidayInfo(
        LocalDate date,
        String localName,
        String name,
        String countryCode
) {
}
