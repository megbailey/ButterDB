package com.github.megbailey.butter.db;

import com.github.megbailey.butter.domain.ObjectModel;
import com.github.megbailey.butter.google.exception.GAccessException;
import com.github.megbailey.butter.google.exception.InvalidInsertionException;
import com.github.megbailey.butter.google.exception.InvalidQueryException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


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
        Get  data fromm a gsheet. all or query
    */
    @GetMapping( path = "/{table}" )
    public ResponseEntity<String> all( @PathVariable("table") String tableName,
                                       @RequestParam(required=false) Map<String,String> queryParams ) {
        try {
            if ( !queryParams.isEmpty()) {
                ArrayList<String> queryAr = new ArrayList<>();
                for (Map.Entry<String,String> entry : queryParams.entrySet()) {
                    queryAr.add( entry.getKey() + "=" + entry.getValue());
                }
                String queryStr = String.join("&", queryAr);
                JsonArray queryResults = this.butterTableService.query(tableName, queryStr);
                addClassProperty(tableName, queryResults);
                return ResponseEntity.status( HttpStatus.OK )
                        .contentType(MediaType.APPLICATION_JSON)
                        .body( queryResults.toString() );
            }
            JsonArray all = this.butterTableService.all(tableName);
            addClassProperty(tableName, all);
            return ResponseEntity.status( HttpStatus.OK ).body( all.toString() );
        } catch ( ResourceNotFoundException | ClassNotFoundException | InvalidQueryException  e) {
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
    */

    @DeleteMapping( path = "/{table}/delete" )
    public @ResponseBody Object delete( @PathVariable("table") String tableName,
                                        @RequestParam Map<String,String> queryParams ) {
        //return new ResponseEntity<>("delete item " , HttpStatus.OK);
        try {
            ArrayList<String> queryAr = new ArrayList<>();
            for (Map.Entry<String,String> entry : queryParams.entrySet()) {
                queryAr.add( entry.getKey() + "=" + entry.getValue());
            }
            String queryStr = String.join("&", queryAr);
            JsonArray queryResults = this.butterTableService.query(tableName, queryStr);
            queryResults = addClassProperty(tableName, queryResults);
            // Element does not exist
            if (queryResults.isEmpty())
                throw new ResourceNotFoundException();
            //Put elements we need to delete in an array list
            List<String> queryList = new ArrayList<>(queryResults.size());
            for (JsonElement el: queryResults) {
                queryList.add(el.toString());
            }
            // Calculate location from looping through all values. Unfortunately there is no better way to do this
            JsonArray allValues = this.butterTableService.all(tableName);
            addClassProperty(tableName, allValues);
            int count = 2; //data starts at row 2
            List<Integer> deleteLocations = new ArrayList<Integer>(queryList.size());
            for (JsonElement el: allValues) {
                System.out.println(el.toString());
                if (queryList.contains(el.toString()))
                    deleteLocations.add(count);
                count +=1;
            }
            System.out.println("delete me locations:" + deleteLocations);

            //TODO: actual delete
            //this.butterTableService.delete(tableName, );
            return ResponseEntity.status( HttpStatus.OK ).body( queryList.toString() );
        }  catch ( InvalidQueryException | ResourceNotFoundException | ClassNotFoundException  e ) {
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

    private JsonArray addClassProperty(String tableName, JsonArray data) throws ClassNotFoundException {
        // Interface implementations are placed in this package
        String thisPackage = this.getClass().getPackageName();
        String domainPackage = thisPackage.substring(0, thisPackage.lastIndexOf('.') +1 ) + "domain";
        //Class exists
        Class om = Class.forName(domainPackage + "." + tableName);
        //Add @class property from returned values
        for (JsonElement el: data ) {
            JsonObject obj = el.getAsJsonObject();
            obj.addProperty("@class", om.getCanonicalName());
        }
        return data;
    }

}
