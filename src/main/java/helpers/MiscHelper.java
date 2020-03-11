package helpers;

import com.fasterxml.uuid.Generators;

public class MiscHelper {
    public static String newId() {
        return Generators.timeBasedGenerator().generate().toString();
    }
}
