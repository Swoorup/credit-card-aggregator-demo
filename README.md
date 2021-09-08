# Credit Card aggregator service

An aggregator of multiple credit card provider service, powered by [scala 3](https://docs.scala-lang.org/scala3/).

## Build Requirements.

-   Scala 3 compiler.
-   Java Virtual Machine, version 11 or greater.
-   Scala Build Tool `sbt`
-   Either one of the following IDE should be fine to navigate the codebase.
    -   [VSCode with Metals plugin](https://scalameta.org/metals/docs/editors/vscode/)
    -   [IntelliJ with Scala plugin](https://www.jetbrains.com/help/idea/discover-intellij-idea-for-scala.html)

## Building

This project uses [Sbt-native-packager](https://github.com/sbt/sbt-native-packager).
To build native binary run the following sbt command:

```shell
sbt stage
```

This will generate a local executable binary in `./target/universal/stage/bin/credit-card-aggregator`.
For other platforms and different formats, see also [Creating package](https://www.scala-sbt.org/sbt-native-packager/gettingstarted.html#create-a-package)

## Running

Once you have built the binaries locally, You can launch the app using the following command.

For help:

```shell
./target/universal/stage/bin/credit-card-aggregator --help
```

To run the aggregator service, run the following command:

```shell
./target/universal/stage/bin/credit-card-aggregator \
  -p 9090 \
  -c "https://app.clearscore.com/api/global/backend-tech-test/" \
  -s "https://app.clearscore.com/api/global/backend-tech-test/"
```

Or via `start.sh`. For example:

```shell
export HTTP_PORT=9090
export CSCARDS_ENDPOINT="https://app.clearscore.com/api/global/backend-tech-test/"
export SCOREDCARDS_ENDPOINT="https://app.clearscore.com/api/global/backend-tech-test/"
./start.sh
```

Note: To shutdown, either press enter or `Ctrl-C` to halt.

## Test

Test can be run with the command: `sbt test`

## Design

I have chosen tapir as so endpoints can be desribed in a declarative way. i.e the credit card endpoint is described as a ServerEndpoint i.e `ServerEndpoint[CreditCardRequest, ErrorResponse, CreditCardResponse, Any, F]` in `CreditCardEndpoint.scala`.

This makes the backend agnostic and easy to test endpoint/group of endpoints in an isolated way.

### Folder Structure

```shell
.
├── README.md
├── build.sbt
├── project
│   ├── build.properties
│   └── plugins.sbt
├── purge-local-build-cache.sh
├── src
│   ├── main
│   │   └── scala
│   │       ├── Main.scala
│   │       ├── app
│   │       │   ├── App.scala
│   │       │   └── Server.scala
│   │       ├── common
│   │       │   └── Scale.scala
│   │       ├── endpoints
│   │       │   ├── CreditCardEndpoint.scala
│   │       │   └── RootEndpoint.scala
│   │       ├── infrastructure
│   │       │   ├── config
│   │       │   │   └── CardProviderConfig.scala
│   │       │   ├── cscards
│   │       │   │   ├── DefaultCsCardProviderConfig.scala
│   │       │   │   └── client
│   │       │   │       ├── CsCardRequest.scala
│   │       │   │       ├── CsCardResponse.scala
│   │       │   │       └── CsCardsClient.scala
│   │       │   └── scoredcards
│   │       │       ├── DefaultScoredCardsProviderConfig.scala
│   │       │       └── client
│   │       │           ├── ScoredCardResponse.scala
│   │       │           ├── ScoredCardsClient.scala
│   │       │           └── ScoredCardsRequest.scala
│   │       ├── model
│   │       │   ├── CreditCard.scala
│   │       │   ├── User.scala
│   │       │   ├── request
│   │       │   │   └── CreditCardRequest.scala
│   │       │   └── response
│   │       │       ├── CreditCardResponse.scala
│   │       │       └── ErrorResponse.scala
│   │       ├── routes
│   │       │   ├── CreditCardRoutes.scala
│   │       │   └── Routes.scala
│   │       ├── service
│   │       │   ├── CreditCardAggregatorService.scala
│   │       │   └── CreditCardPartnerApi.scala
│   │       └── worksheets
│   │           └── DumpDocs.worksheet.sc
│   └── test
│       └── scala
│           ├── app
│           │   └── CreditCardAppIntegrationTest.scala
│           ├── common
│           │   └── ScaleSuite.scala
│           ├── endpoints
│           │   └── CreditCardEndpointsSuite.scala
│           ├── integration
│           │   └── package.scala
│           ├── mocks
│           │   └── package.scala
│           └── service
│               └── CreditCardAggregatorServiceSuite.scala
└── start.sh
```

### Filder/Folder Purpose

| File/Folder                                   | Purpose                                                              |
| --------------------------------------------- | -------------------------------------------------------------------- |
| main/scala/endpoints/CreditCardEndpoint.scala | Definition of `/creditcards` endpoint with business logic            |
| main/scala/routes                             | Compose all endpoints to routes                                      |
| main/scala/model                              | Models schema and validation logic of server logic                   |
| main/scala/service                            | Domain services i.e `CreditCardAggregatorService` used by the server |
| main/scala/infrastructure                     | Implementations of `CreditCardPartnerApi` interface                  |
| main/scala/app                                | Composition of services and routes into an HttpApp                   |