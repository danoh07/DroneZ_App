package com.example.dronez_beta;

import static android.os.SystemClock.sleep;
import static java.lang.Thread.interrupted;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class ManualControl extends AppCompatActivity {

    private static final int[] RC = {0, 0, 0, 0};  // Integer array to store the strength from the joystick
    Pattern statePattern = Pattern.compile("-*\\d{0,3}\\.?\\d{0,2}[^\\D\\W\\s]");  // a regex pattern to read the tello state

    private ImageView FeedingView;
    private TextView droneBattery;
    private TextView wifiConnection;
    private ImageView connectToDrone;
    private ImageView takeoff;
    private ImageView land;
    JoystickView leftjoystick;
    JoystickView rightjoystick;
    private Handler telloStateHandler;  // and handler needs to be created to display the tello state values in the UI in realtime
    private boolean connectionFlag = false; // to check and maintain the connection status of the drone. Initially the drone is not conected, so the status is false
    private int connectionClickCounter = 1; // for counting the number of times the button is clicked

    // Video feeding
    private Switch videoFeeding;
    private boolean videoStreamFlag = false;   // Tracking the video feeding status
    long startMs;                       // variable to calculate the time difference for video codec
    private MediaCodec m_codec;         // MediaCodec is used to decode the incoming H.264 stream from tello drone
    private ImageView videocam;
    private Uri videopath;
    private int videoRecordCounter = 1;

    // Detection
    private boolean detectionFlag;      // Tracking if the user wants to do object detection
    private FloatingActionButton DroneObjectDetection;
    private Module jMod = null;
    private DetectionResult jResults;
    private float rtThreshold = 0.30f;


    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // remove title bar from android screen
        // Removes the action bar
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_manual_control);

        // Sets the  background color white
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#000000"));
        }

        // ===================================================================================================Recording===============================================================================================
        // ===========================================================================================================================================================================================================
        // Saving the video feed into album
        videocam = findViewById(R.id.videocam);
