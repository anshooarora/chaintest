# ChainTest [![CodeFactor](https://www.codefactor.io/repository/github/anshooarora/chaintest/badge)](https://www.codefactor.io/repository/github/anshooarora/chaintest)

An open-source reporting framework with multiple generators creating static reports and a reporting server (ChainLP) for long-playing analytics.

Note: ChainTest is still in active development with no artifacts currently in release.

### Generators

* SimpleGenerator: [Light](https://chaintestblob.blob.core.windows.net/chaintest/light/Index.html) | [Dark](https://chaintestblob.blob.core.windows.net/chaintest/dark/Index.html)
* EmailGenerator: [Sample](https://chaintestblob.blob.core.windows.net/chaintest/email/Email.html)
* ChainLP: https://chaintest.onrender.com/ 

### What is a ChainTest generator?

A generator is responsible for creating output files based on the test results and configuration settings. It processes templates, saves necessary resources, and generates reports in a specified format.

#### Supported Test Frameworks

* [junit-jupiter](https://github.com/anshooarora/chaintest/tree/main/plugins/chaintest-junit-jupiter)
* [cucumber-jvm](https://github.com/anshooarora/chaintest/tree/main/plugins/chaintest-cucumber-jvm)
* [testng](https://github.com/anshooarora/chaintest/tree/main/plugins/chaintest-testng)
* pytest (in progress)

### Contributing

Contributions are welcome. Please open an issue or submit a pull request.

### License

ChainTest is open-source software and licensed under Apache-2.0.
