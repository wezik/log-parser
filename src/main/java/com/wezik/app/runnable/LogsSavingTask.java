package com.wezik.app.runnable;

import com.wezik.app.tracker.LogsProcessedProgressTracker;
import com.wezik.app.domain.FullLog;
import com.wezik.app.service.database.DatabaseService;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class LogsSavingTask implements Runnable {

    private final DatabaseService databaseService;
    private final Set<FullLog> logs;

    @Override
    public void run() {
        LogsProcessedProgressTracker.TOTAL_LOGS_SAVED+=logs.size();
        databaseService.saveAll(logs);
    }

}
