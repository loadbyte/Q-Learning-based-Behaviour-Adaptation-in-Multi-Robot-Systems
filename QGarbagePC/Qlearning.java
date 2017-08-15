
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/*import util.Consts;
import util.MyFormatter;*/

 
public class Qlearning {

	/* 
	 * states Open, Red_Ball, Blue_Ball, Yellow_Ball, Corner_A, Corner_B, Corner_C,
	 * Corner_D, Battery, Wall, Object
	 * e.g. 
	 * 		The Goal is to put the color balls in their respective corner
	 * __________
	 * |A|    |B|
	 * |_|    |_|
	 * |        |
	 * |_      _|
	 * |C|    |D|
	 * |_|____|_|
	 * 
	 */

	private final DecimalFormat df;
    private final int[] states;
    private final int[] actions;
    private final int [][] R;
    private double[][][] Q, Qinit;
    private int [][] prevRewards;
    private String[] stateNames, actionNames;
    public GarbageAgentPC agent;
    public Sender sender;
    private static Logger logger;
    private static Logger qlogger;
    private int state;
    private int oldState;
    private static int count = 0;
    private Random randGen;
	
	private long startTime;
	private long duration;
	private final int[] objPriority;
	private int currentPriority = 0;	//State = 0,1,2 ... 0=Yellow high pref to drop
    
    public Qlearning() {

    	df = new DecimalFormat("#.##");
    	
    	states = new int[]{Consts.OPEN, Consts.RED_BALL, Consts.GREEN_BALL, Consts.YELLOW_BALL, Consts.CORNER_A, Consts.CORNER_B,
        		Consts.CORNER_C, Consts.CORNER_E, Consts.BATTERY, Consts.WALL, Consts.OBJECT, Consts.SENSE_RED, Consts.SENSE_GREEN, Consts.SENSE_YELLOW};
    	
    	actions = new int[]{ Consts.GOTO_A, Consts.GOTO_B, Consts.GOTO_C, Consts.GOTO_E, Consts.FORWARD, Consts.BACKWARD,
        		Consts.LEFT, Consts.RIGHT, Consts.GRAB};
    	
    	stateNames = new String[] {"Open", "Red Ball", "Blue Ball", "Yellow Ball", "Corner A", "Corner B", "Corner C", "Corner D", 
    			"Battery", "Wall", "Object", "Sense Red", "Sense Green", "Sense Yellow"};
    	
    	actionNames = new String[] {"GOTO A", "GOTO B", "GOTO C", "GOTO E", "FORWARD", "BACWARD", "LEFT", "RIGHT", "GRAB"};
    	
    	//Q = new double[Consts.STATES_COUNT][Consts.ACTIONS_COUNT]; 	// Q Values
    	prevRewards = new int[Consts.STATES_COUNT][Consts.ACTIONS_COUNT];
		
		Qinit = new double[3][][];
		for(int i=0; i<3; i++){
			Qinit[i] = new double[][] {{-1, -1, -1, -5, 1, 0,	0,	0,	-2},	// Q Value
				 {0, 0, 0, -5, -5, -3 , -5, -5,	-5},
				 {0, 0, 0, -5, -5, -3 , -5, -5,	-5},
				 {0, 0, 0, -5, -5, -3 , -5, -5,	-5},
				 {-5, -5, -5, -5, 3, -5, -3, -3, -2},
				 {-5, -5, -5, -5, 3, -5, -3, -3, -2},
				 {-5, -5, -5, -5, 3, -5, -3, -3, -2},
				 {-5, -5, -5, -5, 3, -5, -3, -3, -2},
				 {-5, -5, -5, 5, -5, -5, -5, -5, -5},
				 {-1, -1, -1, -5, -5, -5, 2, 2, -2},
				 {-5, -5, -5, -5, -5, -5, -5, -5, 5},
				 {-3, -3, -3, -5, -3, 0, -3, -3, 1},
				 {-3, -3, -3, -5, -3, 0, -3, -3, 1},
				 {-3, -3, -3, -5, -3, 0, -3, -3, 1}};
		}
    	Q = Qinit;
    	R = new int[][] {{-1, -1, -1, -5, 1, 0,	0,	0,	-2},	// Reward Lookup
    					 {0, 0, 0, -3, -3, -3 , -3,	-3,	-2},
    					 {0, 0, 0, -3, -3, -3 , -3,	-3,	-2},
    					 {0, 0, 0, -3, -3, -3 , -3,	-3,	-2},
    					 {-5, -5, -5, -5, 3, -5, -3, -3, -2},
    					 {-5, -5, -5, -5, 3, -5, -3, -3, -2},
    					 {-5, -5, -5, -5, 3, -5, -3, -3, -2},
    					 {-5, -5, -5, -5, 3, -5, -3, -3, -2},
    					 {-5, -5, -5, 5, -5, -5, -5, -5, -5},
    					 {-1, -1, -1, -5, -5, -5, 2, 2, -2},
    					 {-5, -5, -5, -5, -5, -5, -5, -5, 5},
					 {-3, -3, -3, -5, -3, 0, -3, -3, 1},
					 {-3, -3, -3, -5, -3, 0, -3, -3, 1},
					 {-3, -3, -3, -5, -3, 0, -3, -3, 1}};
    	
    					 
    	agent = new GarbageAgentPC();
    	sender = new Sender();
    	randGen = new Random();
    	state = Consts.CORNER_E;		//Assumption : Robot is left at the Charging station
    	
    	logger = Logger.getLogger(Qlearning.class.getName());
    	qlogger = Logger.getLogger("Qlog");
		duration = 60 * 10000;
		resetTimer();
		objPriority = new int[] {Consts.YELLOW_BALL, Consts.RED_BALL, Consts.GREEN_BALL};
    	try {
    		
		    Handler fileHandler = new FileHandler("logs/QLearning.log", 5000000, 4);
		    Handler qfileHandler = new FileHandler("logs/Qval.log", 5000000, 4);
		   
		    MyFormatter formatter = new MyFormatter();
		    MyFormatter qformatter = new MyFormatter();
		  
		    //setting custom filter for FileHandler
		    logger.addHandler(fileHandler);
		    qlogger.addHandler(qfileHandler);
		    logger.setUseParentHandlers(false);
		    qlogger.setUseParentHandlers(false);
		    //SimpleFormatter formatter = new SimpleFormatter();
		    fileHandler.setFormatter(formatter);
		    qfileHandler.setFormatter(qformatter);
			logger.log(Level.INFO, "QLearning Initialised");
	    } catch (SecurityException | IOException e) {
	        e.printStackTrace();
	    }
    }
    
  
 
