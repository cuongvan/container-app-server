package helpers;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {
    private final String threadName;

    public NamedThreadFactory(String threadName) {
        this.threadName = threadName;
    }
    
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(threadName);
        return t;
    }
}
