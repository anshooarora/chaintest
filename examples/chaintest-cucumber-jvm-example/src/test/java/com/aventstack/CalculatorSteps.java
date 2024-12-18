package com.aventstack;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertEquals;

public class CalculatorSteps {

    private int firstNumber;
    private int secondNumber;
    private int result;

    @Given("the first number is {int}")
    public void theFirstNumberIs(int number) {
        firstNumber = number;
    }

    @Given("the second number is {int}")
    public void theSecondNumberIs(int number) {
        secondNumber = number;
    }

    @When("I add the numbers")
    public void iAddTheNumbers() {
        result = firstNumber + secondNumber;
    }

    @When("I subtract the numbers")
    public void iSubtractTheNumbers() {
        result = firstNumber - secondNumber;
    }

    @When("I multiply the numbers")
    public void iMultiplyTheNumbers() {
        result = firstNumber * secondNumber;
    }

    @When("I divide the numbers")
    public void iDivideTheNumbers() {
        result = firstNumber / secondNumber;
    }

    @Then("the result should be {int}")
    public void theResultShouldBe(int expectedResult) {
        assertEquals(expectedResult, result);
    }

}
