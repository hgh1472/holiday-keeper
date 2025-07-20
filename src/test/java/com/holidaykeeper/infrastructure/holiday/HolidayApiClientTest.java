package com.holidaykeeper.infrastructure.holiday;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.holidaykeeper.domain.holiday.HolidayInfo;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HolidayApiClientTest {

    private final HolidayApiClient holidayApiClient = new HolidayApiClient();

    @DisplayName("date.nagar.at API를 통해 국가/연도 조합으로 공휴일을 조회한다.")
    @Test
    void getHolidays() {
        List<HolidayInfo> holidays = holidayApiClient.findHolidays("KR", 2023);

        assertAll(
                () -> assertNotNull(holidays),
                () -> assertFalse(holidays.isEmpty()),
                () -> assertTrue(holidays.stream().allMatch(holiday -> "KR".equals(holiday.countryCode()))),
                () -> assertTrue(holidays.stream().allMatch(holiday -> holiday.date().getYear() == 2023))
        );
    }

}