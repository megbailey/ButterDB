package com.github.megbailey.google.gspreadsheet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1")
public class GSpreadsheetController {
    private GSpreadsheetService gSpreadsheetService;

    @Autowired
    GSpreadsheetController(GSpreadsheetService gSpreadsheetService)  {
        this.gSpreadsheetService = gSpreadsheetService;
    }

    @GetMapping("/")
    public String index() {
        return "GSpreadsheet.";
    }


    /*
        Create a GSheet
        TODO: pass in a object structure
    */
    @PutMapping( path = "/create/{newTable}" )
    public @ResponseBody boolean create( @PathVariable("newTable") String sheetName ) {
        return this.gSpreadsheetService.create(sheetName);
    }


//
//    /*
//        Delete a GSheet - Return name
//    */
//    @DeleteMapping( path = "/delete/{tableID}" )
//    public @ResponseBody Object delete( @PathVariable Integer tableID ) {
//        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);
//
//        return null;
//    }






}
