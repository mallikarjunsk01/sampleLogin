Feature: Login

  Scenario: Successful login
    Given I am on the login page
    When Green colour should come while I enter valid credentials.
    Then I should see the home page

  Scenario: Cart on page
    Given I am on the login page ui
    When I enter valid credentials and log in will be on page
    And I open my profile menu and check if im login not
    Then im clicking on cart option
    Then I should be able to log out successfully from login page ui

  #Scenario: Invalid login
    #Given I am on the login page
    #When I enter invalid credentials
    #Then I should see an error message
