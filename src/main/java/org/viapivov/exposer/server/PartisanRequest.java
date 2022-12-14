package org.viapivov.exposer.server;

public class PartisanRequest {
    private final String jsonrpc;
    private final String method;
    private final Either<String, Integer> id;

    public PartisanRequest(String jsonrpc, String method, Either<String, Integer> id) {
        this.jsonrpc = jsonrpc;
        this.method = method;
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public Either<String, Integer> getId() {
        return id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }
}
