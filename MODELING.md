# **Modeling**

We began our application development journey by hosting a meeting to determine what kind of application we wanted to create and identify our target audience. Once we had a clear understanding of these factors, we conducted a survey via Facebook groups for boat drivers. The purpose of this survey was to gather feedback from representatives of our target audience, in order to determine the most desired functions for our application.

Now we will present the results of the survey in the form of user stories **(“As a [role], I want [function] in order to achieve [benefit]”)**, showcasing functions requested by our potential users, ranged from the most popular to the least.

1. As a boat driver, I want a weather alert feature that notifies me about high wind conditions, ensuring my safety while on the water.
2. As a boat driver, I want the ability to access guest parking information in order to easily find and utilize available parking spots.
3. As a boat driver, I want a feature that displays nearby gas stations so that I can easily locate and refuel my boat when needed.
4. As a boat driver, I want to be able to check the water temperature at different locations in order to plan and enjoy swimming activities.
5. As a boat driver, I want to receive alerts regarding waves and their intensity, helping me make decisions about current conditions and potential risks.


## **Use case diagram** 

To illustrate the interaction between our application and the environment, we will use a Use Case diagram. We define the user as the primary actor. The user's goal in interacting with the system is to be able to check the data defined in user stories (weather and meteorological alerts in the area they are interested in). We have decided to implement these functions through the following pattern:
There are two screens in the application. On the main screen users see a map.
As an overlay on the map users can see markers for guest parking and gas stations. It is also possible to click on the markers to see additional information.
Users can check the area they need by navigating on the map.
If there are any meteorological alerts in the area a user navigated to, they come up to the screen in form of a warning triangle in the top right corner on the map. The user can click on it for additional information.
After navigating to the area the user can check the weather and sea conditions by navigating to the weather-screen.

### Precondition

The user opens the application and the main screen (map) is loaded, showing Oslo and the Oslofjord as a hard coded start position. We assume that internet connection is available, and no errors occur when the user works with the application. 
The user wants to plan a boat trip and uses the application to check the weather and sea conditions in the area he wants to travel to.

### **Main flow**:

1. The user searches for the location he is interested in to check available guest harbours and gas stations there.
2. No meteorological alerts appear on the map for the new area.
3. The user clicks on different guest harbors to check available facilities there.
4. The user navigates to the weather screen to check the weather and sea conditions.

### **Alternative flow 1, step 2**
* 2.1 There are meteorological alerts in the new area. A triangle appears on the map indicating meteorological alerts. The user clicks on the warning triangle for more information.
* 2.2 The alerts are quite dangerous, so the user decides to change his destination point (goes back to step 1).

### **Alternative flow 2, step 2.2**
* 2.2.1 The alerts are not dangerous, so the user continue planing of his trip (goes to the step 3).

For the application to function, and for the user to be able to perform these actions, we need to fetch different data from MET´s Api (weather forecast, ocean forecast, meteorological alerts) and Google (map), which means they are secondary actors in our system.

* **Primary actor**: User
* **Secondary actors**: Google, MET


![useCaseMainFlow.png](useCaseMainFlow.png)


## Sequence diagram

After creating a Use Case diagram to depict the interaction between our application and the environment, we want to demonstrate the information flow within the application using a Sequence diagram. As the Sequence diagram provides a more deep view compared to the Use Case diagram, we need to define additional participants within the system to show the flow:
* **Actor**: User
* **participant**: MapScreen - main screen 
* **participant**: WeatherScreen
* **participant**: GoogleMaps Api - we get the map from the Api
* **participant**: MetAlerts Api - we get data about meteorological from the Api
* **participant**: Guest harbours / gas stations JSON-file - The most important function requested by our potential users was the ability to access information on guest harbors and gas stations. Since we could not find any suitable free API with this data, we made the decision to generate our own temporary solution by creating a JSON file containing the desired information. We acknowledge that this data source does not offer optimal scalability, we want to emphasize that it is only a temporary measure.
* **participant**: LocationForecast Api - we get weather conditions data
* **participant**: OceanForecast Api - we get sea conditions data


