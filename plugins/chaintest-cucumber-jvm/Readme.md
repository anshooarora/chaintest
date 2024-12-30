# ChainTest Cucumber-JVM Plugin

ChainTest plugin for [Cucumber-JVM](https://cucumber.io/docs/installation/java/).  Visit [chaintest-cucumber-jvm-example](https://github.com/anshooarora/chaintest/tree/main/examples/chaintest-cucumber-jvm-example) for example usage.

## Setup

1. Add [chaintest.properties](https://github.com/anshooarora/chaintest/blob/main/Config.md) to your classpath, example: `src/test/resources/chaintest.properties`
2. Add `com.aventstack.chaintest.plugins.ChainTestCucumberListener:` to the runner class

```java
@RunWith(Cucumber.class)
@CucumberOptions(plugin = { 
  "com.aventstack.chaintest.plugins.ChainTestCucumberListener:" 
})
public class RunCukes {
}

```

## License

Cucumber-JVM plugin for ChainTest is open-source software and licensed under Apache-2.0.
