package com.github.megbailey.database;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/table")
public class TableController {

    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }


    //Requires Schema
    @GetMapping(
            path = "/create",
            consumes = "application/json",
            produces = "application/json")
    public String createTable() {
        return "";
    }

    @GetMapping(
            path = "/update/schema",
            consumes = "application/json",
            produces = "application/json")
    public String updateTableSchema() {
        return "";
    }

}
