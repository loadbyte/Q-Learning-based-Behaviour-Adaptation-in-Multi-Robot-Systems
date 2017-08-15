
public class Consts {
	public static final int ROT_SPEED = 130;
	public static final int TRAV_SPEED = 300; //travel speed
	public static final int ROT_ANGLE = 200;
	public static final int OBST_MAX = 23;
	public static final int OBST_MIN = 23;
	public static  int OBST_DIST_THRES = 23;
	public static final int TURN_RIGHT = -45;
	public static final int TURN_LEFT = 45;
	public static final int BT = 2, USB = 1; //connection mode
	public static enum CORNER { A, B, C, CH }; //ch is charging center
	public static enum MYCOLOR { RED, GREEN, BLUE,YELLOW, MAGENTA, ORANGE, WHITE, BLACK, PINK, GRAY, LIGHT_GRAY, DARK_GRAY,CYAN };  //custum color id
 

	public static final int GREEN = 1; //colorid from sensor
	public static final int RED = 0;
	public static final int YELLOW = 3;
	public static final int CLAWOPEN = 1; //clam open status 1
	public static final int CLAWCLOSE = 0;
	public static final float WHEEL_DIA = 26f; //wheel diameter
	public static final float TRACK_WIDTH = 120f;
	//public static final float NORTH = 0f;
	public static final int CLAW_ROT = 60; 
	public static float ERROR = 0.0f;
	public static final float LA_CORR = 3.0f;//LOCK_ANGLE_CORRECTION;
	public static float NORTH = 0.0f;
	public static float EAST = 90.0f;
	public static float SOUTH = 180.0f;
	public static float WEST = 270.0f;
	public static float NE = 45.0f;
	public static float SE = 135.0f;
	public static float SW = 225.0f;
	public static float NW = 315.0f;
	
	public static float RELATIVE_ZERO = 0.0f;
	public static float TOTAL_ANGLE = 360.0f;

}
