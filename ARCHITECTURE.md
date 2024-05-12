# Architecture



## Technologies used

The technologies we have used are the ones that were specified in the [project criteria](https://www.uio.no/studier/emner/matnat/ifi/IN2000/v24/prosjektarbeid/produktkrav-in2000-v24.pdf). The entire project is written in the programming language **Kotlin** and we have used **Jetpack Compose** as a UI toolkit. We used the IDE **Android Studio** for development and testing, and stored the code in a **Git** repository on UiO Github.



## Object Oriented Principles and Design Pattern

We are striving towards developing our app in accordance with the [Recommendations for Android Architecture](https://developer.android.com/topic/architecture/recommendations). The main recommendation is to divide the architecture into two distinct layers: the UI layer and the data layer. To adhere to this recommendation, we have organized our codebase into three main packages: "data", "model" and "ui", following the Model - View - ViewModel (MVVM) design pattern. This promotes separation of concerns and helps create a clear division and structure within our code.

The data layer in our application consists of Repositories, DataSources and external APIs. We have created three sets of Repositories and their corresponding DataSources, where each pair handles different types of data.  We have created interfaces to be able to modify or change the implementation of the classes without it affecting other parts of the code, which promotes to loose coupling between the different units. Another best practice recommendation is to use dispatchers to fetch and update data in the data layer. When fetching data from an API, we use coroutines to dispatch a thread from the "view model scope" to avoid blocking the main thread. The data layer handles the business logic of our application, which mostly involves retrieving and manipulating data and interacting with external data providers such as the APIs.

The UI layer contains all UI elements in our application, which are implemented as composable functions created with the framework Jetpack Compose. To handle input from the user or changes in data from external sources, we use ViewModel classes to handle these inputs and update the state accordingly. The ViewModels function as state holders which also manages UI logic such as user interactions and updating UI elements. Another recommendation we are following, is to expose UiStates as immutable objects, meaning that only the ViewModels have access to modify the UiState.

By adopting this structure, it was easier to implement important object-oriented principles such as loose coupling and high cohesion. It promotes loose coupling through the clear division of code into different components, and the use of interfaces. And there is high cohesion within in each component, because each class has a single responsibilty, and classes that work together and depend on each other are encapsulated within the same module.



## The Architecture of our Application

![Architecture](https://media.github.uio.no/user/9545/files/ab774338-59a9-4c86-ab4f-ce06e82eef99)


*This is an illustration of the apps architecture, which is structured according to the MVVM design pattern, and divided into a UI layer and a data layer.*

This illustration clearly distinguishes the two layers. The UI layer contains UI elements, which is represented by the boxes named SeaMapScreen and WeatherScreen. These make up the elements which are visible to the user. 

The UI layer also consists of three ViewModels. Two of them are bound to a screen, either SeaMapScreen or WeatherScreen. The last viewmodel: SharedViewModel, is not directly part of any screens, but instead serves a single purpose - to handle the state of the SharedUiState. This UiState contains data which both screens need access to, and if it is updated in one screen, it also needs to be updated in the other screen. This means that each screen has exactly one ViewModel, while also having access to the SharedUiState.

The data layer consists of Repositories and DataSources which fetches data from external APIs. In our application we fetch data from three distinct APIs, and because the data from each one is quite unique, we decided to create a DataSource and a Repository for each API. These include the  "MetAlerts", "LocationForecast" and "OceanForecast" APIs.

There are also two APIs which are already built into composable functions. The first one is the "Google Map" composable which only need access to an API key (which we store in the codebase using the "google secrets" plugin), and the second one is the Tile Overlay from OpenSeaMap which can be placed on top of the map. These are APIs, but they do not need data sources or repositories, as the functionality of these classes are already built into and handled by the composable functions. 



## API Level

We have chosen minimum API level 25 while setting target and compileSdk level to 34. This decision enables us to utilize many new features while still ensuring compatibility with a majority of devices - approximately 95.6% according to [apilevels.com](https://apilevels.com). 

One challenge we encountered was that this version of the Android SDK had some issues with date and time, which got fixed in later versions. We wanted to use the ZonedDateTime class, which is not available at this level. To address this issue we decided to use a library developed by Google called 'desugar_jdk_libs' which allows us to use certain features like ZonedDateTime on lower API levels.

We started out at minimum API level 24, but later in our project discovered that the Tile Overlay from OpenSeaMap did not work on this level. We also observed that certain features of the application appeared much slower on this level. We therefore increased the level from 24 to 25 which resolved these issues.
