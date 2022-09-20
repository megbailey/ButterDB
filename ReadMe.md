# ButterDB
ButterDB is an application that facilitates an HTTP API so that applications can fetch and manipulate data using POJOs (Plain-Old-Java-Objects), JSON Serialization and Object-Relational Mapping techniques to store objects/data in and manipulate a Google spreadsheet.

Behind the scenes, ButterDB's CRUD functionality is powered by the [Google Sheets API](https://developers.google.com/sheets/api/reference/rest)
and while query functionality utilizies the [Google Visualization API](https://developers.google.com/chart/interactive/docs/reference). Java's SpringBoot Framework facilitates the HTTP API.


## POJOs and Implementing ObjectModel
POJOs that implement the ObjectModel interface inherit the ability to be seralizied/deserilized into/from JSON which allows ButterDB to read & write any ObjectModel implementation. 
Valid JSON sent to ButterDB endpoints for storage creation, object creation, or queries is deserialized into ObjectModel subclasses by com.fasterxml.jackson.annotation package and must contain additional type information into the JSON to deserialize implementations of ObjectModel. This is achieved by the annotation in `@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)` in ObjectModel implementations.
A single running instance of ButterDB can process many different data models (ObjectModel implementations). Behind the scenes, ButterDB will use a different table/Google Sheet per model. Google has a limit of 5 million cells of data per spreadsheet which can be spreadout over any number of sheets.

This is a sample payload for the /{ objectStorage }/create endpoint to add a new object to storage where the @class field is set.
```
{
    "@class": "com.github.megbailey.butter.SampleObjectImpl",
    "ID": 1,
    "Property": ":)"
}
```

## Endpoints

**ButterDB Object Manipulation (DML)**
Base URL: localhost:3000/api/v1/orm

| Method | Name | Endpoint | Description |
|-----|-----|-----|-----|
| GET | All Objects | /{ objectStorage } | Retrieve all objects contained in the storage.
| GET | Query Objects | /{ objectStorage }/{ constraints } | Query the storage for objects. [More documention on querying for objects.](/docs/butterdb-query.md)
| POST | Create Object(s) | /{ objectStorage }/create | Insert a new object into storage.
| DELETE | Delete Object(s) | /{ objectStorage }/delete | TODO: Limitation w/ Google API. Queries do not return location of an object, so would need to fetch all and compute location. 
| DELETE | Delete from a query Object(s) | /{ objectStorage }/delete/{constraints} |  TODO: Limitation w/ Google API. Queries do not return location of an object, so would need to query and then fetch all to compute location.

**ButterDB Data Definition endpoints (DDL)**
Base URL: localhost:3000/api/v1

| Method | Name | Endpoint | Description |
|-----|-----|-----|-----|
| PUT | Create object storage | /create/{ objectStorage } | Create storage for a new object model. Equivalent of adding a sheet to a Google spreadsheet.
| DELETE | Delete object storage | /delete/{ objectStorage } | Delete storage for an old object model. Equivalent of deleteing a sheet from a Google spreadsheet.

note: **{}** denotes your ObjectModel implementation

## Running ButterDB

First things first, ButterDB's foundation is a Google Spreadsheet. 
Navigate to Google Drive and create a Google Spreadsheet. This will hold the object storages. 
Copy the ID of the spreadsheet located in the URL and set the [spreadsheetID in the getGSpreadsheet @Bean](./src/main/java/com/github/megbailey/butter/ButterDBApp). 
Set api.google.secret to be a relative path to the json file in [applications.properties](./src/main/resources/applications.properties).

In order to interact with Google's Sheets services via a 3d party i.e. ButterDB, you'll need to tell Google to give the application permission to use your Google acct.
To do this, follow these steps on the Google Cloud Project (GCP) console: 

- Create a new Google Cloud Project -> https://console.cloud.google.com/
- Enable the Google Sheets API -> https://console.cloud.google.com/apis/library/sheets.googleapis.com
- Create a service account for the project -> https://console.cloud.google.com/apis/credentials
- Navigate to 'Keys' tab in the service account menu and generate a JSON key. This will initiate a download of a file.
- Rename the downloaded JSON file to client_secret.json and place in src/main/resources in this project.
- Head back to Google Drive to the new spreadsheet and share the new spreadsheet (with editor rights) with the service account email from GCP. 

### Steps to run 
./mvnw spring-boot:run
