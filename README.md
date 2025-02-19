# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## Phase 2 URL for Sequence Diagram

https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDrco67F8H5LCBALnAWspqig5QIAePKwvuh6ouisTYgmhgumGbpkhSBq0uWo7vvorxLCaRLhhaHLcryBqCsKMCvmIxidq6LLlCxpELEsoZUW6nZYVxxFvjAYHvI6zoErhnEwJ63pBrO6BESOoyUUygmRhyMDRrpQbxnK2HJlBJYoTy2a5pg-4gjBJRXAMoljhOXzTsp87jq2fTfletmFNkPYwP2g69I5anOV5NZKY2zaeYuZicKu3h+IEXgoOge4Hr4zDHukmSYAFF5FNQ17SAAoruZX1GVzQtA+qhPt0bkxWg7a-mcQIltO0BIAAXvEiTlAAPM1c75NZpntcJMAIfY2XIVlvpoRimFGRqsmkjA5JgO5AbRXOGlmhGhSWnRtruYxYSQHOmg6OxG3ujAo3oPxmnSkml6weUz2tVh60CXJHAoNwmS7T9h3Udp5TSMDFKGD9UmJiZnVpih2WWQgeaTUJvlXD5JV+V2ORgH2A5DkuiWeMlG6Qrau7QjAADio6srlp4FeezB2dejNVbV9ijk1+0vT+bI2d9QY9f1BCDTAI3C2g402Wy0307EzOjKosLIOrLPLRhiPYTJAObdtYMKxDWknbRPLnS1l0wNdL1sR9MgPRLcbaK9R0419T0K7MLGG-9b2mxSADqeUoBraiqUB6ne5D1v4cwMesskLTMR0T3p1Ht33Sbj1q2AQ1wKkLIx+NHHmq7qvQqX5fqJXwc4YXKcAEIIKADZp3H9KJ1b7Ip0zLMwBnWcdvoQpdyADb5671dF-XZcV6OVcPb7Trynuy+N6ozd-a74s77rmsY1jKObwBUwC5r4yVP0t8oAAktI98AIy9gAzAALE8J6ZANBWCYMxuhTB0DPBsQDgIgKeE-AAcqOEBewYDNHxscV2hUSZBTJr0G+LN74VEfqOV+H9v5-ymAA-UTkVigKSH0CB3doGjFoXA0ciDRjINQTACmK4qbrkCNgHwUBsDcHgLqTII9RgpDzlglWuNKi1AaPzQWwQFZDgQaOdBHZkapg9qkKWA00DDR+vkDR7CtETUvnZcE4ivSZBjrCOAEiUD6yxC3Y2odHpmxantOsLVLbvWTmdGAF1tBCiui1eercvH6IdIvTetiEaH08Udb6NpHGaITgkqGukbQwDQCgZIUjWJ3QXu7LaJDpAD3ejY7eT9X4t10cCcozj7GuLUFZZWtcFE9HwaMUh5RP6-x4W1TsWDSYhT6X0Bpb8hnkNGcuJKAiAiWGBghYpAApCAPISmBEYbPDmxN5EE3KNUSkd4WhPyFv4ucQ5RHADWVAOAEAEJQFmLM7R7Vj7dSgH1IxJiFZmN6A8p5Ly3kfKqZBaxn0t5wRgNsnkjjEVoDcatWCIc0lbQpObW56BAk12CbbUJ9twlMSdq1F2MSsXTniRvHpfsfqBych4t2bdsVgEyVUglx0h4wBCTHB2szompPDGSUcdLC6JPqRKr2KS2WxJgEgAAZuJLuYLXnQCVayEIvxdDcFKN0PwWgHGjlhKCyg4LoDMvCjymi5Q9IWtgJAEpTS-yTXKCi8+VjUxXzTD0L54zOaTPJglPha4UoBC8I8rsXpYDAGwKIwgRiZHszkdzNMFRyqVWqrVYwYzmkliBiDMQ3TYW2JANwPATiq1ZhQGiFarLF7lErXGg0Ws7W5MiMMCANAnrKjtB2CAOgABWKBwAaHlc2+StbYypA7TUwlfLu29vhgOud7kYDDrHROptFTW14F7p25O6Ye19uAAOmOW7R3jrAJOtaR8PWxrwN67pGa8YFtOBMnBUzeGYCAA

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
