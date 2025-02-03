# ChainTest [![CodeFactor](https://www.codefactor.io/repository/github/anshooarora/chaintest/badge)](https://www.codefactor.io/repository/github/anshooarora/chaintest) [![Maven Central](https://img.shields.io/maven-central/v/com.aventstack/chaintest-core.svg?maxAge=300)](http://search.maven.org/#search|ga|1|g:"com.aventstack") [![Docker Image Version](https://img.shields.io/docker/v/anshooarora/chaintest?style=flat&label=ChainLP&link=https%3A%2F%2Fhub.docker.com%2Fr%2Fanshooarora%2Fchaintest)](https://hub.docker.com/r/anshooarora/chaintest)

A comprehensive reporting framework supporting multiple generators - static, email & realtime, historical analytics with ChainLP. 

## Generators

* Simple: [Dark](https://chaintest.s3.us-east-2.amazonaws.com/samples/SimpleDark.html) | [Light](https://chaintest.s3.us-east-2.amazonaws.com/samples/SimpleLight.html)
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

Refer the [support matrix](ChainLPSupportMatrix.md) for support `chaintest-core` and `chainlp` versions.

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

### Screenshots/Embeds

The chaintest-core client is the framework component that supports plugins to store embeds for each report. For example, with SimpleGenerator, the client saves all embeds relative to the report file in the `resources` folder.

Storage support will be available with S3 start version `1.0.7` of plugins and `0.0.5` of ChainLP. At present, screenshots/embeds are not available with ChainLP

For embeds to work with ChainLP, the client requires the following to be enabled:

```
# storage
chaintest.storage.service.enabled=true
# [azure-blob, aws-s3]
chaintest.storage.service=azure-blob
# s3 bucket or azure container name
chaintest.storage.service.container-name=
```

* Storage support will be released in `1.0.7` and will initially be rolled out for `aws-s3` only
  * Storage parameters need to be configured on both client-side and on ChainLP
  * The client and ChainLP both use the [AWS Credential Chain](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials-chain.html) to authenticate against the bucket and store/access blob data
  * For ChainLP, the secrets can be configured via `<host>/settings` by clicking the `Secrets` tab which will set these parameters as System properties and ChainLP will create a presigned URL for each embed
* Future support will be available for Azure Blob and storing embed data directly in the database (which means, blob storage will not be required, more on this later)

## Is Docker required for all ChainTest reports?
* Docker is not a requirement for any of the static reports (SimpleGenerator, EmailGenerator)
* Docker is required to host ChainLP as it is only available as a Docker image

## When is Docker required for ChainTest?
In ChainTest's context, Docker is required only if setting up ChainLP given one or more of the below requirements:

* Comprehensive Dashboard: Ideal for generating historical analytics and consolidating multiple project reports in one place
* Quick Setup: If you want to avoid manually setting up dependencies, Docker provides a pre-configured environment

## Contributing

Contributions are welcome. Please open an issue or submit a pull request.

## License

ChainTest is open-source software and licensed under Apache-2.0.
