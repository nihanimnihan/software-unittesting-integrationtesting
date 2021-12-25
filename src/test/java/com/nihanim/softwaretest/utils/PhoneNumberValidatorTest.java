package com.nihanim.softwaretest.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class PhoneNumberValidatorTest {

    private PhoneNumberValidator underTest;

    @BeforeEach
    void setUp() {
        underTest = new PhoneNumberValidator();
    }


    @ParameterizedTest
    @CsvSource({"+447000000000, true" , "+44700000000000, false"})
    void itShouldVValidatePhoneNumber(String phoneNumber, boolean expected) {
        // When
        boolean isValid = underTest.test(phoneNumber);

        // Then
        assertThat(isValid).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should fail when length is bigger than 13")
    void itShouldVValidatePhoneNumberWhenIncorrectAndHasLengthBiggerThan13() {
        // Given
        String phoneNumber = "+447000000000000";

        // When
        boolean isValid = underTest.test(phoneNumber);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should fail when not start with plus")
    void itShouldVValidatePhoneNumberWhenNotStartWithPlus() {
        // Given
        String phoneNumber = "4470000000000000";

        // When
        boolean isValid = underTest.test(phoneNumber);

        // Then
        assertThat(isValid).isFalse();
    }
}
