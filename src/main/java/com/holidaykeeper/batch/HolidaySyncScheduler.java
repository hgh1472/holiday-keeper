package com.holidaykeeper.batch;

import com.holidaykeeper.application.holiday.HolidayFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HolidaySyncScheduler {
    private final HolidayFacade holidayFacade;

    @Scheduled(cron = "0 0 1 2 1 *", zone = "Asia/Seoul")
    public void syncHolidays() {
        holidayFacade.synchronizeHolidays();
    }
}
