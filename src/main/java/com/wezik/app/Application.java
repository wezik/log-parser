package com.wezik.app;

import com.wezik.app.facade.FileValidatorFacade;
import com.wezik.app.facade.LogsFacade;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class);
        LogsFacade logsFacade = context.getBean(LogsFacade.class);
        FileValidatorFacade fileValidatorFacade = context.getBean(FileValidatorFacade.class);
        String filePath = fileValidatorFacade.getFilePathFromArgs(args);
        logsFacade.processFile(filePath);
    }
}
