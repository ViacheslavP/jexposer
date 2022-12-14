package org.viapivov.exposer.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.viapivov.exposer.parser.CustomParsingException;
import org.viapivov.exposer.parser.GsonRpcProcessor;

public abstract class ExposerServer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExposerServer.class);

    private final ServerSocket socket;
    protected final Collection<T> instances;
    protected final GsonRpcProcessor<T> parser;

    public ExposerServer(ExposerServer<T> server) {
        this.socket = server.socket;
        this.instances = server.instances;
        this.parser = server.parser;
    }

    public ExposerServer(ServerSocket socket, Collection<T> instances, GsonRpcProcessor<T> parser) {
        this.socket = socket;
        this.instances = instances;
        this.parser = parser;
    }

    public void listen() {
        while (true) {
            acceptAndRespond();
        }
    }

    public void acceptAndRespond() {
        try (
                Socket socket = this.socket.accept();
                InputStream stream = socket.getInputStream();
                Reader reader = new InputStreamReader(stream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                OutputStream out = socket.getOutputStream();
                Writer writer = new OutputStreamWriter(out);
                BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            JsonRequest request;
            for (String message = bufferedReader.readLine(); message != null; message = bufferedReader.readLine()) {
                try {
                    LOGGER.debug("Received request: " + message);
                    request = parser.fromJson(message, ServerExceptionHandler::check);
                } catch (CustomParsingException e) {
                    JsonResponse response = e.getResponse();
                    sendResponse(response, bufferedWriter);
                    continue;
                }
                respond(request, bufferedWriter);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    protected abstract void respond(JsonRequest request, BufferedWriter bufferedWriter) throws IOException;

    protected void sendResponse(JsonResponse response, BufferedWriter bufferedWriter) throws IOException {
        String message = parser.toJson(response);
        LOGGER.debug("Sending response:" + message);
        bufferedWriter.write(message);
        bufferedWriter.flush();

    }
}
