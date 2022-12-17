package com.github.megbailey.test.butter;

import com.github.megbailey.butter.db.ButterTableRepository;
import com.github.megbailey.butter.db.ButterTableService;
import com.github.megbailey.butter.google.GSpreadsheet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ButterTableTestConfiguration {

    @Bean(name = "butterTableService")
    public ButterTableService getButterTableService(ButterTableRepository butterTableRepository) {
        return new ButterTableService(butterTableRepository);
    }

    @Bean(name = "butterTableRepository")
    public ButterTableRepository getButterTableRepository(GSpreadsheet gSpreadsheet) {
        return new ButterTableRepository(gSpreadsheet);
    }

}
