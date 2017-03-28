package com.cse.np.util;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class RecMultiCast {

	public static void main(String[] args) {

		try {

			//don't know why, but 224.0.0.3 can be used while 224.0.0.6 cannot be. 
			InetAddress group = InetAddress.getByName("224.0.0.3");
			MulticastSocket client = new MulticastSocket(9999);
			client.joinGroup(group);
			byte[] recData = new byte[1024];

			while (true) {

				DatagramPacket recvPacket = new DatagramPacket(recData, recData.length);
				client.receive(recvPacket);
				String input = new String(recData, 0, recvPacket.getLength());
				System.out.println("Get a new messgae from server: " + input);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
