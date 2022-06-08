package com.github.megbailey.google.api.request;

import com.github.megbailey.google.api.GAuthentication;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

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

    public static String buildQuery(Set<String> columnIDs) {
        String gVizQuery = "select ";
        for (String id : columnIDs) {
            if (gVizQuery.substring(gVizQuery.length() - 1).equals(" "))
                gVizQuery += id;
            else
                gVizQuery += ", " + id;
        }
        String gVizQueryHTML = StringEscapeUtils.escapeHtml4(gVizQuery);
        return gVizQueryHTML;
    }

    public static String buildQuery(Set<String> columnIDs, String[] constraints) {
        String gVizQueryHTML = buildQuery(columnIDs);
        for (String constraint: constraints) {

        }
        return gVizQueryHTML;
    }

    public JsonArray executeGVizQuery(Integer sheetID, String query) throws IOException {
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
