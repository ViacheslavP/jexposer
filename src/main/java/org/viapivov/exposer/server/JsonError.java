package org.viapivov.exposer.server;

@SuppressWarnings("unused")
public class JsonError {
    private final int code;
    private final String message;
    private final Object data;

    private JsonError(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static JsonError parserError(String message) {
        return new JsonError(-32700, message, null);
    }

    public static JsonError methodNotFound(String message) {
        return new JsonError(-32601, message, null);
    }

    public static JsonError paramsInvalid(String message) {
        return new JsonError(-32602, message, null);
    }

    public static JsonError exception(String message, Throwable t) {
        return new JsonError(-32603, message, t);
    }

}
