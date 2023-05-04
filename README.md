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
- Automatic Controls for the Tello drone where users can excute pre-programmed paths
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
<img src="https://user-images.githubusercontent.com/51523562/236315300-f4d8aee0-19da-409d-8003-8503412abecb.jpg" width="300" height="600>
