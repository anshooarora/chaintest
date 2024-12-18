import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/com/aventstack", plugin = {
        "com.aventstack.chaintest.plugins.ChainTestCucumberListener:", "summary"},
        glue = { "com.aventstack" }
)
public class RunCukes {
}
