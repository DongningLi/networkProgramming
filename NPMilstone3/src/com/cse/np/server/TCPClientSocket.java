package com.cse.np.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import com.cse.np.util.*;

/**
 * The Class TCPServerSocket.
 *
 * 
 * 
 */

public class TCPClientSocket implements Runnable {

	Socket connectionSocket;
	static String messageSent;
	static String timeStamp;
	static String serverName;
	static int port;
	TCPServerSocket TCPServer = new TCPServerSocket();
	ModifyCommand mdfcInstance = new ModifyCommand();

	public TCPClientSocket(Socket socket, String messageSent, String timeStamp, String serverName, int portNumber) {
		try {
			connectionSocket = socket;
			this.messageSent = messageSent;
			this.timeStamp = timeStamp;
			this.serverName = serverName;
			this.port = portNumber;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String input;
		String inputFromServer;
		BufferedReader reader = null;
		PrintWriter writer = null;
		String sendToSever;

		try {
			System.out.println("TCP Client Started!");

			//get the input from console
			BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));
			reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			writer = new PrintWriter(connectionSocket.getOutputStream());

			//if message conatians with the command line at first
			if (messageSent != null || timeStamp != null) {

				input = mdfcInstance.modifyInput(messageSent, timeStamp);
				writer.println(input);
				writer.flush();

				inputFromServer = reader.readLine();
				System.out.println(inputFromServer);
			}

			for (;;) {

				//get the user input continuously.
				input = clientInput.readLine();

				if (input != null) {

					String[] splits = ModifyCommand.modifyRcvGossip(input);

					if (splits[0].equals(Constant.PEER) || splits[0].equals(Constant.PEERS)) {

						sendToSever = input;

					} else { // default consider as gossip

						sendToSever = mdfcInstance.modifyInput(input, Constant.LOCAL_TIME);
					}

					writer.println(sendToSever);
					writer.flush();

					inputFromServer = reader.readLine();
					System.out.println(inputFromServer);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				writer.close();
				connectionSocket.close();
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}
}