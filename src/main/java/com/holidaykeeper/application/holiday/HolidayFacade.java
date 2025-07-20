package com.holidaykeeper.application.holiday;

import com.holidaykeeper.domain.country.CountryClient;
import com.holidaykeeper.domain.country.CountryCommand;
import com.holidaykeeper.domain.country.CountryInfo;
import com.holidaykeeper.domain.country.CountryService;
import com.holidaykeeper.domain.holiday.HolidayClient;
import com.holidaykeeper.domain.holiday.HolidayCommand;
import com.holidaykeeper.domain.holiday.HolidayInfo;
import com.holidaykeeper.domain.holiday.HolidayService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HolidayFacade {
    public static final int START_YEAR = 2020;
    public static final int LAST_YEAR = 2025;

    private final CountryClient countryClient;

    private final HolidayClient holidayClient;

    private final CountryService countryService;

    private final HolidayService holidayService;

    public List<HolidayResult> loadHolidays() {
        List<CountryInfo> countries = countryService.findAllCountries();
        if (countries.isEmpty()) {
            countries = countryClient.getCountries();
            countryService.createCountries(countries.stream()
                    .map(country -> new CountryCommand.Create(country.countryCode(), country.name()))
                    .toList());
        }
        return loadHolidaysOf(countries);
    }

    private List<HolidayResult> loadHolidaysOf(List<CountryInfo> countries) {
        List<HolidayResult> holidayResults = new ArrayList<>();
        List<HolidayInfo> totalHolidays = new ArrayList<>();
        for (CountryInfo country : countries) {
            int countryHolidayCount = 0;
            for (int year = START_YEAR; year <= LAST_YEAR; year++) {
                List<HolidayInfo> countryHolidays = holidayClient.findHolidays(country.countryCode(), year);
                countryHolidayCount += countryHolidays.size();
                totalHolidays.addAll(countryHolidays);
            }
            holidayResults.add(new HolidayResult(country.countryCode(), countryHolidayCount));
        }
        holidayService.saveHolidays(totalHolidays.stream()
                .map(holiday -> new HolidayCommand.Create(holiday.date(), holiday.localName(), holiday.name(),
                        holiday.countryCode()))
                .toList());
        return holidayResults;
    }
}
