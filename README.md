# Feature Toggles

Driving Idea: Have a centralized toggle server with in-memory db and REST API for retrieving real-time toggle values.

Goals:
- server:
    - REST API for toggle values
    - provide consumable client jar
    - Have UI for toggle management (set, add, remove)
- client jar (consumed in client-service):
    - Provide a method such as `ToggleClient.isFeatureEnabled("my-toggle")` that gets the first available value:
        1. REST API (server value)
        2. `application.properties` on classpath of _client-service_
        3. default to false