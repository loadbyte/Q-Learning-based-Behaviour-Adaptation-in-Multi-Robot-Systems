import java.io.IOException;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import lejos.util.Delay;
/*import util.BTSend;
import util.Consts;
import util.MyFormatter;*/

public class GarbageAgentPC {
	BTSend tonxt;
	String msg = "";
	public  float energy;
	static Logger logger = Logger.getLogger(GarbageAgentPC.class.getName());
	public GarbageAgentPC(){
		tonxt = new BTSend();
		energy = Consts.ENERGY;
		try {
		    Handler fileHandler = new FileHandler("logs/interface.log", 5000000, 4);
		    
		    //setting custom filter for FileHandler
		    logger.addHandler(fileHandler);
		    logger.setUseParentHandlers(false);
		    //SimpleFormatter formatter = new SimpleFormatter();
		    MyFormatter formatter = new MyFormatter();
		    fileHandler.setFormatter(formatter);
			if(!tonxt.NXTconnect(Consts.USB)) {
				System.out.println("failed-nxt");
				logger.log(Level.INFO, "failed-nxt");
				System.exit(1);
			}
	    } catch (SecurityException | IOException e) {
	        e.printStackTrace();
	    }
	}
	public void moveForward() throws IOException {
		//long startTime = System.currentTimeMillis();
		logger.log(Level.INFO, "in move forward");
		msg="!0";
		tonxt.send(tonxt.dos, msg);
		//long entTime = System.currentTimeMillis();
		logger.log(Level.INFO, "finshed call to  forward");
		energy-=Consts.EN_LOW;
	}
	public void moveBackward() throws IOException{
		logger.log(Level.INFO, "in move backward");
		msg="!1";
		tonxt.send(tonxt.dos, msg);
		int val = tonxt.recvI(tonxt.dis);
		logger.log(Level.INFO, "in move backward: "+val);
		energy-=Consts.EN_LOW;
	}
	public void turnRight()throws IOException{
		logger.log(Level.INFO, "in turnRight");
		msg="!2";
		tonxt.send(tonxt.dos, msg);
		int val = tonxt.recvI(tonxt.dis);
		logger.log(Level.INFO, "turnedright: "+val);
	}
	public void turnLeft()throws IOException{
		logger.log(Level.INFO, "in  turnLeft");
		msg="!3";
		tonxt.send(tonxt.dos, msg);
		int val = tonxt.recvI(tonxt.dis);
		logger.log(Level.INFO, "turnedleft: "+val);
	}
	
	public void pickObject()throws IOException{
		logger.log(Level.INFO, "in pickObject");
		msg="!4";
		tonxt.send(tonxt.dos, msg);
		energy-=Consts.EN_MED;
	}
	
	public void dropObject()throws IOException{
		logger.log(Level.INFO, "in dropObject");
		msg="!5";
		tonxt.send(tonxt.dos, msg);
		energy-=Consts.EN_LOW2;
	}
	public void goToCorner(char Loc)throws IOException{
		logger.log(Level.INFO, "in goToCorner: "+Loc);
		System.out.println("goToCorner: "+Loc);
		msg=">"+Loc;
		tonxt.send(tonxt.dos, msg);
		
		char reached = tonxt.recvC(tonxt.dis);
		logger.log(Level.INFO, "reached: "+reached);
		System.out.println("reached:: "+reached);
		energy-=Consts.EN_HIGH;
		if (reached == 'E'){
			energy = Consts.ENERGY;
		}
	}
	
	public float getEnergy()throws IOException{
		logger.log(Level.INFO, "in getEnergy: "+energy);
		
		return energy;
	}
	
	public boolean isObstableFront()throws IOException{
		logger.log(Level.INFO, "in isObstableFront");
		msg="<O";
		tonxt.send(tonxt.dos, msg);
		boolean ob = tonxt.recvB(tonxt.dis);
		logger.log(Level.INFO, "return isObstableFront "+ob);
		return ob;
		//return false;
	}
	
