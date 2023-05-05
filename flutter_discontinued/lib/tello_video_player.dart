import 'dart:async';
import 'dart:io';
import 'dart:typed_data';
import 'package:flutter/material.dart';

class VideoBufferPlayer extends StatefulWidget {
  @override
  _VideoBufferPlayerState createState() => _VideoBufferPlayerState();
}

class _VideoBufferPlayerState extends State<VideoBufferPlayer> {
  final Uint8List _buffer = Uint8List(2048); // Buffer to hold incoming data

  // Create a stream controller to handle the incoming video stream
  StreamController<Uint8List> _streamController = StreamController.broadcast();

  // Create a timer to regularly check the streamController's buffer for incoming data
  late Timer _timer;

  // Create a RawDatagramSocket to listen for incoming video data
  late RawDatagramSocket _socket;

  int _bufferIndex = 0;

  @override
  void initState() {
    super.initState();
    _initSocket();
    _startTimer();
  }

  @override
  void dispose() {
    _streamController.close();
    _timer.cancel();
    _socket.close();
    super.dispose();
  }

  void _initSocket() async {
    _socket = await RawDatagramSocket.bind(InternetAddress.anyIPv4, 11111);
    _socket.listen(_onDataReceived);
  }

  void _onDataReceived(RawSocketEvent event) {
    if (event == RawSocketEvent.read) {
      final Datagram? datagram = _socket.receive();
      if (datagram != null) {
        // Write incoming data to buffer
        final bytes = datagram.data.buffer.asUint8List();
        int offset = 0;
        while (offset < bytes.length) {
          final int remaining = _buffer.length - _bufferIndex;
          final int copyLength = remaining < bytes.length - offset
              ? remaining
              : bytes.length - offset;
          _buffer.setRange(
              _bufferIndex, _bufferIndex + copyLength, bytes, offset);
          offset += copyLength;
          _bufferIndex += copyLength;
          if (_bufferIndex == _buffer.length) {
            // A full frame has been received, add it to the stream
            print(Uint8List.fromList(_buffer));
            _streamController.add(Uint8List.fromList(_buffer));
            _bufferIndex = 0;
          }
        }
      }
    }
  }

  void _startTimer() {
    _timer = Timer.periodic(Duration(milliseconds: 16), (_) {
      // Add buffer to stream controller
      _streamController.add(_buffer);
    });
  }

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<Uint8List>(
      stream: _streamController.stream,
      builder: (context, snapshot) {
        if (snapshot.data == null) return CircularProgressIndicator();
        print(snapshot.data);
        return Image.memory(snapshot.data!);
      },
    );
  }
}
