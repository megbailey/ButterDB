package com.github.megbailey.gsheets.api.request;

import com.github.megbailey.gsheets.api.GAuthentication;
import com.github.megbailey.gsheets.api.request.APIRequest;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonObjectParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

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

    public Response executeGVizQuery(String query, Integer sheetID) throws IOException {
        Request request = new Request.Builder()
            .url( this.gVizEndpoint + "tq?tq=" + query + "&gid=" + sheetID)
            .method("GET", null)
            .addHeader("Authorization", "Bearer " + this.getAccessToken())
            .build();
        return client.newCall(request).execute();
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
