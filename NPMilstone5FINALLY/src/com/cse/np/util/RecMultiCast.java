package com.cse.np.util;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;

import com.cse.np.asn.Decoder;
import com.cse.np.dao.MsgBrodcastCon;
import com.cse.np.server.TCPChildServer;

/**
 * The Class RecMultiCast.
 *
 * Wait for the broadcasted message as a witness.
 * 
 */

public class RecMultiCast {
	
	public static void main(String[] args) {
		

		try {

			// don't know why, but 224.0.0.3 can be used while 224.0.0.6 cannot be.
			InetAddress group = InetAddress.getByName("224.0.0.3");
			MulticastSocket client = new MulticastSocket(9999);
			client.joinGroup(group);
			byte[] recData = new byte[1024];

			while (true) {

				DatagramPacket recvPacket = new DatagramPacket(recData, recData.length);
				client.receive(recvPacket);
				String input = new String(recData, 0, recvPacket.getLength());
				Decoder decode = new Decoder(input.getBytes());
				MsgBrodcastCon msg = new MsgBrodcastCon();
				MsgBrodcastCon msgBDecoded = msg.decode(decode);
				input = msgBDecoded.getMessage();
				System.out.println("Get a new messgae from server: " + input);
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
