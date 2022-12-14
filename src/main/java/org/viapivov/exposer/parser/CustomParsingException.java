package org.viapivov.exposer.parser;

import org.viapivov.exposer.server.JsonResponse;

public class CustomParsingException extends RuntimeException {

    private final JsonResponse response;

    public CustomParsingException(JsonResponse response) {
        this.response = response;
    }

    public JsonResponse getResponse() {
        return response;
    }

}
