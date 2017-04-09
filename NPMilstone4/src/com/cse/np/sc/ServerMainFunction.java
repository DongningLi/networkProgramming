package com.cse.np.sc;

import java.io.IOException;
import com.cse.np.server.TCPServerSocket;
import com.cse.np.server.UDPServerSocket;
import com.cse.np.util.*;

/**
 * The MainFunction.
 *
 * Call the server and client thread based on input.
 * 
 */

public class ServerMainFunction {

	public static DatabaseUtl dbInstance = new DatabaseUtl();
	public static ModifyCommand modifyCommandInstance = new ModifyCommand();
	static ClientMainFunction clientapp;

	public static void main(String[] args) throws ClassNotFoundException {

		if (args.length == 0) {

			dbInstance.createDb();
			startServer(Constant.PORTNUMBER);

		} else {

			clientapp = new ClientMainFunction(args);
			startClient();
		}

	}

	public static void startServer(int portNumber) {

		try {

//			TCPServerSocket tcpServer = new TCPServerSocket(portNumber, dbInstance);
			UDPServerSocket udpServer = new UDPServerSocket(portNumber, dbInstance);
//			Thread tcpServerThread = new Thread(tcpServer);
			Thread udpServerThread = new Thread(udpServer);
//			tcpServerThread.start();
			udpServerThread.start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void startClient() {

		Thread client = new Thread(clientapp);
		client.start();
	}

}
