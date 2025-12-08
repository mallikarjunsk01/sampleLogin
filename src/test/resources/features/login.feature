Feature: Login

  Scenario: Successful login
    Given I am on the login page
    When I enter valid credentials
    Then I should see the home page

  Scenario: CartIn on page
    Given I am on the login page ui
    When I enter valid credentials and log in will be on page
    And I open my profile menu and check if im login not
    Then im clicking on cartIn option
    Then I should be able to add the iteam to cart successfully

  #Scenario: Invalid login
    #Given I am on the login page
    #When I enter invalid credentials
    #Then I should see an error message
