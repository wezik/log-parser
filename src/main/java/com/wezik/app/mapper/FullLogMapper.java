package com.wezik.app.mapper;

import com.wezik.app.domain.FullLog;
import com.wezik.app.domain.PartLog;
import org.springframework.stereotype.Component;

@Component
public class FullLogMapper {

    public FullLog mapPartialLogsToFullLog(PartLog startLog, PartLog finishLog, boolean flag) {
        return new FullLog(
                startLog.getId(),
                startLog.getType() != null ? startLog.getType() : finishLog.getType(),
                startLog.getHost() != null ? startLog.getHost() : finishLog.getHost(),
                finishLog.getTimestamp()-startLog.getTimestamp(),
                flag
        );
    }

}
