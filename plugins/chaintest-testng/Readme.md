# ChainTest TestNG Plugin

ChainTest plugin for [TestNG](https://testng.org/) framework.  Visit [chaintest-testng-example](https://github.com/anshooarora/chaintest/tree/main/examples/chaintest-testng-example) for example usage.

## Setup

1. Add [chaintest.properties](https://github.com/anshooarora/chaintest/blob/main/Config.md) to your classpath, example: `src/test/resources/chaintest.properties`
2. Add `ChainTestListener` to the test class

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

## License

TestNG plugin for ChainTest is open-source software and licensed under Apache-2.0.
