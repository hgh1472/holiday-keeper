package com.holidaykeeper.infrastructure.country;

import com.holidaykeeper.domain.country.CountryClient;
import com.holidaykeeper.domain.country.CountryInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class CountryApiClient implements CountryClient {

    public static final String BASE_URL = "https://date.nager.at";

    @Override
    public List<CountryInfo> getCountries() {
        WebClient webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .build();

        return webClient.get()
                .uri("/api/v3/AvailableCountries")
                .retrieve()
                .bodyToFlux(CountryInfo.class)
                .collectList()
                .block();
    }
}
