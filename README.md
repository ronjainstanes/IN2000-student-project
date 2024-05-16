# Team 11

## Running the App
To run the app you need access to the code files from GitHub. Additionally need to have downloaded Android Studio to your computer.
Open the folder with the code files in Android Studio.
  
  ### API keys needed for running the app:
  To be able to see the map, you need an API-key which can be downloaded from https://developers.google.com/maps/documentation/android-sdk/get-api-key.
  In the same folder as "local.properties", make a new file called "secrets.properties".
  In this file you should ONLY write: MAPS_API_KEY='your api key'.
  For example, if your API key is 12345678910, you should write MAPS_API_KEY=12345678910

Before running the app, you should go to 'Build' and press 'Clean Project', then 'Rebuild Project', followed by 'Sync Project with Gradle Files' inside Android Studio.
When all this is done, you can press "Run app" to run the app.
The documentation can be found in our report 'Team 11 Rapport', all features of our app is described there.

We hope you enjoy our app!

## Libraries Used in the Project
This project uses several open-source and commercial libraries. Below are some of the main libraries included:

  ### UI Libraries
  For our user interface we have used plenty of Jetpack Compose libraries like material3. 
  Additionally, we have used a lot of other UI libraries. For example, we used cardview to show the multiple days of the weather and ocean forecast in 'WeatherScreen.kt'.

  ### Networking Libraries
  For our networking need, we used a combination of Ktor and OkHttp libraries to handle API interaction efficiently. 

  ### Navigation Libraries
  For navigation within our app, we used a combination of libraries from the AndroidX Navigation component.
  For example, we used 'navigation-compose' for our 'NavHost'. 

  ### Lifecycle Libraries
  For managing the lifecycle(different stages) of our components, we used several libraries from the AndroidX Lifecycle suite. 
  For example, we used 'lifecycle-runtime-compose' to manage lifecycle events in our Compose based UI. 

  ### Google Libraries
  For our map and a variety of functionalities attached to it, we used several libraries from Google. 
  For example, we used  'maps-compose' to integrate the map into our app. 

  ### Testing Libraries
  To ensure the quality and reliability of our app, we used the JUnit and MockK library and several testing libraries from the AndroidX test and Jetpack Compose testing libraries.

  ### Other Libraries 
  For additional functionalities, we used a various set of libraries to help with a variety of feathers in our app.
 
