import 'utilities.dart' as util;
import 'flight_option_page.dart';
import 'package:flutter/material.dart';

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
