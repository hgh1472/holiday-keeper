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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class HolidayFacade {
    public static final int START_YEAR = 2020;
    public static final int LAST_YEAR = 2025;

    private final ExecutorService executorService;
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
        return loadHolidaysOf(countries, START_YEAR, LAST_YEAR);
    }

    public List<HolidayInfo> upsert(HolidayCriteria.Upsert criteria) {
        List<HolidayInfo> findHolidays = holidayClient.findHolidays(criteria.countryCode(), criteria.year());
        if (findHolidays.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 국가 코드이거나, 해당 연도의 공휴일이 없습니다.");
        }
        List<HolidayInfo> existHolidays = holidayService.findHolidays(criteria.toFindCommand());

        List<HolidayCommand.Create> commands = findHolidays.stream()
                .filter(findHoliday -> !existHolidays.contains(findHoliday))
                .map(nonExist ->
                        new HolidayCommand.Create(nonExist.date(), nonExist.localName(), nonExist.name(),
                                nonExist.countryCode()))
                .toList();
        holidayService.saveHolidays(commands);

        return commands.stream()
                .map(command -> new HolidayInfo(command.date(), command.localName(), command.name(),
                        command.countryCode()))
                .toList();
    }

    @Transactional
    public void synchronizeHolidays() {
        int thisYear = LocalDate.now().getYear();
        holidayService.deleteHolidaysOf(thisYear - 1);
        holidayService.deleteHolidaysOf(thisYear);

        List<CountryInfo> countries = countryClient.getCountries();
        loadHolidaysOf(countries, thisYear - 1, thisYear);
    }

    private List<LoadResult> loadHolidaysOf(List<CountryInfo> countries, int startYear, int lastYear) {
        List<CompletableFuture<LoadResult>> futures = new ArrayList<>();
        List<HolidayInfo> findHolidays = Collections.synchronizedList(new ArrayList<>());

        for (CountryInfo country : countries) {
            CompletableFuture<LoadResult> future = CompletableFuture.supplyAsync(
                    () -> findHolidaysOf(startYear, lastYear, country, findHolidays), executorService);
            futures.add(future);
        }

        List<LoadResult> loadResults = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        holidayService.saveHolidays(findHolidays.stream()
                .map(holiday -> new HolidayCommand.Create(holiday.date(), holiday.localName(), holiday.name(),
                        holiday.countryCode()))
                .toList());

        return loadResults;
    }

    private LoadResult findHolidaysOf(int startYear, int lastYear, CountryInfo country,
                                      List<HolidayInfo> totalHolidays) {
        List<HolidayInfo> countryHolidays = new ArrayList<>();
        for (int year = startYear; year <= lastYear; year++) {
            List<HolidayInfo> holidays = holidayClient.findHolidays(country.countryCode(), year);
            countryHolidays.addAll(holidays);
        }
        totalHolidays.addAll(countryHolidays);
        return new LoadResult(country.countryCode(), countryHolidays.size());
    }
}
