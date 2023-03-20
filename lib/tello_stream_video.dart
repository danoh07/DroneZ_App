import 'dart:io';
import 'dart:typed_data';
import 'dart:collection';
import 'dart:async';
import 'package:ryze_tello/ryze_tello.dart';

import 'package:flutter/material.dart';
import 'package:handy/handy.dart';

class Address {
  final InternetAddress ip;
  final int port;

  const Address({required this.ip, required this.port});

  @override
  String toString() => "$Address($ip, $port)";
}

class TelloStream {
  final TelloVideoSocket _videoReceiver;

  static Future<TelloStream> telloStream({
    Duration? timeout = const Duration(seconds: 12),
    Address? telloAddress,
    Address? localAddress,
    Address? videoReceiverAddress,
  }) async {
    videoReceiverAddress = videoReceiverAddress ??
        Address(ip: InternetAddress.anyIPv4, port: 11111);

    TelloVideoSocket socket = await TelloVideoSocket.telloSocket(
        telloAddress: telloAddress,
        localAddress: videoReceiverAddress,
        timeout: timeout);

    return TelloStream._(socket);
  }

  TelloStream._(this._videoReceiver);
}

class TelloVideoSocket {
  late final Stream<Uint8List> _responses;

  final RawDatagramSocket _socket;

  final Address _telloAddress;

  final Queue<Completer<Uint8List>> _responseQueue =
      Queue<Completer<Uint8List>>();

  final Cleaner<StreamSubscription<Uint8List>> _subscriptionCleaner =
      Cleaner<StreamSubscription<Uint8List>>(
          (StreamSubscription<Uint8List> subscription) {
    subscription.cancel();
  });

  final Cleaner<Timer> _timeoutCleaner = Cleaner<Timer>((Timer timer) {
    timer.cancel();
  });

  final Duration? _timeout;

  static Future<TelloVideoSocket> telloSocket(
      {Duration? timeout = const Duration(seconds: 12),
      Address? telloAddress,
      Address? localAddress}) async {
    print('Connection made');
    return TelloVideoSocket._(
        await RawDatagramSocket.bind(InternetAddress.anyIPv4, 11111),
        telloAddress ??
            Address(ip: InternetAddress("192.168.10.1"), port: 8889),
        timeout);
  }

  TelloVideoSocket._(this._socket, this._telloAddress, this._timeout) {
    print("this got called");
    _responses = _socket
        .where((RawSocketEvent event) => event == RawSocketEvent.read)
        .map((RawSocketEvent event) => _socket.receive())
        .where((Datagram? datagram) => datagram != null)
        .map((Datagram? receivedData) {
      receivedData!;
      InternetAddress receivedDataAddress = receivedData.address;

      print(receivedData.data);

      if (receivedDataAddress != _telloAddress.ip) {
        throw SocketException(
            "Unknown connection from ip $receivedDataAddress");
      }
      print(receivedData.data);
      return receivedData.data;
    }).asBroadcastStream(
            onListen: _subscriptionCleaner.add,
            onCancel: _subscriptionCleaner.remove);

    _responses.listen((Uint8List response) {
      if (_responseQueue.isEmpty) return;

      _responseQueue.removeFirst().complete(response);
    });
  }

  bool get waiting => _responseQueue.isNotEmpty;

  Future<Uint8List> receive() {
    Completer<Uint8List> responseWaiter = Completer<Uint8List>();

    final Duration? timeout = _timeout;

    if (timeout != null) {
      StackTrace outerStackTrace = StackTrace.current;

      _timeoutCleaner.add(Timer(timeout, () {
        if (responseWaiter.isCompleted) return;

        responseWaiter.completeError(
            TimeoutException(
                "The Tello's response didn't arrive within the Timeout's duration."),
            outerStackTrace);
      }));
    }
    _responseQueue.add(responseWaiter);

    return responseWaiter.future;
  }

  Stream<Uint8List> get responses => _responses;

  void disconnect() {
    _subscriptionCleaner.cleanup();
    _timeoutCleaner.cleanup();
    _socket.close();
  }
}

class TelloVideoWidget extends StatefulWidget {
  final TelloStream telloStream;
  final Tello tello;

  TelloVideoWidget({required this.telloStream, required this.tello});

  @override
  _TelloVideoWidgetState createState() => _TelloVideoWidgetState();
}

class _TelloVideoWidgetState extends State<TelloVideoWidget> {
  late Stream<Uint8List> _videoStream;
  late StreamSubscription<Uint8List> _videoSubscription;
  Uint8List _videoFrame = Uint8List(0);

  @override
  void initState() {
    super.initState();
    getSocketConnection();
    _videoStream = widget.telloStream._videoReceiver.responses;
    _videoSubscription = _videoStream.listen(_handleVideoFrame);
  }

  void getSocketConnection() async {
    await widget.tello.startVideo();
    final socket = await RawDatagramSocket.bind(InternetAddress.anyIPv4, 8890);
    await Future.delayed(const Duration(seconds: 2));
    print('Connected');
    // Listen for incoming packets
    socket.listen((event) {
      print(event);
      if (event == RawSocketEvent.read) {
        final datagram = socket.receive();
        if (datagram != null) {
          print(
              'Received packet from ${datagram.address.address}:${datagram.port}: ${String.fromCharCodes(datagram.data)}');
        }
      }
    });
  }

  @override
  void dispose() {
    super.dispose();

    _videoSubscription.cancel();
    widget.telloStream._videoReceiver.disconnect();
  }

  void _handleVideoFrame(Uint8List frame) {
    setState(() {
      _videoFrame = frame;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      child: _videoFrame.isNotEmpty
          ? Image.memory(_videoFrame)
          : SizedBox.shrink(),
    );
  }
}
