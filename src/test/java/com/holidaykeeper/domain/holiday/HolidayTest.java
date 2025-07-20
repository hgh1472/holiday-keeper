package com.holidaykeeper.domain.holiday;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.holidaykeeper.support.error.CoreException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class HolidayTest {

    @Nested
    class Create {
        @DisplayName("date가 null인 경우, CoreException을 발생시킨다.")
        @Test
        void throwCoreException_whenDateIsNull() {

            assertThatThrownBy(() -> Holiday.of(null, "새해", "New Year's Day", "KR"))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("날짜는 null일 수 없습니다.");
        }

        @DisplayName("LocalName이 빈 문자열이거나 null인 경우, CoreException을 발생시킨다.")
        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = {" "})
        void throwCoreException_whenInvalidLocalName(String localName) {
            assertThatThrownBy(() -> Holiday.of(LocalDate.of(1, 1, 1), localName, "New Year's Day", "KR"))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("현지 이름은 빈 문자열일 수 없습니다.");
        }

        @DisplayName("Name이 빈 문자열이거나 null인 경우, CoreException을 발생시킨다.")
        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = {" "})
        void throwCoreException_whenInvalidName(String name) {
            assertThatThrownBy(() -> Holiday.of(LocalDate.of(1, 1, 1), "새해", name, "KR"))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("이름은 빈 문자열일 수 없습니다.");
        }

        @DisplayName("Name이 빈 문자열이거나 null인 경우, CoreException을 발생시킨다.")
        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = {" "})
        void throwCoreException_whenInvalidCountryCode(String countryCode) {
            assertThatThrownBy(() -> Holiday.of(LocalDate.of(1, 1, 1), "새해", "New Year`s Day", countryCode))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("국가 코드는 빈 문자열일 수 없습니다.");
        }
    }
}