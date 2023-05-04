# DroneZ: App for connecting the drone to the users Phone

## In this project, there are currently two applications:
- #### Flutter application
- #### Andriod Stuido application 

In the future we plan to implement more features from the Andriod Stuido applaction to the Flutter application for cross platform support. 

## Flutter App Features:
- Connect to Tello drone
- Manaual Controls for the Tello drone
- Automatic Controls for the Tello drone where users can excute pre-programmed paths
- receieve stream data from the Tello drone 

## Andriod Studio App Features:
- Connect to Tello drone
- Manaual Controls for the Tello drone
- Automatic Controls for the Tello drone where users can excute pre-programmed path
- Display video stream and preform YOLO object dectection algorithm 

## Getting Started
- To deploy the application on your machine, you will need to install Dart and Flutter for the Flutter app, and Android Studio for Android app 
- To connect to the drone, Tello drone is neeeded

### Prerequisites
* Tello Drone (Model:TLW004) (if you want to fly it)
* Android Device to download our application (dronez beta app)
* Download <a href="https://developer.android.com/studio?gclid=Cj0KCQjwk7ugBhDIARIsAGuvgPb497EJwWBBpNRe0kE56rmhBMo8bCTHDCaanpjdUWFn4spUkeVxhbYaAlK2EALw_wcB&gclsrc=aw.ds">Android Studio</a> and <a href="https://docs.flutter.dev/get-started/install?gclid=Cj0KCQjwk7ugBhDIARIsAGuvgPbPkX7_k-mLrQT-DR1b4OT5R97D4nLXvA5lwDYK30NdFq12g-XzjXQaAjclEALw_wcB&gclsrc=aw.ds">Flutter</a> according to your operating system

## Installation 
With the environment configured, just clone this repository. 
You might also need to add any packages that might not be installed.


## Running Builds
### For Flutter application: 
- once in the cloned directory, execute:
```
flutter run 
```

### For Andriod Studio application
Open android studio > navigate to cloned directory and open app_AS_only > Build > run app (preferably connecting your phone)

## Notice
- video stream feature don't work on emulator devices because of connection issues between the device and the drone. If you want full functionalities please test on actual andriod devices
- For Android app, since the size of the app is big due to the object detection algorithm, it will not be able to build and install on the online emulator. Thus, actual physical phone is needed to run Android app

## How to use Android App
### Default page
<img src="https://user-images.githubusercontent.com/51523562/236315300-f4d8aee0-19da-409d-8003-8503412abecb.jpg" width="300" height="600">
- When the app is opened, the default page is the control selection page
- Select "Manual Control" to manually control the drone with joysticks
- Select "Automatic Control" to automatically control the drone with command or in pre-programmed path

### Manual Control
#### Default Manual Control Page
<img src="https://user-images.githubusercontent.com/51523562/236319158-7928f2b7-b3b0-4caf-8238-3a0ae8159048.jpg" width="600" height="300">

#### Manual Control page with Live Feeding View
<img src="https://user-images.githubusercontent.com/51523562/236323642-8c142274-ecce-4d6c-8cc5-6b42d63f3eb0.jpg" width="600" height="300">

#### Connecting to drone
- Connect to the drone by clicking "wifi-figure" button
- When the battery and connection status are in green, the drone is connected
#### After connecting
- Able to turn on the live feeding view by clicking "video view" switch
- If the video view is on, the switch will turn in green
- Take off the drone by clicking taking off button on the right
- After taking off, able to control the drone with joysticks
- Land the drone by clicking landing button under the take off button
- When battery is lower than 15%, the battery status will turn in red

### Automatic Control
#### Default Automatic Control Page
<img src="https://user-images.githubusercontent.com/51523562/236326581-cd043e78-aad2-4652-b0c3-6df55231c383.jpg" width="600" height="300">

#### Automatic Control page with Live Feeding View
<img src="https://user-images.githubusercontent.com/51523562/236326610-ddb2bb4d-a448-48f8-9508-aad66dec1dae.jpg" width="600" height="300">

#### Connecting to drone
- Connect to the drone by clicking "wifi-figure" button
- When the battery and connection status are in green, the drone is connected
#### After connecting
- Able to turn on the live feeding view by clicking "video view" switch
- If the video view is on, the switch will turn in green
- Take off the drone by clicking "TAKEOFF" button
- Choose direction and amount of distance from the drop down boxes and click "GO" to submit distance command
- Choose rotation and amount of angle from the drop down boxes and click "GO" to submit rotation command
- Submitting the command will work for one direction or rotation at a time
- "GO" buttons will only submit the command when wither both direction and distance value or rotation and angle value are chosen
- To make the drone to fly itself with pre-programmed path, click "AUTO" button
