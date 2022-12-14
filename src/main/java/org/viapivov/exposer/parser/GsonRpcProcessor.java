package org.viapivov.exposer.parser;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Consumer;

import org.viapivov.exposer.parser.adapters.IdAdapter;
import org.viapivov.exposer.parser.adapters.MethodAdapter;
import org.viapivov.exposer.parser.adapters.ParamsAdapter;
import org.viapivov.exposer.server.Either;
import org.viapivov.exposer.server.JsonError;
import org.viapivov.exposer.server.JsonRequest;
import org.viapivov.exposer.server.JsonResponse;
import org.viapivov.exposer.server.PartisanRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class GsonRpcProcessor<T> extends JsonRpcProcessor<T> {

    private static final Gson UNTAINTED_GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(Either.class, new IdAdapter())
            .setLenient().create();

    private GsonRpcProcessor(IndexedClass<T> klass) {
        super(klass);
    }

    public JsonRequest fromJson(String jsonString, Consumer<String> checker) {
        PartisanRequest weakRequest = UNTAINTED_GSON.fromJson(jsonString, PartisanRequest.class);
        String methodName = weakRequest.getMethod();
        Collection<Method> candidates = indexedClass.getMethods().get(methodName);
        if (candidates == null) {
            JsonError error = JsonError.methodNotFound(
                    String.format("No method `%s` was found. Use one of the following: %s",
                            methodName, indexedClass));
            JsonResponse response = new JsonResponse(weakRequest, error);
            throw new CustomParsingException(response);
        }
        for (Method method : candidates) {
            TypeToken<?>[] types = getTypeToken(method);
            Gson gsonCandidate = new GsonBuilder()
                    .registerTypeHierarchyAdapter(Method.class, new MethodAdapter(method))
                    .registerTypeHierarchyAdapter(Either.class, new IdAdapter())
                    .registerTypeHierarchyAdapter(Object[].class, new ParamsAdapter(UNTAINTED_GSON, types))
                    .create();
            try {
                return gsonCandidate.fromJson(jsonString, JsonRequest.class);
            } catch (Exception e) {
                continue;
            }
        }
        JsonError error = JsonError.paramsInvalid("Failed to parse params");
        JsonResponse response = new JsonResponse(weakRequest, error);
        throw new CustomParsingException(response);
    }

    public String toJson(JsonResponse response) {
        return UNTAINTED_GSON.toJson(response);
    }

    private static TypeToken<?>[] getTypeToken(Method method) {
        Class<?>[] classes = method.getParameterTypes();
        TypeToken<?>[] types = new TypeToken<?>[classes.length];
        for (int i = 0; i < classes.length; i++) {
            types[i] = TypeToken.get(classes[i]);
        }
        return types;
    }

    public static <T> GsonRpcProcessor<T> compile(Class<T> klass) {
        IndexedClass<T> indexedClass = IndexedClass.create(klass);
        return new GsonRpcProcessor<>(indexedClass);
    }
}
