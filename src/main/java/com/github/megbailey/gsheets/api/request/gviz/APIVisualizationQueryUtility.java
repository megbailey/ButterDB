package com.github.megbailey.gsheets.api.request.gviz;

import com.github.megbailey.gsheets.api.GAuthentication;
import com.github.megbailey.gsheets.api.request.APIRequest;
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
            //.addHeader("Cookie", "NID=511=n7v5FhhvlTrkTxdWCRFQeC9zS1hkoz981bF4ufWprpwTG2zMMK7kvoy2tyu8QDrnbkVXf6a3ndwOQnxUd4QGKxgoWcI1UeOfnjpscu584V8GJPtIYKG-w0kK_z0mn2zurxt8JAoTZDaMluNgzCdW9SClpk71ijfpYa47TWQdJN0")
            .build();
        return client.newCall(request).execute();
    }

}
