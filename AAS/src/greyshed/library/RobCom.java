package greyshed.lib;

import processing.core.*;

import java.io.PrintWriter;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.io.InputStreamReader;



/**
 * Provide methods for communicating with UR Robots (Ethernet) and ABB Robots
 * (Serial/Ethernet)
 * 
 * @example Hello
 */

public class RobCom {

	// myParent is a reference to the parent sketch
	PApplet myParent;
	private PrintWriter out; //our socket connection
	private BufferedReader in;


	public boolean testingMode = false; // setup two modes so the code can easily run if not connected
	public boolean socketMode = false;
	public PMatrix3D xForm = new PMatrix3D(); // create the identity matrix. If we set a workobject, this will be used to transform coordinate systems
	public float v = 50.0f; // the speed of our tool in mm/s
	public float a = 0.05f; //the acceleration (should confirm units)
	public float zone = 1.0f; // the blend radius of our tool in mm
	public float scaledV = .1f; // the speed of our tool in m/s
	public float scaledZone = .0001f; // the blend radius of our tool in m
	public String urToolString = "p[0.0000,0.0000,0.0000,0.0000,0.0000,0.0000]";
	public String toolString = "";
	public String buffer = ""; //empty message to store the text to send to the robot
	public boolean UR = false; //what is the type of robot we are using
	public boolean ABB = false; //what type of robot are we using

	public String openingLines = ""; //text to add to the beginning of the buffer
	public String closingLines = ""; //text to add to the end of the buffer

	public String urOpeningLines = "def urscript():\n " +  " set_tool_voltage(12)\n"; //start ur script and set our tool voltage
	public String urClosingLines = "end\n"; //end with these lines //consider adding stopj(1)\n or stopl
	public String gripperOpenCommand = " set_tool_digital_out(0, False)\n";
	public String gripperCloseCommand = " set_tool_digital_out(0, True)\n";
			
	
	public String suckCmd = " set_tool_digital_out(0, False)\n" + " set_standard_digital_out(0, True)\n";
	public String releaseSuctionCmd = " set_standard_digital_out(0, False)\n" + " set_tool_digital_out(0, True)\n";
	
	/**
	 *Initialize with robot brand type and communication protocol
	 *@param botType
	 * 			the brand of robot (i.e. "ABB" or "UR")    
	 *@param comType
	 * 			the variety of communication to use.  "testing" for none, "socket" or "serial"
	 */

	public RobCom(String botType, String comType) { //construct in either testing or serial mode, for our brand of robot
		if (comType == "testing") {
			testingMode = true;
			socketMode = false;
		} else if (comType == "socket") {
			testingMode = false;
			socketMode = true;
		}
		if(botType == "UR"){
			UR = true;
			openingLines = urOpeningLines;
			closingLines = urClosingLines;
			toolString = urToolString;
		}
		else if(botType == "ABB"){
			ABB = true;
		}
	}


	/**
	 *Initialize with robot brand type and communication protocol
	 * @param theParent
	 * 			Processing PApplet ("this")
	 *@param botType
	 * 			the brand of robot (i.e. "ABB" or "UR")    
	 *@param comType
	 * 			the variety of communication to use.  "testing" for none, "socket" or "serial"
	 */

	public RobCom(PApplet theParent, String botType, String comType) { //construct in either testing or serial mode, for our brand of robot
		myParent = theParent;
		if (comType == "testing") {
			testingMode = true;
			socketMode = false;
		} else if (comType == "socket") {
			testingMode = false;
			socketMode = true;
		}
		if(botType == "UR"){
			UR = true;
			openingLines = urOpeningLines;
			closingLines = urClosingLines;
			toolString = urToolString;
		}
		else if(botType == "ABB"){
			ABB = true;
		}
	}

	/**
	 *Switch modes after setup
	 *@param comType
	 * 			the variety of communication to use.  "testing" for none, "socket" or "serial"
	 */

	public void setMode(String comType) { //switch modes after initial setup
		if (comType == "testing") {
			testingMode = true;
			socketMode = false;
		} else if (comType == "socket") {
			testingMode = false;
			socketMode = true;
		}
	}

	/**
	 *Switch modes after setup
	 *@param botType
	 * 			the brand of robot (i.e. "ABB" or "UR")    
	 *@param comType
	 * 			the variety of communication to use.  "testing" for none, "socket" or "serial"
	 */

