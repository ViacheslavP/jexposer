package org.viapivov.exposer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.Test;

public class ParamsTest {

    @Test
    public void fullOkTest() {
        String testString = "com.class@21a";
        TestParams testParams = new TestParams("com.class", 21, true);
        ok(testString, testParams);
    }

    @Test
    public void syncOkTest() {
        String testString = "class@21";
        TestParams testParams = new TestParams("class", 21, false);
        ok(testString, testParams);
    }

    @Test
    public void noHostOkTest() {
        String testString = "class@21a";
        TestParams testParams = new TestParams("class", 21, true);
        ok(testString, testParams);
    }

    @Test
    public void noClassNameNotOkTest() {
        String testString = "@local:21a";
        notOk(testString);
    }

    @Test
    public void noPortNotOkTest() {
        String testString = "class@local";
        notOk(testString);
    }

    private static final void ok(String testString, TestParams testParams) {
        assertDoesNotThrow(() -> Args.compile(testString));
        Args params = Args.compile(testString);
        assertTrue(testParams.test(params));
    }

    private static final void notOk(String testString) {
        assertThrows(Args.ParamsParsingException.class, () -> Args.compile(testString));
    }

    private static class TestParams {
        private final String fqn;
        private final int port;
        private final boolean isAsync;

        public TestParams(String fqn, int port, boolean isAsync) {
            this.fqn = fqn;
            this.port = port;
            this.isAsync = isAsync;
        }

        public boolean test(Args params) {
            return Objects.equals(fqn, params.getClassName())
                    && Objects.equals(port, params.getPort()) && Objects.equals(isAsync, params.isAsync());
        }

    }
}
