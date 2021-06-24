package com.wezik.app.facade;

import com.wezik.app.service.LogsParserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogsFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogsFacade.class);

    private final LogsParserService logsParserService;

    public void processFile(String path) {
        LOGGER.info("Starting processing of the file...");
        logsParserService.parseLogsToDatabase(path);
    }

}
