package com.holidaykeeper.infrastructure.country;

import com.holidaykeeper.domain.country.Country;
import com.holidaykeeper.domain.country.CountryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CountryRepositoryImpl implements CountryRepository {
    private final CountryJpaRepository countryJpaRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveAll(List<Country> countries) {
        String sql = "INSERT INTO COUNTRY (country_code, name) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(
                sql,
                countries,
                countries.size(),
                (ps, country) -> {
                    ps.setString(1, country.getCountryCode());
                    ps.setString(2, country.getName());
                }
        );
    }

    @Override
    public List<Country> findAll() {
        return countryJpaRepository.findAll();
    }

}
