import 'dart:async';
import 'package:ryze_tello/ryze_tello.dart';
import 'package:flutter/material.dart';
import 'manual_control_page.dart';

class TakeFlightPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text('Select your', style: TextStyle(fontSize: 35)),
          Text('piloting mode', style: TextStyle(fontSize: 35)),
          Padding(
            padding: const EdgeInsets.all(20.0),
            child: Text('so many choices...', style: TextStyle(fontSize: 17)),
          ),
          SizedBox(height: 50),
          FlightControlOptionCard(),
        ],
      ),
    );
  }
}

class FlightControlOptionCard extends StatelessWidget {
  const FlightControlOptionCard({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Stack(
        alignment: Alignment.center,
        children: [
          Container(
            width: 310,
            height: 360,
            decoration: BoxDecoration(
              color: Colors.blueGrey[50],
              borderRadius: BorderRadius.circular(3),
              boxShadow: [
                BoxShadow(
                  color: Colors.grey,
                  blurRadius: 3,
                  offset: Offset(0, 6), // Shadow position
                ),
              ],
            ),
          ),
          Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              SizedBox(
                width: 230,
                height: 80,
                child: ElevatedButton(
                    onPressed: () async {
                      // temporary to test will replace an actaul automatic page
                      // TODO: implement Automatic control page
                      print('Automatic');

                      final Tello tello;

                      try {
                        tello = await Tello.tello();

                        await Future.delayed(const Duration(seconds: 4));

                        await tello.takeoff();
                        print('take off');
                        await Future.delayed(const Duration(seconds: 1));

                        tello.remoteControl(yaw: 90);
                        print('rotate');
                        await Future.delayed(const Duration(seconds: 5));

                        // await tello.fly(FlyDirection.forward, 300);
                        // print('forward');

                        // await tello.rotate(-90);
                        // print('rotate -90');

                        // await tello.flyToPosition(
                        //     x: 500, y: 0, z: 0, speed: 100);
                        // print('fly to 500 forwards');

                        // await tello.rotate(-90);
                        // print('1');

                        // await tello.fly(FlyDirection.forward, 500);
                        // print('1');

                        // await tello.rotate(-90);
                        // print('1');

                        // await tello.flyToPosition(
                        //     x: 500, y: 0, z: -100, speed: 100);
                        // print('1');

                        // await tello.rotate(-90);
                        // print('1');

                        // await tello.fly(FlyDirection.forward, 200);
                        // print('1');

                        await tello.land();
                        print('1');

                        tello.disconnect();
                      } catch (error, stacktrace) {
                        print("Error: $error");
                        print("Stack Trace: $stacktrace");
                      }
                    },
                    style: ElevatedButton.styleFrom(
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(3.0)),
                      backgroundColor: Colors.blueGrey[100],
                    ),
                    child: Text(
                      'AUTOMATIC',
                      style: TextStyle(
                        fontSize: 30,
                        color: Colors.black,
                      ),
                    )),
              ),
              SizedBox(height: 75),
              SizedBox(
                width: 230,
                height: 80,
                child: ElevatedButton(
                    onPressed: () {
                      print('Manual');
                      Navigator.push(
                          context,
                          MaterialPageRoute(
                              builder: (context) => ManualControlPage()));
                    },
                    style: ElevatedButton.styleFrom(
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(3.0)),
                      backgroundColor: Colors.blueGrey[100],
                    ),
                    child: Text(
                      'MANUAL',
                      style: TextStyle(
                        fontSize: 30,
                        color: Colors.black,
                      ),
                    )),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
