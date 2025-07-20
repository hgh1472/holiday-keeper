package com.holidaykeeper.domain.holiday;

import com.holidaykeeper.support.error.CoreException;
import com.holidaykeeper.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@Entity
@Table(name = "HOLIDAY")
public class Holiday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "local_name", nullable = false)
    private String localName;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "country_code", nullable = false)
    private String countryCode;

    protected Holiday() {
    }

    public Holiday(LocalDate date, String localName, String name, String countryCode) {
        this.date = date;
        this.localName = localName;
        this.name = name;
        this.countryCode = countryCode;
    }

    public static Holiday of(LocalDate date, String localName, String name, String countryCode) {
        if (date == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "날짜는 null일 수 없습니다.");
        }
        if (localName == null || localName.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "현지 이름은 빈 문자열일 수 없습니다.");
        }
        if (name == null || name.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이름은 빈 문자열일 수 없습니다.");
        }
        if (countryCode == null || countryCode.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "국가 코드는 빈 문자열일 수 없습니다.");
        }
        return new Holiday(date, localName, name, countryCode);
    }
}
