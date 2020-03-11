package zrun;

import helpers.DBHelper;
import org.junit.Test;

public class Recreate {
    @Test
    public void recreate() {
        DBHelper.dropTables();
        DBHelper.createTables();
    }
}
