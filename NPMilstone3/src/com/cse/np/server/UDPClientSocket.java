package com.cse.np.server;

import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;

import com.cse.np.util.*;

/**
 * The Class TCPServerSocket.
 *
 * 
 */

public class UDPClientSocket implements Runnable {

	static String messageSent;
	static String timeStamp;
	static String serverName;
	static int port;
	private byte[] receiveData;
	ModifyCommand mdfcInstance = new ModifyCommand();
	UDPServerSocket UDPServer = new UDPServerSocket();

	public UDPClientSocket(String messageSent, String timeStamp, String serverName, int port) throws SocketException {

		this.messageSent = messageSent;
		this.timeStamp = timeStamp;
		this.serverName = serverName;
		this.port = port;
	}

	@Override
	public void run() {

		System.out.println("UDP Client Started!");

		String input;

		InetAddress address;
		BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));

		try {

			String sendToSever;
			address = InetAddress.getByName(serverName);
			DatagramSocket socket = new DatagramSocket(); // 创建套接字

			byte[] sendBuf;
			DatagramPacket sendPacket;
			byte[] recBuf = new byte[1024];
			DatagramPacket recPacket = null;

			if (messageSent != null || timeStamp != null) {

				input = mdfcInstance.modifyInput(messageSent, timeStamp);
				sendBuf = input.getBytes();
				sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
				socket.send(sendPacket);

				recPacket = new DatagramPacket(recBuf, recBuf.length);
				socket.receive(recPacket);
				String recMsg = new String(recBuf, 0, recPacket.getLength());
				System.out.println(recMsg);
			}

			while (true) {

				input = clientInput.readLine();
				String[] splits = ModifyCommand.modifyRcvGossip(input);
				if (splits[0].equals(Constant.PEER) || splits[0].equals(Constant.PEERS)) {
					sendToSever = input;

				} else {

					sendToSever = mdfcInstance.modifyInput(input, Constant.LOCAL_TIME);
				}
				sendBuf = sendToSever.getBytes();
				sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
				socket.send(sendPacket);

				recPacket = new DatagramPacket(recBuf, recBuf.length);
				socket.receive(recPacket);
				String recMsg = new String(recBuf, 0, recPacket.getLength());
				System.out.println(recMsg);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
