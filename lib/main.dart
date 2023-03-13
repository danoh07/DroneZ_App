import 'package:ryze_tello/ryze_tello.dart';
import 'package:flutter/material.dart';
import 'joy_stick.dart';
import 'package:flutter/services.dart';

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
              child:
                  Center(child: Text('LOGO', style: TextStyle(fontSize: 50))),
            ),
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

class MenuPage extends StatefulWidget {
  final int pageIndex;

  const MenuPage({Key? key, required this.pageIndex}) : super(key: key);

  @override
  State<MenuPage> createState() => _MenuPage();
}

class _MenuPage extends State<MenuPage> {
  var selectedIndex = 0;
  var firstTime = true;

  @override
  Widget build(BuildContext context) {
    // initial page
    if (firstTime) {
      selectedIndex = widget.pageIndex;
      firstTime = false;
    }

    Widget page;

    switch (selectedIndex) {
      case 0:
        page = Placeholder();
        break;
      case 1:
        page = TakeFlightPage();
        break;
      case 2:
        page = Placeholder();
        break;
      default:
        throw UnimplementedError('no widget for $selectedIndex');
    }

    var mainArea = AnimatedSwitcher(
      duration: Duration(milliseconds: 200),
      child: page,
    );

    return Scaffold(
      body: Stack(children: <Widget>[
        BackButton(onPressed: () {
          Navigator.pop(context);
        }),
        mainArea,
      ]),
      bottomNavigationBar: BottomNavigationBar(
        items: [
          BottomNavigationBarItem(
              icon: Icon(Icons.bookmark), label: 'Past Flight'),
          BottomNavigationBarItem(
              icon: Icon(Icons.rocket), label: 'Take Flight'),
          BottomNavigationBarItem(
              icon: Icon(Icons.question_mark), label: 'lorum ipsum'),
        ],
        currentIndex: selectedIndex,
        onTap: (value) {
          setState(() {
            selectedIndex = value;
          });
        },
      ),
    );
  }
}

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
                      print('Automatic');
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

class BackButton extends StatelessWidget {
  final VoidCallback onPressed;
  const BackButton({Key? key, required this.onPressed}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Align(
        alignment: Alignment.topLeft,
        child: IconButton(
          icon: const Icon(Icons.arrow_back),
          color: Colors.black,
          onPressed: onPressed,
          splashColor: Colors.transparent,
          hoverColor: Colors.transparent,
          iconSize: 30,
        ),
      ),
    );
  }
}

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
    ]);
  }

  @override
  Widget build(BuildContext context) {
    //Make function to control drone
    void callback(x, y) {}

    return Scaffold(
      body: Stack(children: <Widget>[
        BackButton(onPressed: () {
          SystemChrome.setPreferredOrientations([
            DeviceOrientation.landscapeRight,
            DeviceOrientation.landscapeLeft,
            DeviceOrientation.portraitUp,
            DeviceOrientation.portraitDown,
          ]);
          Navigator.pop(context);
        }),
        Column(
          mainAxisAlignment: MainAxisAlignment.end,
          children: [
            Row(
              children: <Widget>[
                Padding(
                    padding: EdgeInsets.only(left: 45),
                    child: JoyStick(
                        radius: 70, stickRadius: 15, callback: callback)),
                Spacer(),
                Padding(
                    padding: EdgeInsets.only(right: 45),
                    child: JoyStick(
                        radius: 70, stickRadius: 15, callback: callback)),
              ],
            ),
            SizedBox(height: 30)
          ],
        ),
      ]),
    );
  }
}
