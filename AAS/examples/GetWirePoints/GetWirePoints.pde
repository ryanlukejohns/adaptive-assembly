/*
Gets relevant points on long, skinny objects found using blob detection.
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
    PVector [] wirePts = sm.getWirePoints(blobMatrix.get(i)); //get the "important points" on this straw and save them to an array
    //now that we have our points, draw them to the screen
    strokeWeight(10); //draw our wire points as a big dot
    stroke(0, 255, 0); //draw the center as green
    point(wirePts[0].x, wirePts[0].y); //draw the found center pt
    stroke(255, 255, 0); //draw the edgepoints near the center as yellow
    point(wirePts[1].x, wirePts[1].y);//draw the found edge pt
    point(wirePts[2].x, wirePts[2].y);//draw the found edge pt
    stroke(0, 0, 255); //draw the end points as blue
    point(wirePts[3].x, wirePts[3].y);//draw the found end pt
    point(wirePts[4].x, wirePts[4].y);//draw the found end pt
    float wireRadius = max(PVector.dist(wirePts[0], wirePts[3]), PVector.dist(wirePts[0], wirePts[4])); //get the radius of the straw
  }
}