@calculator
Feature: Basic Calculator Operations
  As a user
  I want to perform basic arithmetic operations
  So that I can calculate results

  Scenario: Addition
    Given the first number is 5
    And the second number is 3
    When I add the numbers
    Then the result should be 8

  Scenario: Subtraction
    Given the first number is 10
    And the second number is 4
    When I subtract the numbers
    Then the result should be 6

  Scenario: Multiplication
    Given the first number is 7
    And the second number is 6
    When I multiply the numbers
    Then the result should be 42

  Scenario: Division
    Given the first number is 20
    And the second number is 4
    When I divide the numbers
    Then the result should be 5
