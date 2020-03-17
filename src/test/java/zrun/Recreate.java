package zrun;

import helpers.DBHelper;
import helpers.MyFileUtils;
import java.io.IOException;
import org.junit.Test;

public class Recreate {
    @Test
    public void recreate() throws IOException {
        DBHelper.dropTables();
        DBHelper.createTables();
        
        MyFileUtils.deleteAppDirs();
        MyFileUtils.createRequiredDirs();
    }
}
