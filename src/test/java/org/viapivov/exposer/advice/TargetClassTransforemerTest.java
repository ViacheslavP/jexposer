package org.viapivov.exposer.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.instrument.ClassFileTransformer;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class TargetClassTransforemerTest {

    static Class<?> modifiedClass = loadModified(TestClass.class);;

    @Test
    void testConstantIsInitialized() throws Exception {

        Object queue = modifiedClass.getField(TargetClassTransformer.RPC_INSTANCES).get(null);

        assertNotNull(queue);
        assertTrue(queue instanceof Collection);
        assertEquals(0, ((Collection<?>) queue).size());
    }

    @Test
    void testInstanceAddedToCollection() throws Exception {

        Collection<?> queue = (Collection<?>) modifiedClass.getField(TargetClassTransformer.RPC_INSTANCES).get(null);

        Object instance = modifiedClass.getConstructor().newInstance();
        assertEquals(instance, queue.iterator().next());
        queue.clear();
    }

    @Test
    void testTwoInstancesAddedToCollection() throws Exception {

        Collection<?> queue = (Collection<?>) modifiedClass.getField(TargetClassTransformer.RPC_INSTANCES).get(null);

        Object instance = modifiedClass.getConstructor().newInstance();
        Object instance2 = modifiedClass.getConstructor().newInstance();

        assertFalse(queue.isEmpty());
        assertTrue(queue.stream().allMatch(obj -> obj == instance || obj == instance2));
        queue.clear();
    }

    private static Class<?> loadModified(Class<?> klass) {
        String fqn = klass.getName();
        ClassFileTransformer transformer = new TargetClassTransformer(fqn);

        ClassLoader loader = new ClassLoader(TargetClassTransforemerTest.class.getClassLoader()) {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    byte[] byteCode = transformer.transform(this, name.replace('.', '/'), klass, null,
                            null);
                    if (byteCode != null) {
                        return defineClass(fqn, byteCode, 0, byteCode.length);
                    }
                } catch (Exception icfe) {
                    throw new AssertionFailedError();
                }
                return super.loadClass(name);
            }
        };
        try {
            return loader.loadClass(fqn);
        } catch (ClassNotFoundException cnfe) {
            throw new AssertionFailedError();
        }
    }

}
