package com.holidaykeeper.domain.country;

public class CountryCommand {
    public record Create(
            String countryCode,
            String name
    ) {
    }
}
