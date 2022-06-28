package com.github.megbailey.butter.table;

import com.github.megbailey.butter.ObjectModel;
import com.github.megbailey.google.exception.*;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api/v1/orm")
public class ButterTableController {
    private ButterTableService butterTableService;

    @Autowired
    ButterTableController(ButterTableService butterTableService)  {
        this.butterTableService = butterTableService;
    }

    @GetMapping("/")
    public String index() {
        return "GSheet ORM index.";
    }

    /*
        Get a GSheet - Return all data in the table
    */
    @GetMapping( path = "/{table}" )
    public ResponseEntity<String> all(@PathVariable("table") String tableName ) {
        try {
            JsonArray values = this.butterTableService.all(tableName);
            return ResponseEntity.status( HttpStatus.ACCEPTED ).body( values.toString() );
        } catch ( SheetNotFoundException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( "Resource was not found." );
        }  catch (GAccessException e) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.UNAUTHORIZED ).body( "Authenticate to continue." );
        } catch (CouldNotParseException e) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).body( "Unable to parse response." );

        }
    }

    /*
        Create objects in the table
    */
    @PostMapping( path = "/{table}/create", consumes = "application/json")
    public ResponseEntity<String> create( @PathVariable("table") String table,
                                          @RequestBody ObjectModel payload ) {
        try {
            this.butterTableService.create( table, payload );
            return ResponseEntity.status( HttpStatus.CREATED ).body( "Resource created." );
        } catch ( SheetNotFoundException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( "Table not found" );
        }  catch ( InvalidInsertionException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( "Unable to create resource." );
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
        }  catch ( InvalidQueryException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( "Invalid query parameters." );
        }  catch ( GAccessException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.UNAUTHORIZED ).body( "Unauthorized." );
        } catch ( SheetNotFoundException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( "Sheet not found." );
        } catch ( CouldNotParseException e ) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).body( "Unable to parse response." );
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
