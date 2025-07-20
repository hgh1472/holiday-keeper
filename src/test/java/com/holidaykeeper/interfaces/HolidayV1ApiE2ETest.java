package com.holidaykeeper.interfaces;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.holidaykeeper.domain.holiday.HolidayClient;
import com.holidaykeeper.domain.holiday.HolidayInfo;
import com.holidaykeeper.interfaces.holiday.HolidayV1Dto;
import com.holidaykeeper.interfaces.holiday.HolidayV1Dto.LoadResponse;
import com.holidaykeeper.utils.DatabaseCleanUp;
import java.util.ArrayList;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HolidayV1ApiE2ETest {
    private final TestRestTemplate testRestTemplate;
    private final HolidayClient holidayClient;
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public HolidayV1ApiE2ETest(TestRestTemplate testRestTemplate,
                               HolidayClient holidayClient,
                               DatabaseCleanUp databaseCleanUp) {
        this.testRestTemplate = testRestTemplate;
        this.holidayClient = holidayClient;
        this.databaseCleanUp = databaseCleanUp;
    }

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
            List<HolidayInfo> koreaHolidays = new ArrayList<>();
            for (int year = 2020; year <= 2025; year++) {
                List<HolidayInfo> holidays = holidayClient.findHolidays("KR", year);
                koreaHolidays.addAll(holidays);
            }

            ParameterizedTypeReference<ApiResponse<LoadResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<LoadResponse>> response =
                    testRestTemplate.exchange(REQUEST_URL, HttpMethod.POST, new HttpEntity<>(null), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertTrue(response.getBody().data().loads()
                            .contains(new HolidayV1Dto.LoadResponse.Load("KR", koreaHolidays.size())))
            );
        }
    }
}
