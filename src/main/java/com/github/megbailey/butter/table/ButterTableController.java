package com.github.megbailey.butter.table;

import com.github.megbailey.butter.ObjectModel;
import com.github.megbailey.google.exception.*;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
            if (values != null)
                return ResponseEntity.status( HttpStatus.ACCEPTED ).body( values.toString() );
            throw new NullPointerException();
        } catch ( ResourceNotFoundException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( e.getMessage() );
        }  catch ( GAccessException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.UNAUTHORIZED ).body( e.getMessage() );
        } catch ( NullPointerException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).body( e.getMessage() );

        }
    }

    /*
        Create objects in the table
    */
    @PostMapping( path = "/{table}/create", consumes = "application/json")
    public ResponseEntity<String> create( @PathVariable("table") String table,
                                          @RequestBody List<ObjectModel> payload ) {
        try {
            this.butterTableService.create( table, payload );
            return ResponseEntity.status( HttpStatus.CREATED ).body( "Resource created." );
        } catch ( ResourceNotFoundException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( e.getMessage() );
        } catch ( InvalidInsertionException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( e.getMessage() );
        }
    }

    /*
       Query objects in the table
    */
    @GetMapping( path = "/{table}/{constraints}", produces = "application/json" )
    public ResponseEntity<String> query( @PathVariable("table") String tableName,
                                          @PathVariable("constraints") String constraints ) {
        try {
            String queryResults = this.butterTableService.query(tableName, constraints).toString();
            return ResponseEntity.status( HttpStatus.ACCEPTED ).body( queryResults );
        }  catch ( InvalidQueryException | ResourceNotFoundException | NullPointerException  e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( e.getMessage() );
        }  catch ( GAccessException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.UNAUTHORIZED ).body( e.getMessage() );
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
