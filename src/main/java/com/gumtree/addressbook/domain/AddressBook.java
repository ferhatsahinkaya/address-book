package com.gumtree.addressbook.domain;

import com.gumtree.addressbook.domain.AddressBookEntry.Gender;
import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Comparator.comparing;

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

    Optional<String> findOldest() {
        return entries.entrySet()
                .stream()
                .sorted(comparing(o -> o.getValue().dateOfBirth()))
                .findAny()
                .map(Map.Entry::getKey);
    }

    long ageDifference(String name1, String name2) {
        AddressBookEntry entry1 = Optional.ofNullable(entries.get(name1)).orElseThrow(IllegalArgumentException::new);
        AddressBookEntry entry2 = Optional.ofNullable(entries.get(name2)).orElseThrow(IllegalArgumentException::new);
        return DAYS.between(entry1.dateOfBirth(), entry2.dateOfBirth());
    }
}
