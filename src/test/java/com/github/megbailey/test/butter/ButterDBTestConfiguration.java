package com.github.megbailey.test.butter;

import com.github.megbailey.butter.db.ButterDBRepository;
import com.github.megbailey.butter.db.ButterDBService;
import com.github.megbailey.butter.google.GSpreadsheet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ButterDBTestConfiguration {

    @Bean(name = "butterDBService")
    public ButterDBService getButterDBService(ButterDBRepository butterTableRepository) {
        return new ButterDBService(butterTableRepository);
    }

    @Bean(name = "butterDBRepository")
    public ButterDBRepository getButterDBRepository(GSpreadsheet gSpreadsheet) {
        return new ButterDBRepository(gSpreadsheet);
    }

}
