import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:ryze_tello/ryze_tello.dart';
import 'package:flutter/material.dart';

class AutomaticController extends StatefulWidget {
  const AutomaticController({super.key});

  @override
  State<AutomaticController> createState() => _AutomaticControllerState();
}

class _AutomaticControllerState extends State<AutomaticController> {
  late final Tello tello;
  final GlobalKey<AnimatedListState> _key = GlobalKey<AnimatedListState>();
  var _instructions = [];

  FlyDirection dropdownValue = FlyDirection.up;
  int lengthCm = 10;
  bool connected = false;

  void connectTello() async {
    tello = await Tello.tello();
    setState(() {
      connected = true;
    });
  }

  void add() {
    _instructions.insert(0, [dropdownValue, lengthCm]);
    _key.currentState!
        .insertItem(0, duration: const Duration(milliseconds: 200));
  }

  void remove(int index) {
    _key.currentState!.removeItem(index, (_, animation) {
      return SizeTransition(
        sizeFactor: animation,
        child: const Card(
          margin: EdgeInsets.all(10),
          color: Colors.blueGrey,
          child: ListTile(
            title: Text('Deleted'),
          ),
        ),
      );
    }, duration: const Duration(milliseconds: 200));
    _instructions.removeAt(index);
  }

  void execute() async {
    if (connected) {
      try {
        await tello.takeoff();
        print('take off');

        for (int i = _instructions.length - 1; i >= 0; i--) {
          var instuction = _instructions[i];
          print('fly ${instuction[0]}, ${instuction[1]}');
          try {
            await tello.fly(instuction[0], instuction[1]);
          } catch (error) {
            print(error);
            if (error.toString() == 'TelloError: Motor stop' ||
                error.toString().startsWith('SocketException')) {
              break;
            }
            i++;
            continue;
          }

          remove(i);
        }

        await tello.land();
        print('Completed');
      } catch (error, stacktrace) {
        print("Error: $error");
        print("Stack Trace: $stacktrace");
        await tello.land();
      }
    } else {
      print("Tello not connected");
    }
  }

  void clear() {
    setState(() {
      _instructions.clear();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Expanded(
                child: AnimatedList(
                    key: _key,
                    itemBuilder: (context, index, animation) {
                      return SizeTransition(
                          key: UniqueKey(),
                          sizeFactor: animation,
                          child: InstructionCard(
                            item: _instructions[index],
                            onPressed: () {
                              remove(index);
                            },
                          ));
                    }),
              ),
              Text('Tello drone connection: $connected'),
              Padding(padding: EdgeInsets.all(10)),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  SizedBox(
                    width: 110,
                    child: DropdownButton(
                      value: dropdownValue,
                      items: [
                        DropdownMenuItem<FlyDirection>(
                            value: FlyDirection.up, child: Text('Up')),
                        DropdownMenuItem<FlyDirection>(
                            value: FlyDirection.down, child: Text('Down')),
                        DropdownMenuItem<FlyDirection>(
                            value: FlyDirection.forward,
                            child: Text('Forward')),
                        DropdownMenuItem<FlyDirection>(
                            value: FlyDirection.back, child: Text('Backward')),
                        DropdownMenuItem<FlyDirection>(
                            value: FlyDirection.left, child: Text('Left')),
                        DropdownMenuItem<FlyDirection>(
                            value: FlyDirection.right, child: Text('Right')),
                      ],
                      onChanged: (value) => {
                        setState(() {
                          dropdownValue = value!;
                        })
                      },
                    ),
                  ),
                  Padding(
                    padding: EdgeInsets.all(15),
                  ),
                  CupertinoButton.filled(
                      child: Text('$lengthCm cm'),
                      onPressed: () => showCupertinoModalPopup(
                          context: context,
                          builder: (_) => SizedBox(
                                width: double.infinity,
                                height: 250,
                                child: CupertinoPicker(
                                  backgroundColor: Colors.white,
                                  itemExtent: 30,
                                  scrollController: FixedExtentScrollController(
                                      initialItem: lengthCm - 10),
                                  children: [
                                    for (int i = 10; i <= 300; i++) ...[
                                      Text('$i')
                                    ]
                                  ],
                                  onSelectedItemChanged: (int value) {
                                    setState(() {
                                      lengthCm = value + 10;
                                    });
                                  },
                                ),
                              ))),
                ],
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  ElevatedButton(
                      onPressed: () {
                        add();
                        print(_instructions);
                      },
                      child: Text('Add')),
                  Padding(padding: EdgeInsets.all(8.0)),
                  ElevatedButton(
                      onPressed: () {
                        execute();
                      },
                      child: Text('Execute')),
                  Padding(padding: EdgeInsets.all(8.0)),
                  ElevatedButton(
                      onPressed: () {
                        connectTello();
                      },
                      child: Text('Connect'))
                ],
              ),
              Padding(padding: EdgeInsets.all(30))
            ],
          ),
        ),
      ),
    );
  }
}

class InstructionCard extends StatelessWidget {
  const InstructionCard({required this.item, required this.onPressed});

  final List item;
  final VoidCallback onPressed;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListTile(
        title: Text('${item[0]} for ${item[1]} cm'),
        trailing: IconButton(
          icon: const Icon(Icons.delete),
          onPressed: onPressed,
        ),
      ),
    );
  }
}
