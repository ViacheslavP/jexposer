package org.viapivov.exposer.parser.adapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class ParamsAdapter extends TypeAdapter<Object[]> {

    private final Gson gson;
    private final TypeToken<?>[] types;

    public ParamsAdapter(Gson gson, TypeToken<?>[] types) {
        this.gson = gson;
        this.types = types;
    }

    @Override
    public Object[] read(JsonReader in) throws IOException {
        List<Object> objects = new ArrayList<>();
        JsonToken token = in.peek();
        switch (token) {
            case STRING:
                if (types.length == 1 && types[0].getRawType() == String.class) {
                    String value = gson.fromJson(in, String.class);
                    return new Object[] { value };
                }
            case NUMBER:
                if (types.length == 1 && types[0].getRawType() == Integer.class) {
                    Integer number = gson.fromJson(in, Integer.class);
                    return new Object[] { number };
                }
                if (types.length == 1 && types[0].getRawType() == Double.class) {
                    Double number = gson.fromJson(in, Double.class);
                    return new Object[] { number };
                }
                if (types.length == 1 && types[0].getRawType() == Long.class) {
                    Long number = gson.fromJson(in, Long.class);
                    return new Object[] { number };
                }
            case BEGIN_ARRAY:
                in.beginArray();
                for (TypeToken<?> typeToken : types) {
                    Object obj = gson.fromJson(in, typeToken.getType());
                    objects.add(obj);
                }
                in.endArray();
                return objects.toArray();
            case BEGIN_OBJECT:
                if (types.length == 0) {
                    gson.fromJson(in, Object.class);
                    return new Object[0];
                }
                if (types.length != 1) {
                    throw new JsonParseException("Not this one");
                }
                TypeToken<?> singleToken = (TypeToken<?>) types[0];
                Object[] result = new Object[1];
                result[0] = gson.fromJson(in, singleToken.getType());
                return result;
            default:
                throw new JsonParseException("Not supported token " + token);

        }
    }

    @Override
    public void write(JsonWriter out, Object[] value) throws IOException {
        throw new IllegalStateException();
    }
}
