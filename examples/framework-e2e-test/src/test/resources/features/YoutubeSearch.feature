Feature: Youtube Sample Feature

  As a User,
  I want to search, pause and replay youtube video,
  So that i can enjoy the music

  Background:
    Given User opened youtube

  @search
  Scenario Outline: Search Youtube
    When User search "<search_query>" and select video number "<number>"
    Then The video was uploaded by "<uploader>"
    Examples:
      | search_query     | number | uploader    |
      | don't cry for me | 1      | Madonna     |
      | hey jude         | 1      | The Beatles |

  @togglePlay
  Scenario: Play and pause Youtube video
    When User search "Don't cry for me argentina" and select video number "1"
    Then Video is in "play" mode
    And User "pause" the video
    Then Video is in "pause" mode
    And User "play" the video
    Then Video is in "play" mode
