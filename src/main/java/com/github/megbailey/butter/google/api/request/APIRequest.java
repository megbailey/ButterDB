package com.github.megbailey.butter.google.api.request;

import com.github.megbailey.butter.google.api.GAuthentication;
import com.google.api.services.sheets.v4.Sheets;

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
