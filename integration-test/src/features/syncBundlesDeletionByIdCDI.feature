Feature: SyncBundlesDeletionByIdCDI

  Background: 
    Given the configuration "cdis.json"

  Scenario: Delete Bundles by IdCDI
    Given the URL to delete bundles by idCDI     
    When the client call the DELETE API
    Then check statusCode is 200