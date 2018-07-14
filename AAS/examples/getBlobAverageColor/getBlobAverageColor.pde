/*
Opens an image containing blobs and saves the outlines of the blobs 
 to an arraylist made of arraylists of PVectors.  Draws the found outlines in red.
 Gets the average color of each blob and draws it as a rectangle in the upper left corner of the screen
 
 Install the blob detection library (http://www.v3ga.net/processing/BlobDetection/)
 and lee byron's mesh library (http://leebyron.com/mesh/)
 */
import greyshed.lib.*; //import the library
import megamu.mesh.*; //import mesh library
import blobDetection.*; //import blob detection library

PImage img; //this is the image we'll find blobs in
//make an arraylist to store all of our blobs.  Each blob is itself an arraylist of PVectors (the points on the blob)
ArrayList<ArrayList<PVector>> blobMatrix = new ArrayList<ArrayList<PVector>>();
ShapeMethods sm = new ShapeMethods(this);

void setup() {
  size(640, 480); //set our screen size
  img = loadImage("colorStraws.jpg"); //load the picture containing our blobs (or replace with webcam image)
}

void draw() {
  image(img, 0, 0); //draw our image containing blobs to the screen
  //find the blobs in the image using a threshold of .5, and save them to the arraylist "blobMatrix"
  blobMatrix = sm.getBlobs(img, .2); 
  noFill(); //don't fill our drawn shapes
  stroke(255, 0, 255); //set our stroke color to red
  strokeWeight(2); //set our lineweight to 2
  //draw each of our found blobs to the screen
  for (int i = 0; i< blobMatrix.size(); i++) {
    noFill();
    sm.drawPolylineShape(blobMatrix.get(i)); //draw the outline of the blob
    color myColor = sm.getBlobColor(blobMatrix.get(i), img); //get the color of the blob
    fill(myColor); //set the fill of the blob
    rect(30*i, 0, 30, 30); //draw a rectangle in the upper left hand of the screen representing the average color of the blob
  }
}