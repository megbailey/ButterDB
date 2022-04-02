# SpringBoot API - Google Sheets as a SQL Database
This project creates a RESTful API with SpringBoot to provide an abstraction for treating a Google Sheets as a SQL database.
A user authenticates their own Google account via a service account.

## Wanted functionality
- create new SQL table
  - equivalent of a new sheet
  - specify schema
- abstract SQL statements to google sheet functionality
  - SELECT 
  - FROM
  - JOINS
  - FOREIGN keys

## Setup your GCP Project
- Create a new Google Cloud Project https://console.cloud.google.com/
- Enable the Google Sheets API https://console.cloud.google.com/apis/library/sheets.googleapis.com
- Begin creating a service account on Google Cloud Platform https://console.cloud.google.com/apis/credentials
- After you've named your account, navigate to 'Keys' within the service account menu and generate a JSON key. This will initiate a download.
- Rename the downloaded JSON file to client_secret.json and place in src/main/resources in this project.
- Head over to Google Drive and create a Google Spreadsheet. 
- Share the new sheet with editor rights with the email of the generated service account. 
- Copy the ID of the spreadsheet located in the URL and set the SPREADSHEET_ID in the GSheets class
- You can now manipulate that sheet with this application.


## Steps to run the API
./mvnw spring-boot:run
