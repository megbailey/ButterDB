package com.github.megbailey.butter.db;

import com.github.megbailey.butter.domain.ObjectModel;
import com.github.megbailey.butter.google.exception.GAccessException;
import com.github.megbailey.butter.google.exception.InvalidInsertionException;
import com.github.megbailey.butter.google.exception.InvalidQueryException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "/api/v1/orm")
public class ButterTableController {
    private ButterTableService butterTableService;

    @Autowired
    ButterTableController(ButterTableService butterTableService)  {
        this.butterTableService = butterTableService;
    }

    @GetMapping(path = "/")
    public ResponseEntity<String> index() {
        return ResponseEntity.status( HttpStatus.OK ).body( "GSheet ORM index." );
    }

    /*
        Get a GSheet - Return all data in the table
    */
    @GetMapping( path = "/{table}" )
    public ResponseEntity<String> all(@PathVariable("table") String tableName ) {
        try {
            JsonArray values = this.butterTableService.all(tableName);
            return ResponseEntity.status( HttpStatus.OK ).body( values.toString() );
        } catch ( ResourceNotFoundException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( e.getMessage() );
        }  catch ( GAccessException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.UNAUTHORIZED ).body( e.getMessage() );
        }
    }

    /*
        Create objects in the table
    */
    @PostMapping(
            path = "/{table}/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> create( @PathVariable("table") String table,
                                          @RequestBody List<ObjectModel> payload ) {
        try {
            this.butterTableService.create( table, payload );
            return ResponseEntity
                    .status( HttpStatus.CREATED )
                    .contentType( MediaType.APPLICATION_JSON )
                    .body( payload );
        } catch ( ResourceNotFoundException e ) {
            e.printStackTrace();
            return ResponseEntity
                    .status( HttpStatus.NOT_FOUND )
                    .contentType( MediaType.TEXT_PLAIN )
                    .body( e.getMessage() );
        } catch ( InvalidInsertionException e ) {
            e.printStackTrace();
            return ResponseEntity
                    .status( HttpStatus.BAD_REQUEST )
                    .contentType( MediaType.TEXT_PLAIN )
                    .body( e.getMessage() );
        }
    }

    /*
       Query objects in the table
    */
    @GetMapping(
            path = "/{table}/{constraints}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> query( @PathVariable("table") String tableName,
                                          @PathVariable("constraints") String constraints ) {
        try {
            String queryResults = this.butterTableService.query(tableName, constraints).toString();
            return ResponseEntity.status( HttpStatus.OK )
                    .contentType(MediaType.APPLICATION_JSON)
                    .body( queryResults );
        }  catch ( InvalidQueryException | ResourceNotFoundException | NullPointerException  e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.BAD_REQUEST )
                    .contentType(MediaType.TEXT_PLAIN)
                    .body( e.getMessage() );
        }  catch ( GAccessException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.UNAUTHORIZED )
                    .contentType(MediaType.TEXT_PLAIN)
                    .body( e.getMessage() );
        }
    }










    //TODO: Right now, it is only possible to delete by range because
    // GViz does not return affected rows.

    /*
        Delete a specific object in the table
    *//*

    @DeleteMapping( path = "/{table}/delete" )
    public @ResponseBody Object delete( @PathVariable String table, @RequestBody Object object ) {
        //return new ResponseEntity<>("delete item " , HttpStatus.OK);
        return null;
    }

    *//*
        Delete objects filtered by str in the table
    *//*

    @DeleteMapping( path = "/{table}/{delete}" )
    public @ResponseBody Object delete( @PathVariable String table, @PathVariable String deleteStr ) {
        //return new ResponseEntity<>("delete item " , HttpStatus.OK);
        return null;
    }*/

}
