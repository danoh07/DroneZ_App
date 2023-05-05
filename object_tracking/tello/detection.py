import cv2
import djitellopy

# Initialize the Tello drone
drone = djitellopy.Tello()

# Connect to the Tello drone's camera
drone.connect()
drone.streamon()

# Initialize the face detection algorithm
face_cascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')

# Create a video capture object to read the video stream from the drone
cap = cv2.VideoCapture('udp://@0.0.0.0:11111')

# Process each frame of video data in real-time
while True:
    # Capture a frame of video data from the Tello drone
    ret, frame = cap.read()

    if not ret:
        break

    # Convert the frame to grayscale for face detection
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # Detect faces in the grayscale frame
    faces = face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5, minSize=(30, 30))

    # Draw a rectangle around each detected face
    for (x, y, w, h) in faces:
        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)

    # Display the video feed with detected faces
    cv2.imshow('Tello Face Detection', frame)

    # Exit the program if the 'q' key is pressed
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Clean up resources
cv2.destroyAllWindows()
cap.release()
drone.streamoff()
drone.land()
drone.disconnect()