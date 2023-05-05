# Position estimation and localization
### In order to localize, we need to map out the area first. We will being using COLMAP for mapping algorithm. After the model of the area is created, the new images are fed and mapped to the selected database model to get the relative position. All the algorithm will be ran on the Nvidia Jetson Xavier NX 
#### Requirements: 
* Nvidia Jetson Xavier NX with CUDA support
#### Dependencies:
* <a herf='https://colmap.github.io/'> COLMAP </a>

### As an example, in the models folder, there are database produced by COLMAP using a regular PC of the 2nd floor of BU's CDS building. The sparse model can be viewed using COLMAP GUI. However, in the actual implementation, everything will be ran on Nvida Jetson Xavier NX  

<img width="600" height="300" src="https://user-images.githubusercontent.com/123419648/236561781-4c95436b-cdec-4a2d-ba02-98dbe42eaf9e.png">

#### Above is screen shot of the mapped area of the 2nd floor of CDS building from the input of the mp4 file in the video directory

