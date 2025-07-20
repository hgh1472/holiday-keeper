package com.holidaykeeper.infrastructure.holiday;

import com.holidaykeeper.domain.holiday.Holiday;
import com.holidaykeeper.domain.holiday.HolidayRepository;
import java.sql.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepository {
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
}
