package org.viapivov.exposer.server;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.viapivov.exposer.Args;

public class ServerThreadFactory implements ThreadFactory {

    private final String threadName;

    private static final AtomicInteger COUNT = new AtomicInteger();

    public ServerThreadFactory(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread result = new Thread(r);
        result.setDaemon(Args.IS_DAEMON);
        result.setName(threadName + "-" + COUNT.incrementAndGet());
        result.setPriority(Args.THREAD_PRIORITY);
        return result;
    }

}
