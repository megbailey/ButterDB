package com.github.megbailey.gsheets.api.request;

import com.github.megbailey.gsheets.api.GAuthentication;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.oauth2.AccessToken;

import java.io.IOException;

public abstract class APIRequest {
    private GAuthentication gAuthentication;

    public APIRequest(GAuthentication gAuthentication) {
        this.gAuthentication = gAuthentication;
    }

    protected String getSpreadsheetID() {
        return this.gAuthentication.getSpreadsheetID();
    }

    protected Sheets getSheetsService() {
        return this.gAuthentication.getSheetsService();
    }

    protected String getAccessToken() throws IOException {
        return this.gAuthentication.getAccessToken();
    }
}
