package com.holidaykeeper.interfaces.holiday;

import java.util.List;

public class HolidayV1Dto {
    public record LoadResponse(List<Load> loads) {
        public record Load(String countryCode, int holidayCount) {
        }
    }
}
