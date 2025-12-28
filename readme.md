In order for this project to use firebase, you have to setup a firebase project with Firebase Auth with GoogleSign in
enabled and place the google-services.json file into the project root directory.
You also have to client id from google console for the server (web) and set it as a the variable "
FIREBASE_SERVER_CLIENT_ID" in the "local.properties" file