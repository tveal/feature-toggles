# Feature Toggles

Driving Idea: Have a centralized toggle server with in-memory db and REST API for retrieving real-time toggle values.

Why? The pursuit (and necessity) of [Trunk Based Development](https://trunkbaseddevelopment.com/). See [Feature Flags](https://trunkbaseddevelopment.com/feature-flags/), especially [Runtime Switchable](https://trunkbaseddevelopment.com/feature-flags/#runtime-switchable).

Goals:
- server:
    - REST API for toggle values
    - provide consumable client jar
    - Have UI for toggle management (set, add, remove)
    - Have in-memory and persistent toggle storage for server
    - SSE impl for toggle changes
- client jar (consumed in client-service):
    - Provide a method such as `ToggleClient.isFeatureEnabled("my-toggle")` that gets the first available value:
        1. server value
        2. `application.properties` on classpath of _client-service_
        3. default to false

---

## Build Things

### toggle-server and api's

The `toggle-server` project contains the `api-<lang>-<protocol>` subprojects, collectively producing:
- build/libs/toggle-server-${version}.war
- api-java-rest/build/libs/api-java-rest-${version}.jar
- api-java-sse/build/libs/api-java-sse-${version}.jar
- ...

```bash
# from toggle-server directory
./gradlew clean build
```

### sample-client-service

```bash
# from sample-client-service directory
./gradlew clean build
```

---

## Run Things Locally

### Option 1

Fast dev workflow; Run Server, UI, and Sample Client separately

#### Server

```bash
# from toggle-server directory
./gradlew bootRun
```

Request toggle value with a url like:

http://localhost:8090/toggle-server/get-toggle?toggleId=my-feature

#### UI

Allows file-watching for ui changes; No rebuilds needed.

```bash
# from toggle-server/ui directory
npm start
```

On startup, this should automatically open the ui at http://localhost:3000/

#### Sample Client Service

```bash
# from sample-client-service directory
./gradlew bootRun
```

Visit the following url:

http://localhost:8900/sample-client-service

### Option 2

Slower startup; Run Server/UI as one deployable war.
Currently, `cargoRunLocal` gradle task doesn't show the slf4j logs.
Haven't gotten around to configuring log properties.

#### Server + UI

```bash
# from toggle-server directory
./gradle cargoRunLocal -i
```

Visit the following url:

http://localhost:8090/toggle-server/
