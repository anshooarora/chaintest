package com.aventstack;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LibrarySteps {

    @Given("the book title is {string}")
    public void theBookTitleIs(String title) {
        System.out.println("Book title: " + title);
    }

    @Given("the author is {string}")
    public void theAuthorIs(String author) {
        System.out.println("Author: " + author);
    }

    @When("I add the book to the library")
    public void iAddTheBookToTheLibrary() {
        System.out.println("Book added to the library");
    }

    @Then("the book should be available in the library")
    public void theBookShouldBeAvailableInTheLibrary() {
        System.out.println("Book is available in the library");
    }

    @Given("the book is available in the library")
    public void theBookIsAvailableInTheLibrary() {
        System.out.println("Book is available in the library");
    }

    @When("I borrow the book")
    public void iBorrowTheBook() {
        System.out.println("Book borrowed from the library");
    }

    @Then("the book should not be available in the library")
    public void theBookShouldNotBeAvailableInTheLibrary() {
        System.out.println("Book is not available in the library");
    }

    @Given("the book is not available in the library")
    public void theBookIsNotAvailableInTheLibrary() {
        System.out.println("Book is not available in the library");
    }

    @When("I return the book")
    public void iReturnTheBook() {
        System.out.println("Book returned to the library");
    }

}
