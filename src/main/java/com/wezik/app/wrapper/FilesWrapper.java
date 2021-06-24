package com.wezik.app.wrapper;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FilesWrapper {

    public boolean exists(Path path) {
        return Files.exists(path);
    }

}