    public void run() {
        /*
         * 1. Set parameter, environment and reward matrix R 
         * 2. Initialize matrix Q as zero matrix 
         * 3. For each episode: Select random initial state 
         * 	  Do while not reach goal state o 
         *   	Select one among all possible actions for the current state o 
         *   	Using this possible action, consider to go to the next state o 
         *   	Get maximum Q value of this next state based on all possible actions o 
         *   	Compute o Set the next state as the current state
         */
 
        // For each episode
        Random rand = new Random();
        int i=0;
        logger.log(Level.INFO, "In RUN");
        while(true) {
        	if(checkTimer() && currentPriority != 2){
				currentPriority ++;
				if( currentPriority == 1){
					for(i=0; i<3; i++)
						setQ(Consts.YELLOW_BALL, i, Q[Consts.PREF_HIGH][Consts.YELLOW_BALL][i]);
				}
				if( currentPriority == 2){
					for(i=0; i<3; i++){
						setQ(Consts.YELLOW_BALL, i, Q[Consts.PREF_MEDIUM][Consts.YELLOW_BALL][i]);
						setQ(Consts.RED_BALL, i, Q[Consts.PREF_MEDIUM][Consts.RED_BALL][i]);
					}
				}
				resetTimer();
			}
			
        	int action = getArgMax(state);
        	
        	if(rand.nextDouble() ==0)//<= Consts.EPSILON)
        	{
        		int rAction = rand.nextInt(Consts.ACTIONS_COUNT);
        		logger.log(Level.INFO, "Random action " + actionNames[rAction]);
        		oldState = state;
        		doAction(rAction);
        	}
        	else {
        		logger.log(Level.INFO, "Q Value action " + actionNames[action]);
        		logger.log(Level.INFO, "Q ["+ stateNames[state] + "]["+ actionNames[action] +"] = "+ Q(state, action) );
        		oldState = state;
        		doAction(action);
        	}
                // Action outcome is set to deterministic in this example
                // Transition probability is 1
        	System.out.println("\nState - "+stateNames[oldState]+" , Action - "+actionNames[action]);
            int nextState;
            if(oldState == state)
            	nextState = getState(oldState); // data structure
            else
            	nextState = state;
            logger.log(Level.INFO, "State changed from" + stateNames[oldState] + " to " + stateNames[nextState]);
            
		    // Using this possible action, consider to go to the next state
            double q = Q(oldState, action);
            double maxQ = getArgMax(nextState);
            int r = R(oldState, action);
         
            
            if(r>0)
            	System.out.println("\nGOT REWARD "  + r + "\n");
            else
            	System.out.println("\nGOT PENALTY " + r + "\n");
            
            //logger.log(Level.INFO, "Got Reward for new state =" + stateNames[state] + " action = " + actionNames[action] + " = " + r);
 
            // Start of Modified code
            
            if(prevRewards[oldState][action] != r)	// Enviornment Change, then reset Q values
            	Q[currentPriority][oldState][action] = Qinit[currentPriority][oldState][action];

            double value = q + Consts.ALPHA * (r + Consts.GAMMA * maxQ - q);
            
            logger.log(Level.INFO, "Value to be Updated Q value = " + value);
            setQ(oldState, action, value);

            logger.log(Level.INFO, "Actual Q value = " + Q[currentPriority][oldState][action] );
          
            // Set the next state as the current state
		    state = nextState;
		    logger.log(Level.INFO, "RUN action: "+action+" nextState: "+nextState+" maxQ: "+maxQ);
		    count++;
		    
		    sendQ(oldState, action, r);  
            prevRewards[oldState][action] = r;
            
		    String text="\n\t";
		    if(count == 10)
		    {
		    	for(i=0; i<Consts.ACTIONS_COUNT; i++)
		    		text = actionNames[i] + "\t";
		    	text += "\n";
		    	for(i=0; i<Consts.STATES_COUNT; i++){
		    		text += stateNames[i] + "\t";
		    		for(int j=0; j< Consts.ACTIONS_COUNT; j++)
		    		{
		    			text += Q[currentPriority][i][j] + "\t";
		    		}
		    		text += "\n";
		    	}
		    	qlogger.log(Level.INFO, text);
		    }
        }
    }