```mermaid
    sequenceDiagram
        actor User
        participant MapScreen
        participant WeatherScreen 
        participant GoogleMaps Api
        participant OpenSeaMap Api
        participant MetAlerts Api
        participant Guest harbours / gas stations JSON-file
        participant LocationForecast Api
        participant OceanForecast Api

        User->>MapScreen: Search a location

        MapScreen->>GoogleMaps Api: update the map
        
        GoogleMaps Api->>OpenSeaMap Api: get tile overlay
        
        OpenSeaMap Api-->>GoogleMaps Api: place overlay on map
        
        GoogleMaps Api-->>MapScreen: return map and update location
        
        MapScreen->>Guest harbours / gas stations JSON-file: fetch Guest harbours and gas stations at the location
        
        Guest harbours / gas stations JSON-file-->>MapScreen: return data
        
        MapScreen->>MetAlerts Api: fetch MetAlerts at the location
        
        MetAlerts Api-->>MapScreen: return MetAlerts data
        
        MapScreen-->>User: Show map 

        alt has MetAlerts at the location
        
            MapScreen-->>User: Show a warning triangle on the map
            User->>MapScreen: Click on the triangle for more info
            MapScreen-->>User: Show info

        else no MetAlerts at the location
        
            MapScreen-->>User: No data comes up on the map

        end
        
        User->>WeatherScreen: navigate to WeatherScreen
        
        MapScreen->>WeatherScreen: send location
        
        WeatherScreen->>LocationForecast Api: fetch weather data
        
        LocationForecast Api-->>WeatherScreen: return weather data
        
        WeatherScreen-->>User: Show weather conditions
        
        User->>WeatherScreen: Open sea conditions table
        
        WeatherScreen->>OceanForecast Api: fetch sea data
        
        OceanForecast Api-->>WeatherScreen: return sea data
        
        WeatherScreen-->>User: Show sea conditions
```

## Class diagram 

In the class diagram, we have depicted the system structure necessary for the execution of our Use Case.

