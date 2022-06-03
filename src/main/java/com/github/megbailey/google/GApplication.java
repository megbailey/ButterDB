package com.github.megbailey.google;

import com.github.megbailey.google.gspreadsheet.GSpreadsheet;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/*
    This is the main thread which starts the ORM.
*/
@SpringBootApplication
public class GApplication {
    private static GLogHandler logger = new GLogHandler();

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(GApplication.class, args);
    }

/*
    The GSpreadsheet bean is given to both the GSpreadsheet & GSheet Repositories
*/
    @Bean
    public GSpreadsheet getGSpreadsheet() {
        GSpreadsheet gSpreadsheet = new GSpreadsheet("1hKQc8R7wedlzx60EfS820ZH5mFo0gwZbHaDq25ROT34");
        return gSpreadsheet;
    }
}
