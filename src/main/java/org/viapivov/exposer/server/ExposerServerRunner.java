package org.viapivov.exposer.server;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.ThreadFactory;
import java.util.function.UnaryOperator;

import org.viapivov.exposer.Args;
import org.viapivov.exposer.advice.InstanceSalvager;
import org.viapivov.exposer.parser.GsonRpcProcessor;

public class ExposerServerRunner<T> {

    private final ExposerServer<T> prototype;
    private final ThreadFactory threadFactory;

    public ExposerServerRunner(ExposerServer<T> prototype, ThreadFactory threadFactory) {
        this.prototype = prototype;
        this.threadFactory = threadFactory;
    }

    public void start() {
        start(UnaryOperator.identity());
    }

    public void start(UnaryOperator<ExposerServer<T>> switcher) {
        ExposerServer<T> newServer = switcher.apply(prototype);
        Thread thread = threadFactory.newThread(newServer::listen);
        thread.start();
    }

    public static <T> ExposerServerRunner<T> create(Args args, Instrumentation inst) throws UnknownHostException {
        int port = args.getPort();
        ServerSocket address;
        try {
            address = new ServerSocket(port);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        InstanceSalvager<T> salvager = InstanceSalvager.<T>compile(args.getClassName(), inst);
        Class<T> klass = salvager.getTargetClass();
        Collection<T> instances = salvager.getInstances();

        GsonRpcProcessor<T> parser = GsonRpcProcessor.compile(klass);
        ExposerServer<T> server = args.isAsync()
                ? new AsyncExposerServer<>(address, instances, parser)
                : new SyncExposerServer<>(address, instances, parser);
        String threadName = args.getThreadName();
        ThreadFactory threadFactory = new ServerThreadFactory(threadName);

        return new ExposerServerRunner<>(server, threadFactory);
    }
}
