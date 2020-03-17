package check3rdparties.docker;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ForceCreateDirectory {
    @Test
    public void test() throws IOException {
        FileUtils.forceMkdir(new File("/ram/testing"));
    }
}
