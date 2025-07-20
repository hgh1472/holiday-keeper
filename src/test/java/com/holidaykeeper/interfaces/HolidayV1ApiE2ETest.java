package com.holidaykeeper.interfaces;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.holidaykeeper.domain.holiday.HolidayClient;
import com.holidaykeeper.domain.holiday.HolidayCommand;
import com.holidaykeeper.domain.holiday.HolidayInfo;
import com.holidaykeeper.domain.holiday.HolidayService;
import com.holidaykeeper.interfaces.holiday.HolidayV1Dto;
import com.holidaykeeper.utils.DatabaseCleanUp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HolidayV1ApiE2ETest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private HolidayClient holidayClient;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private HolidayService holidayService;


    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("POST /api/v1/holidays")
    class LoadHolidays {
        private static final String REQUEST_URL = "/api/v1/holidays";

        @DisplayName("최근 5년(2020~2025)의 공휴일을 저장한다.")
        @Test
        void loadHolidays() {
            ParameterizedTypeReference<ApiResponse<HolidayV1Dto.LoadResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<HolidayV1Dto.LoadResponse>> response =
                    testRestTemplate.exchange(REQUEST_URL, HttpMethod.POST, new HttpEntity<>(null), responseType);

            List<HolidayInfo> koreaHolidays = new ArrayList<>();
            for (int year = 2020; year <= 2025; year++) {
                List<HolidayInfo> holidays = holidayClient.findHolidays("KR", year);
                koreaHolidays.addAll(holidays);
            }
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertTrue(response.getBody().data().loads()
                            .contains(new HolidayV1Dto.LoadResponse.Load("KR", koreaHolidays.size())))
            );
        }
    }

    @Nested
    @DisplayName("GET /api/v1/holidays")
    class SearchHolidays {
        private static final String REQUEST_URL = "/api/v1/holidays";

        @DisplayName("특정 연도의 국가 공휴일을 빠른 순으로 조회한다.")
        @Test
        void getHolidays_dateAsc() {
            List<HolidayInfo> setup = holidayClient.findHolidays("KR", 2025);
            List<HolidayCommand.Create> commands = setup.stream()
                    .map(h -> new HolidayCommand.Create(h.date(), h.localName(), h.name(), h.countryCode()))
                    .toList();
            holidayService.saveHolidays(commands);

            ParameterizedTypeReference<ApiResponse<HolidayV1Dto.SearchResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            String url = UriComponentsBuilder.fromPath(REQUEST_URL)
                    .queryParam("countryCode", "KR")
                    .queryParam("year", 2025)
                    .queryParam("sort", "DATE_ASC")
                    .queryParam("page", 1)
                    .queryParam("size", 10)
                    .toUriString();
            ResponseEntity<ApiResponse<HolidayV1Dto.SearchResponse>> response =
                    testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null), responseType);

            List<HolidayInfo> koreaHolidays = holidayClient.findHolidays("KR", 2025);
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().countryCode()).isEqualTo("KR"),
                    () -> assertThat(response.getBody().data().pagination().totalPages()).isEqualTo(
                            koreaHolidays.size() / 10 + 1),
                    () -> assertThat(response.getBody().data().pagination().page()).isEqualTo(1),
                    () -> assertThat(response.getBody().data().pagination().size()).isEqualTo(10),
                    () -> assertThat(response.getBody().data().pagination().totalCount()).isEqualTo(
                            koreaHolidays.size()),
                    () -> {
                        List<HolidayV1Dto.HolidayResponse> holidays = response.getBody().data().holidays();

                        List<LocalDate> dates = holidays.stream()
                                .map(HolidayV1Dto.HolidayResponse::date)
                                .toList();
                        List<LocalDate> sortedDates = dates.stream()
                                .sorted()
                                .toList();

                        assertThat(dates).isEqualTo(sortedDates);
                    }
            );
        }

        @DisplayName("특정 연도 및 특정 국가의 공휴일을 늦은 순으로 조회한다.")
        @Test
        void getHolidays_dateDesc() {
            List<HolidayInfo> setup = holidayClient.findHolidays("KR", 2025);
            List<HolidayCommand.Create> commands = setup.stream()
                    .map(h -> new HolidayCommand.Create(h.date(), h.localName(), h.name(), h.countryCode()))
                    .toList();
            holidayService.saveHolidays(commands);

            ParameterizedTypeReference<ApiResponse<HolidayV1Dto.SearchResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            String url = UriComponentsBuilder.fromPath(REQUEST_URL)
                    .queryParam("countryCode", "KR")
                    .queryParam("year", 2025)
                    .queryParam("sort", "DATE_DESC")
                    .queryParam("page", 1)
                    .queryParam("size", 10)
                    .toUriString();
            ResponseEntity<ApiResponse<HolidayV1Dto.SearchResponse>> response =
                    testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null), responseType);

            List<HolidayInfo> koreaHolidays = holidayClient.findHolidays("KR", 2025);
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().countryCode()).isEqualTo("KR"),
                    () -> assertThat(response.getBody().data().pagination().totalPages()).isEqualTo(
                            koreaHolidays.size() / 10 + 1),
                    () -> assertThat(response.getBody().data().pagination().page()).isEqualTo(1),
                    () -> assertThat(response.getBody().data().pagination().size()).isEqualTo(10),
                    () -> assertThat(response.getBody().data().pagination().totalCount()).isEqualTo(
                            koreaHolidays.size()),
                    () -> {
                        List<HolidayV1Dto.HolidayResponse> holidays = response.getBody().data().holidays();

                        List<LocalDate> dates = holidays.stream()
                                .map(HolidayV1Dto.HolidayResponse::date)
                                .toList();
                        List<LocalDate> sortedDates = dates.stream()
                                .sorted(Comparator.reverseOrder())
                                .toList();

                        assertThat(dates).isEqualTo(sortedDates);
                    }
            );
        }

        @DisplayName("기본 페이지 사이즈는 10으로 조회한다.")
        @Test
        void getHolidays_defaultPageSize() {
            List<HolidayInfo> setup = holidayClient.findHolidays("KR", 2025);
            List<HolidayCommand.Create> commands = setup.stream()
                    .map(h -> new HolidayCommand.Create(h.date(), h.localName(), h.name(), h.countryCode()))
                    .toList();
            holidayService.saveHolidays(commands);

            ParameterizedTypeReference<ApiResponse<HolidayV1Dto.SearchResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            String url = UriComponentsBuilder.fromPath(REQUEST_URL)
                    .queryParam("countryCode", "KR")
                    .queryParam("year", 2025)
                    .queryParam("sort", "DATE_DESC")
                    .queryParam("page", 1)
                    .toUriString();
            ResponseEntity<ApiResponse<HolidayV1Dto.SearchResponse>> response =
                    testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().pagination().size()).isEqualTo(10)
            );
        }

        @DisplayName("기본 정렬 조건은 시간순으로 조회한다.")
        @Test
        void getHolidays_defaultSort() {
            List<HolidayInfo> setup = holidayClient.findHolidays("KR", 2025);
            List<HolidayCommand.Create> commands = setup.stream()
                    .map(h -> new HolidayCommand.Create(h.date(), h.localName(), h.name(), h.countryCode()))
                    .toList();
            holidayService.saveHolidays(commands);

            ParameterizedTypeReference<ApiResponse<HolidayV1Dto.SearchResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            String url = UriComponentsBuilder.fromPath(REQUEST_URL)
                    .queryParam("countryCode", "KR")
                    .queryParam("year", 2025)
                    .queryParam("page", 1)
                    .queryParam("size", 10)
                    .toUriString();
            ResponseEntity<ApiResponse<HolidayV1Dto.SearchResponse>> response =
                    testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> {
                        List<HolidayV1Dto.HolidayResponse> holidays = response.getBody().data().holidays();

                        List<LocalDate> dates = holidays.stream()
                                .map(HolidayV1Dto.HolidayResponse::date)
                                .toList();
                        List<LocalDate> sortedDates = dates.stream()
                                .sorted()
                                .toList();

                        assertThat(dates).isEqualTo(sortedDates);
                    }
            );
        }
    }
}