	public void resetTimer(){
		startTime = System.currentTimeMillis();
	}
	
	public boolean checkTimer(){
		long endTime = System.currentTimeMillis();
		return (endTime - startTime)> duration;
	}	
	
	/*
	 * Used to broadcast the Qvalue after each action
	 */
    public void sendQ(int state, int action, int curReward)
    {
		String d = ";";
    	String msg = new String(state + d + action + d + Q[currentPriority][state][action] + d + prevRewards[state][action] + d + curReward + "\0" );
    	
		logger.log(Level.INFO, "Sending msg to all : " + msg);
    	sender.sendData(msg); // send msg to nearby nxt's
    }
    
    /*
     * This method is used to update the Q-value of our robot after
     * receiving the q-value from the neighboring robots 
     */
    
    public void updateQ(String msg)
    {
    	int state=0, action=0, prevR=0, curR=0, i=0;
		double qval=0;
    	// Parse msg to get values
		msg = msg.replaceAll("[^0-9.;-]", "");
    	String[] tokens = msg.split(";");
    	
    	state = Integer.parseInt(tokens[0]);
    	action = Integer.parseInt(tokens[1]);
    	qval = Double.parseDouble(tokens[2]);
    	prevR = Integer.parseInt(tokens[3]);
    	System.out.println("\n----"+tokens[4]+"---\n");
    	curR = Integer.parseInt(tokens[4]);
		 
		if(state == Consts.SENSE_YELLOW){
			currentPriority = Consts.PREF_HIGH;		
			resetTimer();
		}
		if(state == Consts.SENSE_RED) {
			if(currentPriority == Consts.PREF_LOW){
				currentPriority = Consts.PREF_MEDIUM;
				resetTimer();
			}
		}
		
		
		logger.log(Level.INFO, "State : " + stateNames[state]);
		logger.log(Level.INFO, "Action : " + actionNames[action]);    	
		logger.log(Level.INFO, "Previous Reward from other agent : " + prevR);
		logger.log(Level.INFO, "Current Reward from other agent  : " + curR);
		logger.log(Level.INFO, "Qval of agent : " + qval);
		logger.log(Level.INFO, "Qval of us	  : " + Q[currentPriority][state][action]);
		
	if(curR == prevRewards[state][action])	// Case when this robot is already up to date
    	{
			logger.log(Level.INFO, "Our system is in updated state.");
    		Q[currentPriority][state][action] = (Consts.BETA * Q[currentPriority][state][action]) + ((1 - Consts.BETA) * qval);
    	}
    	else if(prevR == prevRewards[state][action])	// Case when this robot is having previous enviornment details.
    	{
			logger.log(Level.INFO, "Our agent is not updated");
    		Q[currentPriority][state][action] = qval;
    	}
    	else
    	{
			logger.log(Level.INFO, "Our agent reaching this state for first time");
    		Q[currentPriority][state][action] = qval;					// Case when this robot has not performed state at all.
    	}
		logger.log(Level.INFO, "Updated Q value : " + Q[currentPriority][state][action]);
    }
    
