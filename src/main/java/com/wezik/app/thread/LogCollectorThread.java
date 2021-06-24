package com.wezik.app.thread;

import com.wezik.app.config.CoreConfig;
import com.wezik.app.domain.FullLog;
import com.wezik.app.domain.LogState;
import com.wezik.app.domain.PartialLog;
import com.wezik.app.mapper.FullLogMapper;
import com.wezik.app.service.database.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;

@Component
@RequiredArgsConstructor
public class LogCollectorThread implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(LogCollectorThread.class);

    private BlockingQueue<Optional<PartialLog>> queue;
    private final Map<String,PartialLog> logsMap = new HashMap<>();
    private final DatabaseService databaseService;
    private final CoreConfig coreConfig;
    private final FullLogMapper fullLogMapper;

    public void setQueue(BlockingQueue<Optional<PartialLog>> queue) {
        this.queue = queue;
    }

    private long total;
    private long flagged;
    private long flagTime = 4;

    @Override
    public void run() {
        LOGGER.info("Running collector");
        flagTime = coreConfig.getFlagTime();
        boolean notified = false;
        total = 0;
        flagged = 0;
        long startTime = System.currentTimeMillis();

        while(!notified) {
            try {
                Optional<PartialLog> partialLogOptional = queue.take();
                if (partialLogOptional.isPresent()) {
                    processPartialLog(partialLogOptional.get());
                } else {
                    notified = true;
                    LOGGER.info("Collector stopped");
                }
            } catch(InterruptedException e) {
                LOGGER.error("Queue got interrupted");
                LOGGER.debug(e.getMessage());
            }
        }
        printSummary(startTime);
    }

    private void processPartialLog(PartialLog partialLog) {
        if (logsMap.containsKey(partialLog.getId())) {
            parseLog(partialLog);
        } else {
            logsMap.put(partialLog.getId(), partialLog);
        }
    }

    private void parseLog(PartialLog initialLog) {
        PartialLog mapLog = logsMap.get(initialLog.getId());
        PartialLog partialLogStart = initialLog.getState().equals(LogState.STARTED.toString())
                ? initialLog : mapLog;
        PartialLog partialLogFinish = initialLog.getState().equals(LogState.FINISHED.toString())
                ? initialLog : mapLog;
        boolean flag = partialLogFinish.getTimestamp() - partialLogStart.getTimestamp() > flagTime;
        FullLog fullLog = fullLogMapper.mapPartialLogsToFullLog(partialLogStart,partialLogFinish,flag);
        saveLog(fullLog);
        if (flag) flagged++;
        total++;
    }

    private void saveLog(FullLog fullLog) {
        databaseService.save(fullLog);
    }

    private void printSummary(long timeStart) {
        long totalTimeMs = System.currentTimeMillis() - timeStart;
        Date date = new Date(totalTimeMs);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        LOGGER.info("Found "+total+" logs and flagged "+flagged+" total execution time "+formatter.format(date));

        LOGGER.info("Database contains "+databaseService.countTotal()+" logs, "+databaseService.countFlagged()+" of them are flagged");
    }

}
