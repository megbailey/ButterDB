package com.github.megbailey.butter.db;

import com.github.megbailey.butter.domain.ObjectModel;
import com.github.megbailey.butter.google.exception.GAccessException;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping(path = "/api/v1/orm")
public class ButterTableController {
    private ButterTableService butterTableService;

    @Autowired
    ButterTableController(ButterTableService butterTableService) {
        this.butterTableService = butterTableService;
    }

    @GetMapping(path = "/")
    public ResponseEntity<String> index() {
        return ResponseEntity.status(HttpStatus.OK).body("GSheet ORM index.");
    }

    /*
        Get  data fromm a gsheet. all or query
    */
    @GetMapping(path = "/{table}")
    public ResponseEntity<String> all(@PathVariable("table") String tableName,
                                      @RequestParam(required = false) Map<String, String> queryParams) {
        try {
            if (!queryParams.isEmpty()) {
                ArrayList<String> queryAr = new ArrayList<>();
                for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                    queryAr.add(entry.getKey() + "=" + entry.getValue());
                }
                String queryStr = String.join("&", queryAr);
                JsonArray queryResults = this.butterTableService.query(tableName, queryStr);
                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(queryResults.toString());
            }
            JsonArray all = this.butterTableService.all(tableName);
            return ResponseEntity.status(HttpStatus.OK).body(all.toString());
        } catch (ResourceNotFoundException | ClassNotFoundException | BadRequestException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (GAccessException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    /*
        Create objects in the table
    */
    @PostMapping(
            path = "/{table}/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> create(@PathVariable("table") String table,
                                         @RequestBody List<ObjectModel> payload) {
        try {
            this.butterTableService.create(table, payload);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload);
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(e.getMessage());
        } catch (BadRequestException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(e.getMessage());
        }
    }

    /*
        Delete objects filtered by str in the table
    */
    @DeleteMapping(path = "/{table}/delete")
    public @ResponseBody
    ResponseEntity<Object> delete(@PathVariable("table") String tableName,
                                  @RequestParam Map<String, String> queryParams) {
        try {
            // Params are required
            if ( !queryParams.isEmpty() ) {
                ArrayList<String> queryAr = new ArrayList<>();
                for( Map.Entry<String, String> entry : queryParams.entrySet() ) {
                    queryAr.add(entry.getKey() + "=" + entry.getValue());
                }
                String queryStr = String.join("&", queryAr);
                boolean results = this.butterTableService.delete(tableName, queryStr);
                if (results)
                    return ResponseEntity.status(HttpStatus.OK).body("");
            }
            throw new BadRequestException();
        } catch (ResourceNotFoundException | BadRequestException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_PLAIN)
                .body(e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_PLAIN)
                .body(e.getMessage());
        } catch (GAccessException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.TEXT_PLAIN)
                .body(e.getMessage());
        }

    }
}
