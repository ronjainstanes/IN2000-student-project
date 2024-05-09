# Architecture

## Object Oriented Principles and Design Pattern

We are striving towards developing our app in accordance with the [Recommendations for Android Architecture](https://developer.android.com/topic/architecture/recommendations). The main recommendation is to divide the architecture into two distinct layers: the UI layer and the data layer. To adhere to this recommendation, we have organized our codebase into three main packages: "data", "model" and "ui", following the Model - View - ViewModel (MVVM) design pattern. This helps create a clear division and structure within our code.

The data layer in our application is made of Repositories, DataSources and APIs. We have created three Repositories, each handling different types of data which is provided by DataSources. The responsibility of the DataSource classes is to fetch data from APIs, which includes parsing the data and handling exceptions. Another best practice recommendation is to use dispatchers to update data from the data layer. For instance, when fetching data from an API, we use coroutines to dispatch a thread from the "view model scope" to avoid blocking the main thread. This layer handles the business logic of the app, for example retrieving and maipulating data and interacting with external data providers such as the APIs.

All UI elements are contained within the UI layer of our application. For this, we have used the framework Jetpack Compose to create UI elements as composable functions. To handle input from the user and changes in data from external sources, we use ViewModel classes to handle these inputs and update the state accordingly. The ViewModels function as state holders and manage UI logic, such as user interactions and updating UI elements. We also want to note that the UiState is exposed as immutable variables, where only the ViewModels have access to modify the UiState.

By adopting this structure, it was easier to implement important object-oriented principles such as low coupling and high cohesion. It promotes low coupling because the different components of the application are organized in a way that minimizes dependencies between them. And there is high cohesion within in each component, because everything within a component is closely related and function together logically.

## The Architecture of our Application


![Architecture](https://media.github.uio.no/user/9545/files/68aaf943-7fc6-4194-b875-12c184671116)


*This is an illustration of the apps architecture, which is structured according to the MVVM design pattern, and divided into a UI layer and a data layer.*

This illustration clearly distinguishes the two distinct layers. The UI layer contains UI elements, which is represented in the boxes named SeaMapScreen and WeatherScreen. These make up the elements which are visible to the user. 

The UI layer also consists of three ViewModels. Two of them are contained within a screen, either SeaMapScreen or WeatherScreen. The last viewmodel: SharedViewModel, is not part of a screen, but instead has a single responsibility - to handle the state of the SharedUiState, which is what is shared with the two screens. In other words: each screen only has one viewmodel, but the screen composable functions also takes another argument - the SharedUiState which is provided by the SharedViewModel. This is because we have data which both screens need access to, and if this data is updated from one screen, it also needs to be updated in the other screen. After much discussion of how to solve this problem, we endred up with the solution of having a SharedUiState, which is handled by a SharedViewModel.

The data layer consists of Repositories and DataSources which fetches data from external APIs. In our application we fetch data from three distinct APIs, and because the data is quite unique, we created a DataSource and Repository for each API. These include the  "MetAlerts", "LocationForecast" and "OceanForecast" APIS from MET. 

There are also two API's which are already built into composable functions, such as the "Google Map" composable which is provided by the Google Maps Compose Library, and a Tile Overlay from OpenSeaMap which can be placed on top of the map. These are also APIs, but does not provide data which can be handled by DataSources and saved into a UiState like the other data mentioned above. 

## API Level

We have chosen minimum API level 25 while setting target and compileSdk level to 34. This decision enables us to utilize many new features while still ensuring compatibility with a majority of devices - approximately 95.6% according to [apilevels.com](https://apilevels.com). 

One challenge we encountered was that this version of the Android SDK had some issues with date and time, which got fixed in later versions. We wanted to use the ZonedDateTime class, which is not available at this level. To address this issue we decided to use a library developed by Google called 'desugar_jdk_libs' which allows us to use certain features like ZonedDateTime on lower API levels.

We started out at minimum API level 24, but later in our project discovered that the Tile Overlay from OpenSeaMap did not work on this level. We also observed that certain features of the application appeared much slower on this level. We therefore increased the level from 24 to 25 which resolved these issues.
