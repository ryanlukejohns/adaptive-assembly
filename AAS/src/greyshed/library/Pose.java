package greyshed.lib;
import processing.core.*;

/**
 *A simple class to store, define and modify a "pose" (also known as a frame, target, or plane).
 *A Pose composed of position data "pos" (PVector) and orientation data "orient" (Quaternion)
 *<p>
 *Pose is the format for defining target planes for ABB robots.  
 *(An ABB "robTarget", without configuration or exeternal axis data).
 *<p>
 *Options for returning alternative representations of orientations are included in the quaternion class, including
 *axis angles (UR robots) and PMatrix formats.
 *
 * @example Hello 
 */

public class Pose {

public PVector pos;
public Quaternion orient;
public PMatrix3D matrix;


/**
 *Initialize a pose with position (0,0,0) and identity matrix (XY plane)
 */
public Pose() { //initialize pose with base XY Plane values
  pos=new PVector(0, 0, 0);
  PVector xAxis = new PVector(1, 0, 0);
  PVector yAxis = new PVector(0, 1, 0);
  PVector zAxis = new PVector(0, 0, 1);
  orient = new Quaternion();
  orient.fromFrame(xAxis, yAxis, zAxis);
  matrix = getMatrix();
}

/**
 *Initialize a pose with a designated position (pos) and orientation (orient)
 *@param posePos
 * 			the position of the pose as a PVector     
 *@param poseOrient
 * 			the orientation of the Pose/Plane in quaternion notation
 */
public Pose(PVector posePos, Quaternion poseOrient) { //initialize pose with set values
  pos=posePos;
  orient=poseOrient;
  matrix = getMatrix();
}

/**
 *Initialize a pose with a PMatrix3D
 *@param m
 * 			the matrix defining the position and orientation of the pose  
 */
public Pose(PMatrix3D m) {
  float [] mArray = new float[16];
  m.get(mArray); //convert the matrix to an array of numbers so that we can use them.
  PVector posElement = new PVector(mArray[3], mArray[7], mArray[11]); //might want to double check these numbers...impatient
  PVector orientX = new PVector(mArray[0], mArray[4], mArray[8]);
  PVector orientY = new PVector(mArray[1], mArray[5], mArray[9]);
  PVector orientZ = new PVector(mArray[2], mArray[6], mArray[10]);
  pos = posElement;
  orient = new Quaternion();
  orient.fromFrame(orientX, orientY, orientZ);
  matrix = getMatrix();
}

/**
 *Get the position of a pose.
 *@return the "pos" or position of the pose as a PVector
 */
public PVector getPos() {
  return pos;
}

/**
 *Get the orientation of a pose.
 *@return the "orient" of the pose as a Quaternion
 */
public Quaternion getOrient() {
  return orient;
}

/**
 *Prints the pose out to the console as [x,y,z,qW,qX,qY,qZ]
 */
public void printPose(){
	System.out.println("[" + pos.x + pos.y + pos.z + orient.qW + orient.qX + orient.qY + orient.qZ + "]");
  
}

/**
 *Sets a pose from a PMatrix3D
 *@param m
 * 			the matrix defining the position and orientation of the pose  
 */
public void fromPMatrix3D(PMatrix3D m) {
  float [] mArray = new float[16];
  m.get(mArray); //convert the matrix to an array of numbers so that we can use them.
  PVector posElement = new PVector(mArray[3], mArray[7], mArray[11]); //might want to double check these numbers...impatient
  PVector orientX = new PVector(mArray[0], mArray[4], mArray[8]);
  PVector orientY = new PVector(mArray[1], mArray[5], mArray[9]);
  PVector orientZ = new PVector(mArray[2], mArray[6], mArray[10]);
  pos = posElement;
  orient.fromFrame(orientX, orientY, orientZ);
}

/**
 *Gets a PMatrix3D from a Pose
 *@return the current pose in PMatrix3D format
 */
public PMatrix3D getMatrix() { 
  PVector[] frameVectors = orient.toMatrix();
  PVector x = frameVectors[0];
  PVector y = frameVectors[1];
  PVector z = frameVectors[2];
  //PMatrix3D myPose = new PMatrix3D(x.x, x.y, x.z, 0, y.x, y.y, y.z, 0, z.x, z.y, z.z, 0, pos.x, pos.y, pos.z, 1);
  PMatrix3D myPose = new PMatrix3D(x.x, y.x, z.x, pos.x, x.y, y.y, z.y, pos.y, x.z, y.z, z.z, pos.z, 0, 0, 0, 1);
  //float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33
  //Processing is weird...  http://forum.processing.org/one/topic/understanding-pmatrix3d.html
  //(x.x, x.y, x.z, 0, y.x, y.y, y.z, 0, z.x, z.y, z.z, 0, pos.x, pos.y, pos.z, 1);
  //(x.x,y.x,z.x,pos.x,x.y,y.y,z.y,pos.y,x.z,y.z,z.z,pos.z,0,0,0,1)

  return myPose;
}

/**
 *Sets a pose/plane from three points.  The equavalent to "plane from 3 points" in Rhino/Grasshopper.
 *@param origin
 * 			the origin of the pose
 *@param xPt
 * 			a point on the x axis of the desired plane
 *@param yPt
 * 			a point on the y axis of the desired plane  
 */
public void fromPoints(PVector origin, PVector xPt, PVector yPt) {
  //defines a Pose from probed points.  Origin, a point along the x axis, and a point along the y axis.  This code is X axis dominant,
  //meaning that the x axis will be exactly as defined, and the Y axis will be as close as possible to the probed point while maintaining a true plane definition
  PVector xAxis = PVector.sub(xPt, origin).normalize(); //our x axis is defined by the line between 
  PVector tempYAxis = PVector.sub(yPt, origin).normalize();
  PVector zAxis = xAxis.cross(tempYAxis);
  PVector yAxis = zAxis.cross(xAxis);
  pos = origin;
  orient.fromFrame(xAxis, yAxis, zAxis);
  matrix = getMatrix();
}

/**
 *Sets a pose/plane from the plane origin and a vector representing the z-axis of the desired plane (plane normal).  The plane is rotationally aligned as close as possible to the world x axis.
 *@param target
 * 			the origin of the pose/plane
 *@param zDir
 * 			the normal direction of the plane/pose  
 */
public void fromTargetAndGuide(PVector target, PVector zDir) {
  //define a pose based on a taget point and a vector defining the z axis of the tool, and an optional guide x axis vector to use for rotational alignment
  PVector xGuide = new PVector(1, 0, 0);//we want our x guide to point along the x axis
  pos = target;
  zDir.normalize();
  PVector yDir = zDir.cross(xGuide);
  PVector xDir = yDir.cross(zDir);
  orient.fromFrame(xDir, yDir, zDir);
  matrix = getMatrix();
}

/**
 *Sets a pose/plane from the plane origin and a vector representing the z-axis of the desired plane (plane normal).  The rotation of the plane is set by the third vector, which represents the desired x axis of the plane.
 *@param target
 * 			the origin of the pose/plane
 *@param zDir
 * 			the normal direction of the plane/pose  
 *@param xGuide
 * 			the x guide vector for aligning the rotation of the plane about the designated z axis
 */
public void fromTargetAndGuide(PVector target, PVector zDir, PVector xGuide) {
  //define a pose based on a taget point and a vector defining the z axis of the tool, and an optional guide x axis vector to use for rotational alignment
  pos = target;
  zDir.normalize();
  PVector yDir = zDir.cross(xGuide);
  PVector xDir = yDir.cross(zDir);
  orient.fromFrame(xDir, yDir, zDir);
  matrix = getMatrix();
}

/**
 *Using the Pose of a plane in the world/workobject coordinate system (i.e. the pose of the origin of the kinect in the world system)
 *and given a point in the coordinate system of a plane (i.e. a kinect point)
 *and using the Pose the Pose of that plane in the world/wobj coordinate system,
 *return the location of that point in the world/wobj coordinate system
 *<p>
 *This function is especially useful if the kinect is mounted to the robot and the tooldata of the robot has been calibrated at the 
 *kinect's origin.  Or, if the kinect is at a fixed point in space, and it's origin has been calculated as a pose in world or workobject coords.
 *Basically, it tranforms a point in kinect coordinate space to robot coordinate space
 *<p>
 *Ensure that your incoming points are right hand rule, i.e. not mirrored across the x axis...
 *<p>
 *information and code on rotations with quaternions can be found here:
 *http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/transforms/
 * 			the origin of the pose/plane
 *@param thePt
 * 			the point to transform 
 */
public PVector transformPoint(PVector p1){
	  PVector p2 = new PVector();  //our tansformed point
	  float w = orient.qW; // real part of quaternion
	  float x = orient.qX; // imaginary i part of quaternion
	  float y = orient.qY; // imaginary j part of quaternion
	  float z = orient.qZ; // imaginary k part of quaternion
	  //rotate our point
	  p2.x = w*w*p1.x + 2*y*w*p1.z - 2*z*w*p1.y + x*x*p1.x + 2*y*x*p1.y + 2*z*x*p1.z - z*z*p1.x - y*y*p1.x;
	  p2.y = 2*x*y*p1.x + y*y*p1.y + 2*z*y*p1.z + 2*w*z*p1.x - z*z*p1.y + w*w*p1.y - 2*x*w*p1.z - x*x*p1.y;
	  p2.z = 2*x*z*p1.x + 2*y*z*p1.y + z*z*p1.z - 2*w*y*p1.x - y*y*p1.z + 2*w*x*p1.y - x*x*p1.z + w*w*p1.z;
	  //translate our point
	  p2.x = p2.x + pos.x;
	  p2.y = p2.y + pos.y;
	  p2.z = p2.z + pos.z;

	  return p2;
}


}
