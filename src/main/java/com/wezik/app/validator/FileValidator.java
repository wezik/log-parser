package com.wezik.app.validator;

import com.wezik.app.wrapper.FilesWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class FileValidator {

    private final FilesWrapper filesWrapper;

    public boolean isArgValid(String[] args) {
        String fileFormat = ".txt";
        return args.length >= 1 && args[0].endsWith(fileFormat) && filesWrapper.exists(Paths.get(args[0]));
    }
}
