# ButterDB
ButterDB is an application that facilitates an HTTP API so that applications can fetch and manipulate data using POJOs, JSON Serialization and Object-Relational Mapping techniques to store objects in and manipulate a Google spreadsheet.

Behind the scenes, ButterDB is powered by the [Google Sheets API](https://developers.google.com/sheets/api/reference/rest)
and [Google Visualization API](https://developers.google.com/chart/interactive/docs/reference) and utilizes the 
Java's SpringBoot Framework.


## Endpoints

### ButterDB Object Manipulation (DML)
Base URL: localhost:3000/api/v1/orm

| Method | Name | Endpoint | Description |
|-----|-----|-----|-----|
| GET | All Objects | /{ objectStorage } | Retrieve all objects contained in the storage.
| GET | Query Objects | /{ objectStorage }/{ constraints } | Query the storage for objects. [More documention on querying for objects.](/docs/butterdb-query.md)
| POST | Create Object(s) | /{ objectStorage }/create | Insert a new object into storage.

### ButterDB manipulation endpoints (DDL)
Base URL: localhost:3000/api/v1

| Method | Name | Endpoint | Description |
|-----|-----|-----|-----|
| PUT | Create object storage | /create/{ objectStorage } | Create storage for a new object model.
| DELETE | Delete object storage | /delete/{ objectStorage } | Delete storage for an old object model.

* {} denotes a variable

## Running ButterDB

First things first, Navigate to Google Drive and create a Google Spreadsheet this will hold the object storages. Copy the ID of the spreadsheet located in the URL and set the [spreadsheetID in the getGSpreadsheet @Bean](./src/main/java/com/github/megbailey/butter/ButterDBApp). 
Set api.google.secret to be a relative path to the json file in [applications.properties](./src/main/resources/applications.properties).

In order to interact with Google's Sheets services via a 3d party i.e. ButterDB, authenticate the application to act on your behalf.
To do this, follow these steps on the Google Cloud Project (GCP) console: 

- Create a new Google Cloud Project -> https://console.cloud.google.com/
- Enable the Google Sheets API -> https://console.cloud.google.com/apis/library/sheets.googleapis.com
- Create a service account for the project -> https://console.cloud.google.com/apis/credentials
- Navigate to 'Keys' in the service account menu and generate a JSON key. This will initiate a download.
- Rename the downloaded JSON file to client_secret.json and place in src/main/resources in this project.
- Head back to Google Drive to the new spreadsheet and share the new spreadsheet (with editor rights) with the service account email from GCP. 

### Steps to run 
./mvnw spring-boot:run
