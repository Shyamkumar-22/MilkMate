package com.example.milkmate.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * App-wide executors for background work.
 * Ensures DB operations never run on main thread.
 */
public class AppExecutors {

    private static final int THREAD_COUNT = 4;
    private final Executor diskIO;
    private final Executor mainThread;

    public AppExecutors(Executor diskIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
    }

    public AppExecutors() {
        this(Executors.newFixedThreadPool(THREAD_COUNT),
             new MainThreadExecutor());
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private final android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainHandler.post(command);
        }
    }
}
