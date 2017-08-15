import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Battery;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.USB;
import lejos.nxt.comm.USBConnection;
import lejos.util.Delay;

/*
This class abstracts the robot functionality and provides interface, which can be called from GarbageAgentNXTPC program 
*/
public class GarbageAgentNXT {
	private StandardRobot me;
	
	public GarbageAgentNXT() throws IOException, ClassNotFoundException, InterruptedException{
		me = new StandardRobot();
		
	}
	@SuppressWarnings("deprecation")
	public void run() throws IOException, ClassNotFoundException, InterruptedException{
		char terminate = 0;
		int buttonID;
		
		String connected = "Connected";
        String waiting = "Waiting....";
        String closing = "Closing...";
        DataInputStream dis = null;
 		DataOutputStream dos = null;
 		BTConnection btc = null;
 		USBConnection conn = null;
 		
 		 if(terminate!='@'){
 			LCD.drawString(waiting,0,0);
			LCD.refresh();

			//start| usb connection mode added
			LCD.clear();
			LCD.drawString("Mode of Connect:",0,0);
			LCD.drawString("Left: Bluetooth",0,1);
			LCD.drawString("Right: USB",0,2);
			while(true) {
				
				//buttonID = Button.waitForAnyPress();
				buttonID = Button.ID_RIGHT; // bluetooth default
				if(buttonID == Button.ID_LEFT) {
					LCD.clear();
					LCD.drawString(waiting,0,0);
					btc = Bluetooth.waitForConnection();
					dis = btc.openDataInputStream();
					dos = btc.openDataOutputStream();
					break;
				} else if(buttonID == Button.ID_RIGHT){
					LCD.clear();
					LCD.drawString(waiting,0,0);
					conn = USB.waitForConnection();
					dis = conn.openDataInputStream();
					dos = conn.openDataOutputStream();
					break;
				}
				LCD.drawString("invalid!",0,0);
			}
	        //end | usb connection mode added
			

			LCD.clear();
			LCD.drawString(connected,0,0);
			LCD.refresh();	
			LCD.drawString("Stream Created",0,4);
			/*
			Below part of code is on receving char from stream checks for first char like '!,>,<,@' 
			which are action delimiters and then the code for performing action like 1,2..,A,B,,etc 
			*/
			while(terminate!='@'){ //terminate the loop on @ delimiter
				
				String Msg	=	"";
				char temp	=	0;
				int flag	=	0;
				int i		=	0;
				int res		=	0;
				
				try{
					temp = dis.readChar();
				}
				catch(Exception e)
				{
					LCD.clear();
					LCD.drawString("Disconnected",0,0);
					Thread.sleep(5000);
					flag = 100;
					break;
				}
				if(temp == '@'){
					terminate='@';
					break;
				} else if(temp == '!'){ //on '!' delimiter perform given action
					char opcode = dis.readChar();
					switch(opcode){
					
					case '0':
						me.moveForward();
						break;
						
					case '1':
						me.moveBackward();
						dos.writeInt(1);
						dos.flush();
						break;
						
					case '2':
						me.turnRight();
						dos.writeInt(1);
						dos.flush();
						break;
						
					case '3':
						me.turnLeft();
						dos.writeInt(1);
						dos.flush();
						break;
					case '4':
						me.pickObject();
						break;
					case '5':
						me.dropObject('E');
						break;
					case '6':
						me.stop();;
						break;
					}
					
				}else if(temp == '>'){ //on '>' delimiter perform action and then return value this is blocking call
					char opcode = dis.readChar();
					char cor;
					switch(opcode){
					
					case 'A':
						me.goToCorner('A');
						cor = me.getCorner();
						dos.writeChar(cor);
						dos.flush();
						break;
						
					case 'B':
						me.goToCorner('B');
						cor = me.getCorner();
						dos.writeChar(cor);
						dos.flush();
						break;
						
					case 'C':
						me.goToCorner('C');
						cor = me.getCorner();
						dos.writeChar(cor);
						dos.flush();
						break;
						
					case 'E':
						me.goToCorner('E');
						cor = me.getCorner();
						dos.writeChar(cor);
						dos.flush();
						break;
					
					}
					
				} else if(temp == '<'){ //on '<' delimiter get the request value and send it through stream
					char opcode = dis.readChar();
					switch(opcode){
					
					case 'E':
						float en = me.getEnergy();
						dos.writeFloat(en);
						LCD.drawString("E: "+String.valueOf(en),0,4);
						dos.flush();
						break;
						
					case 'O':
						boolean ob = me.isObstableFront();
						dos.writeBoolean(ob);
						LCD.drawString("O: "+String.valueOf(ob),0,4);
						dos.flush();
						break;
						
					case 'C':
						int col = me.getColorObject();
						dos.writeInt(col);
						LCD.drawString("C: "+String.valueOf(col),0,4);
						dos.flush();
						break;
						
					case 'F':
						int colf = me.getColorFloor();
						dos.writeInt(colf);
						LCD.drawString("F: "+String.valueOf(colf),0,4);
						dos.flush();
						break;
					case 'c':
						int coli = me.getColorObjectInt();
						dos.writeInt(coli);
						LCD.drawString("c: "+String.valueOf(coli),0,4);
						dos.flush();
						break;
						
					case 'f':
						int colfi = me.getColorFloorInt();
						dos.writeInt(colfi);
						dos.flush();
						LCD.drawString("f: "+String.valueOf(colfi),0,4);
						
						break;
					case 'R':
						char cor = me.getCorner();
						dos.writeChar(cor);
						LCD.drawString("R: "+String.valueOf(cor),0,4);
						dos.flush();
						break;
					case 'U':
						float dist = me.getUltraSonicValue();
						dos.writeFloat(dist);
						LCD.drawString("U: "+String.valueOf(dist),0,4);
						dos.flush();
						break;
					case 'M':
						float comp = me.getCompassValue();
						dos.writeFloat(comp);
						LCD.drawString("M: "+String.valueOf(comp),0,4);
						dos.flush();
						break;
					case 'W':
						int claw = me.getClawStatus();
						dos.writeInt(claw);
						LCD.drawString("W: "+String.valueOf(claw),0,4);
						dos.flush();
						break;
					case 'D':
						int val = me.delay(1000);
						dos.writeInt(val);
						LCD.drawString("D: "+String.valueOf(1),0,4);
						dos.flush();
						break;
					}
					
				}
				
			}
		}
 		Button.waitForAnyPress();
 		LCD.clear();
		LCD.drawString(closing,0,5);
		LCD.refresh();
		
	}
	
	public static void main(String[] args) throws Exception {
		GarbageAgentNXT agent = new GarbageAgentNXT();
		agent.run();
		//uncomment the below code for turning the robot in the west , south,north directions on button press
		
		
		
		/*
		while(!Button.ESCAPE.isDown()){
			agent.me.turnToAngle(Consts.WEST);
			//System.out.println(agent.me.getCompassValue());
			LCD.clear();
			LCD.drawString("west:"+agent.me.getCompassValue(),0,2);
			Button.waitForAnyPress();
			agent.me.turnToAngle(Consts.SOUTH);
			//System.out.println(agent.me.getCompassValue());
			LCD.clear();
			LCD.drawString("south:"+agent.me.getCompassValue(),0,2);
			Button.waitForAnyPress();
			agent.me.turnToAngle(Consts.NORTH);
			//System.out.println(agent.me.getCompassValue());
			LCD.clear();
			LCD.drawString("north:"+agent.me.getCompassValue(),0,2);
			Button.waitForAnyPress();
		}
		
		*/
	
	} // end main

}
