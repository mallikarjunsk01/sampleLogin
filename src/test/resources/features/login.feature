Feature: Login

  Scenario: Successful login
    Given I am on the login page
    When I enter valid credentials
    Then I should see the home page

  #Scenario: Invalid login
    #Given I am on the login page
    #When I enter invalid credentials
    #Then I should see an error message

  Scenario: Logout from profile
    Given I am on the login page
    When I enter valid credentials and log in
    And I open my profile menu
    Then I should be able to log out successfully