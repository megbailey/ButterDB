package com.github.megbailey.google.gsheet;

import com.github.megbailey.google.GException;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


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
        try {
            return this.gSheetService.all(tableName).toString();
        } catch (IOException e) {
            JsonArray ar = new JsonArray();
            ar.add("noelements");
            return ar.toString();
        }
    }

    /*
       Filter objects in the table
    */
    @GetMapping( path = "/{table}/{constraints}" )
    public @ResponseBody Object filter( @PathVariable("table") String tableName, @PathVariable("constraints") String constraints ) {
        try {
            return this.gSheetService.query(tableName, constraints).toString();
        } catch (IOException | GException e) {
            System.out.println("issue with query");
            return null;
        }
    }



//    /*
//        Create objects in the table
//     */
//    @PostMapping( path = "/create/{table}" )
//    public @ResponseBody Object create( @PathVariable String table, @RequestBody Object object ) {
//        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);
//        return null;
//    }
//
//
//    /*
//        Delete a specific object in the table
//     */
//    @DeleteMapping( path = "/delete/{table}" )
//    public @ResponseBody Object delete( @PathVariable String table, @RequestBody Object object ) {
//        //return new ResponseEntity<>("delete item " , HttpStatus.OK);
//        return null;
//    }
//
//    /*
//        Delete objects filtered by str in the table
//    */
//    @DeleteMapping( path = "/delete/{table}/{delete}" )
//    public @ResponseBody Object delete( @PathVariable String table, @PathVariable String deleteStr ) {
//        //return new ResponseEntity<>("delete item " , HttpStatus.OK);
//        return null;
//    }





}
