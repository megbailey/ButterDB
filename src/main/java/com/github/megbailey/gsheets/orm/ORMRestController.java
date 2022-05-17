package com.github.megbailey.gsheets.orm;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/orm")
public class ORMRestController {

    @GetMapping("/")
    public String index() {
        return "GSheets ORM index";
    }


    @GetMapping( path = "/filter/{filter}" )
    public @ResponseBody Object filter(@PathVariable String filterStr) {
        //return new ResponseEntity<>("filter item " , HttpStatus.ACCEPTED);
        return null;
    }

    @PutMapping( path = "/create/{create}" )
    public @ResponseBody Object create(@PathVariable Object object) {
        //return new ResponseEntity<>("create item " , HttpStatus.CREATED);
        return null;
    }

    @DeleteMapping( path = "/delete/{delete}" )
    public @ResponseBody Object delete(@PathVariable String deleteStr) {
        //return new ResponseEntity<>("delete item " , HttpStatus.OK);
        return null;
    }

}
