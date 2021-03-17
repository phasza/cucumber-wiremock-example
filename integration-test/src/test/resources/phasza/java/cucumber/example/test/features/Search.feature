@search
Feature: Search command
  The application is able to search packages on a maven repository

  Background:
    Given an installation of the application
    And a maven server is available
    And the following artifacts are uploaded to the maven server
      | com.pot.java | gigen    | 1.2.3.4   |
      | com.google   | guice    | 1.0-alpha |
      | iron.rtf     | captain  | 1.0       |
      | no.men       | est-omen | 6.0       |

  Scenario: Searching packages on the maven repository
    When I run "mvn-query search com"
    Then the command returns with success
    And the following lines are included in the standard output
      | com.google:guice:1.0-alpha |
      | com.pot.java:gigen:1.2.3.4 |

  Scenario: Limit number of diplayed results for search
    When I run "mvn-query search com -h=1"
    Then the command returns with success
    And the following lines are included in the standard output
      | com.google:guice:1.0-alpha |

  @negative
  Scenario: No matching packages for search term
    When I run "mvn-query search not_existing"
    Then the command returns with error
    And the following lines are included in the standard error
      | No matching artifacts could be found! |

  @negative
  Scenario: Cannot reach the maven repository
    Given the maven server is down
    When I run "mvn-query search com"
    Then the command returns with error
    And the following lines are included in the standard error
      | Repository cannot be reached! |