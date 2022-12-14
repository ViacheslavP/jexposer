package org.viapivov.exposer.parser.adapters;

import java.io.IOException;
import java.lang.reflect.Method;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class MethodAdapter extends TypeAdapter<Method> {

    private final Method method;

    public MethodAdapter(Method method) {
        this.method = method;
    }

    @Override
    public void write(JsonWriter out, Method value) throws IOException {
        throw new IllegalStateException();
    }

    @Override
    public Method read(JsonReader reader) throws IOException {
        JsonToken token = reader.peek();
        switch (token) {
            case STRING:
                reader.nextString();
                return method;
            default:
                return null;
        }
    }

}
