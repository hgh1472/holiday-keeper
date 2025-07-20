package com.holidaykeeper.domain.country;

public record CountryInfo(
        String countryCode,
        String name
) {
    public static CountryInfo of(Country country) {
        return new CountryInfo(country.getCountryCode(), country.getName());
    }
}
