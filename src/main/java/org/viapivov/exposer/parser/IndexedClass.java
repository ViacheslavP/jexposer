package org.viapivov.exposer.parser;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class IndexedClass<T> {

    private final Map<String, List<Method>> methods;

    public IndexedClass(Map<String, List<Method>> methods) {
        this.methods = methods;
    }

    public Map<String, List<Method>> getMethods() {
        return methods;
    }

    public static <T> IndexedClass<T> create(Class<T> klass) {
        Map<String, List<Method>> methods = new HashMap<>();
        for (Method method : klass.getMethods()) {
            int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers)) {
                continue;
            }
            String methodName = method.getName();
            methods.computeIfAbsent(methodName, _k -> new ArrayList<>()).add(method);
        }
        return new IndexedClass<>(methods);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, List<Method>> entry : methods.entrySet()) {
            for (Method method : entry.getValue()) {
                sb.append(entry.getValue()).append('(');
                String types = Arrays.stream(method.getParameterTypes()).map(Class::getName)
                        .collect(Collectors.joining(", "));
                sb.append(types).append(')');
            }
        }
        return sb.toString();
    }
}
