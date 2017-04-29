package com.cse.np.server;


import java.net.SocketException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import com.cse.np.util.*;

/**
 * The Class UDPServerSocket.
 *
 * When a UDP client connects, start a new thread.
 * 
 */

public class UDPServerSocket implements Runnable {

	static DatabaseUtl db = new DatabaseUtl();
	static int port;
	ModifyCommand mdfcInstance = new ModifyCommand();
	private int delayTime;

	public UDPServerSocket(int port, DatabaseUtl db, int delayTime) throws SocketException {

		this.port = port;
		this.db = db;
		this.delayTime = delayTime;
	}

	public UDPServerSocket() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {

		try {
			
			DatagramSocket serverSocket = new DatagramSocket(port);
			System.out.println("UDP Server Started!");

			//At most 5 clients could connect to UDP
			for(int i = 0; i < 5; i++) {
				
				UDPChildServer childServer = new UDPChildServer(serverSocket, port, db, delayTime);

				Thread childServerThread = new Thread(childServer);
				childServerThread.start();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
