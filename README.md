
# Credit Card aggregator service

An aggregator of multiple credit card provider service, powered by [scala 3](https://docs.scala-lang.org/scala3/).

## Build Requirements.

* Scala 3 compiler.
* Java Virtual Machine, version 11 or greater.
* Scala Build Tool `sbt`
* Either one of the following IDE should be fine to navigate the codebase.
  * [VSCode with Metals plugin](https://scalameta.org/metals/docs/editors/vscode/)
  * [IntelliJ with Scala plugin](https://www.jetbrains.com/help/idea/discover-intellij-idea-for-scala.html)

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