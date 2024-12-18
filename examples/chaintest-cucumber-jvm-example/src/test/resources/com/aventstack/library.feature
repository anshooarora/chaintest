@library
Feature: Library Book Management
  As a librarian
  I want to manage books in the library
  So that I can keep track of available books

  Scenario: Add a new book
    Given the book title is "The Great Gatsby"
    And the author is "F. Scott Fitzgerald"
    When I add the book to the library
    Then the book should be available in the library

  Scenario: Borrow a book
    Given the book title is "1984"
    And the author is "George Orwell"
    And the book is available in the library
    When I borrow the book
    Then the book should not be available in the library

  Scenario: Return a book
    Given the book title is "To Kill a Mockingbird"
    And the author is "Harper Lee"
    And the book is not available in the library
    When I return the book
    Then the book should be available in the library