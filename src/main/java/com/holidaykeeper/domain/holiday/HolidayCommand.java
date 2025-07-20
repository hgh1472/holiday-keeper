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

    public record Search(
            String countryCode,
            int year,
            int page,
            int size,
            HolidaySort holidaySort
    ) {
        public enum HolidaySort {
            DATE_ASC,
            DATE_DESC
        }
    }

    public record Find(
            String countryCode,
            int year
    ) {
    }
}
