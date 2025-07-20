package com.holidaykeeper.infrastructure.country;

import com.holidaykeeper.domain.country.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryJpaRepository extends JpaRepository<Country, Long> {
}
