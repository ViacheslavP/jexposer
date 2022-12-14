package org.viapivov.exposer.server;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Map;

/**
 * Interrupts server sequence {@link ServerExceptionHandler#PANICS}
 */
public class ServerExceptionHandler implements UncaughtExceptionHandler {

    private static final Map<String, ? extends RuntimeException> PANICS = Map.of(
            "rpcShutdown", new RuntimeException(),
            "rpcSwitchAsync", new AsyncExposerServer.Interruption(),
            "rpcSwitchSync", new SyncExposerServer.Interruption());

    private final ExposerServerRunner<?> serverRunner;

    public ServerExceptionHandler(ExposerServerRunner<?> serverRunner) {
        this.serverRunner = serverRunner;
    }

    /**
     * Checks if method name is among rpc methods. If so, it throws
     * {@code java.lang.RuntimeException} which is then handled by instance of
     * {@link ServerExceptionHandler}
     *
     * @param name of the called method
     * @throws RuntimeException from {@link ServerExceptionHandler#PANICS}
     */
    public static final void check(String name) {
        RuntimeException exception = PANICS.get(name);
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (e instanceof AsyncExposerServer.Interruption) {
            serverRunner.start(AsyncExposerServer::new);
        }
        if (e instanceof SyncExposerServer.Interruption) {
            serverRunner.start(SyncExposerServer::new);
        }
        throw (RuntimeException) e;
    }
}
