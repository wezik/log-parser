package com.wezik.app.service;

import com.wezik.app.config.CoreConfig;
import com.wezik.app.domain.PartialLog;
import com.wezik.app.thread.LogCollectorThread;
import com.wezik.app.thread.LogParserThread;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Service
@RequiredArgsConstructor
public class LogsParserService {

    private final static Logger LOGGER = LoggerFactory.getLogger(LogsParserService.class);

    private final CoreConfig coreConfig;
    private final LogCollectorThread logCollectorThread;
    private final LogParserThread logParserThread;

    public void parseLogsToDatabase(String path) {

        BlockingQueue<Optional<PartialLog>> logsQueue = new ArrayBlockingQueue<>(coreConfig.getLogsQueue());

        logParserThread.setQueue(logsQueue);
        logParserThread.setPath(path);
        logCollectorThread.setQueue(logsQueue);

        Thread fileThread = new Thread(logParserThread);
        Thread databaseThread = new Thread(logCollectorThread);

        LOGGER.info("Initializing threads");
        fileThread.start();
        databaseThread.start();
    }

}
