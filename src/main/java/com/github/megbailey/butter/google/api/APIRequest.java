package com.github.megbailey.butter.google.api;

import com.github.megbailey.butter.google.api.GAuthentication;
import com.github.megbailey.butter.google.exception.GAccessException;
import com.google.api.services.sheets.v4.Sheets;


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

    protected String getAccessToken() throws GAccessException {
        return this.gAuthentication.getAccessToken();
    }
}
