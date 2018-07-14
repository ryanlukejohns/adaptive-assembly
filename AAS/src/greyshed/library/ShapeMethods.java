package greyshed.lib;

import processing.core.*;
import processing.data.FloatList;
import blobDetection.*; //blob detection dependency (http://www.v3ga.net/processing/BlobDetection/)
import megamu.mesh.*; //Convex hull dependency on lee byron's mesh library (http://leebyron.com/mesh/)
//======IMPORT JAVA SHAPE LIBRARIES========
import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.*;
//========IMPORT JAVA COMPARISON/SORTING LIBRARIES==
import java.util.Comparator; //used for sorting things
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
//=======Utils=========
import java.util.ArrayList;
import java.util.HashMap;
/*
 * Some methods for working with shapes for the purpose of robotic assembly, etc.
 * 
 *
 * @example Hello 
 */

public class ShapeMethods {
	
	// myParent is a reference to the parent sketch
	PApplet myParent;

	int myVariable = 0;
	
	public final static String VERSION = "1.0.0";
	

	/**
	 * a Constructor, usually called in the setup() method in your sketch to
	 * initialize and start the Library.
	 * 
	 * @example Hello
	 * @param theParent
	 * 			Processing PApplet ("this")
	 */
	public ShapeMethods(PApplet theParent) {
		myParent = theParent;
		welcome();
	}
	
	private void welcome() {
		System.out.println("Assembling All Sorts 1.0.0 by Ryan Luke Johns http://www.greyshed.com");
	}
	
	public String sayHello() {
		return "hello library.";
	}
	/**
	 * return the version of the Library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}
	
	
	/**
	 * Get the blobs in an image as an arraylist of PVectors in the image pixel coordinate space.  
	 * Requires the <a href="http://www.v3ga.net/processing/BlobDetection/">blobdetection library</a>
	 * @param theImg
	 *          the image to search for blobs in
	 * @return a two dimensional ArrayList of PVectors describing the found blobs in the image pixel coordinate system
	 * @example SimpleBlobs
	 */
	public ArrayList<ArrayList<PVector>> getBlobs(PImage theImg) {
	  ArrayList<ArrayList<PVector>> blobMatrix = new ArrayList<ArrayList<PVector>>(); //create a matrix to store all of our blobs
	  BlobDetection theBlobDetection;  //create a new blob detection instance
	  theBlobDetection = new BlobDetection(theImg.width, theImg.height);  //set blob detection based on image properties
	  theBlobDetection.setPosDiscrimination(false);
	  theBlobDetection.setThreshold(0.6f); //THRESHOLD VALUE, CHANGE THIS IF NEEDED, OR DO IT ON YOUR PIMAGE BEFORE YOU PUT IT INTO THIS FUNCTION
	  theBlobDetection.computeBlobs(theImg.pixels); //find the blobs!
	  Blob b;
	  EdgeVertex eA, eB;

	  for (int n=0; n<theBlobDetection.getBlobNb(); n++)
	  { //for each found blob...
	    ArrayList<PVector> theCurrentBlob = new ArrayList<PVector>(); //make a new empty blob storage
	    b=theBlobDetection.getBlob(n); //get the blob
	    if (b!=null && b.getEdgeNb()>2)
	    { //if the blob is an actual blob, and it has more that two edges....
	      for (int m=0; m<b.getEdgeNb(); m++)
	      { //for each edge
	        eA = b.getEdgeVertexA(m);
	        eB = b.getEdgeVertexB(m);
	        if (eA !=null && eB !=null) {
	          PVector currentVertex = new PVector(eA.x*theImg.width, eA.y*theImg.height); //the current vertex of our blob
	          theCurrentBlob.add(currentVertex); //add the current vertex to our blob
	          if (m == b.getEdgeNb()-1) {//if we're on the very last point
	            PVector currentBVertex = new PVector(eB.x*theImg.width, eB.y*theImg.height); //the current vertex of our blob
	            theCurrentBlob.add(currentBVertex); //add the current vertex to our blob//also add the "b" vertex to close the curve
	          } //end if on last vertex
	        }//end if eA and eB not null
	      }//end for edges in blob
	    }//end if not null and edges not fewer than two
	    if (theCurrentBlob.size() > 2) { //if the blob has more than two vertices
	      blobMatrix.add(theCurrentBlob);
	    }
	  }//end for each blob in the blob detection
	  return(blobMatrix); //return the matrix of blobs
	}//end getBlobs Function

	/**
	 * Get the blobs in an image as an arraylist of PVectors in the image pixel coordinate space.  Requires the <a href="http://www.v3ga.net/processing/BlobDetection/">blobdetection library</a>
	 * @param theImg
	 *          the image to search for blobs in
	 * @param theThreshold
	 * 			the image threshold for blob detection
	 * @return a two dimensional ArrayList of PVectors describing the found blobs in the image pixel coordinate system
	 * @example SimpleBlobs
	 */
	public ArrayList<ArrayList<PVector>> getBlobs(PImage theImg, float theThreshold) {
		  ArrayList<ArrayList<PVector>> blobMatrix = new ArrayList<ArrayList<PVector>>(); //create a matrix to store all of our blobs
		  BlobDetection theBlobDetection;  //create a new blob detection instance
		  theBlobDetection = new BlobDetection(theImg.width, theImg.height);  //set blob detection based on image properties
		  theBlobDetection.setPosDiscrimination(false);
		  theBlobDetection.setThreshold(theThreshold); //THRESHOLD VALUE, CHANGE THIS IF NEEDED, OR DO IT ON YOUR PIMAGE BEFORE YOU PUT IT INTO THIS FUNCTION
		  theBlobDetection.computeBlobs(theImg.pixels); //find the blobs!
		  Blob b;
		  EdgeVertex eA, eB;

		  for (int n=0; n<theBlobDetection.getBlobNb(); n++)
		  { //for each found blob...
		    ArrayList<PVector> theCurrentBlob = new ArrayList<PVector>(); //make a new empty blob storage
		    b=theBlobDetection.getBlob(n); //get the blob
		    if (b!=null && b.getEdgeNb()>2)
		    { //if the blob is an actual blob, and it has more that two edges....
		      for (int m=0; m<b.getEdgeNb(); m++)
		      { //for each edge
		        eA = b.getEdgeVertexA(m);
		        eB = b.getEdgeVertexB(m);
		        if (eA !=null && eB !=null) {
		          PVector currentVertex = new PVector(eA.x*theImg.width, eA.y*theImg.height); //the current vertex of our blob
		          theCurrentBlob.add(currentVertex); //add the current vertex to our blob
		          if (m == b.getEdgeNb()-1) {//if we're on the very last point
		            PVector currentBVertex = new PVector(eB.x*theImg.width, eB.y*theImg.height); //the current vertex of our blob
		            theCurrentBlob.add(currentBVertex); //add the current vertex to our blob//also add the "b" vertex to close the curve
		          } //end if on last vertex
		        }//end if eA and eB not null
		      }//end for edges in blob
		    }//end if not null and edges not fewer than two
		    if (theCurrentBlob.size() > 2) { //if the blob has more than two vertices
		      blobMatrix.add(theCurrentBlob);
		    }
		  }//end for each blob in the blob detection
		  return(blobMatrix); //return the matrix of blobs
		}//end getBlobs Function
	
	
	/**
	 * Get the measured length (sum of distance between vertices) of a polyline defined as an arraylist of PVectors
	 * @param polylineShape
	 *          polyline to get the length of
	 * @return the polyline length
	 */
	public float getPolylineShapeLength(ArrayList<PVector> polylineShape) {
		  float theLength = 0; //the number to store our total length
		  if (polylineShape.size() >= 2) {
		    for (int i = 0; i<polylineShape.size()-1; i++) { //for each vertex except the last, get the distance to the next vertex
		      theLength += polylineShape.get(i).dist(polylineShape.get(i+1));
		    } //end for each vertex in the polyline
		  } //if our curve only has one point, this should return zero
		  return theLength;
		}
	
