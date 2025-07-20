package com.holidaykeeper.infrastructure.country;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.holidaykeeper.domain.country.CountryInfo;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CountryApiClientTest {

    private final CountryApiClient countryApiClient = new CountryApiClient();

    @DisplayName("date.nagar.at API를 통해 국가 정보를 조회한다.")
    @Test
    void getCountries() {
        List<CountryInfo> countries = countryApiClient.getCountries();

        assertAll(
                () -> assertNotNull(countries),
                () -> assertFalse(countries.isEmpty()),
                () -> assertTrue(countries.stream().anyMatch(country -> "KR".equals(country.countryCode()))),
                () -> assertTrue(countries.stream().anyMatch(country -> "South Korea".equals(country.name())))
        );
    }
}