package org.viapivov.exposer;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Args {

    private static final Pattern PARAM_PATTERN = Pattern
            .compile("(?<klass>[^@:]+)@(?<port>\\d+)(?<async>a)?");

    public static final int PARALLELISM;
    public static final int THREAD_PRIORITY;
    public static final boolean IS_DAEMON;

    private static final String THREAD_NAME;

    static {
        Properties properties = System.getProperties();
        PARALLELISM = Integer.parseInt(properties.getProperty("jexposer.parallelism", "4"));
        THREAD_PRIORITY = Integer.parseInt(properties.getProperty("jexposer.thread.priority", "5"));
        THREAD_NAME = properties.getProperty("jexposer.thread.name");
        IS_DAEMON = Boolean.parseBoolean(properties.getProperty("jexposer.thread.daemon", "true"));

    }

    private final String className;
    private final short port;
    private final boolean isAsync;

    private Args(String className, short port, boolean isAsync) {
        this.className = className;
        this.port = port;
        this.isAsync = isAsync;

    }

    /**
     * Factory method for parsing parameters from string
     *
     * @param paramString
     * @return Args
     */
    public static Args compile(String paramString) {
        Matcher matcher = PARAM_PATTERN.matcher(paramString);
        if (!matcher.matches()) {
            throw new ParamsParsingException();
        }
        String klassName = matcher.group("klass");
        short port = Short.parseShort(matcher.group("port"));
        boolean async = matcher.group("async") != null;
        return new Args(klassName, port, async);
    }

    public String getClassName() {
        return className;
    }

    public int getPort() {
        return port;
    }

    public boolean isAsync() {
        return isAsync;
    }

    public String getThreadName() {
        return THREAD_NAME == null ? "JSON-RPC@" + className : THREAD_NAME;
    }

    public static class ParamsParsingException extends RuntimeException {

        public ParamsParsingException() {
            super();
        }

    }
}
