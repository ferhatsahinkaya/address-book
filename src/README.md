**Assumptions**

* Name is assumed to be unique in the address book and used as the key for the map.
* Gender is processed in a case insensitive way.
* As given date format is dd/MM/yy, with 2 digit year, date of birth is processed the same way SimpleDateFormat would process - rather than Java 8 DateTimeFormatter, which defaults to 2000s. Personally I would prefer 4 digit year for date of birth so that it will be self descriptive.
* If a line contains less than or greater than 3 elements - line is ignored silently.