package com.holidaykeeper.domain.holiday;

import java.time.LocalDate;

public class HolidayCommand {
    public record Create(
            LocalDate date,
            String localName,
            String name,
            String countryCode
    ) {
    }
}
