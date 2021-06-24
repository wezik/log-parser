package com.wezik.app.runnable;

import com.wezik.app.tracker.LogsProcessedProgressTracker;
import com.wezik.app.domain.FullLog;
import com.wezik.app.domain.LogState;
import com.wezik.app.domain.PartLog;
import com.wezik.app.mapper.FullLogMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

@RequiredArgsConstructor
public class LogsCollector implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(LogsCollector.class);

    private final BlockingQueue<Optional<PartLog>> readQueue;
    private final BlockingQueue<Optional<FullLog>> saveQueue;
    private final FullLogMapper fullLogMapper;
    private final long flagTime;

    private final Map<String,PartLog> partLogsMap = new HashMap<>();

    @Override
    public void run() {

        LOGGER.info("Starting collector");

        boolean notified = false;

        while(!notified) {
            Optional<PartLog> optional = pullFromReadQueue();
            if (optional.isPresent()) {
                managePartLog(optional.get());
            } else {
                notified = true;
            }
        }
        LOGGER.info("Stopping collector");
        passToTaskManager(Optional.empty());
    }

    private Optional<PartLog> pullFromReadQueue() {
        try {
            return readQueue.take();
        } catch (InterruptedException e) {
            LOGGER.error("Queue interrupted while pulling from reader");
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void managePartLog(PartLog partLog) {
        if (partLogsMap.containsKey(partLog.getId())) {
            PartLog partLog2 = partLogsMap.get(partLog.getId());
            FullLog fullLog = mapToFullLog(partLog,partLog2);
            passToTaskManager(Optional.of(fullLog));
        } else partLogsMap.put(partLog.getId(),partLog);
    }

    private FullLog mapToFullLog(PartLog partLog, PartLog partLog2) {
        countLogs();
        if (partLog.getState().equals(LogState.STARTED.toString())) {
            long start = partLog.getTimestamp();
            long finish = partLog2.getTimestamp();
            boolean flag = (finish-start)>flagTime;
            countFlags(flag);
            return fullLogMapper.mapPartialLogsToFullLog(partLog,partLog2,flag);
        }
        long start = partLog2.getTimestamp();
        long finish = partLog.getTimestamp();
        boolean flag = (finish-start)>flagTime;
        countFlags(flag);
        return fullLogMapper.mapPartialLogsToFullLog(partLog2,partLog,flag);
    }

    private void passToTaskManager(Optional<FullLog> optional) {
         try {
             saveQueue.put(optional);
         } catch (InterruptedException e) {
             LOGGER.error("Queue interrupted while passing to saving task manager");
             e.printStackTrace();
         }
    }

    private void countFlags(boolean flag) {
        if (flag) LogsProcessedProgressTracker.TOTAL_LOGS_FLAGGED+=1;
    }


    private void countLogs() {
        LogsProcessedProgressTracker.TOTAL_LOGS_FOUND+=1;
    }

}
