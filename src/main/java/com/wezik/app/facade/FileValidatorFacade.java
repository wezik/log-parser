package com.wezik.app.facade;

import com.wezik.app.validator.FileValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class FileValidatorFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileValidatorFacade.class);
    private final FileValidator fileValidator;

    public String getFilePathFromArgs(String[] args) {
        String[] entry = args;
        while (!fileValidator.isArgValid(entry)) {
            LOGGER.warn("File not found or is of invalid type");
            LOGGER.info("Please provide new file path");
            entry = new String[]{new Scanner(System.in).nextLine()};
        }
        LOGGER.info("File localized");
        return entry[0];
    }

}
