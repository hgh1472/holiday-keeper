package com.holidaykeeper.domain.country;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    @Transactional
    public void createCountries(List<CountryCommand.Create> commands) {
        List<Country> countries = commands.stream()
                .map(command -> Country.of(command.countryCode(), command.name()))
                .toList();
        countryRepository.saveAll(countries);
    }

    @Transactional(readOnly = true)
    public List<CountryInfo> findAllCountries() {
        List<Country> countries = countryRepository.findAll();
        return countries.stream()
                .map(CountryInfo::of)
                .toList();
    }
}
