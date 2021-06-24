package com.wezik.app.facade;

import com.wezik.app.service.LogsService;
import com.wezik.app.tracker.LogsProcessedProgressTracker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class LogsFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogsFacade.class);

    private final LogsService logsService;

    public void processFile(String path) {
        File file = new File(path);
        String fileName = file.getName();
        long fileSize = file.length();
        LogsProcessedProgressTracker.FILE_SIZE = fileSize;

        LOGGER.info("Starting processing of " + fileName + " " + fileSize/1048576 + " MB");
        logsService.parseLogsToDatabase(path);
    }

}
