package org.viapivov.exposer.parser.adapters;

import java.io.IOException;

import org.viapivov.exposer.server.Either;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class IdAdapter extends TypeAdapter<Either<String, Integer>> {

    @Override
    public void write(JsonWriter writer, Either<String, Integer> value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }
        if (value.isLeft()) {
            String str = value.getLeft();
            writer.value(str);
            return;
        }
        if (value.isRight()) {
            int id = value.getRight();
            writer.value(id);
            return;
        }
    }

    @Override
    public Either<String, Integer> read(JsonReader reader) throws IOException {
        JsonToken token = reader.peek();
        String str = reader.nextString();
        switch (token) {
            case NULL:
                return null;
            case NUMBER:
                int id = Integer.parseInt(str);
                return Either.right(id);
            case STRING:
                return Either.left(str);
            default:
                return null;
        }
    }
}
