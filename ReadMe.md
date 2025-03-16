# ButterDB

ButterDB is an ORM for Google Sheets allowing your applications to fetch and manipulate data from a Google spreadsheet using POJOs (Plain-Old-Java-Objects) and Object-Relational Mapping techniques. ButterDB abstracts the Google Spreadsheet simplfies programmatic data management and mimicks patterns established by other modern ORMs.

Database interactions are facilitated through Google's APIs. ButterDB's CRUD functionality utilizes the [Google Sheets API](https://developers.google.com/sheets/api/reference/rest) while query functionality utilizes the [Google Visualization API](https://developers.google.com/chart/interactive/docs/reference).

A single running instance of ButterDB can process many DataModel implementations. Behind the scenes, ButterDB will use a different Google Sheet per DataModel implementation. However, since ButterDB relies on other APIs, we inherit their usage limitations. 
See [Limitations](./ReadMe.md#Limitations) to perform the calculations for your application to determine if ButterDB is a good fit for your use-case. 
Likewise, see [Optimizations](./ReadMe.md#Optimizations) for notes on how ButterDB tries to reduce the number of API calls.

## POJOs and Implementing DataModel
POJOs that implement the DataModel inherit the ability to be serialized/deserialized into/from JSON which allows ButterDB to read & write any custom object. 

ButterDB deserializes JSON into DataModel subclasses and, thus, the JSON must contain additional type information denoted by the property "@class". The 
annotation `@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)` at the top of your ObjectModel implementations instruct `com.fasterxml.jackson` to include this property on seralization.

For example, here is a payload to send to the /{ objectStorage }/create endpoint which stores a new object.
```
{
    "@class": "com.github.megbailey.butter.domain.SampleObjectModel",
    "ID": 1,
    "Property": "this is a property"
}
```

Once you've created your DataModel implementation, place it alongside the DataModel.java and recompile the 
application. ButterDB will now recognize your DataModel implementation. See [Setup](./ReadMe.md#Setup) for authorizing ButterDB as a 
GCP project and **Steps to Run** for full compilation instructions.


### Methods exposed by DataModel
- save
- lastInsertedID
- get
- where
- orWhere
- delete

## Limitations

ButterDB relies on Google Spreadsheet and APIs and thus is subject to Google's limitations and usage requirements ([google's api usage documentation](https://developers.google.com/docs/api/limits)).

<img width="772" alt="google's api usage table. 3000 read requests per minute per project. 300 read requests per minute per user per project. 600 write requests per minute per project. 60 write requests per minute per user per project." src="https://user-images.githubusercontent.com/32280319/191318296-5a181712-da3d-4da0-a2d1-419037a864b2.png">

Google has a limit of 5 million cells of data per spreadsheet which can be spread out over any number of sheets. Because of these two limitations, ButterDB is best suited to support smaller projects that will utilize more reads than writes and that don't warrant the efficiency of a traditional relational database that can scale with your data.

If you plan on running multiple instances of ButterDB, consider creating a Google account for that instance to spread out api quotas. If ButterrDB will be included in a long-standing project, a separate google account is a good security practice.

## Setup

First things first, ButterDB's foundation is a Google Spreadsheet. Navigate to Google Drive and create a Google 
Spreadsheet. This will hold the object storages for a single ButterDB instance.

In order to interact with Google's Sheets services via a 3d party i.e. ButterDB, you'll need to tell Google you give the
application permission to use your Google account. 

- Create a new [Google Cloud Project](https://console.cloud.google.com/)
- [Enable the Google Sheets API](https://console.cloud.google.com/apis/library/sheets.googleapis.com)
- [Create a service account](https://console.cloud.google.com/apis/credentials) for the instance
- Navigate to 'Keys' tab in the service account menu and generate a JSON key. This will initiate a download.
- Rename the downloaded file to client_secret.json and place in src/main/resources in this project.
- Navigate back to Google Drive to the new spreadsheet and share the new spreadsheet (with editor rights) with the 
service account email from GCP console.

### Configuration file (this section is //TODO)
- Copy the ID of the spreadsheet in the URL and set the (//TODO: config file) [spreadsheetID in the getGSpreadsheet @Bean](./src/main/java/com/github/megbailey/butter/ButterDBApp). 
- (optional) change the path of client_secret
