package greyshed.lib;
import processing.core.*;

/**
 *Store, convert, and modify orientation information in Quaternion notation.
 *Provides options for creating orientations from reference points, and converting to and from PMatrix and Axis Angle (UR Robots) notation.
 *
 * @example Hello 
 */

public class Quaternion {
	public float qW, qX, qY, qZ; //the values of our quaternion
	
	/**
	 * Quaternion constructor.  If no values are set at initialization, identity is used by default. @param theImg
	 */
	public Quaternion() { //if no values are set at initialization, use the wold xy plane as the default
	    qW=(float)1.00000;
	    qX=(float)0.00000;
	    qY=(float)0.00000;
	    qZ=(float)0.00000;
	  } 

	/**
	 * Construct a new quaternion using four values (qW,qX,qY,qZ)
	 * @param qWIn
	 *          qW
	 * @param qXIn
	 *          qX
	 * @param qYIn
	 *          qY
	 * @param qZIn
	 *          qZ
	 */
	public Quaternion(float qWIn, float qXIn, float qYIn, float qZIn) { //initialize quaternion with set values
	    qW=qWIn;
	    qX=qXIn;
	    qY=qYIn;
	    qZ=qZIn;
	  } 

	/**
	 * Set the quaternion values.
	 * @param qWIn
	 *          qW
	 * @param qXIn
	 *          qX
	 * @param qYIn
	 *          qY
	 * @param qZIn
	 *          qZ
	 */
	public void set(float qWIn, float qXIn, float qYIn, float qZIn) { //define the values of our quaternion
	    qW=qWIn;
	    qX=qXIn;
	    qY=qYIn;
	    qZ=qZIn;
	  }

	/**
	 * Create a new quaternion given the x,y,z vectors of a target frame.
	 * <p>
	 * Using these frame vectors, calculate the quaternion.  See <a href="http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/">euclideanspace</a>
	 * @param xV
	 *          plane X Axis direction vector
	 * @param yV
	 *          plane Y Axis direction vector
	 * @param zV
	 *          plane Z Axis direction vector
	 */
	public void fromFrame(PVector xV, PVector yV, PVector zV) {//create a new quaternion given the x,y,z vectors of a target frame
	    //unitize these frame vectors
	    xV.normalize(); //x axis
	    yV.normalize(); //y axis
	    zV.normalize(); //z axis

	    //using these frame vectors, calculate the quaternion.  Calclation based on information from euclideanspace:
	    //http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/
	    float trace, s, w, x, y, z;
	    trace = xV.x + yV.y + zV.z;
	    if (trace > 0.0) {
	      s = (float)0.5 / PApplet.sqrt(trace + (float)1.0);
	      w = (float)0.25 / s;
	      x = ( yV.z - zV.y) * s;
	      y = ( zV.x - xV.z) * s;
	      z = ( xV.y - yV.x) * s;
	    } else {
	      if (xV.x > yV.y && xV.x > zV.z) {
	        s = (float)2.0 * PApplet.sqrt((float)1.0 + xV.x - yV.y - zV.z);
	        w = (yV.z - zV.y ) / s;
	        x = (float)0.25 * s;
	        y = (yV.x + xV.y ) / s;
	        z = (zV.x + xV.z ) / s;
	      } else if (yV.y > zV.z) {
	        s = (float)2.0 * PApplet.sqrt((float)1.0 + yV.y - xV.x - zV.z);
	        w = (zV.x - xV.z) / s;
	        x = (yV.x + xV.y) / s;
	        y = (float)0.25 * s;
	        z = (zV.y + yV.z ) / s;
	      } else {
	        s = (float)2.0 * PApplet.sqrt((float)1.0 + zV.z - xV.x - yV.y);
	        w = (xV.y - yV.x) / s;
	        x = (zV.x + xV.z ) / s;
	        y = (zV.y + yV.z ) / s;
	        z = (float)0.25 * s;
	      }
	    }

	    //normalize the found quaternion
	    float qLength;
	    qLength = (float)1.0 / PApplet.sqrt(w * w + x * x + y * y + z * z);
	    w *= qLength;
	    x *= qLength;
	    y *= qLength;
	    z *= qLength;
	    //set the components of our quaternion
	    qW=w;
	    qX=x;
	    qY=y;
	    qZ=z;
	  }

