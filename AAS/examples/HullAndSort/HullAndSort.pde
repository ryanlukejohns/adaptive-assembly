/*
Generate random clusters of points, find their convex hull and then sort them based on area
 */
import greyshed.lib.*; //import the library
import megamu.mesh.*; //import mesh library

//make an arraylist to store our randomly generated point clusters
ArrayList<ArrayList<PVector>> pointMatrix = new ArrayList<ArrayList<PVector>>();
//make an arraylist to store the convex hull outlines of each generated point cluster
ArrayList<ArrayList<PVector>> hullMatrix = new ArrayList<ArrayList<PVector>>();
FloatList hullAreas = new FloatList(); //create a float list to store the area of each shape
ShapeMethods sm = new ShapeMethods(this); //create a new instance of our shapemethods class, which we can call with "sm"

int numClusters = 40; //how many point cluster objects should we generate?
int numPointsPerCluster = 6; //set the number of points in our cluster.  More would mean less variability in shapes...
float clusterWidth = 50; //set the possible width of our cluster
float clusterHeight = 100; //set the possible height of our cluster

void setup() {
  size(1200, 600); //set our screen size
  for (int i = 0; i< numClusters; i++) { //for each desired cluster
    ArrayList<PVector> thePoints = new ArrayList<PVector>(); //an arraylist to store the points in this cluster
    for (int j = 0; j< numPointsPerCluster; j++) { //for each desired point
      PVector randomPoint = new PVector(random(0, clusterWidth), random(0, clusterHeight)); //generate a random point
      thePoints.add(randomPoint); //add our random point to our list for this cluster
    }//end for each point
    pointMatrix.add(thePoints); //add our points to the matrix of points
    ArrayList<PVector> theCVHull = sm.getConvexHull(thePoints); //get the convex hull of our cluster
    hullMatrix.add(theCVHull); //add our convex hull to the parallel list that stores that.
  }//end for each cluster
}

void draw() {

  //for each cluster of points/blob...
  for (int i = 0; i< pointMatrix.size(); i++) {
    PVector hullCen = sm.getPolygonCentroid(hullMatrix.get(i)); //get the centroid of the current convex hull
    PVector pointToDrawAt = new PVector((1+i%20)*clusterWidth, (1+round(i/20))*clusterHeight);
    //move the path from the shape center point to an even grid so they don't draw all on top of one another
    ArrayList<PVector> translatedHull = sm.translatePath(hullMatrix.get(i), hullCen, pointToDrawAt); 
    noFill(); //don't fill our drawn shapes
    stroke(255, 0, 0); //set our stroke color to red
    strokeWeight(2); //set our lineweight to 2
    sm.drawPolylineShape(translatedHull, true); //draw the blob to the screen and close it
    strokeWeight(10); //make our point big
    point(pointToDrawAt.x, pointToDrawAt.y); //draw the center point of the shape, which should be on our grid

    float hullArea = sm.get2DPolygonArea(hullMatrix.get(i)); //get the area of our shape
    hullAreas.append(hullArea); //add the area to our list of areas so we can use it later for sorting
  } //end first for loop

  //make a new arraylist of our shapes that is the same shapes sorted by our reference floatlist of areas
  ArrayList<ArrayList<PVector>> sortedHulls = sm.sortByFloatList(hullMatrix, hullAreas); 
  //now, let's draw all the shapes again at the bottom half of the screen, but sorted this time by area
  for (int i = 0; i< sortedHulls.size(); i++) { //for each shape
    PVector hullCen = sm.getPolygonCentroid(sortedHulls.get(i)); //get the centroid of the current convex hull
    PVector pointToDrawAt = new PVector((1+i%20)*clusterWidth, (height/2)+(1+round(i/20))*clusterHeight); //draw it in a grid in the lower half of the screen
    //move the path from the shape center point to an even grid so they don't draw all on top of one another
    ArrayList<PVector> translatedHull = sm.translatePath(sortedHulls.get(i), hullCen, pointToDrawAt); 
    noFill(); //don't fill our drawn shapes
    stroke(0, 0, 255); //set our stroke color to blue
    strokeWeight(2); //set our lineweight to 2
    sm.drawPolylineShape(translatedHull, true); //draw the blob to the screen and close it
  }
}