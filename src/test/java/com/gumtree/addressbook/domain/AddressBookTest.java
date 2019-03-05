package com.gumtree.addressbook.domain;

import com.gumtree.addressbook.domain.AddressBookEntry.Gender;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;

import static com.gumtree.addressbook.domain.AddressBookEntry.Gender.FEMALE;
import static com.gumtree.addressbook.domain.AddressBookEntry.Gender.MALE;
import static java.time.Month.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddressBookTest {
    private final AddressBook underTest = new AddressBook();

    @Nested
    class Count {

        @Test
        void returnsZeroWhenAddressBookIsEmpty() {
            assertThat(underTest.count(MALE)).isZero();
        }

        @Test
        void returnsZeroWhenNoMatchFound() {
            underTest.add("Sarah McKnight", FEMALE, LocalDate.of(2010, AUGUST, 11));

            assertThat(underTest.count(MALE)).isZero();
        }

        @ParameterizedTest
        @EnumSource(Gender.class)
        void returnsOneWhenOneMatchFound(final Gender gender) {
            underTest.add("Bill McKnight", gender, LocalDate.of(2010, AUGUST, 11));

            assertThat(underTest.count(gender)).isOne();
        }

        @ParameterizedTest
        @CsvSource({
                "FEMALE, 2",
                "MALE, 3"
        })
        void returnsMaleCountWhenMultipleMatchesFound(final Gender gender, final int count) {
            underTest.add("Sarah McKnight", FEMALE, LocalDate.of(2008, JANUARY, 30));
            underTest.add("Bill McKnight", MALE, LocalDate.of(2010, AUGUST, 11));
            underTest.add("Johnny Stone", MALE, LocalDate.of(1950, MARCH, 22));
            underTest.add("Emma Robinson", FEMALE, LocalDate.of(1977, NOVEMBER, 4));
            underTest.add("Michael Jackson", MALE, LocalDate.of(1982, JUNE, 12));

            assertThat(underTest.count(gender)).isEqualTo(count);
        }
    }

    @Nested
    class FindOldest {

        @Test
        void returnsEmptyWhenAddressBookIsEmpty() {
            assertThat(underTest.findOldest()).isNotPresent();
        }

        @Test
        void returnsEntryWhenAddressBookHasOneElement() {
            underTest.add("Sarah McKnight", FEMALE, LocalDate.of(2010, AUGUST, 11));

            assertThat(underTest.findOldest())
                    .isPresent()
                    .hasValue("Sarah McKnight");
        }

        @Test
        void returnsEntryWhenAddressBookHasTwoElements() {
            underTest.add("Johnny Stone", MALE, LocalDate.of(1950, MARCH, 22));
            underTest.add("Sarah McKnight", FEMALE, LocalDate.of(2010, AUGUST, 11));

            assertThat(underTest.findOldest())
                    .isPresent()
                    .hasValue("Johnny Stone");
        }

        @Test
        void returnsEntryWhenAddressBookHasMultipleElements() {
            underTest.add("Bill McKnight", MALE, LocalDate.of(2010, AUGUST, 11));
            underTest.add("Johnny Stone", MALE, LocalDate.of(1950, MARCH, 22));
            underTest.add("Emma Robinson", FEMALE, LocalDate.of(1977, NOVEMBER, 4));

            assertThat(underTest.findOldest())
                    .isPresent()
                    .hasValue("Johnny Stone");
        }

        @Test
        void returnsEntryWhenTwoPeopleHaveSameDateOfBirth() {
            underTest.add("Bill McKnight", MALE, LocalDate.of(1950, MARCH, 22));
            underTest.add("Johnny Stone", MALE, LocalDate.of(1950, MARCH, 22));
            underTest.add("Emma Robinson", FEMALE, LocalDate.of(1977, NOVEMBER, 4));

            assertThat(underTest.findOldest())
                    .isPresent()
                    .hasValueSatisfying(o -> assertThat(o).isIn("Bill McKnight", "Johnny Stone"));
        }
    }

    @Nested
    class AgeDifference {

        @Test
        void throwsIllegalArgumentExceptionWhenBothEntriesNotFound() {
            assertThrows(IllegalArgumentException.class, () -> underTest.ageDifference("Bill McKnight", "Johnny Stone"));
        }

        @Test
        void throwsIllegalArgumentExceptionWhenOneEntryNotFound() {
            underTest.add("Bill McKnight", MALE, LocalDate.of(1950, MARCH, 22));

            assertThrows(IllegalArgumentException.class, () -> underTest.ageDifference("Bill McKnight", "Johnny Stone"));
        }

        @Test
        void returnsZeroWhenBothDateOfBirthsAreSame() {
            underTest.add("Bill McKnight", MALE, LocalDate.of(1950, MARCH, 22));
            underTest.add("Johnny Stone", MALE, LocalDate.of(1950, MARCH, 22));

            assertThat(underTest.ageDifference("Bill McKnight", "Johnny Stone")).isZero();
        }

        @ParameterizedTest
        @CsvSource({
                "1950-03-22, 1950-03-30, 8",
                "1950-03-30, 1950-03-22, -8",
                "1970-12-30, 1971-12-30, 365",
                "1977-03-16, 1985-01-15, 2862"
        })
        void returnsInDaysWhenDateOfBirthsAreDifferent(LocalDate firstDateOfBirth, LocalDate secondDateOfBirth, int ageDifference) {
            underTest.add("Bill McKnight", MALE, firstDateOfBirth);
            underTest.add("Emma Robinson", FEMALE, secondDateOfBirth);

            assertThat(underTest.ageDifference("Bill McKnight", "Emma Robinson")).isEqualTo(ageDifference);
        }
    }
}