package com.github.megbailey.butter.google.api;

import com.github.megbailey.butter.google.exception.SystemErrorException;
import com.github.megbailey.butter.google.exception.GoggleAccessException;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

import java.io.IOException;

/*
    Google Visualization API Query Language documentation
    https://developers.google.com/chart/interactive/docs/querylanguage
 */
public class APIVisualizationQueryUtility extends APIRequest {
    private static final Logger logger = LogManager.getLogger(APIVisualizationQueryUtility.class.getName());
    private static String gVizEndpoint;
    private final OkHttpClient client;

    public APIVisualizationQueryUtility(GAuthentication gAuthentication) {
        super(gAuthentication);
        String sheetsEndpoint = "https://docs.google.com/a/google.com/spreadsheets/d/";
        gVizEndpoint = sheetsEndpoint + this.getSpreadsheetID() + "/gviz/";
        this.client = new OkHttpClient().newBuilder()
                .build();
    }

    /*public static String buildQuery(Map<String, String> columns) {
        StringBuilder gVizQuery = new StringBuilder("select ");

        List<String> columnIDs = columns.values().stream().sorted(Comparator.naturalOrder()).toList();

        for (String id : columnIDs) {
            if (gVizQuery.toString().endsWith(" "))
                gVizQuery.append(id);
            else
                gVizQuery.append(", ").append(id);
        }

        return gVizQuery.toString();
    }*/

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

    public JsonArray executeGVizQuery(Integer sheetID, String query) throws GoggleAccessException, SystemErrorException, IOException {
        query = escapeHtml4(query);
        Request request = new Request.Builder()
                .url(gVizEndpoint + "tq?tq=" + query + "&gid=" + sheetID)
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + this.getAccessToken())
                .build();
        Response response = client.newCall(request).execute();
        JsonArray deserializedResponse = parseGVizResponse(response);
        logger.info("Successful Google Visualization API call -> " + query);
        return deserializedResponse;
    }


    private JsonArray parseGVizResponse(Response response) throws SystemErrorException {
        // The JSON response is surrounded by this string, so, first, it must be stripped.
        String preText = "google.visualization.Query.setResponse(";
        String postText = ");";

        String responseAsStr;
        ResponseBody responseBody = response.body();
        // Cant parse an empty response or something went wrong parsing
        if (responseBody == null) {
            throw new SystemErrorException("Response body is null");
        }
        try {
            responseAsStr = responseBody.string();
        } catch (IOException e) {
            throw new SystemErrorException("Something went wrong parsing response body.");
        }
        String jsonResult = responseAsStr.substring(responseAsStr.indexOf(preText) + preText.length());
        jsonResult = jsonResult.substring(0, jsonResult.indexOf(postText));
        JsonArray deserializedResponse;

        // Retrieve JSON rows from response
        try {
            deserializedResponse = JsonParser.parseString(jsonResult)
                .getAsJsonObject()
                .getAsJsonObject("table")
                .getAsJsonArray("rows");
        } catch ( JsonParseException e ) {
            throw new SystemErrorException(e.getMessage());
        }

        return deserializedResponse;
    }

}
