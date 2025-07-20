package com.holidaykeeper.infrastructure.holiday;

import com.holidaykeeper.domain.holiday.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayJpaRepository extends JpaRepository<Holiday, Long> {
}
