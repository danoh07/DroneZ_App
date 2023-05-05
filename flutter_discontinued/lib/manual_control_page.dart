import 'dart:async';
import 'package:ryze_tello/ryze_tello.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_joystick/flutter_joystick.dart';
import 'utilities.dart' as util;
import 'tello_video_player.dart';

class ManualControlPage extends StatefulWidget {
  ManualControlPage({super.key});

  @override
  State<ManualControlPage> createState() => _ManualControlPageState();
}

class _ManualControlPageState extends State<ManualControlPage> {
  @override
  void initState() {
    super.initState();

    SystemChrome.setPreferredOrientations([
      DeviceOrientation.landscapeRight,
      DeviceOrientation.landscapeLeft,
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
    ]);
  }

  late final Tello tello;
  bool _streamOn = false;
  bool _connected = false;

  @override
  Widget build(BuildContext context) {
    //Make function to control drone

    return Scaffold(
      body: Stack(children: <Widget>[
        util.BackButton(onPressed: () {
          SystemChrome.setPreferredOrientations([
            DeviceOrientation.landscapeRight,
            DeviceOrientation.landscapeLeft,
            DeviceOrientation.portraitUp,
            DeviceOrientation.portraitDown,
          ]);
          if (_connected) {
            tello.disconnect();
          }

          Navigator.pop(context);
        }),
        Center(
          child: Container(
            child: _streamOn ? VideoBufferPlayer() : SizedBox.shrink(),
          ),
        ),
        Column(
          mainAxisAlignment: MainAxisAlignment.end,
          children: [
            Align(
                alignment: Alignment.center,
                child: Text('Tello drone connection: $_connected')),
            SizedBox(height: 5),
            Row(
              children: <Widget>[
                Padding(
                    padding: EdgeInsets.only(left: 45),
                    child: Joystick(
                      mode: JoystickMode.horizontalAndVertical,
                      listener: (details) {
                        var x = (details.x * 100).toInt();
                        var y = -(details.y * 100).toInt();

                        print('$x, $y');

                        if (_connected) {
                          tello.remoteControl(yaw: x, vertical: y);
                        }
                      },
                    )),
                ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      fixedSize: const Size(50, 50),
                      shape: const CircleBorder(),
                    ),
                    onPressed: () async {
                      try {
                        tello = await Tello.tello();
                        setState(() {
                          _connected = true;
                        });
                      } catch (error, stack) {
                        print("Error: $error");
                        print("Error: $stack");
                      }
                    },
                    child: Text(
                      "Connect",
                      style: TextStyle(fontSize: 8.5),
                    )),
                ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      fixedSize: const Size(50, 50),
                      shape: const CircleBorder(),
                    ),
                    onPressed: () async {
                      if (_connected) {
                        try {
                          await tello.takeoff();
                          tello.state;
                          print('Successful');
                        } catch (error, stack) {
                          print(error);
                          print("Error: $stack");
                        }
                      } else {
                        print('Tello Not Connected');
                      }
                    },
                    child: Text(
                      "Take Off",
                      style: TextStyle(fontSize: 8.5),
                    )),
                ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      fixedSize: const Size(50, 50),
                      shape: const CircleBorder(),
                    ),
                    onPressed: () async {
                      if (_connected) {
                        try {
                          await tello.land();
                          print('Successful');
                        } catch (error) {
                          print(error);
                        }
                      } else {
                        print('Tello Not Connected');
                      }
                    },
                    child: Text(
                      "Land",
                      style: TextStyle(fontSize: 8.5),
                    )),
                ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      fixedSize: const Size(50, 50),
                      shape: const CircleBorder(),
                    ),
                    onPressed: () async {
                      if (_connected) {
                        try {
                          await tello.startVideo();
                          // var tellovid = await TelloStream.telloStream();

                          setState(() {
                            // telloStream = tellovid;
                            _streamOn = true;
                            print('Stream on');
                          });
                        } catch (error) {
                          print(error);
                        }
                      } else {
                        print('Tello Not Connected');
                      }
                    },
                    child: Text(
                      "Stream",
                      style: TextStyle(fontSize: 8.5),
                    )),
                Spacer(),
                Padding(
                    padding: EdgeInsets.only(right: 45),
                    child: Joystick(
                      mode: JoystickMode.all,
                      listener: (details) {
                        var x = (details.x * 100).toInt();
                        var y = -(details.y * 100).toInt();

                        print('$x, $y');

                        if (_connected) {
                          tello.remoteControl(roll: x, pitch: y);
                        }
                      },
                    )),
              ],
            ),
            SizedBox(height: 30)
          ],
        ),
      ]),
    );
  }
}

class DisplayState extends StatefulWidget {
  final Tello tello;

  DisplayState({required this.tello});

  @override
  State<DisplayState> createState() => _DisplayStateState();
}

class _DisplayStateState extends State<DisplayState> {
  int battery = 0;
  int flightTime = 0;
  late StreamSubscription<TelloState> stateListener;

  @override
  void initState() {
    super.initState();
    stateListener = widget.tello.state.listen((TelloState state) {
      setState(() {
        battery = state.battery;
        flightTime = state.flightTime;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        SizedBox(
          height: 20,
          width: 20,
          child: Text('$battery'),
        ),
        SizedBox(width: 30),
        SizedBox(
          height: 20,
          width: 20,
          child: Text('$flightTime'),
        ),
      ],
    );
  }
}
