package com.holidaykeeper.domain.holiday;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @Transactional(readOnly = true)
    public Page<HolidayInfo> findHolidays(HolidayCommand.Search command) {
        Page<Holiday> holidays = holidayRepository.findPage(command.countryCode(), command.year(),
                command.page(), command.size(), command.holidaySort());

        return holidays.map(holiday -> new HolidayInfo(holiday.getDate(), holiday.getLocalName(), holiday.getName(),
                holiday.getCountryCode()));
    }

    @Transactional(readOnly = true)
    public List<HolidayInfo> findHolidays(HolidayCommand.Find command) {
        return holidayRepository.findByCountryCodeAndYear(command.countryCode(), command.year()).stream()
                .map(holiday -> new HolidayInfo(holiday.getDate(), holiday.getLocalName(), holiday.getName(),
                        holiday.getCountryCode()))
                .toList();
    }
}
