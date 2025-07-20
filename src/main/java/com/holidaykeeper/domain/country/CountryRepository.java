package com.holidaykeeper.domain.country;

import java.util.List;

public interface CountryRepository {
    void saveAll(List<Country> countries);

    List<Country> findAll();
}
