package com.example.dronez_beta;

import static android.os.SystemClock.sleep;
import static java.lang.Thread.interrupted;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.*;

public class AutomaticControl extends AppCompatActivity {

    private static final int[] RC = {0, 0, 0, 0};  // Integer array to store the strength from the joystick
    Pattern statePattern = Pattern.compile("-*\\d{0,3}\\.?\\d{0,2}[^\\D\\W\\s]");  // a regex pattern to read the tello state

    private Handler telloStateHandler;  // and handler needs to be created to display the tello state values in the UI in realtime
    private boolean connectionFlag = false; // to check and maintain the connection status of the drone. Initially the drone is not conected, so the status is false
    private int connectionClickCounter = 1; // for counting the number of times the button is clicked
    private boolean videoStreamFlag = false;   // Tracking the video feeding status
    long startMs;                       // variable to calculate the time difference for video codec
    private MediaCodec m_codec;         // MediaCodec is used to decode the incoming H.264 stream from tello drone

    private ImageView connectToDrone;
    private Button submit;
    private EditText testCommand;
    private ImageView FeedingVideoAuto;
    private Switch videoFeedAuto;
    private Button autoButton;
    private int commandIndex = 0;
    // This is the all the command lines we want to put in Async function calls of telloConnect() function. ----------------------------------------------------------------------
    // The telloConnect function is the function that sends the command signal to the drone.
    private String[] commands = {"takeoff", "up 300", "cw 90", "forward 300", "ccw 90",
            "go 500 0 100 100", "ccw 90", "forward 500", "ccw 90", "go 500 0 -100 100", "ccw 90",
            " forward 200", "land"};
    private Boolean autoControlFlag = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // remove title bar from android screen
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_automatic_control);

        // Sets the  background color white
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#000000"));
        }


        // Go button that will start the auto-pilot flight of the drone
        CountDownLatch latch = new CountDownLatch(1);
        autoButton = findViewById(R.id.autoButton);
        autoButton.setOnClickListener(v -> {
            //This is where the auto commands will be put for auto control --------------------------------------------------------------------------------------------
            if (connectionFlag){
                autoControlFlag = true;
                telloConnect("takeoff");
                sleep(9000);
                telloConnect("go 200 0 100 100");
                sleep(9000);
                telloConnect("cw 90");
                sleep(9000);
                telloConnect("forward 200");
                sleep(9000);
                telloConnect("cw 90");
                sleep(9000);
                telloConnect("forward 200");
                sleep(9000);
                telloConnect("cw 90");
                sleep(9000);
                telloConnect("forward 200");
                sleep(9000);
                telloConnect("cw 90");
                sleep(9000);
                telloConnect("land");

                autoControlFlag = false;
                // While autoControlFlag is true, need to disable all the other buttons
            }
        });

        // Testing codes that I have been trying but does not work ------------------------------------------------------------------------------------------------------
//        autoButton.setOnClickListener(v -> {
//
//                if (connectionFlag) {
//                    telloConnect(commands[commandIndex]);
//                    commandIndex++;
//                    if (commandIndex >= commands.length) {
//                        commandIndex = 0;
//                        connectionFlag = false;
//                    }
//                    autoButton.setOnClickListener(v);
//                }