	/**
	 * Given a polyline (arraylist of PVectors), return the two vertices that are farthest from one another as the crow flies (2D or 3D)
	 * @param contourArray
	 *          polyline to get the far points of
	 * @return a list of two PVectors
	 */
	public PVector [] getFarPoints(ArrayList<PVector> contourArray) {
		  float maxDist = -1000; //set the max dist to an impossibly small number to start
		  PVector [] endPoints = new PVector[2];  //make an array to hold our endpoints
		  for (int i = 0; i<contourArray.size(); i++) { //for each point in our polyine
		    //loop through each vertex
		    PVector currentPt = contourArray.get(i); //get our current pt
		    for (int j = 0; j<contourArray.size(); j++) { //inside of the loop through all points, for each point, loop through all the others
		      float theDist = currentPt.dist(contourArray.get(j)); //get the distance from our current point to this other point
		      if (theDist > maxDist) { //if this is a new world record distance, store these two points
		        endPoints[0] = currentPt;
		        endPoints[1] = contourArray.get(j); //set the two points as our world record points
		        maxDist = theDist; //set a new world record
		      }
		    }//end for j loop
		  }//end for i loop
		  return(endPoints);
		}

	/**
	 *given an arraylist of vertices defining the perimeter of a 2D wire/straw-like (bent elongated rectangle) shape,
	 *return a list of points which contains the following items at each index:
	 *[0] the "center" or the midpoint between [1]+[2],
	 *[1]+[2] the two vertices which are closest to one another as the crow flies, but equally spaced from one another along the perimeter of shape
	 *[3]+[4] the approximate end points, or rather, the points that are farthes from one another as the crow flies, but equally spaced from one another along the perimeter of shape
	 *<p>
	 *note that because this function is only looking at vertices, and not at the spaces between vertices, 
	 *it won't be very accurate with polylines that are not very dense (i.e. with long line segments)
	 * @param contourArray
	 *          a 2D polyline as an arrayList of PVectors
	 * @return a list of five PVectors where [0] is the "center", or the midpoint between [1]+[2] which are the points near the center on the perimeter, and [3]+[4] which are the "endpoints"
	 * @example GetWirePoints
	 */
	public PVector [] getWirePoints(ArrayList<PVector> contourArray) {
		  PVector [] strawPoints = new PVector[5]; //make an empty list to store our answer
		  int halfLength = (contourArray.size())/2; //this is how many vertices are in half of our outline
		  float minDist = (float)10000000.0;  //set a really big number for our minimum distance
		  int bestIndex = 0; //store the location of the edge vertex that's closest to our centerpoint

		  //---------FIND THE CENTER POINT OF THE STRAW
		  for (int i = 0; i<contourArray.size(); i++) { //for each point in our polyine
		    //loop through each vertex
		    PVector currentPt = contourArray.get(i); //get our current pt
		    PVector oppositePt = contourArray.get((i+halfLength)%contourArray.size()); //get the point opposite us
		    float currentDist = PVector.dist(currentPt, oppositePt); //distance between the two points
		    if (currentDist<minDist) {
		      //if the distance between the two points is the smallest so far, save it as a new record
		      minDist = currentDist; //this is the smallest distance so far
		      bestIndex = i; //this is the best point so far
		    }
		  }
		  PVector bestPt = contourArray.get(bestIndex); //now that we've found the best index, this is our best point
		  PVector oppositeBestPt = contourArray.get((bestIndex+halfLength)%contourArray.size()); //this is the point opposite our best point
		  PVector bestMidPt = PVector.lerp(bestPt, oppositeBestPt, (float).5); //this is the point halfway between our two points
		  strawPoints[0] = bestMidPt; //set the first point in our result to the center pt
		  strawPoints[1] = bestPt;//set the second point in our result to the edge point near the center
		  strawPoints[2] = oppositeBestPt;//set the third point in our result to the other edge point near the center

		  //----------NOW FIND THE END POINTS OF THE STRAW
		  float maxDist = -10; //set a very small number to the biggest distance between the center and endpoint
		  for (int i = 0; i<contourArray.size(); i++) { //for each point in our polyine
		    //if we have the longest length between this point and the center, it's probably an endpoint.
		    //we also include the length from our opposite vertex to the center to get a more accurate total length
		    //loop through each vertex
		    PVector currentEndPt = contourArray.get(i); //get our current pt
		    PVector oppositeEndPt = contourArray.get((i+halfLength)%contourArray.size()); //get the point opposite us
		    float currentDistToCenter = PVector.dist(currentEndPt, bestMidPt); //distance between the two points
		    float oppositeDistToCenter = PVector.dist(oppositeEndPt, bestMidPt); //distance between the two points
		    float totalStrawLength = currentDistToCenter + oppositeDistToCenter; //make the total length
		    if (totalStrawLength>maxDist) {
		      //if the distance between the two points is the largest so far, save it as a new record
		      maxDist = totalStrawLength; //this is the largest distance so far
		      bestIndex = i; //this is the best endpoint so far
		      strawPoints[3] = currentEndPt; //store this best result so far in our array
		      strawPoints[4] = oppositeEndPt; //store this best result so far in our array
		      //this way, when the loop is done, the best result will be in our array
		    } //end if length is new world record
		  } //end for each vertex

		  return strawPoints; //return the array of strawpoints, with indices matching the description above
		}
	
	
	/**
	 * Return the convex hull (as an arraylist of PVectors) of a polyline or point cloud defined as an arraylist of PVectors
	 * This function works with 2D PVectors, in the XY plane and currently would strip the z data if given a 3D vector
	 * Requires <a href="http://leebyron.com/mesh/">Lee Byron's mesh library</a>
	 * @param theClusterToHull
	 *          polyline to get the 2D convex hull of
	 * @return the convex hull as a clockwise-ordered arraylist of PVectors
	 * @example GetConvexHull
	 */
	public ArrayList<PVector> getConvexHull(ArrayList<PVector> theClusterToHull) {
		  //this version works in the xy plane
		  float[][] hullCloudPoints = new float[theClusterToHull.size()][2];
		  for (int i = 0; i < theClusterToHull.size(); i++) {
		    hullCloudPoints[i][0] = theClusterToHull.get(i).x; // first point, y
		    hullCloudPoints[i][1] = theClusterToHull.get(i).y; // first point, z
		  }
		  Hull myHull = new Hull(hullCloudPoints);  
		  int[] extrema = myHull.getExtrema();
		  ArrayList<PVector> clockwiseHullPts = new ArrayList<PVector>();
		  for (int i = 0; i < extrema.length; i++) {
		    int extremaIndex = extrema[i];
		    PVector currentHullExtreme = new PVector(theClusterToHull.get(extremaIndex).x, theClusterToHull.get(extremaIndex).y);
		    clockwiseHullPts.add(currentHullExtreme);
		  }
		  return clockwiseHullPts;
		}
	
