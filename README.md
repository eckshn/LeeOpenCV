# All Ore Nothing
## Installation
1. Get Eclipse to work
2. Installing (Windows)
- https://opencv.org/releases/
  - https://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html#set-up-opencv-for-java-in-eclipse
  - Use guide linked above to properly install OpenCV 
- https://gluonhq.com/products/javafx/
  - Download SDK and extract
  - Similar to OpenCV create a user library in Eclipse using all of the .jar files in lib folder
    - When selecting external jars to add, use shift to choose all at once
  - Create libraries using the files inside the libraries folder
  - Right click project in Eclipse → build path → add libraries → user library
- Arduino code is Rover.ino
  - First, upload the Rover.ino to your Arduino board and update the pin numbers in the program according to how you wired it. 
    - Run the file to make sure it works. 
  - Add all the jars in the library folder to your build path. Also include the javafx and opencv jars from earlier. 
  - Change the port number in the “Arduino.java” file to the port that your arduino is plugged into. 
  - Run out of the Video.java file and make sure that the console prints “Connected to Arduino!” 
- If "Error: JavaFX runtime components are missing, and are required to run this application"
  - https://stackoverflow.com/questions/51478675/error-javafx-runtime-components-are-missing-and-are-required-to-run-this-appli
    - --module-path "C:\Users\haste\Desktop\openjfx-16_windows-x64_bin-sdk\javafx-sdk-16\lib" --add-modules javafx.controls,javafx.fxml
      - Replace text in quotes with your path to library
      - Run the program
      - Click Run Configurations
      - In the arguments tab set the VM argument to the library folder location of javafx

