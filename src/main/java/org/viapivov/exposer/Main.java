package org.viapivov.exposer;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.instrument.Instrumentation;

import org.viapivov.exposer.server.ExposerServerRunner;
import org.viapivov.exposer.server.ServerExceptionHandler;

public class Main {

	public static void premain(String cli, Instrumentation inst) throws IOException {
		Args args = Args.compile(cli);
		ExposerServerRunner<?> server = ExposerServerRunner.create(args, inst);
		UncaughtExceptionHandler serverCommandHandler = new ServerExceptionHandler(server);
		Thread.setDefaultUncaughtExceptionHandler(serverCommandHandler);
		server.start();
	}
}
