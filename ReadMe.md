# ORM for Google Sheets
This project creates a RESTful API with SpringBoot to provide an API layer that uses object-relational mapping technique for interacting with Google Sheets as a database.

Supported fuctionality:
- adding objects
- deleting objects (filter/conditional optional)
- querying objects (filter/conditional optional)
- creating object tables
- deleting object tables

## Prequesties
In order to interact with a google sheet, you need to authenticate the application to act on your behalf. Create a GCP project following the steps below and download the client secret json file and place it in yoour project. Set api.google.secret to be a relative path to the json file in applications.properties.

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
