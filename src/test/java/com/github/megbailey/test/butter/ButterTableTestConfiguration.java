package com.github.megbailey.test.butter;

import com.github.megbailey.butter.db.ButterTableService;
import com.github.megbailey.butter.google.GSpreadsheet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ButterTableTestConfiguration {

    @Bean(name = "butterTableService")
    public ButterTableService getButterTableService(GSpreadsheet gSpreadsheet) {
        return new ButterTableService(gSpreadsheet);
    }

}
