package com.wezik.app.service.database;

import com.wezik.app.domain.FullLog;
import com.wezik.app.repository.LogEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatabaseService {

    private final LogEntityRepository logEntityRepository;

    public Iterable<FullLog> saveAll(Iterable<FullLog> logs) {
        return logEntityRepository.saveAll(logs);
    }

    public long countFlagged() {
        return logEntityRepository.countFlagged();
    }

    public long countTotal() {
        return logEntityRepository.count();
    }

}
