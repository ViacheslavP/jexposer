package org.viapivov.exposer.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collection;

import org.viapivov.exposer.parser.GsonRpcProcessor;

public class SyncExposerServer<T> extends ExposerServer<T> {

    public SyncExposerServer(ServerSocket socket, Collection<T> instances, GsonRpcProcessor<T> parser) {
        super(socket, instances, parser);
    }

    public SyncExposerServer(ExposerServer<T> server) {
        super(server);
    }

    @Override
    protected void respond(JsonRequest request, BufferedWriter channel) throws IOException {
        for (T instance : instances) {
            JsonResponse response = request.invoke(instance);
            sendResponse(response, channel);
        }
    }

    public static class Interruption extends RuntimeException {

    }

}