	public void setMode(String botType, String comType) { //switch modes after initial setup
		if (comType == "testing") {
			testingMode = true;
			socketMode = false;
		} else if (comType == "socket") {
			testingMode = false;
			socketMode = true;
		}
		if(botType == "UR"){
			UR = true;
		}
		else if(botType == "ABB"){
			ABB = true;
		}
	}

	/**
	 *connect to robot
	 *@param theIPAddress
	 * 			the ip of the robot   
	 *@param thePort
	 * 			the communication port
	 */

	public void connect(String theIPAddress, int thePort){
		if(socketMode){
			try {			
				Socket clientSocket = new Socket(theIPAddress, thePort);
				this.out = new PrintWriter(clientSocket.getOutputStream(), true);
				this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			}
			catch (UnknownHostException e) {
				System.err.println("Couldn't connect.  Unknown host at " + theIPAddress + " Verify robot IP");
				System.exit(1);
			} catch (IOException e) {
				System.err.println("Couldn't connect.  IOException at " + theIPAddress);
				System.exit(1);
			} 
			System.out.println("Connected to robot on port " + thePort); //verify we have connected
		}
		else if(testingMode){
			System.out.println("Not connecting, in Testing mode");
		}
	}

	/**
	 *Manually add any string to the robot code.  Return character is appended automatically.
	 *@param theText
	 * 			the line of text robot code to add 
	 */

	public void addLine(String theText){
		buffer += theText + "\n";		
	}

	/**
	 *Print the buffer to the console.
	 */
	public void printBuffer(){
		System.out.print(openingLines + buffer + closingLines);
	}

	/**
	 * Get the text from the buffer
	 * @return the buffer text
	 * */
	public String getBuffer(){
		return openingLines + buffer + closingLines;	
	}

	/**
	 *Execute the buffer contents on the robot
	 */
	public void execute(){
		if(!testingMode){
			if(UR){
				this.out.println(openingLines + buffer + closingLines); //send the buffer to the robot
				System.out.println("Sent buffer to robot: ");
				System.out.print(openingLines + buffer + closingLines);
				buffer = "";
				xForm = new PMatrix3D(); // create the identity matrix. If we set a workobject, this will be used to transform coordinate systems
				v = 50.0f; // the speed of our tool in mm/s
				a = 0.05f; //the acceleration (should confirm units)
				zone = 1.0f; // the blend radius of our tool in mm
				scaledV = .1f; // the speed of our tool in m/s
				scaledZone = .0001f; // the blend radius of our tool in m
				urToolString = "p[0.0000,0.0000,0.0000,0.0000,0.0000,0.0000]";
				toolString = urToolString;
				//setTool(toolString);
			}
		} //if not testing
		else{
			System.out.println("Testing mode, did not send the following buffer: ");
			System.out.print(buffer);
			buffer = "";
			//setTool(toolString);
		}
	}

	/**
	 *Set the workobject coordinate system for subsequent moves
	 *@param wObjDat
	 * 			the work object plane defined as a pose 
	 */
	public void setWorkObject(Pose wObjDat) {
		if(UR){
			//The workobject is a local coordinate frame you can define on the robot, then subsequent cartesian moves will be in this coordinate frame. 
			//in this class, we don't actually send this to the robot, but just use it for transformations before we send things to the robot...
			xForm = wObjDat.getMatrix(); //get the transformation matrix of our pose
			float[] matArray = new float[16];
			xForm.get(matArray);
			System.out.println("Set Workobject To:");
			System.out.println(matArray);
		}
	}

	/**
	 *Add a wait/sleep command to the buffer.
	 *@param sec
	 * 			the number of seconds to wait 
	 */
	public void waitTime(float sec){
		if(UR){
			buffer += " sleep(" + String.format("%.2f",sec) + ")\n"; //wait a certain amount of time   
		}
	}

	/**
	 *Set the linear speed for subsequent moving commands
	 *@param velTcp
	 * 			the speed in mm/sec
	 */
	public void setSpeed(float velTcp) { //set speed with only one parameter, mm/sec linear
		v = velTcp;
		scaledV = v/1000.0f;
	}

