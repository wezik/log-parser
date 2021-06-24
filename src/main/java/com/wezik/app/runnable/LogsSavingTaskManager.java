package com.wezik.app.runnable;

import com.wezik.app.domain.FullLog;
import com.wezik.app.service.database.DatabaseService;
import com.wezik.app.tracker.LogsProcessedProgressTracker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
public class LogsSavingTaskManager implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(LogsSavingTaskManager.class);

    private final ExecutorService executorService;
    private final DatabaseService databaseService;
    private final BlockingQueue<Optional<FullLog>> saveQueue;

    private final int chunkLimit;

    private final Set<FullLog> chunk = new HashSet<>();

    @Override
    public void run() {

        LOGGER.info("Starting saving task manager");

        boolean notified = false;

        while(!notified) {
            Optional<FullLog> optional = pullFromQueue();
            if (optional.isPresent()) {
                parse(optional.get());
            } else notified = true;
        }
        if (chunk.size()>0) passToDatabase();
        LOGGER.info("Stopping saving task manager");
        LOGGER.info("Waiting for saving tasks to finish");
        LogsProcessedProgressTracker.ENTIRE_FILE_LOADED = true;
        executorService.shutdown();
    }

    public void parse(FullLog fullLog) {
        chunk.add(fullLog);
        if(chunk.size()>= chunkLimit) passToDatabase();
    }

    public void passToDatabase() {
        executorService.execute(new LogsSavingTask(databaseService,new HashSet<>(chunk)));
        chunk.clear();
    }

    private Optional<FullLog> pullFromQueue() {
        try {
            return saveQueue.take();
        } catch (InterruptedException e) {
            LOGGER.error("Queue interrupted while pulling from collector");
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
