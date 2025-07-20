package com.holidaykeeper.interfaces.holiday;

import com.holidaykeeper.application.holiday.HolidayFacade;
import com.holidaykeeper.application.holiday.HolidayResult;
import com.holidaykeeper.interfaces.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/holidays")
public class HolidayV1Controller implements HolidayV1ApiSpec {
    private final HolidayFacade holidayFacade;

    @Override
    @PostMapping
    public ApiResponse<HolidayV1Dto.LoadResponse> loadHolidays() {
        List<HolidayResult> holidayResults = holidayFacade.loadHolidays();
        return ApiResponse.success(new HolidayV1Dto.LoadResponse(holidayResults.stream()
                .map(info -> new HolidayV1Dto.LoadResponse.Load(info.countryCode(), info.holidayCount()))
                .toList()));
    }
}
