package all;

import common.AppConfig;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    UnitTests.class,
    IntegrationTests.class,
    EndToEndTests.class
})
public class All {
    static {
        AppConfig.RUNNING_MODE = AppConfig.RunningMode.TEST;
    }
}
