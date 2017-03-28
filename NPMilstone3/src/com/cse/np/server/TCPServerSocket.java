package com.cse.np.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.cse.np.util.*;

/**
 * The Class TCPServerSocket.
 *
 *
 * 
 */

public class TCPServerSocket implements Runnable {

	static DatabaseUtl db = new DatabaseUtl();
	ServerSocket connectionSocket;
	static int port;
	ModifyCommand mdfcInstance = new ModifyCommand();

	public TCPServerSocket(ServerSocket s, int port, DatabaseUtl db) {
		try {
			this.connectionSocket = s;
			this.port = port;
			this.db = db;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TCPServerSocket() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String input;
		BufferedReader reader = null;
		PrintWriter writer = null;

		try {

			System.out.println("TCP Server Started!");
			Socket sock = connectionSocket.accept();

			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			writer = new PrintWriter(sock.getOutputStream());

			while (true) {

				input = reader.readLine();
				
				if (input == null || input.isEmpty()) {

					continue;
					
				} else {

					
					//modify the input by splitting it to a String[] 
					String[] splits = ModifyCommand.modifyRcvGossip(input);

					if (splits[0].equals("%")) { // cannot send % to server
						writer.println(Constant.ERROR_MESSAGE_1);
						writer.flush();

					} else if (splits[0].equals(Constant.PEER)) {// Peer Insertion

						updatePeer(input, writer);
					} else if (splits[0].equals(Constant.PEERS)) {// Returns All Peers

						returnPeers(writer);

					} else { // default consider as gossip

						processGossip(input, writer);
					}
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

	// If the gossip already exists, discard it. Otherwise, store it into the
	// database and broadcast it.
	public static void processGossip(String input, PrintWriter writer) throws Exception {
		String[] splits = ModifyCommand.modifyRcvGossip(input);

		if (splits.length != 4 || splits[1] == null || splits[2] == null || splits[3] == null) {
			writer.println(Constant.ERROR_MESSAGE_1);
			writer.flush();
		} else {

			boolean flag = db.ifMsgExist(splits[3]);

			if (flag) {
				writer.println(Constant.ERROR_MESSAGE_3);
				writer.flush();

			} else {

				db.saveMsg(splits[1], splits[2], splits[3]);

				//send the successful message to client
				writer.println("Save message successfully.");
				writer.flush();
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

			while (it.hasNext()) {

				Map hm = (Map) it.next();
				int multiPortNumber = Integer.parseInt((String) hm.get("portNumber"));
				dest = (String) hm.get("IPAddress");

				//broatcast the message to group mates.
				InetAddress group = InetAddress.getByName(dest);
				MulticastSocket s = new MulticastSocket();
				DatagramPacket msgBroadcast = new DatagramPacket(msgSent, msgSent.length, group, multiPortNumber);

				s.send(msgBroadcast);

				s.close();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// updates the peer info
	public static void updatePeer(String input, PrintWriter writer) throws Exception {
		String[] splits = ModifyCommand.modifyRecPeerComm(input);
		if (splits.length != 6 || splits[1] == null || splits[3] == null || splits[5] == null) {
			writer.println(Constant.ERROR_MESSAGE_1);
			writer.flush();
		} else {

			//response message
			writer.println("OK! Peer updated");
			writer.flush();

			db.savePeer(splits);
		}

	}

	// returns all known peers
	public static void returnPeers(PrintWriter writer) throws Exception {

		writer.println(ModifyCommand.modifyPeersAllInfo(db.getPeersRecords()));
		writer.flush();
	}

}