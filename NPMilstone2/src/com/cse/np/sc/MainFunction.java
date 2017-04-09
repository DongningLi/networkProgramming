package com.cse.np.sc;

import java.io.IOException;
import java.net.ServerSocket;

import com.cse.np.util.*;

/**
 * The MainFunction.
 *
 * @author Dongning Li & Haoge Lin
 * 
 * Extend code of Anita Devi(2015)
 * 
 */

public class MainFunction {

	public static DatabaseUtl dbInstance = new DatabaseUtl();
	public static ModifyCommand modifyCommandInstance = new ModifyCommand();
	public static int port;

	public static void main(String[] args) throws ClassNotFoundException {

		dbInstance.createDb();

		// calling GetOptInput to process GetOpt input
		GetOpts option = new GetOpts();
		String[] cmds = option.processCmdInput(args);
		
		cmds[0] = "2345";
		
		if (!cmds[0].trim().isEmpty()) {
			port = Integer.parseInt(cmds[0]);
		}else{
			
			port = Integer.parseInt(args[0]);
		}

		startServer(port);
	}

	public static void startServer(int port) {
		try {
			ServerSocket socket = new ServerSocket(port);

			TCPServerSocket tcpServer = new TCPServerSocket(socket, port, dbInstance);
			UDPServerSocket udpServer = new UDPServerSocket(socket, port, dbInstance);
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
