package com.holidaykeeper.interfaces.holiday;

import com.holidaykeeper.application.holiday.HolidayFacade;
import com.holidaykeeper.application.holiday.LoadResult;
import com.holidaykeeper.domain.holiday.HolidayClient;
import com.holidaykeeper.domain.holiday.HolidayCommand.Search;
import com.holidaykeeper.domain.holiday.HolidayCommand.Search.HolidaySort;
import com.holidaykeeper.domain.holiday.HolidayInfo;
import com.holidaykeeper.domain.holiday.HolidayService;
import com.holidaykeeper.interfaces.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/holidays")
public class HolidayV1Controller implements HolidayV1ApiSpec {
    private final HolidayFacade holidayFacade;

    private final HolidayService holidayService;

    private final HolidayClient holidayClient;

    @Override
    @PostMapping
    public ApiResponse<HolidayV1Dto.LoadResponse> loadHolidays() {
        List<LoadResult> loadResults = holidayFacade.loadHolidays();
        return ApiResponse.success(new HolidayV1Dto.LoadResponse(loadResults.stream()
                .map(info -> new HolidayV1Dto.LoadResponse.Load(info.countryCode(), info.holidayCount()))
                .toList()));
    }

    @Override
    @GetMapping
    public ApiResponse<HolidayV1Dto.SearchResponse> searchHolidays(@RequestParam String countryCode,
                                                                   @RequestParam int year,
                                                                   @RequestParam int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "DATE_ASC") String sort) {
        Page<HolidayInfo> infos = holidayService.findHolidays(
                new Search(countryCode, year, page, size, HolidaySort.valueOf(sort)));

        HolidayV1Dto.SearchResponse searchResponse = new HolidayV1Dto.SearchResponse(
                "KR",
                new HolidayV1Dto.Pagination(infos.getNumber(), infos.getSize(), infos.getTotalElements(),
                        infos.getTotalPages()),
                infos.stream()
                        .map(info -> new HolidayV1Dto.HolidayResponse(info.name(), info.localName(), info.date()))
                        .toList()
        );

        return ApiResponse.success(searchResponse);
    }

    @Override
    @PatchMapping("/{year}/{countryCode}")
    public ApiResponse<HolidayV1Dto.RefreshResponse> refreshHolidays(@PathVariable int year, @PathVariable String countryCode) {
        List<HolidayInfo> holidays = holidayFacade.upsert(countryCode, year);

        return ApiResponse.success(new HolidayV1Dto.RefreshResponse(countryCode, year, holidays.size(),
                holidays.stream()
                        .map(holiday -> new HolidayV1Dto.HolidayResponse(holiday.name(), holiday.localName(), holiday.date()))
                        .toList()));
    }

}
