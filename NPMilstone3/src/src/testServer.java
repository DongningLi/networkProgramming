package src;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class testServer {

	public static void main(String[] args) throws Exception {

		try {

			MulticastSocket client = new MulticastSocket(9999);
			InetAddress group = InetAddress.getByName("224.0.0.3");
			client.joinGroup(group);
			byte[] recData = new byte[1024];

			while (true) {

				DatagramPacket recvPacket = new DatagramPacket(recData, recData.length);
				client.receive(recvPacket);
				String input = new String(recvPacket.getData(), 0, recvPacket.getLength());
				System.out.println("Get a new messgae from server: " + input);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
