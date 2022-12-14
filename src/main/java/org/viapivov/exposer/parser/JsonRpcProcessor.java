package org.viapivov.exposer.parser;

import java.util.function.Consumer;

import org.viapivov.exposer.server.JsonRequest;
import org.viapivov.exposer.server.JsonResponse;

public abstract class JsonRpcProcessor<T> {

    protected IndexedClass<T> indexedClass;

    public JsonRpcProcessor(IndexedClass<T> klass) {
        this.indexedClass = klass;
    }

    public abstract JsonRequest fromJson(String jsonString, Consumer<String> checker);

    public abstract String toJson(JsonResponse response);
}
