package com.holidaykeeper.domain.holiday;

import java.util.List;

public interface HolidayClient {
    List<HolidayInfo> findHolidays(String countryCode, int year);
}
