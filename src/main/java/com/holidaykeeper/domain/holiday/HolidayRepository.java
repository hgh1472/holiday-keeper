package com.holidaykeeper.domain.holiday;

import java.util.List;

public interface HolidayRepository {
    void saveAll(List<Holiday> holidays);
}
