package com.gumtree.addressbook.service;

import com.gumtree.addressbook.domain.AddressBook;
import com.gumtree.addressbook.domain.AddressBookEntry.Gender;
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

import static com.gumtree.addressbook.domain.AddressBookEntry.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReaderTest {


    @TempDir
    static Path tempDir;

    @Test
    void readThrowsExceptionWhenFileDoesNotExist() {
        assertThrows(RuntimeException.class, () -> new Reader().read(Paths.get("unknown")));
    }


    @Test
    void readReturnsEmptyAddressBookWhenFileIsEmpty() throws IOException {
        Path path = Files.createTempFile(tempDir, "", "");

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries()).isEmpty();
    }

    @Test
    void readReturnsEmptyAddressBookWhenFileHasOnlyNewLine() throws IOException {
        Path path = Files.createTempFile(tempDir, "", "");
        Files.write(path, "\n".getBytes());

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries()).isEmpty();
    }

    @Test
    void readReturnsEmptyAddressBookWhenFileHasBlankLine() throws IOException {
        Path path = Files.createTempFile(tempDir, "", "");
        Files.write(path, "  \t\n".getBytes());

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries()).isEmpty();
    }

    @Test
    void readReturnsEmptyAddressBookWhenLineHas2Elements() throws IOException {
        Path path = Files.createTempFile(tempDir, "", "");
        Files.write(path, "Bill McKnight, Male\n".getBytes());

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries()).isEmpty();
    }

    @Test
    void readReturnsEmptyAddressBookWhenLineHas4Elements() throws IOException {
        Path path = Files.createTempFile(tempDir, "", "");
        Files.write(path, "Bill McKnight, Male, 16/03/77, address\n".getBytes());

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries()).isEmpty();
    }

    @Test
    void readThrowsDateTimeParseExceptionWhenLineIsInvalid() throws IOException {
        Path path = Files.createTempFile(tempDir, "", "");
        Files.write(path, "Bill McKnight, Male, 16/13/77".getBytes());

        assertThrows(DateTimeParseException.class, () -> new Reader().read(path));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Bill McKnight, , 16/03/77",
            ", Male, 16/03/77",
            "Bill McKnight, UNKNOWN, 16/03/77"})
    void readThrowsIllegalArgumentExceptionWhenLineIsInvalid(final String line) throws IOException {
        Path path = Files.createTempFile(tempDir, "", "");
        Files.write(path, line.getBytes());

        assertThrows(IllegalArgumentException.class, () -> new Reader().read(path));
    }

    @Test
    void readReturnsAddressBookWhenLineHasLeadingAndTrailingSpaces() throws IOException {
        Path path = Files.createTempFile(tempDir, "", "");
        Files.write(path, "   \t   Bill McKnight,Male,16/03/77\t   \n".getBytes());

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries())
                .extracting("name", "gender", "dateOfBirth")
                .containsOnly(tuple("Bill McKnight", MALE, LocalDate.of(1977, 3, 16)));
    }

    @Test
    void readReturnsAddressBookWhenLineElementsHaveWhitespaces() throws IOException {
        Path path = Files.createTempFile(tempDir, "", "");
        Files.write(path, "Bill McKnight    , Male  \t ,  \t16/03/77\n".getBytes());

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries())
                .extracting("name", "gender", "dateOfBirth")
                .containsOnly(tuple("Bill McKnight", MALE, LocalDate.of(1977, 3, 16)));
    }

    @ParameterizedTest
    @CsvSource({
            "Male, MALE",
            "MALE, MALE",
            "Female, FEMALE",
            "female, FEMALE"})
    void readReturnsAddressBookWhenFileHasEntry(final String fileGender, final String addressBookEntryGender) throws IOException {
        Path path = Files.createTempFile(tempDir, "", "");
        Files.write(path, String.format("Bill McKnight, %s, 16/03/77\n", fileGender).getBytes());

        AddressBook actual = new Reader().read(path);

        assertThat(actual.getEntries())
                .extracting("name", "gender", "dateOfBirth")
                .containsOnly(tuple("Bill McKnight", Gender.valueOf(addressBookEntryGender), LocalDate.of(1977, 3, 16)));
    }
}
