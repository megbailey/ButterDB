package com.github.megbailey.gsheets.controller;

import com.github.megbailey.gsheets.GSpreadsheet;
import com.github.megbailey.gsheets.api.GAuthentication;
import com.github.megbailey.gsheets.api.request.APIBatchRequestUtility;
import com.github.megbailey.gsheets.api.request.APIRequestUtility;
import com.github.megbailey.gsheets.api.request.APIVisualizationQueryUtility;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;


@RestController
@RequestMapping(path = "/api/v1/orm")
public class GSpreadsheetController {
    private GAuthentication gAuthentication;
    private GSpreadsheet gSpreadsheet;
    private APIVisualizationQueryUtility gVizRequestUtility;
    private APIRequestUtility regularRequestUtility;
    private APIBatchRequestUtility batchRequestUtility;

    GSpreadsheetController()  {
     }

    @GetMapping("/")
    public String index() {
        return "GSheets ORM index.Please authenticate to continue";
    }


    @GetMapping( path = "/auth" )
    public @ResponseBody Object authenticate( GAuthentication gAuthentication ) {

        this.gSpreadsheet = new GSpreadsheet(this.gAuthentication);
        this.gVizRequestUtility = new APIVisualizationQueryUtility(gAuthentication);
        this.regularRequestUtility = APIRequestUtility.getInstance(gAuthentication);
        this.batchRequestUtility = APIBatchRequestUtility.getInstance(gAuthentication);
        //return new ResponseEntity<>("filter item " , HttpStatus.ACCEPTED);
        return null;
    }


    /*
        Create a GSheet
    */
    @PutMapping( path = "/create/{table}" )
    public @ResponseBody Object create( @PathVariable String sheetName ) {
        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);

        return null;
    }


    @PutMapping( path = "/delete/{table}" )
    public @ResponseBody Object delete( @PathVariable String tableName ) {
        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);

        return null;
    }

    /*
        Delete a GSheet
    */
    @PutMapping( path = "/delete/{tableID}" )
    public @ResponseBody Object delete( @PathVariable Integer tableID ) {
        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);

        return null;
    }

}
