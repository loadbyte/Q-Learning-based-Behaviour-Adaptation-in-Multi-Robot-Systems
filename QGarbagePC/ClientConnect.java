import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/*
 * Class to connect to the neighbor robots and receive the packets if any from them.
 * This class is used to receive the Q-value from the neighbor robots and update
 * our Q-value, if required.
 */
public class ClientConnect extends Thread 
{   
	private static String inetAddress;
	private InetAddress address;
	private byte[] buffer;
	public Qlearning obj;

    public ClientConnect(String addr, Qlearning obj) throws UnknownHostException {
    	inetAddress = addr;
    	address = InetAddress.getByName(inetAddress);
    	buffer = new byte[Consts.BUFFER_SIZE];
    	this.obj = obj;
    }
    
    public void run() {
    	try(MulticastSocket clientSocket = new MulticastSocket(Consts.PORT)){
    		
    		clientSocket.joinGroup(address);
    		while(true) {
    			DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
    			clientSocket.receive(msgPacket);
    			String msg = new String(msgPacket.getData(), msgPacket.getOffset(), msgPacket.getLength());
			//System.out.println("Got Msg -"+msg);
    			obj.updateQ(msg);
    		}
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
