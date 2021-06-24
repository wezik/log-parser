package com.wezik.app.service.database;

import com.wezik.app.domain.FullLog;
import com.wezik.app.repository.LogEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatabaseService {

    private final LogEntityRepository logEntityRepository;

    public FullLog save(FullLog fullLog) {
        return logEntityRepository.save(fullLog);
    }

    public long countFlagged() {
        return logEntityRepository.countFlagged();
    }

    public long countTotal() {
        return logEntityRepository.count();
    }

}
