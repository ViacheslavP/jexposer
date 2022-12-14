package org.viapivov.exposer.parser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.instrument.Instrumentation;

import org.junit.jupiter.api.Test;
import org.viapivov.exposer.advice.InstanceSalvager;
import org.viapivov.exposer.server.JsonRequest;

@SuppressWarnings("all")
public class RpcBridgeTest {

    public RpcBridgeTest() {

    }

    @Test
    void mustParseEverythingWhenIdIsString() {
        String request = "{\"jsonrpc\":\"2.0\", \"method\":\"callA\", \"id\":\"2\", \"params\":{\"a\":\"a\"}}";
        JsonRequest objs = GsonRpcProcessor.compile(Foo.class).fromJson(request, a -> {
        });
        assertNotNull(objs);
        assertTrue(objs.getParams()[0] instanceof A);
    }

    @Test
    void mustParseEverythingWhenIdIsInt() {
        String request = "{\"jsonrpc\":\"2.0\", \"method\":\"callA\", \"id\":2, \"params\":{\"a\":\"a\"}}";
        JsonRequest objs = GsonRpcProcessor.compile(Foo.class).fromJson(request, a -> {
        });
        assertNotNull(objs);
        assertTrue(objs.getParams()[0] instanceof A);
    }

    @Test
    void mustParseEverythingWhenParamsIsArray() {
        String request = "{\"jsonrpc\":\"2.0\", \"method\":\"callA\", \"id\":2, \"params\":[{\"a\":\"a\"}]}";
        JsonRequest objs = GsonRpcProcessor.compile(Foo.class).fromJson(request, a -> {
        });
        assertNotNull(objs);
        assertTrue(objs.getParams()[0] instanceof A);
    }

    @Test
    void mustParseEverythingWhenParamsIsArrayWithSeveralValues() {
        String request = "{\"jsonrpc\":\"2.0\", \"method\":\"callAB\", \"id\":2, \"params\":[{\"a\":\"a\"}, {\"b\":\"b\"}]}";
        JsonRequest objs = GsonRpcProcessor.compile(Foo.class).fromJson(request, a -> {
        });
        assertNotNull(objs);
        assertTrue(objs.getParams()[0] instanceof A);
    }

    private static class A {
        private String a;
    }

    private static class B {
        private String b;
    }

    private class Foo {
        public void callA(A a) {

        }

        public void callAB(A a, B b) {

        }
    }

}
