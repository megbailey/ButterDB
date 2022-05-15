package com.github.megbailey.gsheets.orm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
    This is the main thread of the execution.
 */
@SpringBootApplication
public class ORMWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(ORMWebApplication.class, args);
        //GSpreadsheet spreadsheet = new GSpreadsheet();
        //service.authenticate();
    }

   /* @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("Let's inspect the beans provided by Spring Boot:");
            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }
        };
    }*/
}