//        videocam.setOnClickListener(view ->{
//            if (connectionFlag) {
//                if (videoStreamFlag) {
//                    FFmpegFrameRecorder recorder = new FFmpegFrameRecorder("/sdcard/test.mp4",256,256);
//                    if (videoRecordCounter % 2 == 1) {
//                        // Recording started
//                        telloConnect("startRecording");
//                        Toast.makeText(this, "recording started", Toast.LENGTH_SHORT).show();
//
//                        try {
//                            recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
//                            recorder.setFormat("mp4");
//                            recorder.setFrameRate(30);
////                            recorder.setPixelFormat(avutil.PIX_FMT_YUV420P10);
//                            recorder.setVideoBitrate(1200);
//                            recorder.startUnsafe();
//                            for (int i = 0; i < 5; i++) {
//                                view.setDrawingCacheEnabled(true);
////                                Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
//                                view.setDrawingCacheEnabled(false);
////                                recorder.record(bitmap);
//                            }
//                        }
//                        catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                    if (videoRecordCounter % 2 == 0) {
//                        telloConnect("stopRecording");
//                        Toast.makeText(this, "recording ended", Toast.LENGTH_SHORT).show();
//
////                        recorder.stop();
//                    }
//                    videoRecordCounter++;
//                }
//                else{
//                    Toast.makeText(this, "video stream is not on", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        // ===================================================================================================Video Feeding & Detection===============================================================================================
        // ===========================================================================================================================================================================================================
        FeedingView = findViewById(R.id.bitView);
        jResults = findViewById(R.id.DetectionResultView); // this is a custom view that will display the object detection results (bounding boxes) on top of video Feed
        videoFeeding = findViewById(R.id.videoFeed);
        videoFeeding.setOnClickListener(view -> {
            if (connectionFlag) {
                if (videoFeeding.isChecked()) {
                    videoStreamFlag = true;
                    detectionFlag = true;
                    videoFeeding.setBackgroundResource(R.drawable.rounded_corner_green);
                    try {
                        BlockingQueue<Bitmap> frameV = new LinkedBlockingQueue<>(2);
                        videoHandler("streamon", frameV);
                        Runnable DLV = new displayBitmap(frameV);
                        new Thread(DLV).start();
                        Runnable r = new objectDetectionThread(frameV);
                        new Thread(r).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (!videoFeeding.isChecked()) {
                    telloConnect("streamoff");
                    videoStreamFlag = false;
                }
            } else {
                Toast.makeText(ManualControl.this, "Drone disconnected", Toast.LENGTH_SHORT);
                videoFeeding.setChecked(false);
                detectionFlag = false;
                videoFeeding.setBackgroundResource(R.drawable.rounded_corner_trans);
            }
        });

        // ===================================================================================================Connection===============================================================================================
        // ===========================================================================================================================================================================================================
        telloStateHandler = new Handler();

        droneBattery = findViewById(R.id.droneBattery);

        // Click wifi button to connect with drone. If connected the connection status will display connected/disconnected
        wifiConnection = findViewById(R.id.wifiConnection);
        connectToDrone = findViewById(R.id.connectToDrone);   // A button to initiate establishing SDK mode with the drone by sending "command" command
        connectToDrone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (connectionClickCounter % 2 == 1) {   // to enable switch like behavior to connect and disconnect from the drone
                    telloConnect("command");
                    Toast.makeText(ManualControl.this, "Drone connected", Toast.LENGTH_SHORT).show();
                    connectionFlag = true;              // set the connection status to true
                }
                if (connectionClickCounter % 2 == 0) {
                    telloConnect("disconnect");
                    connectionFlag = false;
                    Toast.makeText(ManualControl.this, "Drone disconnected", Toast.LENGTH_SHORT).show();
                }
                connectionClickCounter++;
            }
        });

        // ===================================================================================================Commands===============================================================================================
        // ===========================================================================================================================================================================================================
        // Click takeoff button to send "takeoff" command to drone
        takeoff = findViewById(R.id.takeoff);
        takeoff.setOnClickListener(v -> {
            if (connectionFlag) {
                telloConnect("takeoff");
            }
        });

        // Click land button to send "land" command to drone
        land = findViewById(R.id.land);
        land.setOnClickListener(v -> {
            if (connectionFlag) {
                telloConnect("land");
            }
        });


        // ===================================================================================================Joysticks===============================================================================================
        // ===========================================================================================================================================================================================================
        leftjoystick = findViewById(R.id.joystickViewLeft); // left joystick where the angle is the movement angle and strength is the extend to which you push the joystick
        leftjoystick.setOnMoveListener((angle, strength) -> {

            if (angle >45 && angle <=135){
                RC[2]= strength;
            }
            if (angle >226 && angle <=315){
                strength *= -1;
                RC[2]= strength;
            }
            if (angle >135 && angle <=225){
                strength *= -1;
                RC[3]= strength;
            }
            if (angle >316 && angle <=359 || angle >0 && angle <=45){
                RC[3]= strength;
            }

            telloConnect("rc "+ RC[0] +" "+ RC[1] +" "+ RC[2] +" "+ RC[3]); // send the command eg,. 'rc 10 00 32 00'
            Arrays.fill(RC, 0); // reset the array with 0 after every virtual joystick move

        });

        rightjoystick = findViewById(R.id.joystickViewRight);
        rightjoystick.setOnMoveListener((angle, strength) -> {
            if (angle >45 && angle <=135){
                RC[1]= strength;
            }
            if (angle >226 && angle <=315){
                strength *= -1;
                RC[1]= strength;
            }
            if (angle >135 && angle <=225){
                strength *= -1;
                RC[0]= strength;
            }
            if (angle >316 && angle <=359 || angle >0 && angle <=45){
                RC[0]= strength;
            }

            telloConnect("rc "+ RC[0] +" "+ RC[1] +" "+ RC[2] +" "+ RC[3]);
            Arrays.fill(RC, 0); // reset the array with 0 after every virtual joystick move
        });
    }

    // ===================================================================================================Connection Handler===============================================================================================
    // ===========================================================================================================================================================================================================
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
                        udpSocket.setSoTimeout(2000);           // set a timeout to close the connection
                        udpSocket.receive(rpacket);             // receive the response packet from tello
                        String text = new String(message, 0, rpacket.getLength()); // convert the message to text
                        Log.d("Received text", text);       // display the text as log in Logcat
                        new Thread(new Runnable() {             // create a new thread to stream tello state
                            @Override
                            public void run() {
                                while (!interrupted()) {
                                    sleep(2000);            // I chose 2 seconds as the delay
                                    byte[] buf = new byte[0];
                                    try {
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
                                                    droneBattery.setText("Battery: " + dec.get(10) + "%");
                                                        if (Integer.parseInt(dec.get(10)) <= 15){
                                                            droneBattery.setBackgroundResource(R.drawable.rounded_corner_red); // if battery percentage is below 15 set the background of text to red
                                                        }
                                                        else{
                                                            droneBattery.setBackgroundResource(R.drawable.rounded_corner_green);
                                                        }
                                                    if (Integer.parseInt(dec.get(10)) != 0) {
                                                        wifiConnection.setBackgroundResource(R.drawable.rounded_corner_green);     // if wifi is connected and is active then display with green background
                                                        wifiConnection.setText("Connection: connected");
                                                    } else {
                                                        wifiConnection.setBackgroundResource(R.drawable.rounded_corner_red);
                                                        wifiConnection.setText("Connection: disconnected");
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

    // ===================================================================================================Video Feeding Handler===============================================================================================
    // ===========================================================================================================================================================================================================
    // --------------------------This is where the video handler is to start collecting YUVV Frames from drone in the queue and trying to convert it to Bitmap Frames-----------------------------------------------
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
                    format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible); // the output is a YUV420 format which need to be converted later -----------The initial format of the Frame (YUV), which we need to collect for recording-------------------
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

                                        try {
                                            // ------------------------------------------------Basically, before converting the YUV to Image format below, we need to store the data if recording started ------------------------------------------------------
                                            Image image = m_codec.getOutputImage(outputIndex); // store the decoded (decoded by Mediacodec) data to Image format ---------------------------Storing the YUV decoded frame into Image format-----------------------------

                                            //-------------------------------------------------Or we can try to convert Image Frame back to YUV before the below line starts, which I feel is redundant though if we can store it before----------------------------
                                            Bitmap BM = imgToBM(image);                        // convert from image format to BitMap format -----------------------------------------------Converting from Image to Bitmap format
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

    // ===================================================================================================Frame conversion===============================================================================================
    // ===========================================================================================================================================================================================================
    // --------------------------------------------------------------------------------Helper function to conver the Image Frame to Bitmap Frame that is done above----------------------------------------------------------------------------------------
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

    // ===================================================================================================Feeding Handler===============================================================================================
    // ===========================================================================================================================================================================================================
    // ----------------------------------------------------------------------By popping the stored Bipmap Frame queue, display it on the screen----------------------------------------------------------------------------------------------------
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
                            FeedingView.setImageBitmap(displayBitmap_);     // set the bitmap to current frame in the queue
                            FeedingView.invalidate();
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ===================================================================================================Detection Handler===============================================================================================
    // ===========================================================================================================================================================================================================
    public class objectDetectionThread implements Runnable{

        private Bitmap threadBM;                            // create a bitmap variable
        private volatile ArrayList results;                 // create an array list to store the object detection result
        protected BlockingQueue threadFrame = null;         // blocking queue variable to take the data from blocking queue

        public  objectDetectionThread(BlockingQueue consumerQueue){
            this.threadFrame = consumerQueue;               // retrieve element from queue
        }

        @WorkerThread
        @Nullable
        public void run(){
            while (true){
                try {
                    threadBM = (Bitmap) threadFrame.take();
                    threadFrame.clear();                    // clear queue after getting the frame
                    analyseImage(threadBM);                 // function that will analyze the image for object detection
                    sleep(250);                         // change to 1000 if error arises
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        public ArrayList getValue(){
            return results;
        }
    }   // end of objectDetectionThread function

    static class ResA{
        private final ArrayList<Result> jResults;
        public ResA(ArrayList<Result> results){
            jResults = results;
        }
    }

    // ===================================================================================================Detection Analyze===============================================================================================
    // ===========================================================================================================================================================================================================
    @WorkerThread
    @Nullable
    protected ManualControl.ResA analyseImage(Bitmap BMtest){
        try {
            if (jMod == null){
                jMod = LiteModuleLoader.load(ManualControl.assetFilePath(getApplicationContext(),"yolov5s.torchscript.ptl"));        // load pre-trained pytorch module from the assets folder
                BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("classes.txt")));                       // similarly load the classes.txt from assets
                String line;
                List<String> classes = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    classes.add(line);
                }
                ImageProcessing.jClasses = new String[classes.size()];
                classes.toArray(ImageProcessing.jClasses);
            }
        }catch (IOException e){
            Log.e("Object detection: ", "Unable to load model...");
            return null;
        }

        Matrix M = new Matrix();
//        M.postRotate(90.0f);
        BMtest = Bitmap.createBitmap(BMtest, 0,0, BMtest.getWidth(), BMtest.getHeight(), M, true);
        Bitmap resizedBM = Bitmap.createScaledBitmap(BMtest, ImageProcessing.jInputW, ImageProcessing.jInputH, true); // crate a bitmap of 640x640 dimension

        // DL model inference starts here
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBM, ImageProcessing.NO_MEAN_RGB, ImageProcessing.NO_STD_RGB);
        IValue[] oTuple = jMod.forward(IValue.from(inputTensor)).toTuple();
        final Tensor oTensor = oTuple[0].toTensor();
        final float[] O = oTensor.getDataAsFloatArray();

        float imSX = (float)BMtest.getWidth() / ImageProcessing.jInputW;
        float imSY = (float)BMtest.getHeight() / ImageProcessing.jInputH;
        float ivSX = (float)jResults.getWidth() /BMtest.getWidth();
        float ivSY = (float)jResults.getHeight() /BMtest.getHeight();

        final ArrayList<Result> results = ImageProcessing.outputsNMSFilter(O, rtThreshold, imSX, imSY, ivSX, ivSY, 0, 0);
        int listSize =results.size();
        if (results != null){
            runOnUiThread(() -> {
                jResults.setResults(results);
                jResults.invalidate();
            });
        }
        return  new ManualControl.ResA(results);

    }
}

