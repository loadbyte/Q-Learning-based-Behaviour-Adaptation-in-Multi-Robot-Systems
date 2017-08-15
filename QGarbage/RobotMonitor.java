import java.lang.Thread;
import java.lang.Boolean;
import lejos.nxt.*;

public class RobotMonitor extends Thread {
	private static int delay;
	private static StandardRobot me;

	public RobotMonitor(StandardRobot r, int d) {
		this.setDaemon(true);
		me = r;
		delay = d;
	}

	public void run() {
		while (true) {
			LCD.clear();
			//LCD.drawString("Color = " + me.cs.getColorID(), 0, 0);
			//LCD.drawString("Ultra = " + me.us.getRange(), 0, 1);
			
			//LCD.drawString("MotorA = " + me.ma.getTachoCount(), 0, 3);
			//LCD.drawString("MotorB = " + me.ma.getTachoCount(), 0, 4);
			
			
			try {
				this.sleep(delay);
			} catch (Exception e) {
				;
			}
		} // end while
	} // end run
} // end class
