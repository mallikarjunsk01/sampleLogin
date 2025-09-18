
Feature: Login

  Scenario: Successful login
    Given I am on the login page
    When I enter valid credentials
    Then I should see the home page

  #Scenario: Invalid login
    #Given I am on the login page
    #When I enter invalid credentials
    #Then I should see an error message
