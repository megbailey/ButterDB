package com.github.megbailey.google.gsheet;

import com.github.megbailey.google.ObjectModel;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api/v1/orm")
public class GSheetController {
    private GSheetService gSheetService;

    @Autowired
    GSheetController(GSheetService gSheetService)  {
        this.gSheetService = gSheetService;
    }

    @GetMapping("/")
    public String index() {
        return "GSheet ORM index.";
    }

    /*
        Get a GSheet - Return all data in the table
    */
    @GetMapping( path = "/{table}" )
    public @ResponseBody String all(@PathVariable("table") String tableName ) {
        JsonArray values = this.gSheetService.all(tableName);
        return values.toString();
    }

    /*
       Query objects in the table
    */
    @GetMapping( path = "/{table}/{constraints}", produces = "application/json" )
    public @ResponseBody Object filter( @PathVariable("table") String tableName,
                                        @PathVariable("constraints") String constraints ) {
        return this.gSheetService.query(tableName, constraints).toString();
    }

    /*
        Create objects in the table
    */
    @PostMapping( path = "/{table}/create", consumes = "application/json")
    public @ResponseBody void create( @PathVariable("table") String table,
                                      @RequestBody ObjectModel payload ) {
        System.out.println(payload.toString());
        System.out.println(payload.toJson());
        System.out.println(payload.listValues());
        //this.gSheetService.create(table, payload);
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