//                if (connectionFlag) {
//
//                    telloConnect("command");
//                    new Thread(new Runnable() { // create a new runnable thread to handle flight
//                        public void run() {
//
//                            while (commands.length < 13) {
//                                sleep(2000);
//                                telloConnect(commands[commandIndex]);
//                                commandIndex++;
//                            }
//                        }
//                    }).start();
//                }
//        });

        // Feeding the view and display on the screen
        FeedingVideoAuto = findViewById(R.id.FeedingViewAuto);

        videoFeedAuto = findViewById(R.id.videoFeedAuto);
        videoFeedAuto.setOnClickListener(view -> {
            if (connectionFlag) {
                if (videoFeedAuto.isChecked()) {
                    videoStreamFlag = true;
                    try {
                        BlockingQueue<Bitmap> frameV = new LinkedBlockingQueue<>(2);
                        videoHandler("streamon", frameV);
                        Runnable DLV = new AutomaticControl.displayBitmap(frameV);
                        new Thread(DLV).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (!videoFeedAuto.isChecked()) {
                    telloConnect("streamoff");
                    videoStreamFlag = false;
                }
            } else {
                Toast.makeText(AutomaticControl.this, "Drone disconnected", Toast.LENGTH_SHORT);
                videoFeedAuto.setChecked(false);
            }
        });

        // Button to connect to drone
        telloStateHandler = new Handler();

        connectToDrone = findViewById(R.id.connectToDroneAuto);
        connectToDrone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionClickCounter % 2 == 1) {   // to enable switch like behavior to connect and disconnect from the drone
                    telloConnect("command");
                    Toast.makeText(AutomaticControl.this, "Drone connected", Toast.LENGTH_SHORT).show();
                    connectionFlag = true;              // set the connection status to true
                }
                if (connectionClickCounter % 2 == 0) {
                    telloConnect("disconnect");
                    connectionFlag = false;
                    Toast.makeText(AutomaticControl.this, "Drone disconnected", Toast.LENGTH_SHORT).show();
                }
                connectionClickCounter++;
            }
        });

        // Testing the path of the drone by manual input on the screen --------------------------------This one you don't need to worry about--------------------------------------------------
        testCommand = findViewById(R.id.testCommand);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command = testCommand.getText().toString();
                telloConnect(command);
                testCommand.setText("");
            }
        });

    }

    // Connects with tello drone
    public void telloConnect(final String strCommand) {
        new Thread(new Runnable() { // create a new runnable thread to handle tello state
            public void run() {
                Boolean run = true; // always keep running once initiated
                try {
                    if (strCommand == "disconnect") {
                        run = false;
                    }
                    DatagramSocket udpSocket = new DatagramSocket(null); // create a datagram socket with null attribute so that a dynamic port address can be chosen later on

                    InetAddress serverAddr = InetAddress.getByName("192.168.10.1");     // set the tello IP address (refer Tello SDK 1.3)
                    byte[] buf = (strCommand).getBytes("UTF-8");             // command needs to be in UTF-8
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, 8889); // crate new datagram packet
                    udpSocket.send(packet);     // send packets to port 8889
                    while (run) {
                        byte[] message = new byte[1518];        // create a new byte message (you can change the size)
                        DatagramPacket rpacket = new DatagramPacket(message, message.length);
                        Log.i("UDP client: ", "about to wait to receive");
                        udpSocket.setSoTimeout(30000);           // set a timeout to close the connection -----------Initially was 2000 which is too short to wait for the next suto command. I extended it to 30000------------------------------------------------------------------------------------------------------
                        udpSocket.receive(rpacket);             // receive the response packet from tello
                        String text = new String(message, 0, rpacket.getLength()); // convert the message to text
                        Log.d("Received text", text);       // display the text as log in Logcat
                        new Thread(new Runnable() {             // create a new thread to stream tello state
                            @Override
                            public void run() {
                                while (!interrupted()) {
                                    sleep(100);            // I chose 2000 as the delay ------------------This is where delay of the command receiving on drone side is occurring. We can't put it too short as we need to give a drone time for actual flight------------------------------------------------------------------------------------------------
                                    byte[] buf = new byte[0];
                                    try {
//                                        if (autoButtonClicked == true){
//
//                                        }
                                        buf = ("battery?").getBytes("UTF-8");
                                        DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, 8889);
                                        udpSocket.send(packet);

                                        DatagramSocket socket = new DatagramSocket(null);   // create a new datagram socket
                                        socket.setReuseAddress(true);                               // set the reuse
                                        socket.setBroadcast(true);
                                        socket.bind(new InetSocketAddress(8890));              // bind to tello state port (refer to SDK 1.3)

                                        byte[] message = new byte[1518];
                                        DatagramPacket rpacket = new DatagramPacket(message, message.length); //, serverAddr, 8890
                                        socket.receive(rpacket);
                                        String text = new String(message, 0, rpacket.getLength());
                                        Matcher DCML = statePattern.matcher(text);                  // use the regex pattern initiated at the beginning of the code to parse the response from tell drone
                                        List<String> dec = new ArrayList<String>();                      // parse the response and store it in an array
                                        while (DCML.find()) {
                                            dec.add(DCML.group());
                                        }

                                        Log.d("Battery Charge : ", text + "%");
                                        telloStateHandler.post(new Runnable() {                     // use the initiated handler to post the tello state output the drone controller UI
                                            @Override
                                            public void run() {
                                                try {
//                                                    droneBattery.setText("Battery: " + dec.get(10) + "%");
//                                                        if (Integer.parseInt(dec.get(10)) <= 15){
//                                                            jdroneBattery.setBackgroundResource(R.drawable.rounded_corner_red); // if battery percentage is below 15 set the background of text to red
//                                                        }
//                                                        else {
//                                                            jdroneBattery.setBackgroundResource(R.drawable.rounded_corner_green); // else display batter percentage with green background
//                                                        }
                                                    if (Integer.parseInt(dec.get(10)) != 0) {
//                                                            wifiConnection.setBackgroundResource(R.drawable.connect_drone);     // if wifi is connected and is active then display with green background
//                                                        wifiConnection.setText("Connection: connected");
                                                    } else {
//                                                        wifiConnection.setText("Connection: disconnected");
                                                    }

                                                    telloStateHandler.removeCallbacks(this);

                                                } catch (Exception e) {
                                                    Log.e("Array out of bounds", "error", e);
                                                }
                                            }
                                        });

                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }).start();

                    }

                } catch (SocketException | UnknownHostException e) {
                    Log.e("Socket Open:", "Error:", e);
                } catch (IOException e) {
                    Log.e("IOException", "error", e);
                }

            }
        }).start();
    }

    // retrieve the video from tello drone and decode it to display on the UI
    public void videoHandler(final String strCommand, final BlockingQueue frameV) throws IOException { // add this for surfaceView : , Surface surface
        telloConnect(strCommand);

        BlockingQueue queue = frameV; // create a BlockingQueue since this function creates a thread and outputs a video frame which has to be displayed on the UI thread
        if (strCommand == "streamon"){
            new Thread(new Runnable() {
                Boolean streamon = true;    // keeps track if the video stream is on or off

                @Override
                public void run() {
                    // SPS and PPS are the golden key (it is like the right combination of keys used to unlock a lock) to decoding the video and displaying the stream
                    byte[] header_sps = {0, 0, 0, 1, 103, 77, 64, 40, (byte) 149, (byte) 160, 60, 5, (byte) 185}; // the correct SPS NAL
                    byte[] header_pps = {0, 0, 0, 1, 104, (byte) 238, 56, (byte) 128};  // the correct PPS NAL

                    MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 960, 720);
                    format.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps)); // pass the SPS keys
                    format.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps)); // pass the PPS keys
                    // by default the tello outputs 960 x 720 video
                    format.setInteger(MediaFormat.KEY_WIDTH, 960);
                    format.setInteger(MediaFormat.KEY_HEIGHT, 720);
                    format.setInteger(MediaFormat.KEY_CAPTURE_RATE,30);         // 30 fps
                    format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible); // the output is a YUV420 format which need to be converted later
                    format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 960 * 720);

                    try {
                        m_codec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);  // initialize the decoder with AVC format.
                        m_codec.configure(format, null ,null,0); // pass the format configuration to media codec with surface 'null' if processing the video for tasks like object detection, if not set to true
                        startMs = System.currentTimeMillis();   //calculate time to pass to the codec
                        m_codec.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream output = new ByteArrayOutputStream();  // data from video stream will be stored before passing it to media codec
                    DatagramSocket socketVideo = null;
                    try {
                        socketVideo = new DatagramSocket(null);     // create datagram socket with null parameter for address
                        socketVideo.setReuseAddress(true);                  // reusing the address
                        socketVideo.setBroadcast(true);
                        socketVideo.bind(new InetSocketAddress(11111)); // based on tell SDK 1.3, the port for receiving the video frames is 11111


                        byte[] videoBuf = new byte[2048];                   // create an empty byte buffer of size 2018
                        DatagramPacket videoPacket = new DatagramPacket(videoBuf, videoBuf.length); // create a datagram packet
                        int destPos = 0;
                        byte[] data_new = new byte[60000]; // 1460 + 3      // create another byte buffer of size 600000
                        while (streamon) {                                  // infinite loop to continuously receive
                            socketVideo.receive(videoPacket);               // receive packets from socket
                            System.arraycopy(videoPacket.getData(), videoPacket.getOffset(), data_new, destPos, videoPacket.getLength());
                            destPos += videoPacket.getLength();             // get the length of the packet
                            byte[] pacMan = new byte[videoPacket.getLength()]; // create a temporary byte buffer with the received packet size
                            System.arraycopy(videoPacket.getData(), videoPacket.getOffset(), pacMan, 0, videoPacket.getLength());
                            int len = videoPacket.getLength();
                            output.write(pacMan);
                            if (len < 1460) {                               // each frame of video from tello is 1460 bytes in size, with the ending frame that is usually less than <1460 bytes which indicate end of a sequence
                                destPos=0;
                                byte[] data = output.toByteArray();         // one the stream reaches the end of sequence, the entire byte array containing one complete frame is passed to data and the output variable is reset to receive newer frames
                                output.reset();                             // reset to receive newer frame
                                output.flush();
                                output.close();                             // close
                                int inputIndex = m_codec.dequeueInputBuffer(-1);
                                if (inputIndex >= 0) {
                                    ByteBuffer buffer = m_codec.getInputBuffer(inputIndex);
                                    if (buffer != null){
                                        buffer.clear(); // exp
                                        buffer.put(data); //  Caused by: java.lang.NullPointerException: Attempt to get length of null array // if nothing else pass: data
                                        long presentationTimeUs = System.currentTimeMillis() - startMs;
                                        m_codec.queueInputBuffer(inputIndex, 0, data.length, presentationTimeUs, 0);  // MediaCodec.BUFFER_FLAG_END_OF_STREAM -> produce green screen // MediaCodec.BUFFER_FLAG_KEY_FRAME and 0 works too // MediaCodec.BUFFER_FLAG_PARTIAL_FRAME
                                    }
                                }

                                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                                int outputIndex = m_codec.dequeueOutputBuffer(info, 100); // set it back to 0 if there is error associate with this change in value

                                if (outputIndex >= 0){
//
//                                    if (!detectionFlag){
//                                        m_codec.releaseOutputBuffer(outputIndex, false); // true if the surfaceView is available
//                                    }
//
//                                    else if (detectionFlag){
                                    try {
                                        Image image = m_codec.getOutputImage(outputIndex); // store the decoded (decoded by Mediacodec) data to Image format
                                        Bitmap BM = imgToBM(image);                        // convert from image format to BitMap format
                                        try {
                                            if (!queue.isEmpty()){
                                                queue.clear();
                                            }
                                            queue.put(BM);                                 // pass the data to the queue created earlier
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    m_codec.releaseOutputBuffer(outputIndex, false); // true if the surface is available
                                }
//                                }
                            }
                        }
                    } catch (SocketException socketException) {
                        socketException.printStackTrace();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }).start();

        }
        if (strCommand == "streamoff"){
            Log.d("Codec State","stopped and released called...");
            m_codec.stop();         // stop and release the codec
            m_codec.release();
        }

    }


    private Bitmap imgToBM(Image image){        // convert from Image to Bitmap format for neural network processing.
        Image.Plane[] p = image.getPlanes();
        ByteBuffer y = p[0].getBuffer();
        ByteBuffer u = p[1].getBuffer();
        ByteBuffer v = p[2].getBuffer();

        int ySz = y.remaining();
        int uSz = u.remaining();
        int vSz = v.remaining();

        byte[] jm8 = new byte[ySz + uSz + vSz];
        y.get(jm8, 0, ySz);
        v.get(jm8, ySz, vSz);
        u.get(jm8, ySz + vSz, uSz);

        YuvImage yuvImage = new YuvImage(jm8, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0,0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);
        byte[] imgBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imgBytes, 0 , imgBytes.length);
    }

    public class displayBitmap implements Runnable{

        protected BlockingQueue displayQueue;       // create a blocking queue to get the data from queue
        protected Bitmap displayBitmap_;             // create a bitmap variable for displaying bitmap

        public displayBitmap(BlockingQueue displayQueue_){
            this.displayQueue = displayQueue_;
        }

        @Override
        public void run(){

            while (true){
                try {
                    displayBitmap_ = (Bitmap) displayQueue.take();           // take data (video frame) from blocking queue
                    displayQueue.clear();                                   // clear the queue after taking
                    if (displayQueue != null){
                        runOnUiThread(() -> {                               // needs to be on UI thread
                            FeedingVideoAuto.setImageBitmap(displayBitmap_);     // set the bitmap to current frame in the queue
                            FeedingVideoAuto.invalidate();
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}