
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/*import util.Consts;*/

/*
 * This Class is used to send the send packets to multicast address to
 * broadcast the robot's location
 */
public class Sender {

	public static String inetAddress;
	public static InetAddress address;
	
	/*
	 * Constructor where the Multicast IP is initialised. 
	 * IP is defined in the constant file
	 */
	public Sender() {
		inetAddress = Consts.BOT_IP+Consts.SELF;
		try {
			address = InetAddress.getByName(inetAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * This method is used to send the packet to through the 
	 * multicast address
	 */
	public boolean sendData(String data) {
		try (DatagramSocket serverSocket = new DatagramSocket()) {
			
			DatagramPacket msgPacket = new DatagramPacket(data.getBytes(),
					data.getBytes().length, address, Consts.PORT);
			serverSocket.send(msgPacket);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
