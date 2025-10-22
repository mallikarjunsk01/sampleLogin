
Feature: Login

  Scenario: Successful login
    Given I am on the login page
    When I enter valid credentials
    Then I should see the home page

  #Scenario: Invalid login
    #Given I am on the login page
    #When I enter invalid credentials
    #Then I should see an error message

  Scenario: Updating Login flow for UI page with credentials
    Given I am on the login page ui were valid credetntials required
    When I enter valid credentials log in will be success
    And I open my profile menu and check if im login successfully or not
    Then I should be able to log out successfully from login page ui successfully