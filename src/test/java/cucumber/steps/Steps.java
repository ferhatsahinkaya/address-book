package cucumber.steps;

import com.gumtree.addressbook.domain.AddressBook;
import com.gumtree.addressbook.domain.AddressBookEntry.Gender;
import com.gumtree.addressbook.service.Reader;
import cucumber.api.java8.En;
import org.apache.commons.lang3.EnumUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.assertj.core.api.Assertions.assertThat;

public class Steps implements En {

    private AddressBook addressBook;
    private Path path;

    public Steps() {
        Given("an address book", () ->
                path = Files.createTempFile("addressBook", ""));

        And("address book has entry {string} with gender {string} and date of birth {string}", (String name, String gender, String dateOfBirth) ->
                Files.write(path, String.format("%s, %s, %s\n", name, gender, dateOfBirth).getBytes(), StandardOpenOption.APPEND));

        When("address book is queried", () ->
                addressBook = new Reader().read(path));

        Then("number of {word}s is {long}", (String gender, Long count) ->
                assertThat(addressBook.count(EnumUtils.getEnumIgnoreCase(Gender.class, gender))).isEqualTo(count));

        Then("oldest is {string}", (String name) ->
                assertThat(addressBook.findOldest()).isPresent().hasValue(name));

        Then("age difference between {string} and {string} is {long} days", (String name1, String name2, Long ageDiff) ->
                assertThat(addressBook.ageDifference(name1, name2)).isEqualTo(ageDiff));
    }

}