	/**
	 *Set the zone data (blend radius) for subsequent moving commands
	 *@param pZoneTcp
	 * 			the zone radius in mm
	 */
	public void setZone(float pZoneTcp) {  //set zone with one paramater, blend radius
		zone = pZoneTcp;
		scaledZone = pZoneTcp/1000.0f;
	}

	/**
	 *Set the acceleration for subsequent moving commands
	 *@param pAcceleration
	 * 			the acceleration, in m/sec^2 (in mm/sec^2 according to the UR manual, but seems wrong.  Units to confirm)
	 * 			also note that the manual states that moveJ acceleration values are defined in deg/sec^2, but we don't distinguish types yet
	 * 			be aware of this unit difference if you get strange results with moveJ commands
	 */
	public void setAcceleration(float pAcceleration) {  //set acceleration
		a = pAcceleration;
	}

	/**
	 *add a moveJ command to the buffer using degrees for each axis
	 *@param d1
	 * 			axis 1 degrees
	 *@param d2
	 * 			axis 2 degrees
	 *@param d3
	 * 			axis 3 degrees
	 *@param d4
	 * 			axis 4 degrees
	 *@param d5
	 * 			axis 5 degrees
	 *@param d6
	 * 			axis 6 degrees
	 */
	public void moveJDegrees(float d1, float d2, float d3, float d4, float d5, float d6) {
		String msg = "";
		float r1 = myParent.radians(d1);
		float r2 = myParent.radians(d2);
		float r3 = myParent.radians(d3);
		float r4 = myParent.radians(d4);
		float r5 = myParent.radians(d5);
		float r6 = myParent.radians(d6);
		if(UR){
			//movej([-0.689054, -1.944004, -1.382570, -1.385814, 1.570796, -0.689054],a=0.091000,v=2.780000,r=0.000000)
			msg += " movej([";
			msg += String.format("%.6f,", r1);
			msg += String.format("%.6f,", r2);
			msg += String.format("%.6f,", r3);
			msg += String.format("%.6f,", r4);
			msg += String.format("%.6f,", r5);
			msg += String.format("%.6f],", r6);
			msg += "v=" + String.format("%.3f,", scaledV) + "r=" + String.format("%.5f", scaledZone) +", a="+String.format("%.3f",a)+")\n";
		}
		buffer += msg; //add the message to the buffer
	}

	/**
	 *add a comment to the buffer code (linefeed added automatically)
	 *@param theComment
	 * 			the comment to add to the code
	 */
	public void comment(String theComment){
		if(UR){
			buffer += "#" + theComment + "\n";
		}
	}

	/**
	 *add a moveL command to the buffer
	 *@param fPose
	 * 			the pose to move to
	 */
	public void moveL(Pose fPose) {
		String msg = "";
		if(UR){
			//movel(p[.535,.13,-.395,-1.20,2.90,0.00],v=0.30)\n a sample movel
			//movel(p[0.4666,0.3362,0.2317,1.20,-2.90,0.00],v=0.30,r=0.04212)\n another sample movel
			msg += " movel(" + formatPose(fPose);
			msg += ",v=" + String.format("%.3f,", scaledV);
			msg +=  "r=" + String.format("%.5f", scaledZone);
			msg += ", a="+String.format("%.3f",a)+")\n";
		} //end if UR
		buffer += msg; //add the message to the buffer
	}

	/**
	 *Sets the TCP for subsequent moving commands using a Pose that defines the tooltip in tool space
	 *@param toolDat
	 * 			the pose defining the tool
	 */
	public void setTool(Pose toolDat) {
		//Sets the tool centerpoint (TCP) of the robot. 
		//Offsets are from tool0, which is defined at the intersection of the tool flange center axis and the flange face.
		//Recognize that we are not setting mass data here.  For precise movements, this really should be done.  To be edited later, I assume...
		if(UR){ 
			//set_tcp(p[0.0000,0.0000,0.0575,0.0000,0.0000,0.0000]) //example line
			String msg = "set_tcp(" + formatPose(toolDat) + ")\n";
			toolString = formatPose(toolDat);
			buffer += msg;
		}
	}

