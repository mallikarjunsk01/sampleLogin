Feature: Login

  Scenario: Successful login
    Given I am on the login page
    When I enter valid credentials
    Then I should see the home page

  Scenario: new item check in on page
    Given I am on the login page ui
    When I enter valid credentials and log in will be on page
    And im clicking on item which will add item
    Then I should be able to see that item added in cart

  #Scenario: Invalid login
    #Given I am on the login page
    #When I enter invalid credentials
    #Then I should see an error message
