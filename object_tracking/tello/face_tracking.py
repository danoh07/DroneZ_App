from util import * 
import cv2

## Face tracking for closest person but just in terms of rotation

w,h=360,240
pid = [0.5,0.5,0] #kp, kd, and ki
pError = 0
startCounter = 0 # for no Flight then put this as 1 

myDrone = initializeTello()

while True:

    # Flight 
    if startCounter == 0:
        myDrone.takeoff()
        startCounter = 1

    ## Step 1
    img = telloGetFrame(myDrone, w, h)
    ## Step 2 
    img, info = findFace(img)
    # print(info[0][0]) # gives cx 
    # Step 3
    pError = trackFace(myDrone, info, w, pid, pError)

    cv2.imshow('Image', img)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        myDrone.land()
        break


 