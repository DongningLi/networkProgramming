package com.cse.np.sc;

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
 * @author Haoge Lin & Dongning Li
 * 
 * Extend from Marius Silaghi(March 2003)
 * 
 */

public class UDPServerSocket implements Runnable {

	static DatabaseUtl db;
	static int port;
	static DatagramSocket serverSocket;
	private byte[] receiveData;
	ClientSocket csInstance = new ClientSocket();

	public UDPServerSocket(ServerSocket s, int port, DatabaseUtl db) throws SocketException {
		serverSocket = new DatagramSocket(port);
		this.db = db;
	}

	@Override
	public void run() {
		
		System.out.println("UDP Server Started!");
		receiveData = new byte[1024];

		DatagramPacket receivedPacket = new DatagramPacket(receiveData, receiveData.length);

		while (true) {
			try {
				
				serverSocket.receive(receivedPacket);

				String input = new String(receivedPacket.getData());
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
					} else if (splits[0] == Constant.PEER) {// Peer Insertion
						updatePeer(input, ip, pt);
					} else if (splits[0] == Constant.PEERS) {// Returns All
																// Peers
						returnPeers(ip, pt);
					} else { // consider it as gossip message default

						input = csInstance.modifyInput(splits[0]);
						processGossip(input, ip, pt);
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				serverSocket.close();
			}
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
				String msg = "OK! Msg sent to peers\n";
				byte[] msg1 = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(msg1, msg1.length, ip, port);
				serverSocket.send(sendPacket);
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
			msgSent = msg.getBytes("latin1");
			Iterator it = list.iterator();
			String portNumber = null;

			while (it.hasNext()) {

				Map hm = (Map) it.next();
				portNumber = (String) hm.get("portNumber");

				if (portNumber.equals(port + "")) {
					dest = (String) hm.get("IPAdress");

					InetAddress group = InetAddress.getByName(dest);
					MulticastSocket s = new MulticastSocket(port);
					s.joinGroup(group);
					s.setTimeToLive(255);
					DatagramPacket msgBroadcast = new DatagramPacket(msgSent, msgSent.length, group, port);
					s.send(msgBroadcast); // get their responses!
					byte[] buf = new byte[1000];
					DatagramPacket recv = new DatagramPacket(buf, buf.length);
					for (int i = 0; i < 3; i++) {
						s.receive(recv);
						System.out.println(new String(recv.getData()));
					}

					s.leaveGroup(group);
					s.close();

				} else {
					continue;
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
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
			String msg = "OK! Peer updated\n";
			byte[] msg1 = msg.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(msg1, msg1.length, ip, port);
			serverSocket.send(sendPacket);
			db.savePeer(splits);
		}
	}

	// returns all known peers
	public static void returnPeers(InetAddress ip, int port) throws Exception {
		String msg = "OK!\n";
		msg = msg + ModifyCommand.modifyPeersAllInfo(db.getPeersRecords()) + "\n";
		byte[] msg1 = msg.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(msg1, msg1.length, ip, port);
		serverSocket.send(sendPacket);
	}

}
