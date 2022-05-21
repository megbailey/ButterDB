package com.github.megbailey.gsheets.controller;

import com.github.megbailey.gsheets.model.GSpreadsheetModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/orm")
public class GSpreadsheetController {

    private final GSpreadsheetModel gRepository;

    GSpreadsheetController(GSpreadsheetModel gRepository) {
        this.gRepository = gRepository;
    }

    @GetMapping("/")
    public String index() {
        return "GSheets ORM index";
    }


    @GetMapping( path = "/{table}" )
    public @ResponseBody Object all( @PathVariable String table ) {
        //return new ResponseEntity<>("filter item " , HttpStatus.ACCEPTED);
        return null;
    }

    //table name -> id
    //columns -> ids -> build & execute GViz
    @GetMapping( path = "/filter/{table}/{filter}" )
    public @ResponseBody Object filter( @PathVariable String table, @PathVariable String filterStr ) {
        //return new ResponseEntity<>("filter item " , HttpStatus.ACCEPTED);
        return null;
    }

    @PutMapping( path = "/create/{table}/{create}" )
    public @ResponseBody Object create( @PathVariable String table, @PathVariable Object object ) {
        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);
        return null;
    }

    @DeleteMapping( path = "/delete/{table}/{delete}" )
    public @ResponseBody Object delete( @PathVariable String table, @PathVariable String deleteStr ) {
        //return new ResponseEntity<>("delete item " , HttpStatus.OK);
        return null;
    }

}
