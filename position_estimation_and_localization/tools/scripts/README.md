# Instructions to run the scripts


## video_to_database:
### Dependency: 
* ffmpeg
* colmap
### Note:
* If not running on windows, system path needs to adjusted in the script
* COLMAP path needs to be adjusted depending on where COLMAP is installe

The script [video to dataset](./video_to_dataset.py) takes a video and splits it into multiple images that are used to compute a sparse or dense model using COLMAP. Once the reconstruction is done, the script extract poses of the cameras.
```bash
usage: video_to_dataset.sh [-h] -v VIDEO -o OUTPUT_PATH [-f FRAMES] [-c CAMERA_REFENCE_PATH]
                           [-n NUM_THREADS] [-q {low,medium,high,extreme}] [-t {sparse,dense}]

Convert video to a dataset

optional arguments:
  -h, --help            show this help message and exit
  -v VIDEO, --video VIDEO
                        Path to the video
  -o OUTPUT_PATH, --output_path OUTPUT_PATH
                        Output path where images are saved
  -f FRAMES, --frames FRAMES
                        Number of frames to extract
  -c CAMERA_REFENCE_PATH, --camera_refence_path CAMERA_REFENCE_PATH
                        Path where camera db values is saved
  -n NUM_THREADS, --num_threads NUM_THREADS
                        Number of threads to use (default all threads)
  -q {low,medium,high,extreme}, --quality {low,medium,high,extreme}
                        Quality of colmap reconstruction
  -t {sparse,dense}, --type {sparse,dense}
```


## split_absolute_dataset: 
### Note:
* need to run the video_to_dataset.py first
* pathlib imports needs to adjusted based on operating system.
* This is only used for training a ML algorithm 

### Dependency:
* Python: pandas

### Cross validation split
The script [split absolute dataset](./split_absolute_dataset.sh.py) and split dataset into train, validation and test files given a folder created with the video to dataset script.

```bash
usage: split_absolute_dataset.sh [-h] -i INPUT_PATH [-n] [-t TRAIN_SPLIT] [-v VALIDATION_SPLIT]
                                 [-e TEST_SPLIT]

Convert video to a dataset

optional arguments:
  -h, --help            show this help message and exit
  -i INPUT_PATH, --input_path INPUT_PATH
                        Input path where images and models are saved
  -n, --no_normalization
                        Block normalization process
  -t TRAIN_SPLIT, --train_split TRAIN_SPLIT
                        Number of samples for train phase
  -v VALIDATION_SPLIT, --validation_split VALIDATION_SPLIT
                        Number of samples for validation phase
  -e TEST_SPLIT, --test_split TEST_SPLIT
                        Number of samples for test phase
```
### Data folder structure
Given a video, the final folder specified by `OUTPUT_PATH` should present the following structure:
```bash
project 
├──  imgs
│  ├──  0000.png
│  ├──  ........
│  └──  XXXX.png
├──  workspace
│  ├──  sparse
│  │  └──  0
│  │     ├──  cameras.bin
│  │     ├──  images.bin
│  │     ├──  points3D.bin
│  │     └──  project.ini
│  └──  database.db
├──  points3D.csv
├──  positions.csv
├──  test.csv
├──  train.csv
└──  validation.csv
```
Where:
- `imgs` folder contains all frames;
- `workspace/` contains cloud point models created by COLMAP;
- `points3D.csv` contains all features extracted by COLMAP;
- `positions.csv` contains all camera poses extracted by COLMAP;
- `train.csv`, `validation.csv` and `test.csv` are files created with the split scripts.
