package com.holidaykeeper.interfaces.holiday;

import com.holidaykeeper.interfaces.ApiResponse;
import com.holidaykeeper.interfaces.holiday.HolidayV1Dto.LoadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Holiday API", description = "Holiday Keeper의 휴일 관리 API입니다.")
public interface HolidayV1ApiSpec {

    @Operation(
            summary = "데이터 적재",
            description = "최근 5년(2020 ~ 2025)의 공휴일을 외부 API에서 수집하여 저장합니다."
    )
    ApiResponse<LoadResponse> loadHolidays();
}
