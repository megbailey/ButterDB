package com.github.megbailey.butter.google.api;

import com.github.megbailey.butter.google.GSheet;
import com.github.megbailey.butter.google.exception.BadResponse;
import com.github.megbailey.butter.google.exception.GAccessException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

import java.io.IOException;
import java.util.*;

/*
    Google Visualization API Query Language documentation
    https://developers.google.com/chart/interactive/docs/querylanguage
 */
public class APIVisualizationQueryUtility extends APIRequest {
    private static Logger logger = LogManager.getLogger(APIVisualizationQueryUtility.class.getName());
    private static String sheetsEndpoint = "https://docs.google.com/a/google.com/spreadsheets/d/";
    private static String gVizEndpoint;
    private OkHttpClient client;

    public APIVisualizationQueryUtility(GAuthentication gAuthentication)  {
        super(gAuthentication);
        this.gVizEndpoint = this.sheetsEndpoint + this.getSpreadsheetID() + "/gviz/";
        this.client = new OkHttpClient().newBuilder()
                .build();
    }

    public static String buildQuery(Map<String, String> columns) {
        String gVizQuery = "select ";

        List<String> columnIDs = columns.values().stream().sorted( Comparator.naturalOrder() ).toList();

        for (String id : columnIDs) {
            if (gVizQuery.substring(gVizQuery.length() - 1).equals(" "))
                gVizQuery += id;
            else
                gVizQuery += ", " + id;
        }

        return gVizQuery;
    }

    /*public static String buildQuery(

            *//*Map<String, String> columns, String constraints*//*
    ) {

        //replace column labels with column ids
        for (String label: columns.keySet()) {
            constraints = constraints.replaceAll(label, columns.get(label));
        }

        //replace '&' with 'and'
        constraints = constraints.replaceAll("&", " and ");
        //replace '|' with 'or'
        //constraints = constraints.replaceAll("|", " or ");

        String gVizQuery = buildQuery(columns) + " where " + constraints;
        System.out.println("gVizQuery " + gVizQuery);
        gVizQuery = gVizQuery.replaceAll(",", "%2C");
        return gVizQuery;
    }*/

    public JsonArray executeGVizQuery(GSheet gSheet, String query) throws GAccessException, BadResponse, IOException {
        Integer sheetID = gSheet.getID();
        query = escapeHtml4(query);
            Request request = new Request.Builder()
                    .url(this.gVizEndpoint + "tq?tq=" + query + "&gid=" + sheetID)
                    .method("GET", null)
                    .addHeader("Authorization", "Bearer " + this.getAccessToken())
                    .build();
            Response response = client.newCall(request).execute();
            JsonArray deserializedResponse = gVizResponseToJSON( gSheet, response );
            logger.info("Successful Google Visualization API call -> " + query);
            return deserializedResponse;


    }


    private JsonArray gVizResponseToJSON(GSheet gSheet, Response response) throws BadResponse {
        // The JSON response is surrounded by this object so first it must be stripped.
        String preText = "google.visualization.Query.setResponse(";
        String postText = ");";
        JsonArray deserializedResponse;

            String responseAsStr;
            ResponseBody responseBody = response.body();
            // Cant parse an empty response
            if ( responseBody == null) {
                throw new BadResponse();
            }
            //Something went wrong parsing
            try {
                responseAsStr  = responseBody.string();
            } catch ( IOException e ) {
                throw new BadResponse();
            }
            String jsonResult = responseAsStr.substring(responseAsStr.indexOf(preText) + preText.length());
            jsonResult = jsonResult.substring(0, jsonResult.indexOf(postText));
            deserializedResponse = JsonParser.parseString(jsonResult)
                    .getAsJsonObject()
                    .getAsJsonObject("table")
                    .getAsJsonArray("rows");

        logger.info("Converting response to a json array of objects.");
        return toJsonObjects(gSheet, deserializedResponse);

    }

    private JsonArray toJsonObjects(GSheet gSheet, JsonArray results) {
        Set<String> columnLabels = gSheet.getColumnLabels();

        // Iterate through each row in the response
        Iterator<JsonElement> rowIter = results.iterator();
        //Iterate through each element in the row
        Iterator<JsonElement> gVizElementIter;
        //Iterate through the list of column labels in order
        Iterator<String> columnIter;

        // New JSON Array that will store our formatted objects
        JsonArray formattedData = new JsonArray(results.size());
        // New JSON Object thats properly formatted for ORM
        JsonObject formattedObject;
        // Values from the parsed gViz
        JsonArray gVizRow; JsonElement el; JsonObject gVizElement;

        while( rowIter.hasNext() ) {
            gVizRow = rowIter.next().getAsJsonObject().get("c").getAsJsonArray();
            gVizElementIter = gVizRow.iterator();
            columnIter = columnLabels.iterator();
            formattedObject = new JsonObject();

            while( gVizElementIter.hasNext() && columnIter.hasNext() ) {
                String columnKey = columnIter.next();
                el = gVizElementIter.next();
                if ( el.isJsonObject() ) {
                    gVizElement = el.getAsJsonObject();

                    if (gVizElement.has("f")) {
                        formattedObject.add(columnKey, gVizElement.get("f"));
                    } else {
                        formattedObject.add(columnKey, gVizElement.get("v"));
                    }
                } /*else if ( el.isJsonNull() ) { // if json object is null do nothing
                    logger.warn("Unable to parse object -> " + el + "\n" +
                            "If bad manual entry occured or object aren't properly inserted then Java " +
                            "experiences get values we cant parse");
                  }*/
            }

            formattedData.add( formattedObject );
        }

        return formattedData;

    }



}