	public int getColorObject()throws IOException{
		logger.log(Level.INFO, "in getColorObject");
		msg="<C";
		tonxt.send(tonxt.dos, msg);
		int col = tonxt.recvI(tonxt.dis);
		logger.log(Level.INFO, "return getColorObject "+col);
		return col;
	}
	
	public int getColorFloor()throws IOException{
		logger.log(Level.INFO, "in getColorFloor");
		msg="<F";
		tonxt.send(tonxt.dos, msg);
		int col = tonxt.recvI(tonxt.dis);
		logger.log(Level.INFO, "return getColorFloor "+col);
		return col;
	}
	public int getColorObjectInt()throws IOException{
		logger.log(Level.INFO, "in getColorObjectInt");
		msg="<c";
		tonxt.send(tonxt.dos, msg);
		int col = tonxt.recvI(tonxt.dis);
		logger.log(Level.INFO, "return getCologetColorObjectIntrObject "+col);
		return col;
	}
	
	public int getColorFloorInt()throws IOException{
		logger.log(Level.INFO, "in getColorFloorInt");
		msg="<f";
		tonxt.send(tonxt.dos, msg);
		int col = tonxt.recvI(tonxt.dis);
		logger.log(Level.INFO, "return getColorFloorInt "+col);
		return col;
	}
	
	public char getCorner()throws IOException{
		logger.log(Level.INFO, "in getCorner");
		System.out.println("in getCorner");
		msg="<R";
		tonxt.send(tonxt.dos, msg);
		char cor = tonxt.recvC(tonxt.dis);
		logger.log(Level.INFO, "return getCorner "+cor);
		System.out.println("return getCorner "+cor);
		return cor;
	}
	
	public int getClawStatus()throws IOException{
		logger.log(Level.INFO, "in getClawStatus");
		msg="<W";
		tonxt.send(tonxt.dos, msg);
		int claw = tonxt.recvI(tonxt.dis);
		logger.log(Level.INFO, "return getClawStatus "+claw);
		//System.out.println("return getClawStatus "+claw);
		return claw;
	}
	public void terminateConn()throws IOException{
		logger.log(Level.INFO, "in terminateConn");
		msg="@";
		tonxt.send(tonxt.dos, msg);
	}
	public void stop()throws IOException{
		logger.log(Level.INFO, "in stop");
		msg="!6";
		tonxt.send(tonxt.dos, msg);
	}
	public void delay()throws IOException{
		logger.log(Level.INFO, "in delay");
		msg="<D";
		int del =0;
		Delay.msDelay(1000);
		//tonxt.send(tonxt.dos, msg);
		//int del = tonxt.recvI(tonxt.dis);
		
		logger.log(Level.INFO, "delay finished"+del);
		//System.out.println("delay:: "+del);
	}
	
	public float getDist()throws IOException {
		logger.log(Level.INFO, "in getDist");
		
		msg="<U";
		tonxt.send(tonxt.dos, msg);
		float dist = tonxt.recvF(tonxt.dis);
		logger.log(Level.INFO, "return getDist "+dist);
		return dist;
		
	}
	
	public float getComp()throws IOException {
		logger.log(Level.INFO, "in getComp");
		
		msg="<M";
		tonxt.send(tonxt.dos, msg);
		float comp = tonxt.recvF(tonxt.dis);
		logger.log(Level.INFO, "return getComp "+comp);
		return comp;
		
	}
	/*
	public static void main(String [] args)  throws Exception {
		
		GarbageAgentPC agent = new GarbageAgentPC();
		
		System.out.println("moving "+ agent.getCorner());
		agent.moveForward();
		while(agent.getDist() >= Consts.THRES_DIS);
		
		agent.turnLeft();
		agent.moveForward();
		/*
		int col = agent.getColorObject();
		while(col == Consts.MYCOLOR.BLACK.ordinal()){
			col = agent.getColorObject();
		}
		agent.delay();
		agent.pickObject();
		System.out.println("object picked");
		agent.goToCorner('A');
		System.out.println("goto finished");
		System.out.println("reached "+ agent.getCorner());
		*/
		//System.out.println("isObstableFront" + agent.isObstableFront());
		//System.out.println("getColorFloor" + agent.getColorFloor());
	//}
	
}
