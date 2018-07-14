/*
Finds the convex hull of a number of blobs found in an image
 */
import greyshed.lib.*; //import the library
import megamu.mesh.*; //import mesh library
import blobDetection.*; //import blob detection library

PImage img; //this is the image we'll find blobs in
//make an arraylist to store all of our blobs.  Each blob is itself an arraylist of PVectors (the points on the blob)
ArrayList<ArrayList<PVector>> blobMatrix = new ArrayList<ArrayList<PVector>>();
ShapeMethods sm = new ShapeMethods(this); //create a new instance of our shapemethods class, which we can call with "sm"

void setup() {
  size(640, 480); //set our screen size
  img = loadImage("straws.jpg"); //load the picture containing our blobs (or replace with webcam image)
}

void draw() {
  image(img, 0, 0); //draw our image containing blobs to the screen
  //find the blobs in the image using a threshold of .5, and save them to the arraylist "blobMatrix"
  blobMatrix = sm.getBlobs(img, .5); 

  //for each found blob...
  for (int i = 0; i< blobMatrix.size(); i++) {
    noFill(); //don't fill our drawn shapes
    stroke(255, 0, 0); //set our stroke color to red
    strokeWeight(2); //set our lineweight to 2
    sm.drawPolylineShape(blobMatrix.get(i)); //draw the blob to the screen
    ArrayList<PVector> convexHull = sm.getConvexHull(blobMatrix.get(i)); //get the convex hull of the current blob
    stroke(0,255,0); //set our fill color to green
    sm.drawPolylineShape(convexHull); //draw our convex hull in green
    PVector hullCen = sm.getPolygonCentroid(convexHull); //get the centroid of the convex hull
    strokeWeight(10); //make our point big
    point(hullCen.x,hullCen.y); //draw the center point
 }
}