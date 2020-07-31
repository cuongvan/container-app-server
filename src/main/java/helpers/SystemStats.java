package helpers;

import com.sun.management.OperatingSystemMXBean;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import javax.inject.Singleton;

@Singleton
public class SystemStats {
    OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    
//    public double getFreePhysicalMemoryMB() {
//        long mb = 1024 * 1024;
//        return (double) operatingSystemMXBean.getFreePhysicalMemorySize() / mb;
//    }
    
    static long getKB(String line) {
        String[] splits = line.split("\\s+");
        return Long.parseLong(splits[1]);
    }
    
    
    public long getFreePhysicalMemoryMB() {
        try (BufferedReader file = new BufferedReader(new FileReader("/proc/meminfo"))) {
            file.readLine();    // MemTotal
            file.readLine();    // MemFree
            long availableKB = getKB(file.readLine()); // MemAvailable
            return availableKB / 1024;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
