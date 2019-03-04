package com.gumtree.addressbook.domain;

import com.gumtree.addressbook.domain.AddressBookEntry.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;

import static com.gumtree.addressbook.domain.AddressBookEntry.Gender.FEMALE;
import static com.gumtree.addressbook.domain.AddressBookEntry.Gender.MALE;
import static java.time.Month.*;
import static org.assertj.core.api.Assertions.assertThat;

class AddressBookTest {
    private final AddressBook underTest = new AddressBook();

    @Test
    void countReturnsZeroWhenAddressBookIsEmpty() {
        assertThat(underTest.count(MALE)).isZero();
    }

    @Test
    void countReturnsZeroWhenNoMatchFound() {
        underTest.add("Sarah McKnight", FEMALE, LocalDate.of(2010, AUGUST, 11));

        assertThat(underTest.count(MALE)).isZero();
    }

    @ParameterizedTest
    @EnumSource(Gender.class)
    void countReturnsOneWhenOneMatchFound(final Gender gender) {
        underTest.add("Bill McKnight", gender, LocalDate.of(2010, AUGUST, 11));

        assertThat(underTest.count(gender)).isOne();
    }

    @ParameterizedTest
    @CsvSource({
            "FEMALE, 2",
            "MALE, 3"
    })
    void countReturnsMaleCountWhenMultipleMatchesFound(final Gender gender, final int count) {
        underTest.add("Sarah McKnight", FEMALE, LocalDate.of(2008, JANUARY, 30));
        underTest.add("Bill McKnight", MALE, LocalDate.of(2010, AUGUST, 11));
        underTest.add("Johnny Stone", MALE, LocalDate.of(1950, MARCH, 22));
        underTest.add("Emma Robinson", FEMALE, LocalDate.of(1977, NOVEMBER, 4));
        underTest.add("Michael Jackson", MALE, LocalDate.of(1982, JUNE, 12));

        assertThat(underTest.count(gender)).isEqualTo(count);
    }
}