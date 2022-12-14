package org.viapivov.exposer.advice;

import java.lang.instrument.Instrumentation;
import java.util.Collection;

@SuppressWarnings("unchecked")
public class InstanceSalvager<T> {

    private final Class<T> klass;

    private InstanceSalvager(Class<T> klass) {
        this.klass = klass;
    }

    public Collection<T> getInstances() {
        try {
            return (Collection<T>) klass.getField(TargetClassTransformer.RPC_INSTANCES).get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new AdviceInternalError(e);
        }
    }

    public Class<T> getTargetClass() {
        return klass;
    }

    public static <T> InstanceSalvager<T> compile(String fqn, Instrumentation instrumentation) {
        instrumentation.addTransformer(new TargetClassTransformer(fqn), true);
        try {
            Class<T> klass = (Class<T>) Class.forName(fqn);
            // instrumentation.retransformClasses(klass);
            return new InstanceSalvager<>(klass);
        } catch (Exception e) {
            throw new RuntimeException("Class " + fqn + " not found or is unmodifieble", e);
        }
    }
}
