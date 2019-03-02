# Feature Toggles

Driving Idea: Have a centralize toggle server with in-memory db and REST API for retrieving real-time toggle values.

Goals:
- server:
    - REST API for gets
    - provide consumable client jar
    - Have UI for toggle management (set, add, remove)
- Client Jar (consumed in client-service):
    - Provide a method such as `ToggleClient.isFeatureEnabled("my-toggle")` gets first available value:
        1. REST API (server value)
        2. `app.properties` on classpath of client-service
        3. default to false