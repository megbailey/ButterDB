package com.github.megbailey.google.api.request;

import com.github.megbailey.google.api.GAuthentication;
import com.github.megbailey.google.exception.EmptyContentException;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.github.megbailey.google.exception.AccessException;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.*;

/*
    Google Visualization API Query Language documentation: https://developers.google.com/chart/interactive/docs/querylanguage
 */
public class APIVisualizationQueryUtility extends APIRequest {
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

    public static String buildQuery(Map<String, String> columns, String constraints) {

        //replace column labels with column ids
        for (String label: columns.keySet()) {
            constraints = constraints.replaceAll(label, columns.get(label));
        }

        //replace & with and
        constraints = constraints.replaceAll("&", " and ");
        //replace | with or
        //constraints = constraints.replaceAll("|", " or ");

        String gVizQuery = buildQuery(columns) + " where " + constraints;
        gVizQuery = gVizQuery.replaceAll(",", "%2C");
        return gVizQuery;
    }

    public JsonArray executeGVizQuery(Integer sheetID, String query) throws AccessException, EmptyContentException {
        query = HtmlUtils.htmlEscape(query);
        try {
            Request request = new Request.Builder()
                    .url(this.gVizEndpoint + "tq?tq=" + query + "&gid=" + sheetID)
                    .method("GET", null)
                    .addHeader("Authorization", "Bearer " + this.getAccessToken())
                    .build();
            JsonArray deserializedResponse = parseRowsFromGViz( client.newCall(request).execute() );
            return deserializedResponse;

        } catch ( IOException e ) {
            e.printStackTrace();
            throw new AccessException();
        }

    }


    public JsonArray parseRowsFromGViz(Response response) throws EmptyContentException {
        String preText = "google.visualization.Query.setResponse(";
        String postText = ");";
        JsonArray deserialResponse;
        try {
            String responseAsStr = response.body().string();
            String jsonResult = responseAsStr.substring(responseAsStr.indexOf(preText) + preText.length());
            jsonResult = jsonResult.substring(0, jsonResult.indexOf(postText));
            deserialResponse = JsonParser.parseString(jsonResult).getAsJsonObject()
                    .getAsJsonObject("table").getAsJsonArray("rows");
        } catch (IOException e) {
            e.printStackTrace();
            throw new EmptyContentException();
        }

        if (deserialResponse == null || deserialResponse.size() <= 0 )
            throw new EmptyContentException();

        return deserialResponse;

    }



}
