package zrun;

import helpers.DBHelper;
import org.junit.Test;

public class Truncate {
    @Test
    public void truncateTables() {
        DBHelper.truncateTables();
    }
}
