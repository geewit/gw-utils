package io.geewit.utils.javafx.base.scheduler;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Provides a shared Reactor {@link Scheduler} backed by virtual threads for blocking workloads.
 */

public class VirtualThreadScheduler {

    private final ExecutorService executorService;
    private final Scheduler scheduler;

    public VirtualThreadScheduler() {
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.scheduler = Schedulers.fromExecutorService(executorService);
    }

    public Scheduler scheduler() {
        return scheduler;
    }

    public void destroy() {
        scheduler.dispose();
        executorService.shutdownNow();
    }
}

