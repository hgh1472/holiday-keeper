package com.holidaykeeper.infrastructure.holiday;

import com.holidaykeeper.domain.holiday.Holiday;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface HolidayJpaRepository extends JpaRepository<Holiday, Long> {

    @Query("select h from Holiday h where h.countryCode = :countryCode and year(h.date) = :year")
    Page<Holiday> findByCountryCodeAndYear(String countryCode, int year, Pageable pageable);

    @Query("select h from Holiday h where h.countryCode = :countryCode and year(h.date) = :year")
    List<Holiday> findByCountryCodeAndYear(String countryCode, int year);

    @Modifying
    @Query("delete from Holiday h where h.countryCode = :countryCode and year(h.date) = :year")
    int deleteByCountryCodeAndYear(String countryCode, int year);
}
