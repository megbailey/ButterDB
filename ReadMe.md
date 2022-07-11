# ButterDB
ButterDB is an application that facilitates an HTTP API so that applications can fetch & persist data with a Google spreadsheet using POJOs and object-relational mapping techniques.

Behind the scenes, ButterDB is powered by the [Google Sheets API](https://developers.google.com/sheets/api/reference/rest)
and [Google Visualization API](https://developers.google.com/chart/interactive/docs/reference) and utilizes the 
Java's SpringBoot Framework.


## Endpoints

### ButterDB Object Manipulation (DML) -> Base URL: localhost:3000/api/v1/orm

| Method | Name | Endpoint | Description |
| GET | All Objects | /{ objectStorage } | Retrieve all objects contained in the storage
| GET | Query Objects | /{ objectStorage }/{ constraints } | Query the storage for objects. [More documention on querying for objects](/docs/butterdb-query.md)
contained in the storage
| POST | Create Object(s) | /{ objectStorage }/create | Insert a new object into storage.

* {} denotes a variable

### ButterDB manipulation endpoints (DDL) -> BaseURL: localhost:3000/api/v1

| Method | Name | Endpoint | Description |
| PUT | Create object storage | /create/{ objectStorage } | Create storage for a new object model.
| DELETE | Delete object storage | /delete/{ objectStorage } | Delete storage for an old object model.

* {} denotes a variable


## Prequesties
In order to interact with a google sheet, you need to authenticate the application to act on your behalf.
Create a GCP project following the steps below and download the client secret json file and place it in yoour project. 
Set api.google.secret to be a relative path to the json file in applications.properties.

### Setup your GCP Project
- Create a new Google Cloud Project -> https://console.cloud.google.com/
- Enable the Google Sheets API -> https://console.cloud.google.com/apis/library/sheets.googleapis.com
- Create a service account for the project -> https://console.cloud.google.com/apis/credentials
- With a service account created, navigate to 'Keys' on the service account menu and generate a JSON key. This will initiate a download.
- Rename the downloaded JSON file to client_secret.json and place in src/main/resources in this project.
- Navigate to Google Drive and create a Google Spreadsheet. 
- Share the new sheet with editor rights with the email of the generated service account. 
- Copy the ID of the spreadsheet located in the URL and set the SPREADSHEET_ID in the getGSpreadsheet @Bean
- You can now manipulate that sheet with this application.

##Endpoints

## Steps to run the API
./mvnw spring-boot:run
