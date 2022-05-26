package com.github.megbailey.gsheets.mvc;

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
        return "GSheets ORM index.";
    }


    /*
        Create a GSheet
    */
    @PostMapping( path = "/create/{table}" )
    public @ResponseBody Object create( @PathVariable String sheetName ) {
        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);

        return null;
    }

    /*
        Delete a GSheet
    */
    @DeleteMapping( path = "/delete/{table}" )
    public @ResponseBody Object delete( @PathVariable String tableName ) {
        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);

        return null;
    }

    /*
        Delete a GSheet
    */
    @DeleteMapping( path = "/delete/{tableID}" )
    public @ResponseBody Object delete( @PathVariable Integer tableID ) {
        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);

        return null;
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
        Integer sheetID = this.gSpreadsheetService.getGSheet(table).getID();
//        String query = this.gVizRequestUtility.formGVizQuery( filterMap, sheetID );
//
//        this.gVizRequestUtility.executeGVizQuery( query );
        //return new ResponseEntity<>("filter item " , HttpStatus.ACCEPTED);
        return null;
    }



    /*
        Create objects in the table
     */
    @PostMapping( path = "/create/{table}" )
    public @ResponseBody Object create( @PathVariable String table, @RequestBody Object object ) {
        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);
        return null;
    }


    /*
        Delete a specific object in the table
     */
    @DeleteMapping( path = "/delete/{table}" )
    public @ResponseBody Object delete( @PathVariable String table, @RequestBody Object object ) {
        //return new ResponseEntity<>("delete item " , HttpStatus.OK);
        return null;
    }

    /*
        Delete objects filtered by str in the table
    */
    @DeleteMapping( path = "/delete/{table}/{delete}" )
    public @ResponseBody Object delete( @PathVariable String table, @PathVariable String deleteStr ) {
        //return new ResponseEntity<>("delete item " , HttpStatus.OK);
        return null;
    }

}
