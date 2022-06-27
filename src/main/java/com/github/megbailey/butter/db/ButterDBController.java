package com.github.megbailey.butter.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1")
public class ButterDBController {
    private ButterDBService butterDBService;

    @Autowired
    ButterDBController(ButterDBService butterDBService)  {
        this.butterDBService = butterDBService;
    }

    @GetMapping("/")
    public String index() {
        return "Welcome to ButterD!";
    }


    /*
        Create a GSheet
        TODO: pass in a object structure and make post mapping
    */
    @PutMapping( path = "/create/{newTable}" )
    public ResponseEntity<String> create( @PathVariable("newTable") String sheetName ) {
        try {
            this.butterDBService.create(sheetName);
            return ResponseEntity.status( HttpStatus.CREATED ).body( "Resource created" );
        } catch ( ButterDBException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( "Could not create resource." );
        }
    }

    /*
        Create a GSheet Error
    */
    @PutMapping( path = "/create" )
    public ResponseEntity<String> create( ) {
        return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( "Resource was not found on the server." );
    }


    /*
        Delete an object table in the ButterDB. Equivalent of deleting a Google Sheet.
        TODO: pass in object structure and make delete mapping
    */
    @DeleteMapping( path = "/delete/{tableName}" )
    public ResponseEntity<String> delete(@PathVariable("tableName") String tableName ) {
        try {
            this.butterDBService.delete(tableName);
            return ResponseEntity.status( HttpStatus.ACCEPTED ).body( "Resource deleted" );
        } catch ( ButterDBException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( "Could not delete resource." );
        }
    }

    /*
        Bad path for deleting a table in the
    */
    @DeleteMapping( path = "/delete" )
    public ResponseEntity<String> delete( ) {
        return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( "Resource was not found on the server." );
    }






}
