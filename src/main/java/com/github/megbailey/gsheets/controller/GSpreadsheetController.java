package com.github.megbailey.gsheets.controller;

import com.github.megbailey.gsheets.GSpreadsheet;
import com.github.megbailey.gsheets.api.GAuthentication;
import com.github.megbailey.gsheets.api.request.APIBatchRequestUtility;
import com.github.megbailey.gsheets.api.request.APIRequestUtility;
import com.github.megbailey.gsheets.api.request.APIVisualizationQueryUtility;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/orm")
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


    /*

    */
    @PutMapping( path = "/create/{table}" )
    public @ResponseBody Object create( @PathVariable String sheetName ) {
        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);

        return null;
    }

    @PutMapping( path = "/create/{table}/{create}" )
    public @ResponseBody Object create( @PathVariable String table, @PathVariable Object object ) {
        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);
        return null;
    }

    /*

    */
    @DeleteMapping( path = "/delete/{table}/{delete}" )
    public @ResponseBody Object delete( @PathVariable String table, @PathVariable String deleteStr ) {
        //return new ResponseEntity<>("delete item " , HttpStatus.OK);
        return null;
    }

}