	/**
	 * Returns the width and height of the world <a href="https://en.wikipedia.org/wiki/Bounding_volume">Axis-Aligned bounding box</a>
	 * @param vtc
	 *          polyline to get the bounding dimensions of
	 * @return a list of two floats representing the width and height of the bounding rectangle
	 */
	public float [] getDim(ArrayList<PVector> vtc){
		  //get world-aligned bounding rectangle dimensions
		  float minY = 10000000;
		  float maxY = -10000000;
		  float minX = 10000000;
		  float maxX = -10000000;
		  for(int i = 0; i<vtc.size(); i++){
		    PVector thePt = vtc.get(i);
		    if(thePt.x < minX){
		      minX = thePt.x;
		    }
		    if(thePt.x >maxX){
		      maxX = thePt.x;
		    }
		     if(thePt.y < minY){
		      minY = thePt.y;
		    }
		    if(thePt.y >maxY){
		      maxY = thePt.y;
		    }
		  }
		  float xDim = maxX - minX;
		  float yDim = maxY - minY;
		  float [] myDims = {xDim,yDim};
		  return myDims; 
		}
	
	/**
	 * Returns four points from a list of points:  the point with the smallest X, the largest X, the smallest Y, and the largest Y, in that order
	 * @param vtc
	 *          polyline to get the far points of
	 * @return a list of four PVEctors representing the points with the largest and smallest x and y values
	 */
	public PVector  [] getMinMax2D(ArrayList<PVector> vtc){
		  //get world-aligned bounding rectangle dimensions
		  float minY = 10000000;
		  float maxY = -10000000;
		  float minX = 10000000;
		  float maxX = -10000000;
		  PVector minXPV = vtc.get(0);
		  PVector maxXPV = vtc.get(0);
		  PVector minYPV = vtc.get(0);
		  PVector maxYPV = vtc.get(0);
		  for(int i = 0; i<vtc.size(); i++){
		    PVector thePt = vtc.get(i);
		    if(thePt.x < minX){
		      minX = thePt.x;
		      minXPV = thePt;
		    }
		    if(thePt.x >maxX){
		      maxX = thePt.x;
		      maxXPV = thePt;
		    }
		     if(thePt.y < minY){
		      minY = thePt.y;
		      minYPV = thePt;
		    }
		    if(thePt.y >maxY){
		      maxY = thePt.y;
		      maxYPV = thePt;
		    }
		  }
		  
		  PVector [] myDims = {minXPV,maxXPV,minYPV,maxYPV};
		  return myDims; 
		}
	
	/**
	 * Returns four indices of points from a list of points:  the point with the smallest X, the largest X, the smallest Y, and the largest Y, in that order
	 * @param vtc
	 *          polyline to get the far points of
	 * @return a list of four ints representing the index of points with the largest and smallest x and y values
	 */
	public int  [] getExtremeIndex(ArrayList<PVector> vtc){
		  //get world-aligned bounding rectangle dimensions
		  float minY = 10000000;
		  float maxY = -10000000;
		  float minX = 10000000;
		  float maxX = -10000000;
		 int minXInt = 0;
		 int maxXInt = 0;
		 int minYInt = 0;
		 int maxYInt = 0;
		  for(int i = 0; i<vtc.size(); i++){
		    PVector thePt = vtc.get(i);
		    if(thePt.x < minX){
		      minX = thePt.x;
		      minXInt = i;
		    }
		    if(thePt.x >maxX){
		      maxX = thePt.x;
		      maxXInt = i;
		    }
		     if(thePt.y < minY){
		      minY = thePt.y;
		      minYInt = i;
		    }
		    if(thePt.y >maxY){
		      maxY = thePt.y;
		      maxYInt = i;
		    }
		  }
		  
		  int [] myDims = {minXInt,maxXInt,minYInt,maxYInt};
		  return myDims; 
		}
	
	
	
	/**
	 * Splits a polyline into two polylines based on a given start or end index along the curve.  
	 * Two points are provided, and the direction of each returned polyline will move in the direction from the first index to the second index
	 * 
	 * @param vtc
	 *          polyline to split (typically closed)
	* @param start
	 *          index to split at
	 * @param stop
	 *          index to split at
	 * @return a an arraylist<arrayList<PVector>> with two elements, each being a polyline
	 */	
	
	public ArrayList<ArrayList<PVector>> splitCurveTwoPts(ArrayList<PVector> vtc, int start, int stop) {
		  ArrayList<PVector> list1 = new ArrayList<PVector>();
		  ArrayList<PVector> list2 = new ArrayList<PVector>();
		  if (start>stop) {
		    list1 = new ArrayList(vtc.subList(stop, start)); //get the part of the curve that doesn't hit the seam
		    ArrayList<PVector> list2A =  new ArrayList(vtc.subList(start-1, vtc.size())); //get the part of the curve from the start point to the endpoint
		    ArrayList<PVector> list2B =  new ArrayList(vtc.subList(0, stop+1)); //get the part of the curve from the beginning of our curve the specified end point index
		    Collections.reverse(list1);
		    list2 = new ArrayList<PVector>(list2A);//initialize an array with the first part of the list
		    list2.addAll(list2B); //add the second part of the list
		  } else { //start < start
		    list1 = new ArrayList(vtc.subList(start, stop));
		    
		    ArrayList<PVector> list2A =  new ArrayList(vtc.subList(stop-1, vtc.size())); //get the part of the curve from the start point to the endpoint
		    ArrayList<PVector> list2B =  new ArrayList(vtc.subList(0, start+1)); //get the part of the curve from the beginning of our curve the specified end point index
		    
		    list2 = new ArrayList<PVector>(list2A);//initialize an array with the first part of the list
		    list2.addAll(list2B); //add the second part of the list
		    Collections.reverse(list2);
		  }
		  //
		  ArrayList<ArrayList<PVector>> result = new  ArrayList<ArrayList<PVector>>();
		  result.add(list1);
		  result.add(list2);
		  return result;
		}
	
	/**
	 * Given a convex hull, return the minimum, object oriented bounding rectangle. Found using rotating calipers.
	 * @param theConvexHull
	 *          covnex hull to get minimum bounding rectangle of
	 * @return a list of four PVectors representing the four corner points of the bounding rectangle
	 * @deprecated
	 */
	public PVector [] getMinimumBoundingRectangle(ArrayList<PVector> theConvexHull){
		  //get world-aligned bounding rectangle dimensions
		RotatingCalipers rc = new RotatingCalipers();
	    PVector [] bb = rc.getMinimumBoundingRectangle(theConvexHull);
		return bb; 
		}
	
	/**
	 * Given a convex hull, return the minimum, object oriented bounding rectangle. Probably not the most elegant solution.
	 * @param theConvexHull
	 *          covnex hull to get minimum bounding rectangle of
	 * @return a list of four PVectors representing the four corner points of the bounding rectangle
	 */	
	public PVector [] getOOBB(ArrayList<PVector> convexHull) {
		  ArrayList<PVector> cvHull = new ArrayList<PVector>(convexHull);
		  PVector s = cvHull.get(0);
		  PVector e = cvHull.get(cvHull.size()-1);
		  if (s.x != e.x && s.y != e.y) {
		    //if start and end point are not the same, add a duplicate start point
		    cvHull.add(s);
		  }
		  float minArea = Float.MAX_VALUE;
		  PVector bestDir = new PVector();
		  float bestAngle = 0;
		  PVector bestPt = new PVector();
		  for (int i = 0; i<cvHull.size()-1; i++) {
		    PVector dir = PVector.sub(cvHull.get(i), cvHull.get(i+1));
		    float angle = PVector.angleBetween(new PVector(1, 0, 0), dir);
		    ArrayList<PVector> rotatedHull = rotatePath(cvHull, cvHull.get(i), angle);
		    float [] dimensions = getDim(rotatedHull);
		    float area = dimensions[0]*dimensions[1];
		    if (area <minArea) {
		      minArea = area;
		      bestDir = dir;
		      bestAngle = angle;
		      bestPt = cvHull.get(i);
		    }
		  }
		  
		  ArrayList<PVector> rotatedHull = rotatePath(cvHull, bestPt, bestAngle);
		  PVector [] corners = getCorners(rotatedHull); //get the corners of our rotated shape
		  for(int i = 0; i<4; i++){
		    corners[i] = rotatePoint(corners[i],bestPt,bestAngle*-1); //rotate our corners back to our original shape orientation
		  }
		  return corners;
		}
	
