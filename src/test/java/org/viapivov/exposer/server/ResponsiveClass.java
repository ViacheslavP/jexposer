package org.viapivov.exposer.server;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.IntStream;

public class ResponsiveClass {

    private final Thread thread;
    private final Set<Integer> set;

    public ResponsiveClass(Thread thread) {
        this.thread = thread;
        set = Collections.newSetFromMap(new ConcurrentHashMap<>());
        IntStream.range(1, 7).forEach(set::add);
    }

    public String test1() {
        return swallow(1);
    }

    public String test2(String name) {
        return swallow(2);
    }

    public String test3(Wrap<String> name) {
        return swallow(3);
    }

    public String test4(Wrap<String> name, String strName) {
        return swallow(4);
    }

    public Wrap<String> test5() {
        return new Wrap<>(swallow(5));
    }

    public String test6(Integer num) {
        return swallow(6);
    }

    private String swallow(int pill) {
        set.remove(pill);
        if (set.isEmpty()) {
            LockSupport.unpark(thread);
        }
        return "SUCCESS-" + pill;
    }

    private static class Wrap<T> {

        @SuppressWarnings("unused")
        final T wrapee;

        Wrap(T t) {
            this.wrapee = t;
        }
    }
}
