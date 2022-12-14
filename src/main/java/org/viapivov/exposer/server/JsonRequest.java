package org.viapivov.exposer.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class JsonRequest {

    private final String jsonrpc;
    private final Method method;
    private final Either<String, Integer> id;
    private final Object[] params;

    public JsonRequest(String jsonrpc, Method method, String id, Object[] params) {
        this.jsonrpc = jsonrpc;
        this.method = method;
        this.id = Either.left(id);
        this.params = params;
    }

    public JsonRequest(String jsonrpc, Method method, int id, Object[] params) {
        this.jsonrpc = jsonrpc;
        this.method = method;
        this.id = Either.right(id);
        this.params = params;
    }

    public <T> JsonResponse invoke(Collection<T> instances,
            Function<Collection<JsonResponse>, JsonResponse> mergingStrategy) {
        if (instances.size() == 1) {
            T instance = instances.iterator().next();
            return invoke(instance);
        }
        List<JsonResponse> result = new ArrayList<>();
        for (T instance : instances) {
            JsonResponse response = invoke(instance);
            result.add(response);
        }
        return mergingStrategy.apply(result);
    }

    public <T> JsonResponse invoke(T instance) {
        try {
            Object result = method.invoke(instance, params);
            return new JsonResponse(jsonrpc, result, id);
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            JsonError error = JsonError.exception(cause.getMessage(), cause);
            return new JsonResponse(jsonrpc, error, id);
        } catch (IllegalAccessException iae) {
            JsonError error = JsonError.methodNotFound("Cannot access method");
            return new JsonResponse(jsonrpc, error, id);
        }
    }

    public Object[] getParams() {
        return params;
    }

    public Either<String, Integer> getId() {
        return id;
    }

    public String getMethodName() {
        return method.getName();
    }
}
