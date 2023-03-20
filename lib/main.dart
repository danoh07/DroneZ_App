import 'package:flutter/material.dart';
import 'utilities.dart' as util;
import 'flight_option_page.dart';

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
        util.BackButton(onPressed: () {
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
