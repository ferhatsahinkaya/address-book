package com.gumtree.addressbook.domain;

import com.gumtree.addressbook.domain.AddressBookEntry.Gender;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AddressBook {

    private final Map<String, AddressBookEntry> entries = new HashMap<>();

    public Collection<AddressBookEntry> getEntries() {
        return entries.values();
    }

    public void add(final String name, final Gender gender, final LocalDate dateOfBirth) {
        entries.put(name, new AddressBookEntry(name, gender, dateOfBirth));
    }

    long count(Gender gender) {
        return entries.values()
                .stream()
                .filter(e -> e.gender() == gender)
                .count();
    }
}
