package com.wezik.app.thread;

import com.google.gson.Gson;
import com.wezik.app.domain.PartialLog;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

@Component
@RequiredArgsConstructor
public class LogParserThread implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(LogParserThread.class);

    private BlockingQueue<Optional<PartialLog>> queue;
    private String path;
    private final Gson gson;

    public void setQueue(BlockingQueue<Optional<PartialLog>> queue) {
        this.queue = queue;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void run() {
        LOGGER.info("Running parser");
        File file = new File(path);
        try {
            parse(file);
        } catch (IOException e) {
            LOGGER.error("Failed to read of the file");
            LOGGER.debug(e.getMessage());
        }
    }

    private void parse(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        Reader reader = readerFromFile(file);
        int charNumValue;
        while ((charNumValue = reader.read()) >= 0) {
            sb.append((char)charNumValue);
            if (charNumValue == '}') {
                try {
                    parseLog(sb);
                } catch (InterruptedException e) {
                    LOGGER.error("Queue got interrupted");
                    LOGGER.debug(e.getMessage());
                }
            }
        }
        try {
            LOGGER.info("Parser stopped");
            stopCollectorThread();
        } catch (InterruptedException e) {
            LOGGER.error("Failed to stop collector");
            LOGGER.debug(e.getMessage());
        }
    }

    private Reader readerFromFile(File file) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(file);
        BufferedInputStream bin = new BufferedInputStream(inputStream);
        return new InputStreamReader(bin, StandardCharsets.UTF_8);
    }

    private void parseLog(StringBuilder sb) throws InterruptedException {
        Optional<PartialLog> optional = Optional.of(gson.fromJson(sb.toString(), PartialLog.class));
        queue.put(optional);
        sb.setLength(0);
    }

    private void stopCollectorThread() throws InterruptedException {
        queue.put(Optional.empty());
    }

}
