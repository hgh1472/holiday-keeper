package com.holidaykeeper.domain.holiday;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.holidaykeeper.domain.holiday.HolidayCommand.Search;
import com.holidaykeeper.domain.holiday.HolidayCommand.Search.HolidaySort;
import com.holidaykeeper.infrastructure.holiday.HolidayJpaRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

@SpringBootTest
public class HolidayServiceIntegrationTest {
    @Autowired
    private HolidayService holidayService;
    @Autowired
    private HolidayJpaRepository holidayJpaRepository;

    @Nested
    class Find {
        @DisplayName("특정 연도 및 국가에 해당하는 공휴일을 시간순으로 조회한다.")
        @Test
        void findHolidays() {
            holidayJpaRepository.save(new Holiday(LocalDate.of(2023, 12, 25), "성탄절", "Christmas", "KR"));
            holidayJpaRepository.save(new Holiday(LocalDate.of(2023, 1, 1), "새해", "New Year's Day", "KR"));

            Page<HolidayInfo> holidayInfos = holidayService.findHolidays(
                    new Search("KR", 2023, 0, 10, HolidaySort.DATE_ASC));

            assertAll(
                    () -> assertThat(holidayInfos.getTotalElements()).isEqualTo(2),
                    () -> assertThat(holidayInfos.getContent().get(0).date()).isEqualTo(LocalDate.of(2023, 1, 1)),
                    () -> assertThat(holidayInfos.getContent().get(1).date()).isEqualTo(LocalDate.of(2023, 12, 25))
            );
        }

        @DisplayName("특정 연도 및 국가에 해당하는 공휴일을 시간역순으로 조회한다.")
        @Test
        void findHolidaysDateDesc() {
            holidayJpaRepository.save(new Holiday(LocalDate.of(2023, 1, 1), "새해", "New Year's Day", "KR"));
            holidayJpaRepository.save(new Holiday(LocalDate.of(2023, 12, 25), "성탄절", "Christmas", "KR"));

            Page<HolidayInfo> holidayInfos = holidayService.findHolidays(
                    new Search("KR", 2023, 0, 10, HolidaySort.DATE_DESC));

            assertAll(
                    () -> assertThat(holidayInfos.getTotalElements()).isEqualTo(2),
                    () -> assertThat(holidayInfos.getContent().get(0).date()).isEqualTo(LocalDate.of(2023, 12, 25)),
                    () -> assertThat(holidayInfos.getContent().get(1).date()).isEqualTo(LocalDate.of(2023, 1, 1))
            );
        }
    }
}
