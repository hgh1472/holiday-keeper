package com.holidaykeeper.infrastructure.holiday;

import com.holidaykeeper.domain.holiday.Holiday;
import com.holidaykeeper.domain.holiday.HolidayCommand.Search.HolidaySort;
import com.holidaykeeper.domain.holiday.HolidayRepository;
import java.sql.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepository {
    private final HolidayJpaRepository holidayJpaRepository;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveAll(List<Holiday> holidays) {
        String sql = "INSERT INTO HOLIDAY (date, local_name, name, country_code) VALUES (?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(
                sql,
                holidays,
                holidays.size(),
                (ps, holiday) -> {
                    ps.setDate(1, Date.valueOf(holiday.getDate()));
                    ps.setString(2, holiday.getLocalName());
                    ps.setString(3, holiday.getName());
                    ps.setString(4, holiday.getCountryCode());
                }
        );
    }

    @Override
    public Page<Holiday> findByCountryCodeAndYear(String countryCode, int year, int page, int size,
                                                  HolidaySort holidaySort) {
        Sort sort = Sort.by(holidaySort == HolidaySort.DATE_ASC ? Sort.Direction.ASC : Sort.Direction.DESC, "date");

        return holidayJpaRepository.findByCountryCodeAndYear(countryCode, year, PageRequest.of(page, size, sort));
    }
}
