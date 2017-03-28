package com.cse.np.sc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import com.cse.np.server.TCPServerSocket;
import com.cse.np.server.UDPServerSocket;
import com.cse.np.util.*;

/**
 * The MainFunction.
 *
 * 
 * 
 */

public class ServerMainFunction {

	public static DatabaseUtl dbInstance = new DatabaseUtl();
	public static ModifyCommand modifyCommandInstance = new ModifyCommand();

	public static void main(String[] args) throws ClassNotFoundException {

		// create database
		dbInstance.createDb();

		startServer(Constant.PORTNUMBER);

	}

	public static void startServer(int portNumber) {

		try {

			ServerSocket socket = new ServerSocket(portNumber);

			TCPServerSocket tcpServer = new TCPServerSocket(socket, portNumber, dbInstance);
			UDPServerSocket udpServer = new UDPServerSocket(portNumber, dbInstance);
			Thread tcpServerThread = new Thread(tcpServer);
			Thread udpServerThread = new Thread(udpServer);
			tcpServerThread.start();
			udpServerThread.start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
