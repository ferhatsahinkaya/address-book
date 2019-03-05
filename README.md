# Build
- `./gradlew clean build` runs a clean build and runs all tests

# Assumptions

* Name is assumed to be unique in the address book and used as the key for the map.
* If a line contains less than or greater than 3 elements - line is ignored silently.
* Gender is processed in a case insensitive way.
* As given date format is `dd/MM/yy`, with 2 digit year, date of birth is processed the same way SimpleDateFormat would process - rather than Java 8 DateTimeFormatter, which defaults to 2000s. For e.g. `16/02/77` is converted to `16 February 1977`. Personally I would prefer 4 digit year for date of birth to avoid confusion.
* When multiple entries have same date of birth and they are the oldest, `AddressBook.#findOldest` returns one of them in a random fashion.
* `AddressBook.#ageDifference` returns the number of days first entry is older than the second. If second entry is older, the result is a negative value to explicitly indicate which entry is older.