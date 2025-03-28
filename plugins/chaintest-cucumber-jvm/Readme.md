# ChainTest Cucumber-JVM Plugin

ChainTest plugin for [Cucumber-JVM](https://cucumber.io/docs/installation/java/).  Visit [chaintest-cucumber-jvm-example](https://github.com/anshooarora/chaintest/tree/main/examples/chaintest-cucumber-jvm-example) for example usage.

## Setup

1. Add plugin dependency:

    ```xml
    <dependency>
      <groupId>com.aventstack</groupId>
      <artifactId>chaintest-cucumber-jvm</artifactId>
      <version>${chaintest.cucumberjvm.version}</version>
      <scope>provided</scope>
    </dependency>
    ```

    ```json
    implementation 'com.aventstack:chaintest-cucumber-jvm:$version'
    ```

    Note: Do not include the `chaintest-core` dependency in your project, it will automatically be included via plugin dependency.

1. Add [chaintest.properties](https://github.com/anshooarora/chaintest/blob/main/Config.md) to your classpath, example: `src/test/resources/chaintest.properties`

1. Add `com.aventstack.chaintest.plugins.ChainTestCucumberListener:` to the runner class

    ```java
    @RunWith(Cucumber.class)
    @CucumberOptions(plugin = { 
      "com.aventstack.chaintest.plugins.ChainTestCucumberListener:" 
    })
    public class RunCukes {
    }
    ```

#### Attaching Screenshots/Logs to Step instead of Scenarios

Available via config start `1.0.12` from #77.

It will now be possible to revert the default behavior where all embeds/logs are attached to Scenario. Instead, with this property enabled, the last executed Step is used for reference:

```
chaintest.plugins.cucumber.attach-to-step
```

The above can be supplied as a System Property or Environment variable. Example:

```
mvn clean test -Dchaintest.plugins.cucumber.attach-to-step=true

// or
System.setProperty("chaintest.plugins.cucumber.attach-to-step", "true");
```

## License

Cucumber-JVM plugin for ChainTest is open-source software and licensed under Apache-2.0.
