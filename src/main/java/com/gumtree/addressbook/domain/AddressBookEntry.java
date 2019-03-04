package com.gumtree.addressbook.domain;

import java.time.LocalDate;

public class AddressBookEntry {
    public enum Gender {
        MALE, FEMALE;
    }

    private final String name;
    private final Gender gender;
    private final LocalDate dateOfBirth;

    AddressBookEntry(final String name, final Gender gender, final LocalDate dateOfBirth) {
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }
}
