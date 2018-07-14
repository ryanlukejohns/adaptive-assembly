import greyshed.lib.*; //import the library

boolean testing = true; //use this variable to run code if you aren't plugged into the robot (testing is true without robot)
//===========================SET POINTS THAT DEFINE THE BASE PLANE OF OUR COORDINATE SYSTEM===================================
//these values should be read from the teachpendant screen and kept in the same units (Millimeters)
PVector origin = new PVector(314.78, -69.8, 2.55); //this is the probed origin point of our local coordinate system.
PVector xPt = new PVector( -303.42, -71.29, 3.44); //this is a point probed along the x axis of our local coordinate system
PVector yPt = new PVector(316.8, -437, 7.1); //this is a point probed along the z axis of our local coordinate system
//===========================================================================================
//=================================NETWORKING DATA===========================================================================
RobCom ur; //make an instance of our RobCom class for talking to this one robot, we'll call this robot ur

String ipAddress = "169.254.0.0"; //set the ip address of the robot
int port = 30002; //set the port of the robot
//===============================================================
//===========MOVEMENT VARIABLES========
float radius = 6; //set our blend radius in mm for movel and movep commands
float speed = 20; //set our speed in mm/s
float acceleration = 0.05; //set acceleration (m/s^2)

void setup() {
  size(200, 200);
  Pose basePlane = new Pose(); //make a new "Pose" (Position and orientation) for our base plane
  basePlane.fromPoints(origin, xPt, yPt); //define the base plane based on our probed points

  if (testing) {
    ur = new RobCom(this, "UR", "testing"); //comment if connected to the robot (uncomment if not)
  } else {
    //if we are actually connected to the robot, we want to start the class in socket mode...
    ur = new RobCom("UR", "socket");
    ur.connect(ipAddress, port); //connect to the robot
  }

  ur.setWorkObject(basePlane); //set this base plane as our transformation matrix for subsequent movement operations
  ur.setSpeed(speed); //set the speed of the robot movements
  ur.setZone(radius); //set the blend radius for movements
  ur.setAcceleration(acceleration); //set the acceleration for subsequent movements

  Pose p1 = new Pose(); //make a new pose object to store our desired position and orientation of the robot
  //set our pose based on the position we want to be at, and the z axis of our tool, and the direction of the x axis of our tool
  p1.fromTargetAndGuide(new PVector(0, 0, 150), new PVector(0, 0, -1), new PVector(1, 0, 0)); 
  Pose p2 = new Pose(); //make a new pose object to store our desired position and orientation of the robot
  //set our pose based on the position we want to be at, and the z axis of our tool, and the direction of the x axis of our tool
  p2.fromTargetAndGuide(new PVector(50, 100, 200), new PVector(0, 0, -1), new PVector(1, 0, 0)); 

  ur.moveL(p1); //move to p1
  ur.closeGripper(); //close the gripper
  ur.moveL(p2); //move to p2
  ur.waitTime(2);
  ur.moveL(p1);
  ur.openGripper(); //open the gripper
  ur.execute(); //do all the things we just wrote and clear the buffer.
}

void draw() {
}