package org.viapivov.exposer.container;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

public class WeakCollectionTest {

    @Test
    void mustBehaveAsCollection() {
        Object obj1 = new Object();
        Object obj2 = new Object();

        Collection<Object> collection = new WeakCollection<>();
        collection.add(obj1);

        assertTrue(collection.contains(obj1));
        assertTrue(collection.remove(obj1));

        assertTrue(collection.add(obj2));
        assertEquals(1, collection.size());

        assertTrue(collection.add(obj1));
        assertEquals(2, collection.size());

        assertTrue(collection.removeAll(List.of(obj1)));
        assertEquals(1, collection.size());

        assertTrue(collection.add(obj1));
        collection.retainAll(List.of(obj1));
        assertEquals(1, collection.size());
        assertEquals(obj1, collection.iterator().next());

    }

    @Test
    void mustBeWeak() throws Exception {
        Object obj1 = new Object();
        Object obj2 = new Object();

        Collection<Object> collection = new WeakCollection<>();
        collection.add(obj1);
        collection.add(obj2);

        obj2 = null;
        System.gc();
        Thread.sleep(100);

        assertEquals(1, collection.size());
    }
}
