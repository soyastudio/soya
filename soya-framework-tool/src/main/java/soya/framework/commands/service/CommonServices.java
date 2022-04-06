package soya.framework.commands.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommonServices {

    public static ExecutorService executeService() {
        return Executors.newSingleThreadExecutor();
    }

    public static ExecutorService executorService(int poolSize) {
        return Executors.newFixedThreadPool(poolSize);
    }
}
