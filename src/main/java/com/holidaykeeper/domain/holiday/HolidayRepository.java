package com.holidaykeeper.domain.holiday;

import com.holidaykeeper.domain.holiday.HolidayCommand.Search.HolidaySort;
import java.util.List;
import org.springframework.data.domain.Page;

public interface HolidayRepository {
    void saveAll(List<Holiday> holidays);

    Page<Holiday> findPage(String countryCode, int year, int page, int size, HolidaySort holidaySort);

    List<Holiday> findByCountryCodeAndYear(String countryCode, int year);

    int deleteHolidays(String countryCode, int year);
}
