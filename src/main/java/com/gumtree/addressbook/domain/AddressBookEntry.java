package com.gumtree.addressbook.domain;

import java.time.LocalDate;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;

public class AddressBookEntry {
    public enum Gender {
        MALE, FEMALE;
    }

    private final String name;
    private final Gender gender;
    private final LocalDate dateOfBirth;

    AddressBookEntry(final String name, final Gender gender, final LocalDate dateOfBirth) {
        notBlank(name);
        isTrue(nonNull(gender));
        isTrue(nonNull(dateOfBirth));

        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }
}