	/**
	 * Returns the four corner points of the Axis-Aligned (world) bounding box given an arraylist of points
	 * @param vtc
	 *          the points to find the boundaries of
	 * @return a list of four PVectors representing the four corner points of the bounding rectangle
	 */		
	public PVector [] getCorners(ArrayList<PVector> vtc) {

		  //get world-aligned bounding rectangle dimensions
		  float minY = Float.MAX_VALUE;
		  float maxY = Float.MIN_VALUE;
		  float minX = Float.MAX_VALUE;
		  float maxX = Float.MIN_VALUE;
		  for (int i = 0; i<vtc.size(); i++) {
		    PVector thePt = vtc.get(i);
		    if (thePt.x < minX) {
		      minX = thePt.x;
		    }
		    if (thePt.x >maxX) {
		      maxX = thePt.x;
		    }
		    if (thePt.y < minY) {
		      minY = thePt.y;
		    }
		    if (thePt.y >maxY) {
		      maxY = thePt.y;
		    }
		  }
		  PVector TL = new PVector(minX, maxY);
		  PVector TR = new PVector(maxX, maxY);
		  PVector BR = new PVector(maxX, minY);
		  PVector BL = new PVector(minX, minY);
		  PVector [] corners = {TL, TR, BR, BL};
		  return corners;
		}

	
	/**
	 * Given a point cloud in 2D or 3D made of PVectors, cluster the points into groupings based on a given radius
	 * Points that are within a minimum distance of their neighbors are grouped together with them.  
	 * Groups with a count below a give size are not returned.  
	 * As the unculstered beginning points are deleted as the function runs, input a copy of your unclustered point arraylist.
	 * @param unClusteredPoints
	 *          an arraylist of PVectors representing a 2D or 3D point cloud
	 * @param theClusterMatrix
	 * 			an empty (at the beginning) ArrayList of Arraylists of PVectors, containing the clustered points
	 * @param maxClstrDist
	 * 			the maximum distance between points where they should be grouped together and considered part of the same cluster
	 * @param minClstrSize
	 * 			the fewest number of points required to be considered a cluster
	 * @return a matrix of clustered points, grouped with their neighbors
	 */
	public ArrayList<ArrayList<PVector>> recursiveClustering(ArrayList<PVector> unClusteredPoints, ArrayList<ArrayList<PVector>> theClusterMatrix, float maxClstrDist, float minClstrSize) {
		  //given a list of points, find clusters within it with a minimum distance between points and a minimum number of points per cluster
		  if (unClusteredPoints.size() < 1) {
		    return theClusterMatrix;
		  } else {
		    ArrayList<PVector> currentPtCluster = new ArrayList<PVector> (); //create an arraylist to hold our current cluster
		    for (int i = unClusteredPoints.size()-1; i >= 2; i--) {
		      if (unClusteredPoints.get(0).dist(unClusteredPoints.get(i))<=maxClstrDist) {
		        currentPtCluster.add(unClusteredPoints.get(i));
		        unClusteredPoints.remove(i);
		      }
		    }

		    currentPtCluster.add(unClusteredPoints.get(0));
		    unClusteredPoints.remove(0);

		    for (int i = 0; i < currentPtCluster.size(); i++) {
		      for (int j = unClusteredPoints.size()-1; j >= 1; j--) {
		        if (unClusteredPoints.get(j).dist(currentPtCluster.get(i))<=maxClstrDist) {
		          currentPtCluster.add(unClusteredPoints.get(j));
		          unClusteredPoints.remove(j);
		        }
		      }
		    }
		    if (currentPtCluster.size() >= minClstrSize) {
		      theClusterMatrix.add(currentPtCluster);
		    }
		    recursiveClustering(unClusteredPoints, theClusterMatrix, maxClstrDist, minClstrSize);
		  }
		  return theClusterMatrix;
		}
	

	/**
	 * Moves a point using a reference point and a target point. (2D or 3D)
	 * @param pt1
	 *          the point to move
	 * @param start
	 * 			the reference point for moving from
	 * @param end
	 * 			the reference point for moving to
	 * @return the point (p1) moved along the vector defined by end-start
	 */
	public PVector translatePoint(PVector pt1, PVector start, PVector end) {
	  //move a point using two reference points
	  PVector pt = pt1.copy();
	  PVector translationVector = PVector.sub(end, start);  
	  PVector translatedPt = pt.copy();
	  pt.add(translationVector);
	  return pt;
	}
	
	/**
	 * Rotates a point about a center point by a specified degree angle in radians.  2D rotation in the xy plane.
	 * @param pt
	 *          the point to rotate
	 * @param center
	 * 			the point to rotate about
	 * @param radianAngle
	 * 			the amount to rotate (radians)
	 * @return the rotated point
	 * @example Hello
	 */
	public PVector rotatePoint(PVector pt, PVector center, float radianAngle) {
	  PVector point2 = pt.copy();//the point to rotate
	  float newX = center.x + (point2.x-center.x)*PApplet.cos(radianAngle) - (point2.y-center.y)*PApplet.sin(radianAngle);
	  float newY = center.y + (point2.x-center.x)*PApplet.sin(radianAngle) + (point2.y-center.y)*PApplet.cos(radianAngle);
	  PVector newVec = new PVector(newX, newY);
	  return newVec;
	}
	
	/**
	 * Moves a path (or any arraylist of points) from one point to another (2D or 3D)
	 * @param vtc
	 *          the points to move
	 * @param start
	 * 			the reference point for moving from
	 * @param end
	 * 			the reference point for moving to
	 * @return the arraylist of points moved along the vector defined by end-start
	 */
	public ArrayList<PVector> translatePath(ArrayList<PVector> vtc, PVector start, PVector end) {
	  //move a list of points (vtc) from reference point "start" to reference point "end".
	  ArrayList<PVector> translatedVec = new ArrayList<PVector>();
	  PVector translationVector = PVector.sub(end, start);
	  for (int i = 0; i< vtc.size(); i++) {
	    PVector vecToMove = vtc.get(i).copy();
	    vecToMove.add(translationVector);
	    translatedVec.add(vecToMove);
	  }
	  return(translatedVec);
	}

