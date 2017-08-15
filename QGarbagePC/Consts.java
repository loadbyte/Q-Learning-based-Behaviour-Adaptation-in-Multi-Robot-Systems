
public class Consts {
	public static final int DEFAULT_SPEED = 150;
	public static final int THRES_DIS = 16;

	public static final int BT = 2, USB = 1; //connection mode
	//to do need to change to correct value from nxt sensor
	public static final int GREEN = 2;

	public static final int CLAWOPEN = 1;
	public static final int CLAWCLOSE = 0;
	public static enum MYCOLOR { RED, GREEN, BLUE,YELLOW, MAGENTA, ORANGE, WHITE, BLACK, PINK, GRAY, LIGHT_GRAY, DARK_GRAY,CYAN };  //custum color id
	 
	public static float ENERGY = 100.0f;
	public static float EN_LOW = 0.01f;
	public static float EN_LOW2 = 0.4f;
	public static float EN_MED = 4.4f;
	public static float EN_MED2 = 5.4f;
	public static float EN_HIGH = 7.7f;
	public static float EN_HIGH2 = 8.7f;
	
	public final static int NO_OF_BOTS = 1; //Excluding our bot, =>total n+1 bots in arena
	public final static int SELF = 2; 
	public static String BOT_IP = "224.0.0.";
	public final static int PORT = 8888;
	public final static int BUFFER_SIZE = 1024;
	/*public static boolean Q_AVAIL = true;*/
	
	public static final float ENERGY_THRES = 20.0f;
	public static final int DIST_THRES = 16;
	public static final int OBJECT_THRES = 50;
	
	
	public static final double ALPHA = 0.1;
	public static final double GAMMA = 0.9;
	public static final double BETA = 0.7;
	public static final double EPSILON = 0.01;
	
	//The following are different states of the Robot
	public static final int OPEN = 0;
	public static final int RED_BALL = 1;
	public static final int GREEN_BALL = 2;
	public static final int YELLOW_BALL = 3;
	public static final int CORNER_A = 4;
	public static final int CORNER_B = 5;
	public static final int CORNER_C = 6;
	public static final int CORNER_E = 7;
	public static final int BATTERY = 8;
	public static final int WALL = 9;
	public static final int OBJECT = 10;
	public static final int SENSE_RED = 11;
	public static final int SENSE_GREEN = 12;
	public static final int SENSE_YELLOW = 13;
	
	public static final int PREF_HIGH = 0;
	public static final int PREF_MEDIUM = 1;
	public static final int PREF_LOW = 2;
 
	public static final int STATES_COUNT = 14;
	
	//The following are different actions of the Robots
	public static final int GOTO_A = 0;
	public static final int GOTO_B = 1;
	public static final int GOTO_C = 2;
	public static final int GOTO_E = 3;
	public static final int FORWARD = 4;
	public static final int BACKWARD = 5;
	public static final int LEFT = 6;
	public static final int RIGHT = 7;
	public static final int GRAB = 8;
    
	public static final int ACTIONS_COUNT = 9;
}
