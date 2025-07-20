package com.holidaykeeper.interfaces.holiday;

import com.holidaykeeper.interfaces.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Holiday API", description = "Holiday Keeper의 휴일 관리 API입니다.")
public interface HolidayV1ApiSpec {

    @Operation(
            summary = "데이터 적재",
            description = "최근 5년(2020 ~ 2025)의 공휴일을 외부 API에서 수집하여 저장합니다."
    )
    ApiResponse<HolidayV1Dto.LoadResponse> loadHolidays();

    @Operation(
            summary = "공휴일 조회",
            description = "연도별·국가별 공휴일을 조회합니다."
    )
    ApiResponse<HolidayV1Dto.SearchResponse> searchHolidays(String countryCode, int year, int page, int size, String sort);

    @Operation(
            summary = "재동기화",
            description = "특정 연도 및 국가 데이터를 재호출하여 덮어씌웁니다."
    )
    ApiResponse<HolidayV1Dto.RefreshResponse> refreshHolidays(int year, String countryCode);
}