    /*
     * This method is called by the Q-learning algorithm to execute the corresponding
     * action for each state. This method in turn calls the methods which are implemented
     * in NXJ program, thus invoking the action.
     */
    public void doAction(int action) {
    	logger.log(Level.INFO, "In doAction - "+actionNames[action]+" v: "+action+", "+Consts.LEFT+", "+Consts.RIGHT);
    	
    	try{
	    	switch(action){
	    		case Consts.GOTO_A:
	    			agent.goToCorner('A');
	    			//Implement to wait till the action finishes
	    			state = Consts.CORNER_A;
	    			logger.log(Level.INFO, "action taken - A");
	    			break;
	    		case Consts.GOTO_B:
	    			agent.goToCorner('B');
	    			state = Consts.CORNER_B;
	    			logger.log(Level.INFO, "action taken - B");
	    			break;
	    		case Consts.GOTO_C:
	    			agent.goToCorner('C');
	    			state = Consts.CORNER_C;
	    			logger.log(Level.INFO, "action taken - C");
	    			break;
	    		case Consts.GOTO_E:
	    			agent.goToCorner('E');
	    			state = Consts.CORNER_E;
	    			logger.log(Level.INFO, "action taken - E");
	    			break;
	    		case Consts.FORWARD:
	    			agent.moveForward();
	    			logger.log(Level.INFO, "action taken - forward");
	    			break;
	    		case Consts.BACKWARD:
	    			agent.moveBackward();
	    			logger.log(Level.INFO, "action taken - back");
	    			break;
	    		case Consts.LEFT:
	    			logger.log(Level.INFO, "call action - turnleft");
	    			agent.turnLeft();
	    			logger.log(Level.INFO, "action taken - turnleft");
	    			break;
	    		case Consts.RIGHT:
	    			logger.log(Level.INFO, "call action - turnright");
	    			agent.turnRight();
	    			logger.log(Level.INFO, "action taken - turnright");
	    			break;
	    		case Consts.GRAB:
	    			agent.delay();
	    			agent.pickObject();
	    			logger.log(Level.INFO, "action taken - pick");
	    			break;
	    	}
	    	logger.log(Level.INFO, "finished switch");
    	}catch(IOException ie){
    		logger.log(Level.SEVERE, "NXT Communication Exception");
    		System.out.println("NXT Communication Exception");
    	}
    	logger.log(Level.INFO, "exit doAction");
    }

