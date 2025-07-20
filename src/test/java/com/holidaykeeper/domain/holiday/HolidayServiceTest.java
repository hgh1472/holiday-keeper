package com.holidaykeeper.domain.holiday;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import com.holidaykeeper.domain.holiday.HolidayCommand.Find;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {
    @InjectMocks
    private HolidayService holidayService;

    @Mock
    private HolidayRepository holidayRepository;

    @DisplayName("특정 연도 및 국가에 해당하는 공휴일 페이지를 조회한다.")
    @Test
    void searchHolidays() {
        when(holidayRepository.findPage("KR", 2023, 0, 10, HolidayCommand.Search.HolidaySort.DATE_ASC))
                .thenReturn(new PageImpl<>(List.of(
                        new Holiday(LocalDate.of(2023, 1, 1), "새해", "New Year's Day", "KR"),
                        new Holiday(LocalDate.of(2023, 12, 25), "성탄절", "Christmas", "KR")
                )));

        Page<HolidayInfo> holidayInfos = holidayService.findHolidays(
                new HolidayCommand.Search("KR", 2023, 0, 10, HolidayCommand.Search.HolidaySort.DATE_ASC));

        assertAll(
                () -> assertThat(holidayInfos.getTotalElements()).isEqualTo(2),
                () -> assertThat(holidayInfos.getContent().get(0).date()).isEqualTo(LocalDate.of(2023, 1, 1)),
                () -> assertThat(holidayInfos.getContent().get(1).date()).isEqualTo(LocalDate.of(2023, 12, 25))
        );
    }

    @DisplayName("특정 연도 및 국가에 해당하는 공휴일을 모두 조회한다.")
    @Test
    void findHolidays() {
        when(holidayRepository.findByCountryCodeAndYear("KR", 2025))
                .thenReturn(List.of(
                        new Holiday(LocalDate.of(2025, 1, 1), "새해", "New Year's Day", "KR"),
                        new Holiday(LocalDate.of(2025, 12, 25), "성탄절", "Christmas", "KR"))
                );

        List<HolidayInfo> holidays = holidayService.findHolidays(new Find("KR", 2025));

        assertThat(holidays).hasSize(2);
        assertThat(holidays)
                .allMatch(holiday -> holiday.countryCode().equals("KR") && holiday.date().getYear() == 2025);
    }
}