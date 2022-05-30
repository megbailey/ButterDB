package com.github.megbailey.google.gspreadsheet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RestController
@RequestMapping(path = "/api/v1/orm")
public class GSpreadsheetController {
    private GSpreadsheetService gSpreadsheetService;

    @Autowired
    GSpreadsheetController(GSpreadsheetService gSpreadsheetService)  {
        this.gSpreadsheetService = gSpreadsheetService;
    }

    @GetMapping("/")
    public String index() {
        return "GSpreadsheet ORM index.";
    }




    /*
        Create a GSheet - Return ID
    */
    @PostMapping( path = "/create/{table}" )
    public @ResponseBody Object create( @PathVariable String sheetName ) {
        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);

        return null;
    }

    /*
        Delete a GSheet - Return ID
    */
    @DeleteMapping( path = "/delete/{table}" )
    public @ResponseBody Object delete( @PathVariable String tableName ) {
        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);

        return null;
    }

    /*
        Delete a GSheet - Return name
    */
    @DeleteMapping( path = "/delete/{tableID}" )
    public @ResponseBody Object delete( @PathVariable Integer tableID ) {
        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);

        return null;
    }





}
