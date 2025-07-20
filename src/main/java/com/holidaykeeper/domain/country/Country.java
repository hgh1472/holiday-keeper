package com.holidaykeeper.domain.country;

import com.holidaykeeper.support.error.CoreException;
import com.holidaykeeper.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "COUNTRY")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country_code", nullable = false, unique = true)
    private String countryCode;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    protected Country() {
    }

    private Country(String countryCode, String name) {
        this.countryCode = countryCode;
        this.name = name;
    }

    public static Country of(String countryCode, String name) {
        if (countryCode == null || countryCode.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "나라 코드는 빈 문자열일 수 없습니다.");
        }
        if (name == null || name.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이름은 빈 문자열일 수 없습니다.");
        }
        return new Country(countryCode, name);
    }
}
