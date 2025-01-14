# ChainTest [![CodeFactor](https://www.codefactor.io/repository/github/anshooarora/chaintest/badge)](https://www.codefactor.io/repository/github/anshooarora/chaintest) [![Maven Central](https://img.shields.io/maven-central/v/com.aventstack/chaintest-core.svg?maxAge=300)](http://search.maven.org/#search|ga|1|g:"com.aventstack")

A comprehensive reporting framework supporting multiple generators - static, email & realtime, historical analytics with ChainLP. 

## Generators

* Simple: [Dark](https://chaintestblob.blob.core.windows.net/chaintest/dark/Index.html) | [Light](https://chaintestblob.blob.core.windows.net/chaintest/light/Index.html)
* Email: [Sample](https://chaintestblob.blob.core.windows.net/chaintest/email/Email.html)
* ChainLP: https://chaintest.onrender.com/ 

## What is a ChainTest generator?

A generator is responsible for creating output files based on the test results and configuration settings. It processes templates, saves necessary resources, and generates reports in a specified format.

### How to enable generators?

ChainTest is still in active development, and only Java unit test frameworks are supported at present. Generators can be enabled via properties files located on classpath - for more information, see the supported plugins below.

### Supported Test Frameworks

* [junit5](https://github.com/anshooarora/chaintest/tree/main/plugins/chaintest-junit5)
* [cucumber-jvm](https://github.com/anshooarora/chaintest/tree/main/plugins/chaintest-cucumber-jvm)
* [testng](https://github.com/anshooarora/chaintest/tree/main/plugins/chaintest-testng)
* pytest (in progress)

## ChainLP

ChainLP (Chain-Long-Playing like [LP Record](https://en.wikipedia.org/wiki/LP_record)) is a Java (Spring) server which packs the Angular frontend and is distributed as a Docker image. ChainLP is the framework component providing historical analytics.

Docker image is available from https://hub.docker.com/r/anshooarora/chaintest.

The recommended way to run ChainLP is with docker-compose. Currently, the supported databases are listed below but most RDBMS database should work.

* H2
* MySQL
* PostgreSQL

For each database, there is a separate docker-compose.yml available at [chainlp/docker](https://github.com/anshooarora/chaintest/tree/main/chainlp/docker). H2 provides the most straight-forward way to test, but it is NOT recommended for production use.

```
# example
git clone https://github.com/anshooarora/chaintest.git
cd chaintest/chainlp/docker
docker compose -f docker-compose-h2.yml up
```

```
# h2
docker compose -f docker-compose-h2.yml up

# mysql
docker compose -f docker-compose-mysql.yml up

# posgres
docker compose -f docker-compose-postgres.yml up
```

### ChainLP config

Use the `host:port` of the ChainLP server, include it in [chaintest.properties](https://github.com/anshooarora/chaintest/blob/main/Config.md) along with the required value for `chaintest.project.name`. 

The `host:port` combination is where the client (via plugin) will connect to and communicate over TCP. For more information how communication is established, look into [ChainTestApiClient](https://github.com/anshooarora/chaintest/blob/main/core/chaintest-core-java/src/main/java/com/aventstack/chaintest/http/ChainTestApiClient.java).

```
# (only the relevant bits shows below)

# chaintest configuration
chaintest.project.name=default

# generators:
## chainlp
chaintest.generator.chainlp.enabled=true
chaintest.generator.chainlp.host.url=<host:port>
```

## Is Docker must to generate ChainTest Report?
No, you do not necessarily need Docker to generate and view static reports with ChainTest. Docker is a convenient way to deploy and run applications with all their dependencies in an isolated environment, but for generating static reports, it is not a strict requirement.

## When Docker is Needed for ChainTest?
In ChainTest's context, Docker is required only if setting up ChainLP given one or more of the below requirements:

* Comprehensive Dashboard: Ideal for generating historical analytics and consolidating multiple project reports in one place.
* Quick Setup: If you want to avoid manually setting up dependencies, Docker provides a pre-configured environment.
* Consistency: Ensures that the application runs the same way across different systems without dependency conflicts.
* Testing Complete Functionality: If you want to test the entire ChainTest environment with all its features (not just static report generation), Docker simplifies the setup.

## Contributing

Contributions are welcome. Please open an issue or submit a pull request.

## License

ChainTest is open-source software and licensed under Apache-2.0.