    /*
     * This method, given a state retrieves the action with maximum Q-value
     */
    
    public int getArgMax(int state) {
    	logger.log(Level.INFO, "In getArgMax, state : "+stateNames[state]);
    	double max;
    	int action;

    	
    	max = Q[currentPriority][state][0];
    	logger.log(Level.INFO, "Q["+currentPriority+"]["+stateNames[state] +"]["+ actionNames[0] +"] = " + Q[currentPriority][state][0]);
    	action = 0;
    	
    	for(int i=1; i < Consts.ACTIONS_COUNT; i++)
    	{
    		if(Q[currentPriority][state][i] > max)
    		{
    			max = Q[currentPriority][state][i];
    			action = i;
    		}
    		else if(Q[currentPriority][state][i] == max)
    		{
    			if(randGen.nextDouble() <= 0.5)
    			{
    				max = Q[currentPriority][state][i];
    				action = i;
    			}
    		}
    		logger.log(Level.INFO, "Q["+currentPriority+"]["+ stateNames[state] + "][" + actionNames[i] + "] = " + Q[currentPriority][state][i]);
    	}
    	logger.log(Level.INFO, "Out getArgMax, action : "+actionNames[action]);
    	return action;
    }
 
    /*
     * This method retrieves the Q-value for given state and action
     */
    public double Q(int s, int a) {
        return Q[currentPriority][s][a];
    }
 
    /*
     * This method sets the Q-value for the given state and action
     */
    public void setQ(int s, int a, double value) {
        Q[currentPriority][s][a] = value;
    }
 
    /*
     * This method retrieves the reward for a state and action, which resembles the reward
     * or penalty given by the environment.
     * Except knowing the garbage object drop location, all the rewards have been hardcoded to make
     * the learning faster.
     * The robot is made to learn where to drop the object, among the three available bins.
     */
    public int R(int s, int a) {
    	logger.log(Level.INFO, "In getR, s:"+stateNames[s]+", a: "+actionNames[a]);
    	int retValue=0;
    	if((s >= Consts.RED_BALL && s <= Consts.YELLOW_BALL) && ( a >=Consts.GOTO_A && a <= Consts.GOTO_C))
    		retValue = checkMatch(s);
    	else
    		retValue = R[s][a];
    	logger.log(Level.INFO, "Out getR, val: "+retValue);
    	return retValue;
    }
    
    /*
     * Helper method for the previous method, to determine whether 
     * we are dropping in the right location or not and reward or penalize 
     * respectively.
     */
    public int checkMatch(int state){
    	logger.log(Level.INFO, "In chkMatch, s -"+state);
    	int reward = -5;
    	try {
			int floorColor = agent.getColorFloor();
								
			logger.log(Level.INFO, "Reward for "+state+" in "+floorColor+" floor.");
			if(floorColor == Consts.MYCOLOR.RED.ordinal()){
				if(state == Consts.RED_BALL)
					reward = 2;
			} else if(floorColor == Consts.MYCOLOR.GREEN.ordinal() || floorColor == Consts.MYCOLOR.BLUE.ordinal()){
				if(state == Consts.GREEN_BALL)
					reward = 2;
				
			} else if (floorColor == Consts.MYCOLOR.MAGENTA.ordinal()){
				if(state == Consts.YELLOW_BALL)
					reward = 2;
			}
			/* New Change */
			switch(currentPriority){
				case Consts.PREF_HIGH:
					if(state == Consts.RED_BALL || state == Consts.GREEN_BALL)
						reward = -800;
					break;
				case Consts.PREF_MEDIUM:
					if(state == Consts.GREEN_BALL)
						reward = -800;
					break;
			}
		} catch (IOException e) {
			System.out.println(e);
		}
    	logger.log(Level.INFO, "@@@@@ Out chkMatch, rew:"+reward);
    	return reward;
    }
    
