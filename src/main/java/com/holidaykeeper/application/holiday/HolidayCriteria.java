package com.holidaykeeper.application.holiday;

import com.holidaykeeper.domain.holiday.HolidayCommand;

public class HolidayCriteria {
    public record Upsert(
            String countryCode,
            int year
    ) {
        public HolidayCommand.Find toFindCommand() {
            return new HolidayCommand.Find(
                    countryCode,
                    year
            );
        }
    }
}
