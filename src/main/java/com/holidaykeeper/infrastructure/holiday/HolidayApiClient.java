package com.holidaykeeper.infrastructure.holiday;

import com.holidaykeeper.domain.holiday.HolidayClient;
import com.holidaykeeper.domain.holiday.HolidayInfo;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class HolidayApiClient implements HolidayClient {

    @Override
    public List<HolidayInfo> findHolidays(String countryCode, int year) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://date.nager.at")
                .build();

        return webClient.get()
                .uri("/api/v3/PublicHolidays/{year}/{countryCode}", year, countryCode)
                .retrieve()
                .bodyToFlux(HolidayInfo.class)
                .collectList()
                .block();
    }
}
