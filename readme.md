In order for this project to use firebase, you have to setup a firebase project with Firebase Auth with GoogleSign in
enabled and place the google-services.json file into the project root directory.

In order to set up Firebase and get the google-service.json you have to get the sha1 and sha256 of your app. To do this run the following command:

```bash
./gradlew signingReport
```

You also have to client id from google console for the server (web) and set it as a the variable "
FIREBASE_SERVER_CLIENT_ID" in the "local.properties" file
