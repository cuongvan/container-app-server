package helpers;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import javax.inject.Singleton;

@Singleton
public class SystemStats {
    OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    
    public double getFreePhysicalMemoryMB() {
        long mb = 1024 * 1024;
        return (double) operatingSystemMXBean.getFreePhysicalMemorySize() / mb;
    }
}