	/**
	 * @return the quaternion as a string, enclosed in square brackets, comma delineated, and truncated to 6 decimal places.
	 */
	public String toOrientString() { //given a quaternion, return a string in square brackets that has been truncated to 6 decimal places
	    String orientString = String.format("[%.6f,%.6f,%.6f,%.6f]", qW, qX, qY, qZ);
	    return orientString;
	  }

	/**
	 * Normalize the quaternion.
	 */
	public void normalizeQuaternion() {
	    //normalizing function
	    float qLength = (float)1.0 / PApplet.sqrt(qW * qW + qX * qX + qY * qY + qZ * qZ);
	    qW *= qLength;
	    qX *= qLength;
	    qY *= qLength;
	    qZ *= qLength;
	  }

	/**
	 * Given the current quaternion, return the frame/transformation matrix (as an array of three PVectors, X,Y,Z)
	 * <p>
	 * See <a href="http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/index.htm">euclideanspace</a>
	 * @return the frame/transformation matrix as an array of three PVectors (X,Y,Z)
	 */
	public PVector[] toMatrix() { //
	    //info from: http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/index.htm

	    PVector xVector = new PVector();
	    PVector yVector = new PVector();
	    PVector zVector = new PVector();

	    xVector.x = 1 - 2 * qY * qY - 2 * qZ * qZ;
	    xVector.y = 2 * qX * qY + 2 * qZ * qW;
	    xVector.z = 2 * qX * qZ - 2 * qY * qW;

	    yVector.x = 2 * qX * qY - 2 * qZ * qW;
	    yVector.y = 1 - 2 * qX * qX - 2 * qZ * qZ;
	    yVector.z = 2 * qY * qZ + 2 * qX * qW;

	    zVector.x = 2 * qX * qZ + 2 * qY * qW;
	    zVector.y = 2 * qY * qZ - 2 * qX * qW;
	    zVector.z = 1 - 2 * qX * qX - 2 * qY * qY;


	    PVector[] xFormMatrix = new PVector[3];
	    xFormMatrix[0] = xVector;
	    xFormMatrix[1] = yVector;
	    xFormMatrix[2] = zVector;
	    return xFormMatrix;
	  }

	/**
	 * Convert the quaternion to axis angle notation.
	 * <p>
	 * See <a href="http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToAngle/">euclideanspace</a>
	 * @return A vector representing the quaternion in axis angle notation.
	 */
	public PVector toAxisAngle() {
	    //convert a quaternion to axis angle notation.  based on code from: http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToAngle/
	    float aaX = 0;
	    float aaY = 0;
	    float aaZ = 0; //our axis angles to set
	    PVector aa; //the pVector to send
	    if (qW > 1) normalizeQuaternion(); // if w>1 acos and sqrt will produce errors, this cant happen if quaternion is normalised
	    float angle = 2*PApplet.acos(qW);
	    float s = PApplet.sqrt(1-qW*qW); // assuming quaternion normalised then w is less than 1, so term always positive.
	    if (s < 0.001) { // test to avoid divide by zero, s is always positive due to sqrt
	      // if s close to zero then direction of axis not important
	      aaX = qX; // if it is important that axis is normalised then replace with x=1; y=z=0;
	      aaY = qY;
	      aaZ = qZ;
	    } else {
	      aaX = qX / s; // normalise axis
	      aaY = qY / s;
	      aaZ = qZ / s;
	    }
	    aa = new PVector(aaX, aaY, aaZ);
	    aa.setMag(angle);
	    return(aa);
	  }

	/**
	 * Define the quaternion from axis angle notation.
	 * <p>
	 * See <a href="http://www.euclideanspace.com/maths/geometry/rotations/conversions/angleToQuaternion/index.htm">euclideanspace</a>
	 * @param aa
	 * 		a PVector representing an orientation in axis angle notation
	 */
	public void fromAxisAngle(PVector aa) {
	    //make a new quaternion from axis angle notation
	    //based on code from:  http://www.euclideanspace.com/maths/geometry/rotations/conversions/angleToQuaternion/index.htm
	    //our angle is the length of our vector
	    float angle = aa.mag();
	    aa.normalize();
	    //assumes axis is already normalized
	    float s = PApplet.sin(angle/2);
	    qX = aa.x * s;
	    qY = aa.y * s;
	    qZ = aa.z * s;
	    qW = PApplet.cos(angle/2);
	  }

}
