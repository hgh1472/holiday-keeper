package com.holidaykeeper.application.holiday;

import com.holidaykeeper.domain.country.CountryClient;
import com.holidaykeeper.domain.country.CountryCommand;
import com.holidaykeeper.domain.country.CountryInfo;
import com.holidaykeeper.domain.country.CountryService;
import com.holidaykeeper.domain.holiday.HolidayClient;
import com.holidaykeeper.domain.holiday.HolidayCommand;
import com.holidaykeeper.domain.holiday.HolidayInfo;
import com.holidaykeeper.domain.holiday.HolidayService;
import com.holidaykeeper.support.error.CoreException;
import com.holidaykeeper.support.error.ErrorType;
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

    public List<LoadResult> loadHolidays() {
        List<CountryInfo> countries = countryService.findAllCountries();
        if (countries.isEmpty()) {
            countries = countryClient.getCountries();
            countryService.createCountries(countries.stream()
                    .map(country -> new CountryCommand.Create(country.countryCode(), country.name()))
                    .toList());
        }
        return loadHolidaysOf(countries);
    }

    public List<HolidayInfo> upsert(HolidayCriteria.Upsert criteria) {
        List<HolidayInfo> findHolidays = holidayClient.findHolidays(criteria.countryCode(),criteria.year());
        if (findHolidays.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 국가 코드이거나, 해당 연도의 공휴일이 없습니다.");
        }
        List<HolidayInfo> existHolidays = holidayService.findHolidays(criteria.toFindCommand());

        List<HolidayCommand.Create> commands = findHolidays.stream()
                .filter(findHoliday -> !existHolidays.contains(findHoliday))
                .map(nonExist ->
                        new HolidayCommand.Create(nonExist.date(), nonExist.localName(), nonExist.name(), nonExist.countryCode()))
                .toList();
        holidayService.saveHolidays(commands);

        return commands.stream()
                .map(command -> new HolidayInfo(command.date(), command.localName(), command.name(), command.countryCode()))
                .toList();
    }

    private List<LoadResult> loadHolidaysOf(List<CountryInfo> countries) {
        List<LoadResult> loadResults = new ArrayList<>();
        List<HolidayInfo> totalHolidays = new ArrayList<>();
        for (CountryInfo country : countries) {
            int countryHolidayCount = 0;
            for (int year = START_YEAR; year <= LAST_YEAR; year++) {
                List<HolidayInfo> countryHolidays = holidayClient.findHolidays(country.countryCode(), year);
                countryHolidayCount += countryHolidays.size();
                totalHolidays.addAll(countryHolidays);
            }
            loadResults.add(new LoadResult(country.countryCode(), countryHolidayCount));
        }
        holidayService.saveHolidays(totalHolidays.stream()
                .map(holiday -> new HolidayCommand.Create(holiday.date(), holiday.localName(), holiday.name(),
                        holiday.countryCode()))
                .toList());
        return loadResults;
    }
}
