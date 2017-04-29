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

public class MainFunction {

	public static DatabaseUtl dbInstance = new DatabaseUtl();
	public static ModifyCommand modifyCommandInstance = new ModifyCommand();
	static ClientMainFunction clientapp;

	public static void main(String[] args) throws ClassNotFoundException {

		if (args.length == 2) {

			dbInstance.createDb();
			GetOpts option = new GetOpts();
			String delayTime = option.processCmdInput(args);
			startServer(Constant.PORTNUMBER, Integer.parseInt(delayTime));

		} else {

			clientapp = new ClientMainFunction(args);
			startClient();
		}

	}

	public static void startServer(int portNumber, int delayTime) {

		try {

			TCPServerSocket tcpServer = new TCPServerSocket(portNumber, dbInstance, delayTime);
			UDPServerSocket udpServer = new UDPServerSocket(portNumber, dbInstance, delayTime);
			Thread tcpServerThread = new Thread(tcpServer);
			Thread udpServerThread = new Thread(udpServer);
			tcpServerThread.start();
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
