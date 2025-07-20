package com.holidaykeeper.application.holiday;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import com.holidaykeeper.domain.country.Country;
import com.holidaykeeper.domain.country.CountryClient;
import com.holidaykeeper.domain.country.CountryInfo;
import com.holidaykeeper.domain.holiday.Holiday;
import com.holidaykeeper.domain.holiday.HolidayClient;
import com.holidaykeeper.domain.holiday.HolidayInfo;
import com.holidaykeeper.infrastructure.country.CountryJpaRepository;
import com.holidaykeeper.infrastructure.holiday.HolidayJpaRepository;
import com.holidaykeeper.support.error.CoreException;
import com.holidaykeeper.utils.DatabaseCleanUp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class HolidayFacadeIntegrationTest {
    @Autowired
    private HolidayFacade holidayFacade;
    @MockitoSpyBean
    private CountryClient countryClient;
    @MockitoSpyBean
    private HolidayClient holidayClient;
    @Autowired
    private CountryJpaRepository countryRepository;
    @Autowired
    private HolidayJpaRepository holidayRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void setUp() {
        when(countryClient.getCountries()).thenReturn(List.of(new CountryInfo("KR", "South Korea")));
        for (int i = 2020; i <= 2025; i++) {
            when(holidayClient.findHolidays("KR", i)).thenReturn(
                    List.of(new HolidayInfo(LocalDate.of(i, 1, 1), "새해", "New Year`s Day", "KR")));
        }
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    class Load {
        @DisplayName("최초 데이터 적재 이후, 국가는 적재하지 않는다.")
        @Test
        void notLoadCountries_afterFirst() {
            holidayFacade.loadHolidays();
            List<Country> first = countryRepository.findAll();

            holidayFacade.loadHolidays();
            List<Country> second = countryRepository.findAll();

            assertThat(first.size()).isEqualTo(second.size());
        }

        @DisplayName("최근 5년(2020 ~ 2025)의 공휴일을 외부 API에서 수집하여 저장한다.")
        @Test
        void loadHolidays() {
            holidayFacade.loadHolidays();

            List<Country> countries = countryRepository.findAll();
            assertAll(
                    () -> assertThat(countries).hasSize(1),
                    () -> assertThat(countries.get(0).getCountryCode()).isEqualTo("KR"),
                    () -> assertThat(countries.get(0).getName()).isEqualTo("South Korea")
            );
            List<Holiday> holidays = holidayRepository.findAll();
            assertAll(
                    () -> assertThat(holidays).hasSize(6),
                    () -> assertThat(holidays).extracting("date", "localName", "name", "countryCode")
                            .containsExactlyInAnyOrder(
                                    tuple(LocalDate.of(2020, 1, 1), "새해", "New Year`s Day", "KR"),
                                    tuple(LocalDate.of(2021, 1, 1), "새해", "New Year`s Day", "KR"),
                                    tuple(LocalDate.of(2022, 1, 1), "새해", "New Year`s Day", "KR"),
                                    tuple(LocalDate.of(2023, 1, 1), "새해", "New Year`s Day", "KR"),
                                    tuple(LocalDate.of(2024, 1, 1), "새해", "New Year`s Day", "KR"),
                                    tuple(LocalDate.of(2025, 1, 1), "새해", "New Year`s Day", "KR")
                            )
            );
        }
    }

    @Nested
    class Upsert {
        @DisplayName("특정 국가 및 연도의 공휴일 데이터 없는 경우, 해당 공휴일 데이터들이 저장된다.")
        @Test
        void upsert_whenNonExistHolidays() {
            HolidayCriteria.Upsert criteria = new HolidayCriteria.Upsert("KR", 2025);
            List<Holiday> before = holidayRepository.findByCountryCodeAndYear(criteria.countryCode(), criteria.year());

            List<HolidayInfo> holidayInfos = holidayFacade.upsert(criteria);

            List<HolidayInfo> holidays = holidayClient.findHolidays("KR", 2025);
            assertAll(
                    () -> assertThat(before).hasSize(0),
                    () -> assertThat(holidayInfos).hasSize(holidays.size())
            );
        }

        @DisplayName("특정 국가 및 연도의 공휴일 데이터 일부 있는 경우, 이미 존재하는 데이터를 제외하고 저장된다.")
        @Test
        void upsert_whenExistHolidays() {
            HolidayCriteria.Upsert criteria = new HolidayCriteria.Upsert("KR", 2025);
            holidayRepository.save(
                    new Holiday(LocalDate.of(criteria.year(), 1, 1), "새해", "New Year`s Day", criteria.countryCode()));

            List<HolidayInfo> upsertedHolidays = holidayFacade.upsert(criteria);

            List<HolidayInfo> holidays = holidayClient.findHolidays(criteria.countryCode(), criteria.year());
            assertAll(
                    () -> assertThat(upsertedHolidays).hasSize(holidays.size() - 1)
            );
        }

        @DisplayName("존재하지 않는 국가 코드일경우, CoreException을 발생시킨다.")
        @Test
        void throwCoreException_whenNonExistCountryCode() {
            HolidayCriteria.Upsert criteria = new HolidayCriteria.Upsert("NON_EXIST", 2025);

            assertThatThrownBy(() -> holidayFacade.upsert(criteria))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("존재하지 않는 국가 코드이거나, 해당 연도의 공휴일이 없습니다.");
        }

        @DisplayName("해당 연도의 공휴일을 찾을 수 없는 경우, CoreException을 발생시킨다.")
        @Test
        void throwCoreException_whenInvalidYear() {
            HolidayCriteria.Upsert criteria = new HolidayCriteria.Upsert("KR", 10000);

            assertThatThrownBy(() -> holidayFacade.upsert(criteria))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("존재하지 않는 국가 코드이거나, 해당 연도의 공휴일이 없습니다.");
        }
    }

    @Nested
    @DisplayName("전년도 및 금년도 공휴일 데이터 동기화 시, ")
    class Synchronize {
        @DisplayName("HolidayClient로 조회되지 않는 공휴일 데이터는 삭제된다.")
        @Test
        void synchronizeHolidays() {
            Holiday holiday = holidayRepository.save(new Holiday(LocalDate.now(), "사라질 공휴일", "Will Be Deleted", "KR"));

            holidayFacade.synchronizeHolidays();

            Optional<Holiday> before = holidayRepository.findById(holiday.getId());
            assertThat(before).isEmpty();
        }

        @DisplayName("변경되지 않은 공휴일이라도 삭제 후 다시 저장된다.")
        @Test
        void save_afterDelete() {
            Holiday holiday = holidayRepository.save(
                    new Holiday(LocalDate.of(LocalDate.now().getYear(), 1, 1), "새해", "New Year`s Day", "KR"));

            holidayFacade.synchronizeHolidays();

            Optional<Holiday> before = holidayRepository.findById(holiday.getId());
            assertThat(before).isEmpty();
            List<Holiday> savedHolidays = holidayRepository.findByCountryCodeAndYear(holiday.getCountryCode(),
                    holiday.getDate().getYear());
            assertThat(savedHolidays).anyMatch(
                    savedHoliday -> savedHoliday.getDate().equals(holiday.getDate()) &&
                            savedHoliday.getLocalName().equals(holiday.getLocalName()) &&
                            savedHoliday.getName().equals(holiday.getName()) &&
                            savedHoliday.getCountryCode().equals(holiday.getCountryCode())
            );
        }

        @DisplayName("HolidayClient를 통해 조회된 공휴일이 반영된다.")
        @Test
        void synchronize_withHolidayClient() {
            int thisYear = LocalDate.now().getYear();
            List<HolidayInfo> lastYearHolidayInfo = holidayClient.findHolidays("KR", thisYear - 1);
            List<HolidayInfo> thisYearHolidayInfo = holidayClient.findHolidays("KR", thisYear);

            holidayFacade.synchronizeHolidays();

            List<Holiday> lastYearHolidays = holidayRepository.findByCountryCodeAndYear("KR", thisYear - 1);
            assertThat(lastYearHolidayInfo.size()).isEqualTo(lastYearHolidays.size());

            List<Holiday> thisYearHolidays = holidayRepository.findByCountryCodeAndYear("KR", thisYear);
            assertThat(thisYearHolidayInfo.size()).isEqualTo(thisYearHolidays.size());

        }
    }
}