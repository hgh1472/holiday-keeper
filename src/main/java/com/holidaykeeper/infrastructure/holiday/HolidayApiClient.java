package com.holidaykeeper.infrastructure.holiday;

import com.holidaykeeper.domain.holiday.HolidayClient;
import com.holidaykeeper.domain.holiday.HolidayInfo;
import com.holidaykeeper.support.error.CoreException;
import com.holidaykeeper.support.error.ErrorType;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException.BadRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound;

@Component
public class HolidayApiClient implements HolidayClient {

    @Override
    public List<HolidayInfo> findHolidays(String countryCode, int year) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://date.nager.at")
                .build();

        try {
            return webClient.get()
                    .uri("/api/v3/PublicHolidays/{year}/{countryCode}", year, countryCode)
                    .retrieve()
                    .bodyToFlux(HolidayInfo.class)
                    .collectList()
                    .block();
        } catch (NotFound | BadRequest e) {
            return List.of();
        } catch (Exception e) {
            throw new CoreException(ErrorType.INTERNAL_ERROR, "공휴일 정보 조회에 문제가 발생했습니다.");
        }
    }
}
