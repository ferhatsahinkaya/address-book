package com.gumtree.addressbook.service;

import com.gumtree.addressbook.domain.AddressBook;
import com.gumtree.addressbook.domain.AddressBookEntry;
import com.gumtree.addressbook.domain.AddressBookEntry.Gender;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import static org.apache.commons.lang3.EnumUtils.getEnumIgnoreCase;

class Reader {

    private static final String SEPARATOR = "\\s*,\\s*";
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendPattern("dd/MM/")
            .appendValueReduced(ChronoField.YEAR, 2, 2, Year.now().getValue() - 80)
            .toFormatter();

    AddressBook read(Path path) {

        AddressBook addressBook = new AddressBook();
        try {
            Files.readAllLines(path)
                    .stream()
                    .map(StringUtils::trim)
                    .filter(StringUtils::isNotBlank)
                    .map(line -> line.split(SEPARATOR))
                    .filter(split -> split.length == 3)
                    .forEach(split -> addressBook.add(split[0], getEnumIgnoreCase(Gender.class, split[1]), LocalDate.parse(split[2], FORMATTER)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return addressBook;
    }
}
