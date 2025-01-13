# ChainTest JUnit 5 Plugin

ChainTest plugin for [junit 5](https://junit.org/junit5/).  Visit [chaintest-junit5-example](https://github.com/anshooarora/chaintest/tree/main/examples/chaintest-junit5-example) for example usage.

## Setup

1. Add plugin dependency:

    ```xml
    <dependency>
      <groupId>com.aventstack</groupId>
      <artifactId>chaintest-junit5</artifactId>
      <version>${chaintest.junit5.version}</version>
    </dependency>
    ```

    ```json
    implementation 'com.aventstack:chaintest-junit5:$version'
    ```

    Note: Do not include the `chaintest-core` dependency in your project, it will automatically be included via plugin dependency.

1. Add [chaintest.properties](https://github.com/anshooarora/chaintest/blob/main/Config.md) to your classpath, example: `src/test/resources/chaintest.properties`

1. Annotate test class with `@ExtendWith(ChainTestExecutionCallback.class)`

    ```java
    import com.aventstack.chaintest.plugins.ChainTestExecutionCallback;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.junit.jupiter.api.Assertions;
    import org.junit.jupiter.api.Test;

    @ExtendWith(ChainTestExecutionCallback.class)
    public class TestClass { 
      @Test
      public void sum() {
        Assert.assertTrue(true);
      }
    }
    ```

## Screenshots

Screenshots can be attached to tests while the test is in-flight or after completing in the `@AfterTestExecutionCallback` hook:

```
// Version 1.0.5 and greater
@Test
public void testMethod(final TestInfo info) {
  ChainPluginService.getInstance().embed(info.getTestMethod().get(), bytes, "image/png");
}
```

```
// Version 1.0.5 and greater
public class ChainTestExecutionCallback implements AfterTestExecutionCallback {
  @Override
  public void afterTestExecution(final ExtensionContext context) {
    ChainPluginService.getInstance().embed(context.getTestMethod().get(), bytes, "image/png");
  }
}
```

```
@Test
public void testMethod(final TestInfo info) {
  final String qualifiedName = ChainPluginService.getInstance()
      .getQualifiedName(info.getTestMethod().get());
  ChainPluginService.getInstance().embed(qualifiedName, bytes, "image/png");
}
```

```
public class ChainTestExecutionCallback implements AfterTestExecutionCallback {
  @Override
  public void afterTestExecution(final ExtensionContext context) {
    final String qualifiedName = ChainPluginService.getInstance()
        .getQualifiedName(context.getTestMethod().get());
    ChainPluginService.getInstance().embed(qualifiedName, bytes, "image/png");
  }
}
```



## License

Junit 5 plugin for ChainTest is open-source software and licensed under Apache-2.0.
