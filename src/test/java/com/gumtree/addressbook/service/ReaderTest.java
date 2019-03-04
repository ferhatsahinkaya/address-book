package com.gumtree.addressbook.service;

import com.gumtree.addressbook.domain.AddressBook;
import com.gumtree.addressbook.domain.AddressBookEntry.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static com.gumtree.addressbook.domain.AddressBookEntry.Gender.FEMALE;
import static com.gumtree.addressbook.domain.AddressBookEntry.Gender.MALE;
import static java.lang.String.format;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.time.Month.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReaderTest {

    @TempDir
    static Path tempDir;
    private Path path;

    @BeforeEach
    void setup() throws IOException {
        path = Files.createTempFile(tempDir, "", "");
    }

    @Test
    void readThrowsExceptionWhenFileDoesNotExist() {
        assertThrows(RuntimeException.class, () -> new Reader().read(Paths.get("unknown")));
    }


    @Test
    void readReturnsEmptyAddressBookWhenFileIsEmpty() {
        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries()).isEmpty();
    }

    @Test
    void readReturnsEmptyAddressBookWhenFileHasOnlyNewLine() {
        writeLine(path, "");

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries()).isEmpty();
    }

    @Test
    void readReturnsEmptyAddressBookWhenFileHasBlankLine() {
        writeLine(path, "  \t");

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries()).isEmpty();
    }

    @Test
    void readReturnsEmptyAddressBookWhenLineHas2Elements() {
        writeLine(path, "Bill McKnight, Male");

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries()).isEmpty();
    }

    @Test
    void readReturnsEmptyAddressBookWhenLineHas4Elements() {
        writeLine(path, "Bill McKnight, Male, 16/03/77, address");

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries()).isEmpty();
    }

    @Test
    void readThrowsDateTimeParseExceptionWhenLineIsInvalid() {
        writeLine(path, "Bill McKnight, Male, 16/13/77");

        assertThrows(DateTimeParseException.class, () -> new Reader().read(path));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Bill McKnight, , 16/03/77",
            ", Male, 16/03/77",
            "Bill McKnight, UNKNOWN, 16/03/77"})
    void readThrowsIllegalArgumentExceptionWhenLineIsInvalid(final String line) {
        writeLine(path, line);

        assertThrows(IllegalArgumentException.class, () -> new Reader().read(path));
    }

    @Test
    void readReturnsAddressBookWhenLineHasLeadingAndTrailingSpaces() {
        writeLine(path, "   \t   Bill McKnight,Male,16/03/77\t   ");

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries())
                .extracting("name", "gender", "dateOfBirth")
                .containsOnly(tuple("Bill McKnight", MALE, LocalDate.of(1977, 3, 16)));
    }

    @Test
    void readReturnsAddressBookWhenLineElementsHaveWhitespaces() {
        writeLine(path, "Bill McKnight    , Male  \t ,  \t16/03/77");

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries())
                .extracting("name", "gender", "dateOfBirth")
                .containsOnly(tuple("Bill McKnight", MALE, LocalDate.of(1977, MARCH, 16)));
    }

    @ParameterizedTest
    @CsvSource({
            "Male, MALE",
            "MALE, MALE",
            "Female, FEMALE",
            "female, FEMALE"})
    void readReturnsAddressBookWhenFileHasEntry(final String fileGender, final Gender addressBookEntryGender) {
        writeLine(path, format("Bill McKnight, %s, 16/03/77", fileGender));

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries())
                .extracting("name", "gender", "dateOfBirth")
                .containsOnly(tuple("Bill McKnight", addressBookEntryGender, LocalDate.of(1977, MARCH, 16)));
    }

    @Test
    void readReturnsAddressBookWhenFileEntryHasSpace() {
        writeLine(path, "Bill McKnight, Male , 16/03/12");
        writeLine(path, "Paul Robinson, Male, 15/01/85");
        writeLine(path, "Gemma Lane, Female, 20/11/91");
        writeLine(path, "Sarah Stone, Female, 20/09/80");
        writeLine(path, "Wes Jackson, Male, 14/08/74");

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries())
                .extracting("name", "gender", "dateOfBirth")
                .containsOnly(
                        tuple("Bill McKnight", MALE, LocalDate.of(2012, MARCH, 16)),
                        tuple("Paul Robinson", MALE, LocalDate.of(1985, JANUARY, 15)),
                        tuple("Gemma Lane", FEMALE, LocalDate.of(1991, NOVEMBER, 20)),
                        tuple("Sarah Stone", FEMALE, LocalDate.of(1980, SEPTEMBER, 20)),
                        tuple("Wes Jackson", MALE, LocalDate.of(1974, AUGUST, 14)));
    }

    private static void writeLine(Path path, String s) {
        try {
            Files.write(path, format("%s\n", s).getBytes(), APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
