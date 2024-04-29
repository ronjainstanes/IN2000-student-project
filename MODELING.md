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

We assume that internet connection is available, and no errors occur when the user works with the application. The user wants to plan a boat trip and uses the application to check the weather and sea conditions in the area he wants to travel to.

### **Main flow**:

1. The user opens the application and the main screen (map) is loaded, showing Oslo and the Oslofjord as a hard coded start position. At the same time, data about the weather and sea conditions in this area is loaded on the other screen.
2. The user searches for the area he is interested in to check available guest harbours and gas stations there.
3. No meteorological alerts appear on the map for the new area.
4. The user clicks on different guest harbors to check available facilities there.
5. The user navigates to the weather screen to check the weather and sea conditions.

### **Alternative flow, step 3**:

* 3.1 There are meteorological alerts in the new area. A triangle appears on the map indicating meteorological alerts. The user clicks on the warning triangle for more information.
* 3.2.1 The alerts are quite dangerous, so the user decides to change his destination point (goes back to step 2).
* 3.2.2 The alerts are not dangerous, so the user continue planing of his trip and goes to the step 4

For the application to function, and for the user to be able to perform these actions, we need to retrieve different data from MET (weather forecast, ocean forecast, and meteorological alerts) and Google (map), which means they are secondary actors in our system.

* **Primary actor**: User
* **Secondary actors**: Google, MET

### Use case diagram:

![useCaseMainFlow.png](useCaseMainFlow.png)

Sequence Diagram Check:

```mermaid
    sequenceDiagram
        actor Bruker
        participant App
        participant Api

        Bruker->>App: Click on retrieve weather button

        App->>Api: fetchWeatherData()

        alt sukess
        
            Api-->>App: Weather data is returned
            App-->>Bruker: Show weather data

        else feil

            Api-->>App: Error
            App-->>Bruker: Show error message

        end
```