	/**
	 *Sets the TCP for subsequent moving commands using a string that defines the TCP location in the robot specific language
	 *@param toolDat
	 * 			the string defining the tool (i.e. "p[0.03459,-0.03827,0.11576,0.0000,0.0000,0.785398]" for UR)
	 */
	public void setTool(String toolDat) {
		//Sets the tool centerpoint (TCP) of the robot. 
		//Offsets are from tool0, which is defined at the intersection of the tool flange center axis and the flange face.
		//Recognize that we are not setting mass data here.  For precise movements, this really should be done.  To be edited later, I assume...
		//set_tcp(p[0.0000,0.0000,0.0575,0.0000,0.0000,0.0000]) //example line
		String msg = "set_tcp(" + toolDat + ")\n";
		toolString = toolDat; //update our privat variable for speed as well
		buffer += msg; //add the tool setting line to the buffer
	}

	/**
	 *Adds a command to close the gripper to the buffer.  If using a different setup, change the command with setGripperCloseCommand("blah blah");
	 */
	public void closeGripper(){
		buffer += gripperCloseCommand;
	}
	
	/**
	 *Adds a command to open the gripper to the buffer.  If using a different setup, change the command with setGripperOpenCommand("blah blah");
	 */
	public void openGripper(){
		buffer += gripperOpenCommand;

	}

	/**
	 *Sets the default gripper opening command to a provided string (i.e. the necessary code in the specific robot language)
	 *linefeed added automatically to the end
	 */
	public void setGripperOpenCommand(String cmd){
		gripperOpenCommand = cmd + "\n";
	}
	
	/**
	 *Sets the default gripper opening command to a provided string (i.e. the necessary code in the specific robot language)
	 *linefeed added automatically to the end
	 */
	public void setGripperCloseCommand(String cmd){
		gripperCloseCommand = cmd + "\n";
	}
	
	
	/**
	 *Adds a command to turn on suction.  For a different setup, change the command with setSuctionOnCommand("blah blah");
	 */
	public void suck(){
		buffer += suckCmd;
	}
	
	/**
	 *Adds a command to release suction.  For a different setup, change the command with setSuctionReleaseCommand("blah blah");
	 */
	public void releaseSuction(){
		buffer += releaseSuctionCmd;
	}
	
	
	/**
	 *Sets the default suction release command to a provided string (i.e. the necessary code in the specific robot language)
	 *linefeed added automatically to the end
	 */
	public void setSuctionReleaseCommand(String cmd){
		releaseSuctionCmd = cmd + "\n";
	}
	
	/**
	 *Sets the default suction on command to a provided string (i.e. the necessary code in the specific robot language)
	 *linefeed added automatically to the end
	 */
	public void setSuctionOnCommand(String cmd){
		suckCmd = cmd + "\n";
	}


	/**
	 *Format a pose as a brand-specific string for using in MoveL commands, etc
	 *@param framePose
	 * 			the pose to format
	 */
	public String formatPose(Pose framePose) {
		String msg  = "";
		if(UR){
			//first we need to move our pose to the coordinate system of our base
			Pose framePose2 = new Pose();
			PVector framePos2 = new PVector(framePose.pos.x, framePose.pos.y, framePose.pos.z);
			Quaternion frameOrient2 = new Quaternion(framePose.orient.qW, framePose.orient.qX, framePose.orient.qY, framePose.orient.qZ);
			framePose2.pos = framePos2;
			framePose2.orient = frameOrient2;
			PMatrix3D frameMatrix = framePose2.getMatrix(); //get the matrix of this pose
			PMatrix3D newMatrix = new PMatrix3D();
			newMatrix.set(xForm);
			newMatrix.apply(frameMatrix);
			//newMatrix = frameMatrix.apply(xForm); //apply our transformation matrix to this pose
			framePose2.fromPMatrix3D(newMatrix); //reset our framePose to this transformed value

			msg += "p[";
			//add the position data to the string
			msg += String.format("%.6f,", framePose2.pos.x/1000.0);
			msg += String.format("%.6f,", framePose2.pos.y/1000.0);
			msg += String.format("%.6f,", framePose2.pos.z/1000.0);
			//add the orientation data to the string
			//but first we have to transform it to axis angle notation
			PVector aaNotation = framePose2.orient.toAxisAngle();
			msg += String.format("%.6f,", aaNotation.x);
			msg += String.format("%.6f,", aaNotation.y);
			msg += String.format("%.6f", aaNotation.z);
			msg += "]" ;
		}
		return msg;
	}//end format pose



} //end robcom class
