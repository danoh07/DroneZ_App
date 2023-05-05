import 'package:flutter/material.dart';
import 'menu_page.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Drone Z',
      theme: ThemeData(
        primarySwatch: Colors.blueGrey,
      ),
      home: const MyHomePage(title: 'Drone Z Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;
  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  var selectedIndex = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'DroneZ Flight Controller',
              style: TextStyle(fontSize: 30),
            ),
            Container(
                margin: EdgeInsets.all(40),
                height: 200,
                width: 200,
                decoration: BoxDecoration(
                  color: Colors.blueGrey[100],
                  shape: BoxShape.circle,
                ),
                child: Center(
                    child: Image.asset(
                  'assets/dronez_logo.png',
                  scale: 2.3,
                ))),
            SizedBox(
              height: 100,
              width: 320,
              child: ElevatedButton.icon(
                onPressed: () {
                  Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) => MenuPage(pageIndex: 0)));
                },
                icon: Icon(
                  Icons.bookmark,
                  size: 50,
                ),
                label: Text('See Past Flights', style: TextStyle(fontSize: 20)),
                style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.blueGrey[100],
                    foregroundColor: Colors.black),
              ),
            ),
            SizedBox(height: 20),
            SizedBox(
              height: 100,
              width: 320,
              child: ElevatedButton.icon(
                onPressed: () {
                  Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) => MenuPage(pageIndex: 1)));
                },
                icon: Icon(
                  Icons.rocket,
                  size: 50,
                ),
                label: Text(
                  'Take Flight',
                  style: TextStyle(fontSize: 20),
                ),
                style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.blueGrey[100],
                    foregroundColor: Colors.black),
              ),
            ),
            SizedBox(height: 20),
            SizedBox(
              height: 100,
              width: 320,
              child: ElevatedButton.icon(
                onPressed: () {
                  Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) => MenuPage(pageIndex: 2)));
                },
                icon: Icon(
                  Icons.question_mark,
                  size: 50,
                ),
                label: Text('lorum ipsum', style: TextStyle(fontSize: 20)),
                style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.blueGrey[100],
                    foregroundColor: Colors.black),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
