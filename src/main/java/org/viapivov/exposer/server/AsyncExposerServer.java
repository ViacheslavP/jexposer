package org.viapivov.exposer.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;

import org.viapivov.exposer.parser.GsonRpcProcessor;

public class AsyncExposerServer<T> extends ExposerServer<T> {

    private static final ForkJoinPool POOL = new ForkJoinPool(4);

    public AsyncExposerServer(ExposerServer<T> server) {
        super(server);
    }

    public AsyncExposerServer(ServerSocket socket, Collection<T> instances, GsonRpcProcessor<T> parser) {
        super(socket, instances, parser);
    }

    @Override
    protected void respond(JsonRequest request, BufferedWriter channel) throws IOException {
        POOL.submit(() -> instances.parallelStream().forEach(request::invoke));
    }

    public static class Interruption extends RuntimeException {

    }

}
