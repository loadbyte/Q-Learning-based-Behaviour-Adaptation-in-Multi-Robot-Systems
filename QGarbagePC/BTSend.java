import lejos.pc.comm.*;
import java.io.*;

 public class BTSend {

	NXTConnector conn = new NXTConnector();
	public DataOutputStream dos;
	public DataInputStream dis;
	//mode of connection
	public boolean NXTconnect(int connMode)  {

		boolean connected = false;
		if(connMode == Consts.BT) {
			// Connect to any NXT over Bluetooth
			connected = conn.connectTo("btspp://");
			System.out.println("bluetooth connected");
		}else if(connMode == Consts.USB){
			// Connect to any NXT over USB
			connected = conn.connectTo("usb://");
			System.out.println("usb connected");
		}
		
		if (!connected) {
			System.err.println("Failed to connect to any NXT ");
			return false;
		}

		dos =  new DataOutputStream(conn.getOutputStream());
		dis =  new DataInputStream(conn.getInputStream());
		return true;
	}

	public void sample(){
		for(int i=0;i<100;i++) {
			try {
				System.out.println("Sending " + (i*30000));
				send(dos,(i*30000));
			} catch (IOException ioe) {
				System.out.println("IO Exception writing bytes:");
				System.out.println(ioe.getMessage());
				break;
			}

			try {
				System.out.println("Received " + recvI(dis));
			} catch (IOException ioe) {
				System.out.println("IO Exception reading bytes:");
				System.out.println(ioe.getMessage());
				break;
			}
		}

		try {
			close(dis,dos,conn);
		} catch (IOException ioe) {
			System.out.println("IOException closing connection:");
			System.out.println(ioe.getMessage());
		}
	}


	public void send(DataOutputStream dos,int Msg) throws IOException{
		dos.writeInt(Msg);
		dos.flush();

	}
	public void send(DataOutputStream dos,String Msg) throws IOException{
		dos.writeChars(Msg);
		dos.flush();

	}
	public  String recv(DataInputStream dis) throws IOException{
		String Msg=dis.readUTF();
		return Msg;
	}
	public  int recvI(DataInputStream dis) throws IOException{
		int Msg=dis.readInt();
		return Msg;
	}
	public  float recvF(DataInputStream dis) throws IOException{
		float Msg=dis.readFloat();
		
		return Msg;
	}
	public boolean recvB(DataInputStream dis) throws IOException{
		boolean Msg=dis.readBoolean();
		
		return Msg;
	}
	public  char recvC(DataInputStream dis) throws IOException{
		char Msg=dis.readChar();
		return Msg;
	}

	public  void close(DataInputStream dis, DataOutputStream dos, NXTConnector conn) throws IOException{
		dis.close();
		dos.close();
		conn.close();
	}
	public void flushIn(){
		try {
			dis.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
