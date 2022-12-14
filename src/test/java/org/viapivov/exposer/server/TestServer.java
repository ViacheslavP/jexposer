package org.viapivov.exposer.server;

import java.util.concurrent.locks.LockSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestServer.class);

    public static void main(String... strs) throws Exception {
        @SuppressWarnings("unused")
        ResponsiveClass obj = new ResponsiveClass(Thread.currentThread());

        LOGGER.debug("----AWAITING REQUEST----");
        LockSupport.park();
        LOGGER.debug("----LETHAL DOSE TAKEN----");
        LOGGER.debug("----3s TO DEATH----");
        Thread.sleep(3000);
    }
}