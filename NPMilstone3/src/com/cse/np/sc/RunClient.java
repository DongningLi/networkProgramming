package com.cse.np.sc;

import java.io.IOException;
import java.net.Socket;

import com.cse.np.server.TCPClientSocket;
import com.cse.np.server.UDPClientSocket;

public class RunClient {

	public static void startClinetServer(String messageSent, String timeStamp, boolean ifTCP, String serverName,
			int portNumber) {

		try {

			if (ifTCP) {

				Socket socketTCP = new Socket(serverName, portNumber);
				TCPClientSocket TCPClient = new TCPClientSocket(socketTCP, messageSent, timeStamp, serverName,
						portNumber);
				Thread tcpServerThread = new Thread(TCPClient);
				tcpServerThread.start();

			} else {

				UDPClientSocket UDPClient = new UDPClientSocket(messageSent, timeStamp, serverName, portNumber);
				Thread udpServerThread = new Thread(UDPClient);
				udpServerThread.start();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
