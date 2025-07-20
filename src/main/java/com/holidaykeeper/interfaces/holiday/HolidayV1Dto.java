package com.holidaykeeper.interfaces.holiday;

import java.time.LocalDate;
import java.util.List;

public class HolidayV1Dto {
    public record LoadResponse(List<Load> loads) {
        public record Load(String countryCode, int holidayCount) {
        }
    }

    public record HolidayResponse(
            String name,
            String description,
            LocalDate date
    ) {
    }

    public record SearchResponse(
            String countryCode,
            Pagination pagination,
            List<HolidayResponse> holidays
    ) {
    }

    public record Pagination(
            int page,
            int size,
            long totalCount,
            int totalPages
    ) {
    }

    public record RefreshResponse(
            String countryCode,
            int year,
            int refreshedCount,
            List<HolidayResponse> refreshedHolidays
    ) {
    }
}
