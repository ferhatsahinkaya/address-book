Feature: Address Book

  Background: Address book file has entries
    Given an address book
    And address book has entry "Bill McKnight" with gender "Male" and date of birth "16/03/77"
    And address book has entry "Paul Robinson" with gender "Male" and date of birth "15/01/85"
    And address book has entry "Gemma Lane" with gender "Female" and date of birth "20/11/91"
    And address book has entry "Sarah Stone" with gender "Female" and date of birth "20/09/80"
    And address book has entry "Wes Jackson" with gender "Male" and date of birth "14/08/74"

  Scenario: Count number of males in address book
    When address book is queried
    Then number of males is 3

  Scenario: Find oldest in address book
    When address book is queried
    And oldest is "Wes Jackson"

  Scenario: Find age difference of two entries in address book
    When address book is queried
    And age difference between "Bill McKnight" and "Paul Robinson" is 2862 days