package com.wezik.app.runnable;

import com.google.gson.Gson;
import com.wezik.app.domain.PartLog;
import com.wezik.app.tracker.LogsProcessedProgressTracker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

@RequiredArgsConstructor
public class LogsReader implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(LogsReader.class);

    private final String filePath;
    private final BlockingQueue<Optional<PartLog>> readQueue;
    private final Gson gson = new Gson();
    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void run() {

        LOGGER.info("Starting reader");

        File file = new File(filePath);
        try {
            Reader reader = createReader(file);
            int c;
            while ((c = reader.read()) >= 0) {
                appendToLog(c);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read the file");
            e.printStackTrace();
        }
        LOGGER.info("Stopping reader");
        passToCollector(Optional.empty());
    }

    private Reader createReader(File file) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(file);
        BufferedInputStream bin = new BufferedInputStream(inputStream);
        return new InputStreamReader(bin, StandardCharsets.UTF_8);
    }

    private void appendToLog(int characterNumericValue) {
        LogsProcessedProgressTracker.PROCESSED_BYTES+=1;
        char c = (char) characterNumericValue;
        stringBuilder.append(c);
        if (c=='}') {
            PartLog partLog = gson.fromJson(stringBuilder.toString(),PartLog.class);
            passToCollector(Optional.of(partLog));
            stringBuilder.setLength(0);
        }
    }

    private void passToCollector(Optional<PartLog> optional) {
        try {
            readQueue.put(optional);
        } catch (InterruptedException e) {
            LOGGER.error("Queue interrupted while passing to collector");
            e.printStackTrace();
        }
    }

}
