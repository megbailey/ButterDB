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
public class GSheetController {
    private GAuthentication gAuthentication;
    private GSpreadsheet gSpreadsheet;
    private APIVisualizationQueryUtility gVizRequestUtility;
    private APIRequestUtility regularRequestUtility;
    private APIBatchRequestUtility batchRequestUtility;

    GSheetController()  {
    }

    @GetMapping("/")
    public String index() {
        return "GSheets ORM index.Please authenticate to continue";
    }


    /*
        Return all data in the table
     */
    @GetMapping( path = "/{table}" )
    public @ResponseBody Object all( @PathVariable String table ) {
        //return new ResponseEntity<>("filter item " , HttpStatus.ACCEPTED);
        return null;
    }

    /*
        Filter objects in the table
     */
    //table name -> id
    //columns -> ids -> build & execute GViz
    @GetMapping( path = "/filter/{table}/{filter}" )
    public @ResponseBody Object filter( @PathVariable String table, @PathVariable String filterStr ) {
        System.out.println(filterStr);
        String[] filterAssignments = filterStr.split(",");

        String[] attributeValue;
        HashMap filterMap = new HashMap();
        for (String filter: filterAssignments) {
            attributeValue = filter.split("=");
            filterMap.put( attributeValue[0], attributeValue[1] );
        }
        Integer sheetID = this.gSpreadsheet.getGSheet(table).getID();
//        String query = this.gVizRequestUtility.formGVizQuery( filterMap, sheetID );
//
//        this.gVizRequestUtility.executeGVizQuery( query );
        //return new ResponseEntity<>("filter item " , HttpStatus.ACCEPTED);
        return null;
    }



    /*
        Create objects in the table
     */
    @PutMapping( path = "/create/{table}/{create}" )
    public @ResponseBody Object create( @PathVariable String table, @PathVariable Object object ) {
        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);
        return null;
    }

    /*
        Delete objects in the table
     */
    @DeleteMapping( path = "/delete/{table}/{delete}" )
    public @ResponseBody Object delete( @PathVariable String table, @PathVariable String deleteStr ) {
        //return new ResponseEntity<>("delete item " , HttpStatus.OK);
        return null;
    }

}
