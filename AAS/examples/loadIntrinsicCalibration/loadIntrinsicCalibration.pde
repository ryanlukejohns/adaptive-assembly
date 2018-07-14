//use opencv and calibration data from MRPT checkerboard calibration (http://www.mrpt.org/download-mrpt/)
//to undistort an image with barrel distortion
import gab.opencv.*;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;

float [] dc; //store our camera distortion coefficients
float [] cm; //store our camera matrix
PImage img;
OpenCV cvCamImg; //we need to load opencv or things don't work...

void setup() {
  size(640, 480, P2D);
  background(0);
  stroke(255);
  frameRate(12);
  dc = loadDistortionMatrix("distortion_matrix.txt");
  cm = loadIntrinsicMatrix("intrinsic_matrix.txt");
  cvCamImg = new OpenCV(this, width, height);

  img = loadImage("img.jpg");
  img = undistorted(img, dc, cm); //undistort our image using our loaded coefficients
}

void draw() {
  image(img, 0, 0, width, height);
  //background(0);
}