	/**
	 * Rotates a path (or arraylist of points) about a center point by a specified degree angle in radians.  2D rotation in the xy plane.
	 * @param vtc
	 *          the points to rotate
	 * @param center
	 * 			the point to rotate about
	 * @param radianAngle
	 * 			the amount to rotate (radians)
	 * @return the rotated points
	 */
	public ArrayList<PVector> rotatePath(ArrayList<PVector> vtc, PVector center, float radianAngle) {
	  ArrayList<PVector> rotatedVec = new ArrayList<PVector>();
	  for (int i = 0; i< vtc.size(); i++) {
	    PVector point2 = vtc.get(i).copy();//the point to rotate
	    float newX = center.x + (point2.x-center.x)*PApplet.cos(radianAngle) - (point2.y-center.y)*PApplet.sin(radianAngle);
	    float newY = center.y + (point2.x-center.x)*PApplet.sin(radianAngle) + (point2.y-center.y)*PApplet.cos(radianAngle);
	    PVector newVec = new PVector(newX, newY);
	    rotatedVec.add(newVec);
	  }
	  return(rotatedVec);
	}
	
	
	/**
	 * Get average distance between a blob (or point cluster) and a path or point list.  
	 * For each vertex in the first list, it finds the closest point in the second list.  
	 * The function returns the average of those distances for each vertex.  
	 * Good for comparing two shapes or point clouds to see if they are similar, or to see if a blob is placed well on a goal curve. 
	 
	 * <p>
	 * As this function is pretty brute force, the skip variable allows you to use every nth vertex in the first list for comparison
	 * @param blobVtc
	 *          the first set of points to compare
	 * @param pathVtc
	 * 			the second set of points to compare
	 * @param skip
	 * 			an integer for skipping points in the first set to speed up computing.  Minimum value of 1 includes all points.
	 * @return the average distance
	 */
	public float getAverageDist(ArrayList<PVector> blobVtc, ArrayList<PVector> pathVtc, int skip) {
	  //given two paths, find the average distance from the first to the second, skipping skip number of vertices on the first between checks
	  if(skip < 1){
		skip = 1;
	  }
	  
	  float sum = 0;
	  float sumCounter = 0; //how many items have we added
	  for (int i = 0; i<blobVtc.size(); i++) {
	    if (i%skip == 0) { //if we're on a vertex to include
	      PVector currentVtx = blobVtc.get(i);
	      float minDist = 999999999; //set a really big number as our minDist
	      for (int j = 0; j<pathVtc.size(); j++) {
	        float theDist = PVector.dist(pathVtc.get(j), currentVtx);
	        if (theDist<minDist) {
	          minDist = theDist;
	        }
	      }
	      sum += minDist;
	      sumCounter +=1;
	    }
	  }
	  float theAverage = sum/sumCounter;
	  return theAverage;
	}
	
