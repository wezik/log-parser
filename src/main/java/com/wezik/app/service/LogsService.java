package com.wezik.app.service;

import com.wezik.app.config.CoreConfig;
import com.wezik.app.domain.FullLog;
import com.wezik.app.domain.PartLog;
import com.wezik.app.mapper.FullLogMapper;
import com.wezik.app.runnable.LogsCollector;
import com.wezik.app.runnable.LogsSavingTaskManager;
import com.wezik.app.tracker.LogsProcessedProgressTracker;
import com.wezik.app.runnable.LogsReader;
import com.wezik.app.service.database.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class LogsService {

    private final static Logger LOGGER = LoggerFactory.getLogger(LogsService.class);

    private final CoreConfig coreConfig;
    private final ExecutorService executorService;
    private final DatabaseService databaseService;
    private final FullLogMapper fullLogMapper;

    public void parseLogsToDatabase(String path) {
        LOGGER.info("System will inform you about the progress in format [Found|Flagged|Saved]");
        LOGGER.info("Initializing processes");
        long start = System.currentTimeMillis();
        process(path);
        trackProgress();
        long end = System.currentTimeMillis();
        printResults(end-start);
    }

    private void process(String path) {
        BlockingQueue<Optional<PartLog>> readQueue = new ArrayBlockingQueue<>(coreConfig.getLogsQueueLimit());
        BlockingQueue<Optional<FullLog>> saveQueue = new ArrayBlockingQueue<>(coreConfig.getLogsQueueLimit());
        LogsReader reader = new LogsReader(path,readQueue);
        LogsCollector collector = new LogsCollector(readQueue,saveQueue, fullLogMapper, coreConfig.getFlagTime());
        LogsSavingTaskManager parser = new LogsSavingTaskManager(executorService,databaseService,saveQueue,coreConfig.getChunkLimit());
        List<Runnable> tasks = Arrays.asList(reader,collector,parser);
        tasks.forEach(executorService::execute);
    }

    private void trackProgress() {
        long stopwatch = System.currentTimeMillis();
        while (!executorService.isTerminated()) {
            long lap = System.currentTimeMillis();
            if (lap - stopwatch >= coreConfig.getProgressMessageInterval()) {
                stopwatch = lap;
                printProgress();
            }
        }
    }

    private void printProgress() {
        long found = LogsProcessedProgressTracker.TOTAL_LOGS_FOUND;
        long flagged = LogsProcessedProgressTracker.TOTAL_LOGS_FLAGGED;
        long saved = LogsProcessedProgressTracker.TOTAL_LOGS_SAVED;
        if (LogsProcessedProgressTracker.TOTAL_LOGS_FOUND!=0) {
            LOGGER.info((LogsProcessedProgressTracker.ENTIRE_FILE_LOADED ?
                    (saved*100/found + "% of logs saved ") :
                    (LogsProcessedProgressTracker.PROCESSED_BYTES*100/LogsProcessedProgressTracker.FILE_SIZE + "% of file processed "))
                    + "[" + found + "|"+ flagged +"|"+ saved +"]");
        }
    }

    private void printResults(long executionTime) {
        Date date = new Date(executionTime);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        LOGGER.info(">> Complete! <<");
        LOGGER.info("Found " + LogsProcessedProgressTracker.TOTAL_LOGS_FOUND
                + " | Flagged " + LogsProcessedProgressTracker.TOTAL_LOGS_FLAGGED
                + " | Time " + formatter.format(date)+" |");
        LOGGER.info("Database now has: " + databaseService.countTotal() + " logs and " + databaseService.countFlagged() + " flags");
    }

}
