package com.cse.np.server;

import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

public class UDPServerSocket implements Runnable {

	static DatabaseUtl db = new DatabaseUtl();
	static int port;
	static DatagramSocket serverSocket;
	private byte[] receiveData;
	ModifyCommand mdfcInstance = new ModifyCommand();

	public UDPServerSocket(int port, DatabaseUtl db) throws SocketException {
		serverSocket = new DatagramSocket(port);
		this.db = db;

	}

	public UDPServerSocket() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {

		System.out.println("UDP Server Started!");
		receiveData = new byte[1024];

		DatagramPacket receivedPacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			while (true) {
				serverSocket.receive(receivedPacket);
				String input = new String(receiveData, 0, receivedPacket.getLength());
				InetAddress ip = receivedPacket.getAddress();
				int pt = receivedPacket.getPort();

				System.out.println("Input Received:" + input);

				if (input == null || input.isEmpty()) {
					byte[] msg = Constant.ERROR_MESSAGE_1.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, ip, pt);
					serverSocket.send(sendPacket);
					
				} else {
					String[] splits = ModifyCommand.modifyRcvGossip(input);
					if (splits[0] == "%") {// cannot send % to server

						byte[] msg = Constant.ERROR_MESSAGE_1.getBytes();
						DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, ip, pt);
						serverSocket.send(sendPacket);
					} else if (splits[0].equals(Constant.PEER)) {
						updatePeer(input, ip, pt);
					} else if (splits[0].equals(Constant.PEERS)) {

						returnPeers(ip, pt);
					} else { // consider it as gossip message default

						processGossip(input, ip, pt);
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			serverSocket.close();
		}

	}

	// If the gossip already exists, discard it. Otherwise, store it into the
	// database and broadcast it.
	public static void processGossip(String input, InetAddress ip, int port) throws Exception {

		String[] splits = ModifyCommand.modifyRcvGossip(input);
		if (splits.length != 4 || splits[1] == null || splits[2] == null || splits[3] == null) {
			byte[] msg = Constant.ERROR_MESSAGE_1.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, ip, port);
			serverSocket.send(sendPacket);
		} else {

			boolean flag = db.ifMsgExist(splits[3]);
			if (flag) {
				byte[] msg = Constant.ERROR_MESSAGE_3.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, ip, port);
				serverSocket.send(sendPacket);
			} else {

				//response message sent to client
				String msg = "OK! Message saves successful.";
				byte[] msg1 = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(msg1, msg1.length, ip, port);
				serverSocket.send(sendPacket);
				
				//save the message and send to all known peers
				db.saveMsg(splits[1], splits[2], splits[3]);
				sendToPeers(splits[3]);
			}
		}
	}

	// send gossip to all known peers
	public static void sendToPeers(String msg) {

		List list = db.getPeersRecords();

		String dest = null;
		byte[] msgSent;
		try {
			msgSent = msg.getBytes();
			Iterator it = list.iterator();
			String portNumber = null;

			//if there is a peers, send to him.
			while (it.hasNext()) {

				Map hm = (Map) it.next();
				int multiPortNumber = Integer.parseInt((String) hm.get("portNumber"));
				dest = (String) hm.get("IPAddress");

				//send the message
				InetAddress group = InetAddress.getByName(dest);
				MulticastSocket s = new MulticastSocket();
				DatagramPacket msgBroadcast = new DatagramPacket(msgSent, msgSent.length, group, multiPortNumber);

				s.send(msgBroadcast);

				s.close();

			}

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	// updates the peer info
	public static void updatePeer(String input, InetAddress ip, int port) throws Exception {

		String[] splits = ModifyCommand.modifyRecPeerComm(input);
		if (splits.length != 6 || splits[1] == null || splits[3] == null || splits[5] == null) {
			byte[] msg = Constant.ERROR_MESSAGE_1.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, ip, port);
			serverSocket.send(sendPacket);
		} else {
			String msg = "OK! Peer updated";
			byte[] msg1 = msg.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(msg1, msg1.length, ip, port);
			serverSocket.send(sendPacket);
			db.savePeer(splits);
		}
	}

	// returns all known peers
	public static void returnPeers(InetAddress ip, int port) throws Exception {

		String msg = ModifyCommand.modifyPeersAllInfo(db.getPeersRecords());
		byte[] msg1 = msg.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(msg1, msg1.length, ip, port);
		serverSocket.send(sendPacket);
	}

}
