
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.ColorHTSensor;
import lejos.nxt.addon.CompassHTSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;


public class StandardRobot {
	public static ColorHTSensor ballSensor, floorSensor;
	public static CompassHTSensor cps;
	public static UltrasonicSensor us;
	public static NXTRegulatedMotor leftMotor, rightMotor;
	public static NXTRegulatedMotor clawMotor;
	public static int clawStatus;
	public static DifferentialPilot pilot;
	public static int cornerState;
	public static char currCorner = 'M'; //middle of the map
	public static float currentDir = Consts.NORTH;
	public StandardRobot() {
		// instantiate sensors
		try 
		{
			StandardRobot.currCorner = 'E';
			clawStatus = Consts.CLAWOPEN;
			us = new UltrasonicSensor(SensorPort.S2);
			floorSensor = new ColorHTSensor(SensorPort.S4);
			ballSensor = new ColorHTSensor(SensorPort.S3);
			cps = new CompassHTSensor(SensorPort.S1);
			// instantiate motors
			leftMotor = new NXTRegulatedMotor(MotorPort.A);
			rightMotor = new NXTRegulatedMotor(MotorPort.B);
			clawMotor = new NXTRegulatedMotor(MotorPort.C);
			// instantiate Pilot
			pilot = new DifferentialPilot(Consts.WHEEL_DIA, Consts.TRACK_WIDTH, leftMotor, rightMotor, false);
			LCD.drawString("Press Any Key",0,0);
			LCD.drawString("to Calibrate",0,1);
			Button.waitForAnyPress();
			calibrate(); //this method is for calibration of north direction
			currentDir = Consts.NORTH;
			leftMotor.setSpeed(Consts.TRAV_SPEED);
			rightMotor.setSpeed(Consts.TRAV_SPEED);
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void goToAngle(float dir){
		turnToAngle(dir);
		currentDir = dir;
		moveForward();
		while(!isObstableFront());
		stop();
	}
	public void moveForward()   {
		//pilot.forward();
		leftMotor.forward();
		rightMotor.forward();
	}
	public void moveBackward()  {
		/*leftMotor.backward();
		rightMotor.backward();*/
		if(clawStatus == Consts.CLAWCLOSE){
			clawMotor.rotate(Consts.CLAW_ROT);
			clawStatus = Consts.CLAWOPEN;
		}
		stayAway();
		turnToAngle((currentDir+50)%360);
	}
	public void turnRight() {
		stop();
		float angle = getCompassValue();
		angle += 90;
		if(angle > 360)
			angle -= 360;
		turnToAngle(angle);
	}
	public void turnLeft() {
		stop();
		float angle = getCompassValue();
		angle -= 90;
		if(angle < 0)
			angle += 360;
		turnToAngle(angle);
		/*leftMotor.rotateTo(-240, true);
		rightMotor.rotateTo(240);*/
	}
	
	public void pickObject() {
		if(clawStatus == Consts.CLAWOPEN){
			
			clawMotor.rotate(-Consts.CLAW_ROT);
			clawStatus = Consts.CLAWCLOSE;
		} 
	}
	
	public void dropObject(char loc) {
		if(floorSensor.getColorID() == Consts.MYCOLOR.RED.ordinal() 
			||  floorSensor.getColorID() == Consts.MYCOLOR.MAGENTA.ordinal() 
		    || floorSensor.getColorID() == Consts.MYCOLOR.GREEN.ordinal() 
			|| floorSensor.getColorID() == Consts.MYCOLOR.BLUE.ordinal()){
			if(clawStatus == Consts.CLAWCLOSE){
			
			clawMotor.rotate(Consts.CLAW_ROT);
			clawStatus = Consts.CLAWOPEN;
		} 
		stayAway();
		} else if(loc != 'E'){
			goToCorner(loc);
		}
		
	}
	/*
	 *  -----------------------------
		|R (A)					G(B)|
		|			  W				|
		|			  |				|
		|	  S-------|-------N	   0|
		|			  |				|
		|			  E				|
		|Ch						Y(C)|
		-----------------------------
	 */
	public void goToCorner(char loc) {
		switch(loc){
			case 'A':
				Consts.OBST_DIST_THRES = Consts.OBST_MAX;
				goToAngle(Consts.WEST);
				Consts.OBST_DIST_THRES = Consts.OBST_MIN;
				goToAngle(Consts.SOUTH);
				dropObject(loc);
				turnToAngle(Consts.NE);
				StandardRobot.currCorner = 'A';
				break;
			case 'B':
				Consts.OBST_DIST_THRES = Consts.OBST_MAX;
				goToAngle(Consts.NORTH);
				Consts.OBST_DIST_THRES = Consts.OBST_MIN;
				goToAngle(Consts.WEST);
				dropObject(loc);
				turnToAngle(Consts.SE);
				StandardRobot.currCorner = 'B';
				break;
			case 'C':
				Consts.OBST_DIST_THRES = Consts.OBST_MAX;
				goToAngle(Consts.EAST);
				Consts.OBST_DIST_THRES = Consts.OBST_MIN;
				goToAngle(Consts.NORTH);
				dropObject(loc);
				turnToAngle(Consts.SW);
				StandardRobot.currCorner = 'C';
				break;
			case 'E':
				Consts.OBST_DIST_THRES = Consts.OBST_MAX;
				goToAngle(Consts.SOUTH);
				Consts.OBST_DIST_THRES = Consts.OBST_MIN;
				goToAngle(Consts.EAST);
				dropObject(loc);
				turnToAngle(Consts.NW);
				StandardRobot.currCorner = 'E';
				break;
		}
		
		
	}
	
	
	
	public float norm(float angle){
		return angle%360;
	}
	/*
	public float fixAngle(float a, float dir){
		if(a<90 && dir == 270.0f)
			return norm(a+360);
		else
			return norm(a);
	}
	*/
	
	/*
	 *  -----------------------------
		|R (A)					G(B)|
		|			  W				|
		|			  |				|
		|	  S-------|-------N	   0|
		|			  |				|
		|			  E				|
		|Ch						Y(C)|
		-----------------------------
	 */
	public boolean checkBetween(float a, float b, float dir){
		float max = 0.0f, min = 0.0f;
		boolean answer = false;
		max = a >= b ? a : b;
		min = b < a ? b : a;
		answer = dir >= min && dir <= max;
		if(max == a)
			return !answer;
		else
			return answer;
	}

	
	public void turnToAngle(float dir){
		setSpeed(Consts.ROT_SPEED);
		float a = norm(getCompassValue() + Consts.ERROR);
		float angle;
		if(checkBetween(norm(dir), norm(dir+180), a)) {
			leftMotor.backward();
			rightMotor.forward();
			while(true){
				angle = norm(getCompassValue());
				if(angle >= (dir-Consts.LA_CORR) && angle <=(dir+Consts.LA_CORR)){
					stop();
					break;
				}
			}			
		} else {
			leftMotor.forward();
			rightMotor.backward();
			while(true){
				angle = norm(getCompassValue());
				if(angle <= (dir+Consts.LA_CORR) && angle >=(dir-Consts.LA_CORR)){
					stop();
					break;
				}
			}
		}
		currentDir = dir;
		setSpeed(Consts.TRAV_SPEED);
	}
	
	/* this method reads the direction at which robot is faced and corrects it as North direction.
	*/
	public void calibrate(){
		Consts.ERROR = getCompassValue();
		Consts.RELATIVE_ZERO = Consts.ERROR;
		Consts.NORTH = (Consts.NORTH+Consts.ERROR)%360;
		Consts.SOUTH = (Consts.SOUTH+Consts.ERROR)%360;
		Consts.EAST = (Consts.EAST+Consts.ERROR)%360;
		Consts.WEST = (Consts.WEST+Consts.ERROR)%360;
		Consts.NE = (Consts.NE+Consts.ERROR)%360;
		Consts.SE = (Consts.SE+Consts.ERROR)%360;
		Consts.SW = (Consts.SW+Consts.ERROR)%360;
		Consts.NW = (Consts.NW+Consts.ERROR)%360;
	}
	
	public float getEnergy() {
		float en =0.0f;
		return en;
	}
	
	public void setSpeed(int speed){
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
	}
	
	public boolean isObstableFront() {
		float distance = getUltraSonicValue();
		if(distance <= Consts.OBST_DIST_THRES) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getColorObject() {
		int col=ballSensor.getColorID();

		return col;
	}
	
	public int getColorFloor() {
		int col=floorSensor.getColorID();

		return col;
	}
	
	public char getCorner() {
		
		return StandardRobot.currCorner;
	}
	public int getInten(){
		return ballSensor.getRGBNormalized(ballSensor.getColorID());
	}
	
	public int getClawStatus() {
		
		return clawStatus;
	}
	public void stop() {
		leftMotor.stop();
		rightMotor.stop();
	}
	
	public void stayAway(){
		leftMotor.rotate(-Consts.ROT_ANGLE, true);
		rightMotor.rotate(-Consts.ROT_ANGLE);
	}
	
	public int getColorObjectInt() {
		return 0;
	}
	
	public int getColorFloorInt() {
		return 0;
	}
	/*
	 * Get sensors values
	 */
	public float getUltraSonicValue() {
		return us.getRange();
	}
	public int delay(int time){
		Delay.msDelay(time);
		return 0;
	}
	
	public float getCompassValue() {
		return cps.getDegrees();
	}
}
