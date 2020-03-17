package all;

import unittests.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    CreateAppTest.class,
    ExecuteHandlerTest.class,
})
public class UnitTests {

}
