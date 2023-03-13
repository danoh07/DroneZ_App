# DroneZ App for connecting the drone to the users Phone

## This projects aims to:
- Create a UI for controlling future DroneZ drone 
- Handles drone using virtual controls
- Set Path for drones to go in given map environment 
- Stream video from the drone to the phone 
- Support multiplatform: IOS and Andriod support 

## Getting Started
- To deploy the application on your machine, you will need to install Dart, Flutter, and Android Studio 
- For now, Tello Drone is only thing that works with the app 

### Prerequisites
* Tello Drone (Model:TLW004) (if you want to fly it)
* Android Device to download our application (dronez beta app)
* <a href="https://developer.android.com/studio?gclid=Cj0KCQjwk7ugBhDIARIsAGuvgPb497EJwWBBpNRe0kE56rmhBMo8bCTHDCaanpjdUWFn4spUkeVxhbYaAlK2EALw_wcB&gclsrc=aw.ds">Android Studio</a>

## Installation 
With the environment configured, just clone this repository. 
You might also need to add any packages that might not be installed.
The only package that this projected imported is ryze_tello, you can install it by running the following code in your terminal:
```
flutter pub add ryze_tello
```

### Notice
The flutter app written in Dart, only has the wireframe for UI for future Drone Z. We are working on converting the code we have in Android Studio into Dart for future multiplatform support

### If you want to use the working controller
Open android studio > open app_AS_only > Build > run app (preferably connecting your phone)

