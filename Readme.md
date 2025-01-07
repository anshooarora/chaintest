# ChainTest [![CodeFactor](https://www.codefactor.io/repository/github/anshooarora/chaintest/badge)](https://www.codefactor.io/repository/github/anshooarora/chaintest)

A comprehensive reporting framework supporting multiple generators - static, email & realtime, historical analytics with ChainLP. 

Note: ChainTest is still in active development with no artifacts currently in release.

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

## Contributing

Contributions are welcome. Please open an issue or submit a pull request.

## License

ChainTest is open-source software and licensed under Apache-2.0.
