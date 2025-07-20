package com.holidaykeeper.domain.holiday;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HolidayService {
    private final HolidayRepository holidayRepository;

    @Transactional
    public void saveHolidays(List<HolidayCommand.Create> commands) {
        List<Holiday> holidays = commands.stream()
                .map(command -> Holiday.of(command.date(), command.localName(), command.name(), command.countryCode()))
                .toList();
        holidayRepository.saveAll(holidays);
    }
}
