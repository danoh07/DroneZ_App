import 'automatic_page.dart';
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
                    onPressed: () {
                      Navigator.push(
                          context,
                          MaterialPageRoute(
                              builder: (context) => AutomaticController()));
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