```mermaid
classDiagram
    MainActivity --|> Navigation
    Navigation --|> MapScreen
    MapScreen --|> AutocompleteTextField
    Navigation --|> WeatherScreen
    WeatherScreen --|> SharedViewModel
    MapScreen --|> SharedViewModel
    SharedViewModel --|> SharedUiState
    MapScreen --|> SeaMapViewModel
    SeaMapViewModel --|> MapUiState
    WeatherScreen --|> LocationForecastViewModel
    WeatherScreen --|> UtilitiesWeatherScreen 
    SeaMapViewModel --|> SeaSymbolsList
    SeaSymbolsList --|> SeaSymbolsPair
    LocationForecastViewModel --|> OceanForecast
    OceanForecast --|> GeometryOcean
    OceanForecast --|> PropertiesOcean
    OceanForecast --|> MetaOcean
    MetaOcean --|> UnitsOcean
    LocationForecastViewModel --|> TimeseriesOcean
    TimeseriesOcean --|> DataOcean
    DataOcean --|> InstantOcean
    LocationForecastViewModel --|> DetailsOcean
    LocationForecastViewModel --|> LocationForecast
    LocationForecast --|> Geometry
    LocationForecast --|> Properties
    Properties --|> Meta
    Meta --|> Units
    Properties  --|> Timeseries
    Timeseries --|> Data
    Data --|> Instant
    SeaMapViewModel --|> Harbor 
    SharedViewModel --|> MetAlert
    SeaMapViewModel --|> OceanForecastDataSource
    LocationForecastViewModel --|> LocationForecastDataSource
    MetAlert --|> MetAlertsDataSourceImpl
    
    
    class MainActivity{ 
        +onCreate(savedInstanceState: Budle?)}

    class Navigation {
      + SetUpNavigation(placesClient: PlacesClient, connectivityObserver: ConnectivityObserver)
      + <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavHostController)
      + NavigationBarWithButtons(navController: NavController)
      - ButtonMapContext(isSelected: Boolean)
      - ButtonWeatherContext(isSelected: Boolean)
    }
    
    class MapScreen{
    + tileProvider: UrlTileProvider
    + SeaMapScreen(sharedUiState: SharedUiState, navController: NavController, placesClient: PlacesClient, updateLocation: (loc: LatLng) -> Unit, connectivityObserver: ConnectivityObserver, updateSearchHistory: (userInput: String) -> Unit, seaMapViewModel: SeaMapViewModel = viewModel())
    - ExplanationBox(symbolDescription: List<SeaSymbolsPair>, onDismiss: () -> Unit)
    - FilterButtonAndDialog(showSymbols: MutableState<Boolean>, showHarborWithGas: MutableState<Boolean>, showHarborWithoutGas: MutableState<Boolean>, showDialog: MutableState<Boolean>)
    }
    
    class AutocompleteTextField{
    + AutocompleteTextField(context: Context, updateLocation: (loc: LatLng) -> Unit, cameraPositionState: CameraPositionState, placesClient: PlacesClient, active: MutableState<Boolean>, enableSearch: MutableState<Boolean>, sharedUiState: SharedUiState, updateSearchHistory: (userInput: String) -> Unit)
    -fetchPredictions(query: String, placesClient: PlacesClient, onPredictionsFetched: (List<AutocompletePrediction>) -> Unit)
    -getPosition(placeName: String, context: Context, updateLocation: (loc: LatLng) -> Unit, cameraPositionState: CameraPositionState)
    }
    
    class WeatherScreen{
    + WeatherScreen(sharedUiState: SharedUiState, navController: NavController, forecastViewModel: LocationForecastViewModel = viewModel(), connectivityObserver: ConnectivityObserver)
    -ScreenContent(forecastViewModel: LocationForecastViewModel,displayInfo: MutableState<DisplayInfo>,fontNormal: FontFamily)
    -DayWeatherCard(day: LocalDate,timeseriesList: List<Timeseries>,fontNormal: FontFamily,todayOrTmr: String? = " ")
    -DayWeatherCardLongTerm(day: LocalDate,timeseriesList: List<Timeseries>,fontNormal: FontFamily)
    -WeatherRow(data: Timeseries, font: FontFamily, rowColor: Color)
    -WeatherRowLongTerm(data: Timeseries, font: FontFamily, rowColor: Color)
    -WeatherHeader(headerColor: Color, font: FontFamily, shortTerm: Boolean)
    -DayOceanCard(day: LocalDate,timeseriesList: List<TimeseriesOcean>,fontNormal: FontFamily,todayOrTmr: String? = " ")
    -OceanRow(data: TimeseriesOcean, font: FontFamily, rowColor: Color)
    -OceanHeader(headerColor: Color, font: FontFamily)
    -ScreenTop(forecastViewModel: LocationForecastViewModel, fontNormal: FontFamily, fontBold: FontFamily)
    -BottomNavBar(currentScreen: MutableState<DisplayInfo>, font: FontFamily)
    -ShortToLongButton(expanded: MutableState<Expanded>, color: Color, font: FontFamily)
    -DataLastUpdated(forecastViewModel: LocationForecastViewModel, displayInfo: MutableState<DisplayInfo>, fontNormal: FontFamily)
    }
    
    class SharedViewModel{
    -metAlertsRepository: MetAlertsRepositoryImpl()
    -_sharedUiState: MutableStateFlow(SharedUiState())
    +sharedUiState: StateFlow<SharedUiState>
    +updateLocation(newLocation: LatLng)
    -loadMetAlerts()
    +onCleared()
    +updateHistoryItems(userInput: String)
    }
    
    class SharedUiState{
    +currentLocation: LatLng
    +allMetAlerts: List<MetAlert>
    +historyItems: List<String>
    }
    
    class SeaMapViewModel{
    -_mapUiState = MutableStateFlow(MapUiState())
    +mapUiState: StateFlow<MapUiState>
    +harborData = MutableLiveData<List<Harbor>>()
    +placeOrRemoveMarker()
    +loadGuestHarboursFromResources(context: Context)
    
    }
    
    class MapUiState{
    +markerVisible}
    
    class LocationForecastViewModel{
    -repository: LocationForecastRepositoryImpl
    -repositoryOcean: OceanForecastRepositoryImpl
    -saveForecastToFile(context: Context, forecast: LocationForecast?)
    -saveOceanForecastToFile(context: Context, forecast: OceanForecast?)
    -savePlaceNameToFile(context: Context, placeName: String)
    -loadPlaceNameFromFile(context: Context)
    -loadForecastFromFile(context: Context)
    -loadOceanForecastFromFile(context: Context)
    +loadForecast(lat: String, lon: String, context: Context)
    +setCurrentPlaceName(context: Context, lat: Double, lon: Double)
    }
    
    class SeaSymbolsPair{
    +symbol: Int
    +description: String
    }
    
    class SeaSymbolsList{
    +symbolDescription: List<SeaSymbolsPair>}
    
    class OceanForecast{
    +type: String
    +geometry: GeometryOcean
    +properties: PropertiesOcean}
    
    class GeometryOcean{
    +type: String
    +coordinates: List<Double>}
    
    class PropertiesOcean{
    +timeseries: List<TimeseriesOcean>}
    
class MetaOcean{
    val updated_at: String,
    val units: UnitsOcean}
    
    class UnitsOcean{
    +sea_surface_wave_from_direction: String?
    +sea_surface_wave_height: String?
    +sea_water_speed: String?
    +sea_water_temperature: String?
    +sea_water_to_direction: String?}
    
    class TimeseriesOcean{
    +time: String
    +data: DataOcean}
    
    class DataOcean{
    +instant: InstantOcean}
    
    class InstantOcean{
    +details: DetailsOcean}
    
    class DetailsOcean{
    +sea_surface_wave_from_direction: Double?
    +sea_surface_wave_height: Double?
    +sea_water_speed: Double?
    +sea_water_temperature: Double?
    +sea_water_to_direction: Double?}
    
    class UtilitiesWeatherScreen{
    +getWeatherIcon(timeseries: Timeseries): String?
    +getWeatherIconLongTerm(timeseries: Timeseries): String?
    +getWindDirection(timeseries: Timeseries): String
    -getNorthEastVestSouthFromDegrees(degree: Double): String
    +getNorwegianTimeWeather(timeseries: Timeseries): String
    +getTemperature(timeseries: Timeseries): String
    +getTemperatureLongTerm(timeseries: Timeseries): String
    +getWindSpeed(timeseries: Timeseries): String
    +getPrecipitationAmountMaxMin(timeseries: Timeseries): String
    +getPrecipitationAmountMaxMinLongTerm(timeseries: Timeseries): String
    +temperaturePositive(timeseries: Timeseries): Boolean
    +temperaturePositiveLongTerm(timeseries: Timeseries): Boolean
    +getNorwegianTimeOcean(timeseries: TimeseriesOcean): String
    +getSeaWaterTemperature(timeseries: TimeseriesOcean): String
    +seaTemperaturePositive(timeseries: TimeseriesOcean): Boolean
    +getCurrentDirectionTowards(timeseries: TimeseriesOcean): String
    +getCurrentDirectionFrom(timeseries: TimeseriesOcean): String
    +getCurrentSpeed(timeseries: TimeseriesOcean): String
    +List<Timeseries>.getToday(): List<Timeseries>
    +lastUpdates(today: String): String?
    +List<Timeseries>.groupByDay(): Map<LocalDate, List<Timeseries>>
    +List<TimeseriesOcean>.groupByDayOcean(): Map<LocalDate, List<TimeseriesOcean>>
    +GetWeatherIconTopPage(timeseries: Timeseries)
    +GetWeatherIconTopPageHorizontal(timeseries: Timeseries)
    +getFonts(): Array<FontFamily>
    }
    
    class LocationForecast{
    +type: String,
    +geometry: Geometry,
    +properties: Properties}
    
    class Geometry{
    +type: String
    +coordinates: List<Double>}
    
    class Properties{
    +meta: Meta
    +timeseries: List<Timeseries>}
    
    class Meta{
    +updated_at: String
    +units: Units}
    
    class Units{
    +air_pressure_at_sea_level: String
    +air_temperature: String?
    +air_temperature_max: String?
    +air_temperature_min: String?
    +air_temperature_percentile_10: String?
    }
    
    class Timeseries{
    +time: String
    +data: Data}
    
    class Data{
    +instant: Instant
    +next_12_hours: Next_12_hours?
    +next_1_hours: Next_1_hours?
    +next_6_hours: Next_6_hours?}
    
    class Instant{
    val details: InstantDetails}
    
    class Harbor{
    +id: Int
    +name: String
    +location: Array<Double>
    +description: String
    +equals(other: Any?): Boolean
    +hashCode(): Int}
    
    class MetAlert{
    +id: String
    +area: String
    +title: String
    +description: String
    +iconName: String
    +event: String
    +consequences: String?
    +instruction: String
    +awarenessLevel: List<String>
    +riskMatrixColor: String
    +awarenessType: List<String>
    +triggerLevel: String?
    +equals(other: Any?): Boolean
    +fun hashCode(): Int
    }
    
    class OceanForecastDataSource{
    -client
    +fetchOceanForecast(lat: String, lon: String): OceanForecast?
    }
    
    class LocationForecastDataSource{
    -client
    +fetchLocationForecast(lat: String, lon: String): LocationForecast
    }
    
    class MetAlertsDataSourceImpl{
    -client
    +fetchMetAlertsInNorway(): List<MetAlert>
    +fetchMetAlertsAtLocation(lat: String, lon: String): List<MetAlert>
    -fetchMetAlerts(url: String): List<MetAlert>
    
    }
    
    
```

## Activity diagram (Flow diagram)

As a final stage of modeling we want to make an activity diagram to illustrate the flow of events in the use case and to show the interaction with the application from the user's point of view. It will help us to depict the sequence of activities, actions, and decisions that occur during the execution of the use case described over.

```mermaid
flowchart 
A[Search location] -->|MapScreen navigates to the location and shows it to the user| B{MetAlerts appears on the screen?}
B -->|Yes| C[Checks the alert]
B -->|No| D[Checks the guest harbours and gas stations]
C--> E{Is the alert dangerous?}
E-->|Yes| F[Decides to change location]
F-->A
E-->|No| D
D-->G[Navigates to WeatherScreen]
G--> H[Checks the weather conditions]
H--> I[Checks the sea conditions]
```


