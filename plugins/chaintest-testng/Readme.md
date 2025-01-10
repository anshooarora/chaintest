# ChainTest TestNG Plugin

ChainTest plugin for [TestNG](https://testng.org/) framework.  Visit [chaintest-testng-example](https://github.com/anshooarora/chaintest/tree/main/examples/chaintest-testng-example) for example usage.

## Setup

1. Add plugin dependency:

    ```xml
    <dependency>
      <groupId>com.aventstack</groupId>
      <artifactId>chaintest-testng</artifactId>
      <version>${chaintest.testng.version}</version>
    </dependency>
    ```

    ```json
    implementation 'com.aventstack:chaintest-testng:$version'
    ```

1. Add [chaintest.properties](https://github.com/anshooarora/chaintest/blob/main/Config.md) to your classpath, example: `src/test/resources/chaintest.properties`

1. Add `ChainTestListener` in the list of `@Listeners` of the test class

    ```java
    import org.testng.annotations.Listeners;
    import com.aventstack.chaintest.plugins.ChainTestListener;
    import org.testng.Assert;
    import org.testng.annotations.Test;

    @Listeners(ChainTestListener.class)
    public class TestClass { 
      @Test
      public void sum() {
        Assert.assertTrue(true);
      }
    }
    ```

## Screenshots

Screenshots can be attached to tests while the test is in-flight or after completing in the `@AfterMethod` hook:

```
@Test
public void testMethod(final Method method) {
  final String qualifiedName = ChainPluginService.getInstance().getQualifiedName(method);
  ChainPluginService.getInstance().embed(qualifiedName, bytes, "image/png");
}
```

```
@AfterMethod
public void afterMethod(final ITestResult result) {
  final String qualifiedName = result.getMethod().getQualifiedName();
  ChainPluginService.getInstance().embed(qualifiedName, bytes, "image/png");
}
```

## License

TestNG plugin for ChainTest is open-source software and licensed under Apache-2.0.
