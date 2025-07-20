package com.holidaykeeper.domain.country;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.holidaykeeper.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class CountryTest {

    @Nested
    class Create {
        @DisplayName("나라 코드가 빈 문자열이거나 null인 경우, CoreException을 발생시킨다.")
        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = {" "})
        void throwCoreException_whenInvalidCountryCode(String countryCode) {
            assertThatThrownBy(() -> Country.of(countryCode, "South Korea"))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("나라 코드는 빈 문자열일 수 없습니다.");
        }

        @DisplayName("이름이 빈 문자열이거나 null인 경우, CoreException을 발생시킨다.")
        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = {" "})
        void throwCoreException_whenInvalidName(String name) {
            assertThatThrownBy(() -> Country.of("KR", name))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("이름은 빈 문자열일 수 없습니다.");
        }
    }
}