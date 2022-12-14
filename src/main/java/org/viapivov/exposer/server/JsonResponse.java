package org.viapivov.exposer.server;

@SuppressWarnings("unused")
public class JsonResponse {

    private final String jsonrpc;
    private final Object result;
    private final JsonError error;
    private final Either<String, Integer> id;

    public JsonResponse(String jsonrpc, Object result, Either<String, Integer> id) {
        this.jsonrpc = jsonrpc;
        this.result = result;
        this.id = id;
        this.error = null;
    }

    public JsonResponse(String jsonrpc, JsonError error, Either<String, Integer> id) {
        this.jsonrpc = jsonrpc;
        this.result = null;
        this.id = id;
        this.error = error;
    }

    public JsonResponse(PartisanRequest weakRequest, JsonError error) {
        this.jsonrpc = weakRequest.getJsonrpc();
        this.result = null;
        this.id = weakRequest.getId();
        this.error = error;
    }
}
