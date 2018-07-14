//methods for image undistortion
//RLJ 2017 <ryan@gshed.com>
PImage undistorted(PImage input, float [] distortion, float [] matrix) {
  //undistort an image given the distortion coefficients and camera matrix
  Mat undistorted = new Mat();
  Mat distortionCoefficients = new Mat(1, 5, CvType.CV_32FC1); //make a new matrix to hold distortion coefficients
  Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
  distortionCoefficients.put(0, 0, distortion);
  cameraMatrix.put(0, 0, matrix);
  OpenCV cvUndistortedImg = new OpenCV(this, input.width, input.height); //make an open cv version of this image
  cvUndistortedImg.loadImage(input);
  Mat imgInMat = cvUndistortedImg.getColor(); //get its color matrix
  Imgproc.undistort(imgInMat, undistorted, cameraMatrix, distortionCoefficients); //fill the undistorted matrix
  PImage theUndistortedImage = createImage(input.width,input.height,RGB);
  cvUndistortedImg.toPImage(undistorted, theUndistortedImage); //set the PImage to our undistorted data
  return theUndistortedImage;
}

float [] loadIntrinsicMatrix(String filename) {
  float [] coeff = {0, 0, 0, 0, 0};
  String[] lines = loadStrings(filename);
  String[] p0 = split(lines[0], ' ');
  String[] p1 = split(lines[1], ' ');
  String[] p2 = split(lines[2], ' ');
  if (p0.length == 3 && p1.length == 3 && p2.length == 3) {
    coeff = new float[]{
      float(p0[0]), float(p0[1]), float(p0[2]), 
      float(p1[0]), float(p1[1]), float(p1[2]), 
      float(p2[0]), float(p2[1]), float(p2[2]), 
    };
    println("Loaded Camera Intrinsic Matrix: ");
    println(coeff);
  } else {
    println("MATRIX FILE DOES NOT HAVE CORRECTLY FORMATTED INFORMATION.");
    println("Should be three rows of three numbers separated by a space");
  }
  return coeff;
}

float [] loadDistortionMatrix(String filename) {
  
float [] coeff = {0, 0, 0, 0, 0};
  String[] lines = loadStrings(filename);
  String[] pieces = split(lines[0], ' ');
  if (pieces.length == 5) {
    coeff = new float[]{float(pieces[0]), float(pieces[1]), float(pieces[2]), float(pieces[3]), float(pieces[4])};
    println("Loaded Camera Distortion Matrix: ");
    println(coeff);
  } else {
    println("DISTORTION MATRIX FILE DOES NOT HAVE CORRECTLY FORMATTED INFORMATION.");
    println("Should be five numbers separated by a space");
  }
  return coeff;
}