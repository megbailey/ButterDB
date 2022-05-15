package com.github.megbailey.gsheets.orm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/orm")
public class ORMController {

    @GetMapping("/")
    public String index() {
        return "GSheets ORM index";
    }


    @GetMapping(
            path = "/filter/{filter}",
            consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<String> filterItems() {
        return "";
    }

    @PutMapping(
            path = "/create/{create}",
            consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<String> createItem(@PathVariable("name") String name) {
        try {
            return new ResponseEntity<>("create item " , HttpStatus.CREATED);
        } catch () {
            return new ResponseEntity<>("malformed create ", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(
            path = "/delete/{delete}")
    public ResponseEntity<String> deleteItems() {
        try {
            return new ResponseEntity<>("delete item " , HttpStatus.CREATED);
        } catch () {
            return new ResponseEntity<>("malformed delete ", HttpStatus.BAD_REQUEST);
        }
    }

}
