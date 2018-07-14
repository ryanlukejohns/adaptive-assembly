/*
Finds blobs in webcam image and saves the outlines of the blobs 
 to an arraylist made of arraylists of PVectors.  Draws the found outlines in red.
 
 Install the blob detection library (http://www.v3ga.net/processing/BlobDetection/)
 and lee byron's mesh library (http://leebyron.com/mesh/)
 */
import greyshed.lib.*; //import the library
import megamu.mesh.*; //import mesh library
import blobDetection.*; //import blob detection library
import processing.video.*;//processing video

Capture cam;//for the webcam
PImage img; //this is the image we'll find blobs in (from the webcam)
//make an arraylist to store all of our blobs.  Each blob is itself an arraylist of PVectors (the points on the blob)
ArrayList<ArrayList<PVector>> blobMatrix = new ArrayList<ArrayList<PVector>>();
ShapeMethods sm = new ShapeMethods(this);

void setup() {
  size(640, 480); //set our screen size
  //initialize webcam.  See "getting started capture" to find the settings to match your own webcam
  cam = new Capture(this, 640, 480, 30);
  // Comment the following line if you use Processing 1.5
  cam.start();
}

void draw() {

  if (cam.available() == true) {
    cam.read(); //update the webcam image
  }
  img = cam; //set the webcam image to our static PImage
  image(img, 0, 0); //draw our webcam image to the screen
  //find the blobs in the image using a threshold of .5, and save them to the arraylist "blobMatrix"
  blobMatrix = sm.getBlobs(img, .2); 
  noFill(); //don't fill our drawn shapes
  stroke(255, 0, 0); //set our stroke color to red
  strokeWeight(2); //set our lineweight to 2
  //draw each of our found blobs to the screen
  for (int i = 0; i< blobMatrix.size(); i++) {
    //uncomment the "if" statement if you want to restrict blobs below 3000 pixels in area, etc.
    //if(sm.get2DPolygonArea(blobMatrix.get(i)) > 3000){
    sm.drawPolylineShape(blobMatrix.get(i), true); //draw the shape and close it
    //}
  }
}