    /*
     * To get the current state of the robot through the inputs 
     * observed from the environment.
     */
    public int getState(int oldState){
		logger.log(Level.INFO, "In getState, old : "+oldState);
		try {
			//Battery energy High Priority
			if(agent.getEnergy() <= Consts.ENERGY_THRES){
				logger.log(Level.INFO, "return getState engry: "+Consts.BATTERY);
				return Consts.BATTERY;
			}
			if(agent.getDist() <= Consts.DIST_THRES){
				//agent.stop();
				logger.log(Level.INFO, "return getState dist- "+Consts.WALL);
				return Consts.WALL;
			}
			int objectColor = agent.getColorObject();
			logger.log(Level.INFO, " getColorObject: "+Consts.OBJECT);
			if(agent.getClawStatus() == Consts.CLAWOPEN && objectColor != Consts.MYCOLOR.BLACK.ordinal()){
				logger.log(Level.INFO, "return getState object- "+Consts.OBJECT);
				int objColor = agent.getColorObject();
				if(objColor == Consts.MYCOLOR.RED.ordinal()){
					logger.log(Level.INFO, "return getState sense red object- "+Consts.MYCOLOR.RED.ordinal());
					if(currentPriority == Consts.PREF_LOW){
						currentPriority = Consts.PREF_HIGH;
						resetTimer();
					}
					return Consts.SENSE_RED;
				} else if(objColor == Consts.MYCOLOR.GREEN.ordinal() || objColor == Consts.MYCOLOR.BLUE.ordinal()){
					logger.log(Level.INFO, "return getState sense green object- "+Consts.MYCOLOR.GREEN.ordinal());
					return Consts.SENSE_GREEN;					
				} else if (objColor == Consts.MYCOLOR.YELLOW.ordinal()){
					logger.log(Level.INFO, "return getState sense YELLOW object- "+Consts.MYCOLOR.YELLOW.ordinal());
					if(currentPriority == Consts.PREF_MEDIUM || currentPriority == Consts.PREF_LOW){
						currentPriority = Consts.PREF_HIGH;
						resetTimer();
					}
					return Consts.SENSE_YELLOW;
				}

				return Consts.OBJECT;
			}
			if(agent.getClawStatus() == Consts.CLAWOPEN){			// Claw is open
				logger.log(Level.INFO, "return getState open- "+Consts.OPEN);
				return Consts.OPEN;
			}else if(agent.getClawStatus() == Consts.CLAWCLOSE){	// Claw is closed
				logger.log(Level.INFO, "return getState close- "+Consts.CLAWCLOSE);
				int objColor = agent.getColorObject();
				if(objColor == Consts.MYCOLOR.RED.ordinal()){
					logger.log(Level.INFO, "return getState red object- "+Consts.MYCOLOR.RED.ordinal());
					return Consts.RED_BALL;
				} else if(objColor == Consts.MYCOLOR.GREEN.ordinal() || objColor == Consts.MYCOLOR.BLUE.ordinal()){
					logger.log(Level.INFO, "return getState green object- "+Consts.MYCOLOR.GREEN.ordinal());
					return Consts.GREEN_BALL;					
				} else if (objColor == Consts.MYCOLOR.YELLOW.ordinal()){
					logger.log(Level.INFO, "return getState YELLOW object- "+Consts.MYCOLOR.YELLOW.ordinal());
					return Consts.YELLOW_BALL;
				}
			}
			if(oldState >= Consts.CORNER_A && oldState <= Consts.CORNER_E){
				logger.log(Level.INFO, "ret getState, old - "+oldState +" ");
				return oldState;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
    
    public static void main(String[] args) {
        long BEGIN = System.currentTimeMillis();
        int i=0, j=0;

        Qlearning obj = new Qlearning();
 
        ClientConnect[] clients = new ClientConnect[Consts.NO_OF_BOTS];
        for(i=1; i<=Consts.NO_OF_BOTS+1; i++) {
        	if(i==Consts.SELF)
        		continue;
        	try {
				clients[j] = new ClientConnect(Consts.BOT_IP+i, obj);
				clients[j].start();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
        }
        
        obj.run();
 
        long END = System.currentTimeMillis();
        
        System.out.println("Time: " + (END - BEGIN) / 1000.0 + " sec.");
    }
    
}
