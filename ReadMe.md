# ButterDB
ButterDB is an application that facilitates an HTTP API so that applications can fetch and manipulate data using POJOs (Plain-Old-Java-Objects), JSON Serialization and Object-Relational Mapping techniques to store objects/data and manipulate a Google Spreadsheet referred to as 'object storage'.

Behind the scenes, ButterDB's CRUD functionality is powered by the [Google Sheets API](https://developers.google.com/sheets/api/reference/rest)
and query functionality utilizies the [Google Visualization API](https://developers.google.com/chart/interactive/docs/reference). Java's SpringBoot Framework facilitates the HTTP API.


## POJOs and Implementing ObjectModel
POJOs that implement the ObjectModel interface inherit the ability to be seralizied/deserilized into/from JSON which allows ButterDB to read & write any ObjectModel implementation. 

Valid JSON sent to ButterDB endpoints for storage creation, object creation, or queries are deserialized into ObjectModel subclasses by com.fasterxml.jackson and must contain additional type information in the JSON to deserialize implementations of ObjectModel. As a result, a JSON object must contain the additional property "@class" which is implemented by the following annotation at the top of your ObjectModel implementations.
 `@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)`

A single running instance of ButterDB can process many different data models (ObjectModel implementations). Behind the scenes, ButterDB will use a different table/Google Sheet per model.

This is a sample payload for the /{ objectStorage }/create endpoint to add a new object to storage where the @class field is set.
```
{
    "@class": "com.github.megbailey.butter.SampleObjectImpl",
    "ID": 1,
    "Property": ":)"
}
```

Once you've created your ObjectModel implementation, place it alongside the ObjectModel.java and recompile the application. ButterDB will now recognize your ObjectModel implementation. See below for other setup instructions.

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

## Limitations
ButterDB relies on Google's API and thus is subject to its API Usage Requirements. See below or refer to [google's usage documentation](https://developers.google.com/docs/api/limits).
<img width="772" alt="Screen Shot 2022-09-20 at 9 49 53 AM" src="https://user-images.githubusercontent.com/32280319/191318296-5a181712-da3d-4da0-a2d1-419037a864b2.png">

If you plan on running multiple instance of ButterDB, consider creating a google acct just for that instance.

Additionally, Google has a limit of 5 million cells of data per spreadsheet which can be spreadout over any number of sheets. Therefore, ButterDB is limited by how much data it can process, but it is not limited how many different object models it can process. This means ButterDB is best suited for smaller projects especially those that dont warrant the efficiency of a traditional DB.

## Setup 
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
