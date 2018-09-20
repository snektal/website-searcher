import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import reader.UrlReaderTest;
import utils.config.ApplicationConfigTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ApplicationConfigTest.class, UrlReaderTest.class })
public class RunAllTests {
}
