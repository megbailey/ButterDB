package com.github.megbailey.google.api.request;

import com.github.megbailey.google.api.GAuthentication;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

        for (String label : columns.keySet()) {
            if (gVizQuery.substring(gVizQuery.length() - 1).equals(" "))
                gVizQuery += columns.get(label);
            else
                gVizQuery += ", " + columns.get(label);
        }

        return gVizQuery;
    }

    public static String buildQuery(Map<String, String> columns, String constraints) {

        //replace colummn labels with column ids
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

    public JsonArray executeGVizQuery(Integer sheetID, String query) throws IOException {
        query = HtmlUtils.htmlEscape(query);
        Request request = new Request.Builder()
            .url( this.gVizEndpoint + "tq?tq=" + query + "&gid=" + sheetID)
            .method("GET", null)
            .addHeader("Authorization", "Bearer " + this.getAccessToken())
            .build();
        return parseGVizResponse( client.newCall(request).execute() );
    }

    public JsonArray parseGVizResponse(Response response) throws IOException {
        String preText = "google.visualization.Query.setResponse(";
        String postText = ");";
        String responseAsStr = response.body().string();
        String jsonResult = responseAsStr.substring(responseAsStr.indexOf(preText) + preText.length());
        jsonResult = jsonResult.substring(0, jsonResult.indexOf(postText));
        return JsonParser.parseString(jsonResult).getAsJsonObject()
                .getAsJsonObject("table").getAsJsonArray("rows");
    }

}
