package all;

import integration.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    DBAppCallDAOTest.class,
    DBAppParamDAOTest.class,
})
public class IntegratedTests {

}