	/**
	 * Get average distance between a blob (or point cluster) and a path or point list.  
	 * For each vertex in the first list, it finds the closest point in the second list.  
	 * The function returns the average of those distances for each vertex. 
	 * Good for comparing two shapes or point clouds to see if they are similar, or to see if a blob is placed well on a goal curve. 
	 * <p>
	 * @param blobVtc
	 *          the first set of points to compare
	 * @param pathVtc
	 * 			the second set of points to compare
	 * @return the average distance
	 */
	public float getAverageDist(ArrayList<PVector> blobVtc, ArrayList<PVector> pathVtc) {
	 int skip = 1; 
	  float sum = 0;
	  float sumCounter = 0; //how many items have we added
	  for (int i = 0; i<blobVtc.size(); i++) {
	    if (i%skip == 0) { //if we're on a vertex to include
	      PVector currentVtx = blobVtc.get(i);
	      float minDist = 999999999; //set a really big number as our minDist
	      for (int j = 0; j<pathVtc.size(); j++) {
	        float theDist = PVector.dist(pathVtc.get(j), currentVtx);
	        if (theDist<minDist) {
	          minDist = theDist;
	        }
	      }
	      sum += minDist;
	      sumCounter +=1;
	    }
	  }
	  float theAverage = sum/sumCounter;
	  return theAverage;
	}

	
	/**
	 * Get The Centroid of a 2D (X-Y Plane) Vertex-Defined Polygon.  
	 * From <a href="http://stackoverflow.com/questions/2792443/finding-the-centroid-of-a-polygon">Stack Overflow</a>
	 * <p>
	 * @param vertices
	 *          the vertices defining the outline of the shape to find the centroid of
	 * @return the centroid as a PVector
	 * @example GetConvexHull
	 */
	public PVector getPolygonCentroid(ArrayList<PVector> vertices) {
	  //http://stackoverflow.com/questions/2792443/finding-the-centroid-of-a-polygon
	  PVector centroid = new PVector(0, 0);
	  double signedArea = 0.0;
	  double x0 = 0.0; // Current vertex X
	  double y0 = 0.0; // Current vertex Y
	  double x1 = 0.0; // Next vertex X
	  double y1 = 0.0; // Next vertex Y
	  double a = 0.0;  // Partial signed area

	  // For all vertices except last
	  int i=0;
	  for (i=0; i<vertices.size()-1; i++) {
	    x0 = vertices.get(i).x;
	    y0 = vertices.get(i).y;
	    x1 = vertices.get(i+1).x;
	    y1 = vertices.get(i+1).y;
	    a = x0*y1 - x1*y0;
	    signedArea += a;
	    centroid.x += (x0 + x1)*a;
	    centroid.y += (y0 + y1)*a;
	  }

	  // Do last vertex separately to avoid performing an expensive
	  // modulus operation in each iteration.
	  x0 = vertices.get(i).x;
	  y0 = vertices.get(i).y;
	  x1 = vertices.get(0).x;
	  y1 = vertices.get(0).y;
	  a = x0*y1 - x1*y0;
	  signedArea += a;
	  centroid.x += (x0 + x1)*a;
	  centroid.y += (y0 + y1)*a;

	  signedArea *= 0.5;
	  centroid.x /= (6.0*signedArea);
	  centroid.y /= (6.0*signedArea);
	  return centroid;
	}
	
	
	/**
	 * Get The area of a 2D (X-Y Plane) vertex-defined polygon.
	 * Assumes closed polygon does not have duplicated start/endpoint...but will probably work in either case
	 * <a href="http://stackoverflow.com/questions/2792443/finding-the-centroid-of-a-polygon">Stack Overflow</a>
	 * <p>
	 * @param vertices
	 *          the vertices defining the outline of the 2D shape to find the area of
	 * @return the 2D area
	 */
	public float get2DPolygonArea(ArrayList<PVector> vertices) {
	  //http://stackoverflow.com/questions/2792443/finding-the-centroid-of-a-polygon
	  //assumes closed polygon does not have duplicated start/endpoint...but will probably work in either case
	  double signedArea = 0.0;
	  double x0 = 0.0; // Current vertex X
	  double y0 = 0.0; // Current vertex Y
	  double x1 = 0.0; // Next vertex X
	  double y1 = 0.0; // Next vertex Y
	  double a = 0.0;  // Partial signed area
	  // For all vertices except last
	  int i=0;
	  for (i=0; i<vertices.size()-1; i++) {
	    x0 = vertices.get(i).x;
	    y0 = vertices.get(i).y;
	    x1 = vertices.get(i+1).x;
	    y1 = vertices.get(i+1).y;
	    a = x0*y1 - x1*y0;
	    signedArea += a;
	  }
	  // Do last vertex separately to avoid performing an expensive
	  // modulus operation in each iteration.
	  x0 = vertices.get(i).x;
	  y0 = vertices.get(i).y;
	  x1 = vertices.get(0).x;
	  y1 = vertices.get(0).y;
	  a = x0*y1 - x1*y0;
	  signedArea += a;

	  signedArea *= 0.5;

	  return PApplet.abs((float)signedArea); //our convex hull is ordered clockwise, so we will get a negative area if we don't iterate backwards
	}
	
	
	/**
	 * Given a 2D (X-Y plane) arraylist of points representing a polyline shape, return a java generalpath version of that shape
	 * @param pathVtc
	 *          the vertices defining the outline of the 2D shape
	 * @return the converted generalpath
	 */
	public GeneralPath gpFromArrayList(ArrayList<PVector> pathVtc) {
	  GeneralPath blobPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD, pathVtc.size());    
	  for (int i = 0; i< pathVtc.size(); i++) {
	    PVector currentPt = pathVtc.get(i).copy();
	    if (i == 0) {
	      blobPath.moveTo(currentPt.x, currentPt.y); //move to the beginning of the curve
	    } else {
	      blobPath.lineTo(currentPt.x, currentPt.y);
	    }
	  }
	  PVector startPt = pathVtc.get(0);
	  PVector endPt = pathVtc.get(pathVtc.size()-1);
	  if (startPt == endPt) { //if the curve is closed
	    blobPath.closePath();
	  }
	  return blobPath; //return the shape
	}
	
	/**
	 *Perform boolean intersection between two "generalpath" shapes.
	 *Using java's area method, check for intersection between two paths.
	 *Note that the path needs to be closed, and built using the correct winding rule.
	 *That is most likely the problem if this doesn't work...
	 * @param a
	 *          the first shape to check for intersection
	 * @param b
	 * 			the second shape to check for intersection
	 * @return true for intersection, false for no intersection
	 */
	public boolean pathIntersection(GeneralPath a, GeneralPath b) {
	  Area areaA = new Area(a);
	  Area areaB = new Area(b);
	  areaA.intersect(areaB);
	  return !areaA.isEmpty();
	}
	
	
	/**
	 *Check for intersection between two 2D shapes stored as arraylists of PVectors by converting them to generalpath shapes first.
	 * @param a
	 *          the first shape to check for intersection
	 * @param b
	 * 			the second shape to check for intersection
	 * @return true for intersection, false for no intersection
	 * @see #pathIntersection(GeneralPath, GeneralPath)
	 */
	public boolean arrayListIntersection(ArrayList<PVector> a, ArrayList<PVector>b) {
	  GeneralPath gpA = gpFromArrayList(a); //convert to general path
	  GeneralPath gpB = gpFromArrayList(b); //convert to general path
	  return pathIntersection(gpA, gpB); //return path intersection
	}
		
	/**
	 *given an arraylist of vertices defining a shape, shift each vertex away from the center point by the amount specified in "dist"
	 *this is not a true, robust "offset", as different angles will produce different edge offsets and potential intersections
	 *But, if we just need something to approximate a bigger surface to avoid collisions, this will work in a pinch.
	 *Using the convex hull of the shape instead of the shape is going to be safest.	  
	 * @param vtc
	 *          the first shape to check for intersection
	 * @param center
	 * 			the point to offset away from.
	 * @param dist
	 * 			the distance to offset the points
	 * @return the offset vertex arraylist
	 * @see #getPolygonCentroid(ArrayList)
	 * @see #getConvexHull(ArrayList)
	 */
	public ArrayList<PVector> offsetPolylineFromPoint(ArrayList<PVector> vtc, PVector center, float dist) {
		  ArrayList<PVector> theOffsetShape = new ArrayList<PVector>();
		  for (int i = 0; i< vtc.size(); i++) {
		    //for each vertex
		    //get the vector that represents the direction from the center point to our vertex
		    PVector dirVec = PVector.sub(vtc.get(i), center);
		    dirVec.setMag(dist); //set the magnitude of our vector to our desired length
		    PVector shiftedVtx = PVector.add(vtc.get(i), dirVec); //move the vertex away from the center the specified amount
		    theOffsetShape.add(shiftedVtx); //add the new shifted vertex to our stored list
		  }
		  return theOffsetShape;//return the offset shape
		}
	
	/**
	 *This is a method for sorting an arraylist of blobs (an arraylist of arraylist of vertices)
	 *using a separate key list of floats as a guide.
	 *In example, given an arraylist of blobs, and a matching 1d arraylist (FloatList) of blob areas,
	 *return a arraylist of blobs sorted from smallest to biggest
	 * @param theBlobs    
	 * 			the blobs to sort   
	 * @param theSizes
	 * 			a floatlist to use as a key for sorting the first list of Vertex-defined shapes.  Should match the first list in size.
	 * @return the sorted list of polylines
	 * @example HullAndSort
	 */
	public ArrayList<ArrayList<PVector>> sortByFloatList(ArrayList<ArrayList<PVector>> theBlobs, FloatList theSizes) {
	  //HashMap<String,Integer> hm = new HashMap<String,Integer>();
	  Map<Float, ArrayList<PVector>> blobOrder = new HashMap<Float, ArrayList<PVector>>(); //create a map from the floats and blobs
	  for (int i = 0; i < theBlobs.size(); i++)
	  { //for each blob...
	    blobOrder.put(theSizes.get(i), theBlobs.get(i)); //add the values to the map
	  }
	  Map<Float, ArrayList<PVector>> treeMap = new TreeMap<Float, ArrayList<PVector>>(blobOrder); //sort the map
	  ArrayList<ArrayList<PVector>> orderedBlobs = new  ArrayList<ArrayList<PVector>>(); //set up an empty array of sorted blobs
	  for (ArrayList<PVector> value : treeMap.values()) { //for each value in the map
	    orderedBlobs.add(value); //copy it into our 2d arraylist
	  }
	  return(orderedBlobs); //return the ordered arraylist
	}
	
	
	
	/**
	 *Given a FloatList (a bunch of floats), return an array which represents their indices sorted from smallest to largest.
	 *This is useful for using it to sort through an object of a random type, which you can just fill a new list using the order returned from this function.
	 *You should probably just use a TreeMap for this, but this provides a quick method if you are extra extra lazy.
	 * @param theSizes   
	 * 			a FloatList containing values by which you want to sort another list of objects   
	 * @return an array containing the sorted indices
	// */
	public int [] getSortedIndices(FloatList theSizes) {
		  int [] unsortedIndices = new int[theSizes.size()]; //make a new list to store integers
		  for (int i = 0; i<theSizes.size(); i++) { //fill it with a series of ints from 0 to n
		    unsortedIndices[i] = i;
		  }
		  Map<Float, Integer> theOrder = new HashMap<Float, Integer>(); //create a map from the floats and blobs
		  for (int i = 0; i < theSizes.size(); i++){ //for each item, put it in our hashmap
		    theOrder.put(theSizes.get(i), unsortedIndices[i]); //add the values to the map
		  }
		  Map<Float, Integer> treeMap = new TreeMap<Float, Integer>(theOrder); //sort the map
		  int [] orderedInts = new  int [theSizes.size()]; //set up an empty array of sorted integers
		  int i = 0; //for counting
		  for (Integer value : treeMap.values()) { //for each value in the map
		    orderedInts[i] = (value); //copy it into our array
		    i++;
		  }
		  return(orderedInts); //return the ordered array
		}
	
	
	/**
	 * Finds the next index in a sequence of points that is the given radius away from the starting point
	 *this doesn't work particularly well with high curvature.  Could try taking cumulative length of segments instead...
	 *it is also poorly named/documented.  Basically, given a start point, find the next index that is outside the given radius
	 *moving in order of the vertices as ordered in the arraylist
	 *Generally useful for, say, placing a series of fixed length objects along an irregular goal path (bricks, glass, straws, etc)
	 *Currently just returns 0 if no index is found.
	 * @param thePathCurve
	 * 			the polyline to look for indices along       
	 * @param theRadius
	 * 			the search radius
	 * @param theStartIndex
	 * 			the index from which we should start the serch
	 * @return an integer that represents the first index that is "theRadius" or more away from the point at the startIndex
	 */
	public int getNextPathIndex(ArrayList<PVector> thePathCurve, float theRadius, int theStartIndex) {
	  PVector intersectionPt = new PVector(); //where does the radius intersect the curve first?
	  for (int i = theStartIndex; i<thePathCurve.size()-1; i++) {
	    if (PVector.dist(thePathCurve.get(theStartIndex), thePathCurve.get(i))<theRadius && PVector.dist(thePathCurve.get(theStartIndex), thePathCurve.get(i+1))>theRadius) {
	      //if the start of this segment is less than the radius, and the end is greater, this segment is the right one
	      intersectionPt = radialIntersection(thePathCurve.get(i+1), thePathCurve.get(i), thePathCurve.get(theStartIndex), theRadius);  
	      int intersectionIndex = i+1;
	      for (int j = intersectionIndex; j<thePathCurve.size()-1; j++) {
	        //do it again to find ending index
	        if (PVector.dist(intersectionPt, thePathCurve.get(j))<theRadius && PVector.dist(intersectionPt, thePathCurve.get(j+1))>theRadius) {
	          return j+1;
	        }
	      }
	    }
	  }
	  System.out.println("NO INTERSECTION FOUND.  PATH CURVE NOT LONG ENOUGH OR WE ARE NEAR THE END OF THE CURVE.  RESET INDEX TO ZERO");
	  return 0;
	}
	
	/**
	 * Finds the point  (not necessarily a vertex) on a polyline that is the given radius away from the vertex at a specified index
	 *Generally useful for, say, placing a series of fixed length objects along an irregular goal path (bricks, glass, straws, etc)
	 *Currently just returns a null vector if no intersection is found.
	 * @param thePathCurve
	 * 			the polyline to look for indices along       
	 * @param theRadius
	 * 			the search radius
	 * @param theStartIndex
	 * 			the index from which we should start the serch
	 * @return a PVector that represents the point on the polyline that is "theRadius" away from the point at the startIndex
	 * @see #getNextPathIndex(ArrayList, float, int)
	 */
	public PVector getCurveIntersection(ArrayList<PVector> thePathCurve, float theRadius, int theStartIndex) {
		  PVector intersectionPt = new PVector(); //where does the radius intersect the curve first?
		  for (int i = theStartIndex; i<thePathCurve.size()-1; i++) {
		    if (PVector.dist(thePathCurve.get(theStartIndex), thePathCurve.get(i))<theRadius && PVector.dist(thePathCurve.get(theStartIndex), thePathCurve.get(i+1))>theRadius) {
		      //if the start of this segment is less than the radius, and the end is greater, this segment is the right one
		      intersectionPt = radialIntersection(thePathCurve.get(i+1), thePathCurve.get(i), thePathCurve.get(theStartIndex), theRadius);  
		      return intersectionPt;
		    }
		  }
		  System.out.println("NO INTERSECTION FOUND.  PATH CURVE NOT LONG ENOUGH?");
		  return intersectionPt;
		}
	
	/**
	 * Function for determining the intersection of a line segment and a circle
	 *a = point of line origin within circle, b= point of line outside the circle
	 *centerPoint = centerpoint of the circle, rad = radius of the circle
	 * @param a
	 * 			the start point of the line segment      
	 * @param b
	 * 			the end point of the line segment
	 * @param centerPoint
	 * 			the center point of the circle
	 * @param rad
	 * 			the radius of the circle
	 * @return the point where a circle intersects a line segment.  Null if no intersection.
	 */
	public PVector radialIntersection(PVector a, PVector b, PVector centerPoint, float rad) { 
	  PVector intPoint = null;
	  float pX, pY;
	  float A = (b.x - a.x)*(b.x-a.x) + (b.y - a.y)*(b.y - a.y);
	  float B = 2*((b.x-a.x)*(a.x-centerPoint.x)+(b.y-a.y)*(a.y-centerPoint.y));
	  float C = centerPoint.x*centerPoint.x + centerPoint.y*centerPoint.y + a.x*a.x + a.y*a.y - 2*(centerPoint.x*a.x + centerPoint.y*a.y) - rad*rad;
	  float deter = B*B - 4*A*C;
	  if (deter >= 0) {
	    float e = PApplet.sqrt(deter);
	    float u1 = (-1*B + e)/(2*A);
	    float u2 = (-1*B - e)/(2*A);

	    if (0 <= u2 && u2 <= 1) {
	      pX = PApplet.lerp(b.x, a.x, 1 - u2);
	      pY = PApplet.lerp(b.y, a.y, 1 - u2);
	      intPoint = new PVector(pX, pY);
	    }

	    if (0 <= u1 && u1 <= 1) {
	      pX = PApplet.lerp(b.x, a.x, 1 - u1);
	      pY = PApplet.lerp(b.y, a.y, 1 - u1);
	      intPoint = new PVector(pX, pY);
	    }
	  } 
	  return(intPoint);
	}

	
	/**
	 *This function is intended to return a simplified, "single wall" polyline from a possibly messy, double walled curve
	 *It needs the original curve points, an array that is empty at the start, the starting point, and the radius to simplifiy with
	 *The radius should be smaller than the thickness of the double-walled curve
	 *<p>
	 *Essentially, it starts at a given point, and searches within a designated radius, deleting all points within that radius except for the one that is farthest away.
	 *It adds that point to the list of points to return, and does the function again starting at that vertex until there are no more vertices remaining within the search radius.
	 * @param crv
	 * 			the arraylist of points defining a shape to simplify     
	 * @param nextPoint
	 * 			the point to start simplifying from.  This should be at an extreme of the shape, as once the function starts it only moves in one direction
	 * @param simpleCrv
	 * 			the simplified curve to return.  Should be empty at the start of the function
	 * @param searchRadius
	 * 			the radius to simplify by.  Should be larger than the width of a double-walled tube if wanting to turn it into a single walled line.
	 * @return the simplified polyline as an arraylist of PVectors
	 */
	public ArrayList<PVector> recursiveSimplify(ArrayList<PVector> crv, PVector nextPoint, ArrayList<PVector> simpleCrv, float searchRadius) {
		  //this function is intended to return a simplified, "single wall" polyline from a possibly messy, double walled curve
		  //it needs the original curve points, an array that is empty at the start, the starting point, and the radius to simplifiy with
		  //the radius should be smaller than the thickness of the line.
		  if (crv.size() < 1) {
		    //if we don't have any more points left in our original curve, return this simple curve
		    return simpleCrv;
		  } else { //there are still points left to simplify
		    simpleCrv.add(nextPoint.copy());  //add the point found at the next cycle to our new curve
		    //now loop through all the rest of the points, and find points that our within our search radius
		    //if they are, we want to remove them from the simplified curve list
		    //but, we want to keep the point that is within the search radius but closest to the edge as our next start point
		    PVector nextStartPt = new PVector(); //create a holder for our next start point
		    float largestDist = -10;  //set a really small number as our largest distance before we start looking
		    for (int i = crv.size()-1; i > 0; i--) {
		      //loop backwards through all of our vertices and delete any points that our within our search radius
		      float theDist = nextPoint.dist(crv.get(i));

		      if (theDist <= searchRadius) { //if the vertex is within our search radius...
		        if (theDist > largestDist) { //if our distance is bigger than our old record
		          //save it as our next potential start point
		          nextStartPt = crv.get(i).copy(); //save the point as our next start point
		          largestDist = theDist; //set this as our new world record
		        }//end if theDist > largestDist
		        crv.remove(i); //remove this point from the curve because it's inside of our circle
		        //end if nextpoint is within distance
		      }//end if the dist <= searchRadius
		    } //end for loop through vertices

		    if (largestDist >0) { //if the next point isn't the same as our current one
		      recursiveSimplify(crv, nextStartPt, simpleCrv, searchRadius); //run the function again
		    } else {
		      //the points are spaced too far apart for our sample radius
		      //println("Returning simplified curve.  Possible that search radius is too small, or start point isn't at the end of the curve.");
		      //return simpleCrv;
		    }
		    return simpleCrv; //when we are all done...
		  } //end else if points are left
		} //end recursivesimplify function
	
	/**
	 *Increase the vertices of a polyline to match a desired number of vertices.
	 *<p>
	 *This is a very brutish and terribly written function for increasing the density of a polyline.
	 *Basically, it adds vertices to segments that are longer than the average desired distance as even subdivisions between existing vertices...
	 *Needs reworking, but written quickly for use in a pinch...
	 * @param crv
	 * 			the arraylist of points defining a shape to set the density of    
	 * @param goalVtxCount
	 * 			the desired number of vertices on this polyline
	 * @return the polyline with the (approximately?) desired number of vertices
	 */
	public ArrayList<PVector> densifyPolyline(ArrayList<PVector> crv, int goalVtxCount) {
	  //this is a brutish way of increasing the density of a polyline while increasing all original vertices
	  ArrayList<PVector> rebuiltPolyline = new ArrayList<PVector>(); //store our rebuilt polyline
	  //first we need to figure out roughly how far apart our vertices should be
	  float crvLength = getPolylineShapeLength(crv); //get the length of the curve
	  float lengthPerVertex = crvLength/(float)goalVtxCount; //this is roughly how long our segments should be

	  for (int j = 0; j<crv.size(); j++) {//for each vertex in our selected polyline
	    if (j>0) { //if we are not on the first point, see if we need to add vertices between our current position and the last one
	      float segmentLength = crv.get(j).dist(crv.get(j-1));
	      if (segmentLength>lengthPerVertex) { //if we're of a certain size, add vertices
	        int numAddedVertices = PApplet.round(segmentLength/lengthPerVertex) - 1;
	        if (numAddedVertices>0) { //if we actually need to add vertices
	          for (int k = 0; k<numAddedVertices; k++) {
	        	float a = (float)(k+1);
	        	float b = (float)(numAddedVertices+1);
	            PVector segmentInterpolatedVertex = PVector.lerp(crv.get(j-1), crv.get(j),a/b); //get the interpolated point
	            rebuiltPolyline.add(segmentInterpolatedVertex); //add the interpolated point to the index
	          }//end for num added Vertices
	        }//end if numadded vertices greater than zero
	      }//end if segment length>length per vertex
	      rebuiltPolyline.add(crv.get(j)); //add the actual vertex to the curve
	    } else if (j == 0) {
	      rebuiltPolyline.add(crv.get(j)); //add the actual vertex to the curve
	    }
	  }//end for vertices

	  return rebuiltPolyline;
	}
	
	
	/**
	 *Gets the average color of the pixels inside of a blob (argb)
	 * @param shape
	 * 			the arraylist of points defining a shape get the color of.  Same coordinate system as the image.    
	 * @param theImg
	 * 			the image to use to get the average color within a shape
	 * @return the average color of the blob
	 * @example getBlobAverageColor
	 */
	public int getBlobColor(ArrayList<PVector> shape, PImage theImg) {
		  PGraphics pg = myParent.createGraphics(theImg.width, theImg.height); //create a blank image
		  pg.beginDraw();
		  pg.noStroke();
		  pg.fill(255);
		  pg.beginShape(); //begin drawing a processing shape
		  for (int i = 0; i<shape.size(); i++) {
		    pg.vertex(shape.get(i).x, shape.get(i).y);
		  } //end for each vertex in the polyline
		  pg.endShape(); //end drawing a processing shape
		  pg.endDraw();
		  //now iterate through the pixels of our image, which should have a clear background except for the shape
		  pg.loadPixels();
		  float numPixels = theImg.width*theImg.height;

		  float redSum = 0; //store our total color values
		  float greenSum = 0;
		  float blueSum = 0;
		  float pixelCount = 0;
		  float alphaSum = 0;
		  theImg.loadPixels();
		  for (int i = 0; i < numPixels; i++) {
		    if (((pg.pixels[i] >> 24) & 0xFF)>1) { //if we are in an area that has color (faster way of getting alpha)
		      int r = (theImg.pixels[i] >> 16) & 0xFF; // Faster way of getting red(argb)
		      int g = (theImg.pixels[i] >> 8) & 0xFF; // Faster way of getting green(argb)
		      int b = theImg.pixels[i] & 0xFF; // Faster way of getting blue(argb)
		      int a = (theImg.pixels[i] >> 24) & 0xFF; // Faster way of getting alpha(argb)
		      redSum += r; //add our values to our total
		      greenSum += g;
		      blueSum += b;
		      alphaSum += a;
		      pixelCount++; //add one to our total number of pixels in our shape
		    }
		  }

		  redSum/=pixelCount;
		  greenSum/=pixelCount;
		  blueSum/=pixelCount;
		  alphaSum/=pixelCount;
		  int ta = (int)alphaSum << 24;
		  int tr = (int)redSum << 16;
		  int tg = (int)greenSum << 8;
		  int tb = (int)blueSum;

		  // Equivalent to "color argb = color(r, g, b, a)" but faster
		  int averageColor = ta | tr | tg | tb;
		  return averageColor;
		}
	
	
	/**
	 *Draws an arraylist of PVectors as a polyline using processing's beginShape/vertex/endshape sequence.
	 *<p>
	 *This might be the wrong way of doing this, and you can easily implement this on your own, but it's a quick shorthand method.
	 * @param polylineShape
	 * 			the arraylist of points defining a polyline to draw    
	 */
	public void drawPolylineShape(ArrayList<PVector> polylineShape) {
		  myParent.beginShape(); //begin drawing a processing shape
		  for (int i = 0; i<polylineShape.size(); i++) {
			  myParent.vertex(polylineShape.get(i).x, polylineShape.get(i).y);
		  } //end for each vertex in the polyline
		  myParent.endShape(); //end drawing a processing shape
		} //end draw polylineshape
	
	/**
	 *Draws an arraylist of PVectors as a polyline using processing's beginShape/vertex/endshape sequence.
	 *<p>
	 *Specify "close" boolean to designate if shape should be drawn closed or left open
	 * @param polylineShape
	 * 			the arraylist of points defining a polyline to draw  
	 * @param close
	 * 			should the polyline be closed?  True for Closed, False for Open  
	 */
	public void drawPolylineShape(ArrayList<PVector> polylineShape, boolean close) {
		  myParent.beginShape(); //begin drawing a processing shape
		  for (int i = 0; i<polylineShape.size(); i++) {
			  myParent.vertex(polylineShape.get(i).x, polylineShape.get(i).y);
		  } //end for each vertex in the polyline
		  if(close){
		  myParent.endShape(myParent.CLOSE); //end drawing a processing shape
	}
		  else{
			  myParent.endShape();
		  }
		} //end draw polylineshape
	
	/**
	 *Draws an Java generalPath as a polyline using processing's beginShape/vertex/endshape sequence.
	 *<p>
	 *This only draws general paths made of polylines at the moment.
	 * @param gp
	 * 			the polyline generalPath to draw    
	 */
	//
	void drawGeneralPath(GeneralPath gp) {
	  PathIterator theIterator = gp.getPathIterator(null);
	  float [] gpPts = new float[6]; //store coordinate data as we iterate
	  int segType; //sotre the type of segment
	  while (!theIterator.isDone()) {
	    segType = theIterator.currentSegment(gpPts); //ge tthe type of sgment
	    if (segType == PathIterator.SEG_MOVETO) {
	    	myParent.beginShape();
	    	myParent.vertex(gpPts[0], gpPts[1]);
	    }
	    if (segType == PathIterator.SEG_LINETO) { // LINETO
	    	myParent.vertex(gpPts[0], gpPts[1]);
	      //println(pts[0]+","+pts[1]);
	    }
	    if (segType == PathIterator.SEG_CLOSE) {  
	    	myParent.endShape(myParent.CLOSE);
	    }
	    theIterator.next();
	  }
	}
	
	
	
}



