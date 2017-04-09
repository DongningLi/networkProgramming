package com.cse.np.sc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
 * @author Haoge Lin & Dongning Li
 * 
 * Extend code of Anita Devi(2015) & Marius Silaghi(March 2003)
 * 
 */

public class TCPServerSocket implements Runnable {

	static DatabaseUtl db;
	ServerSocket connectionSocket;
	static int port;
	ClientSocket csInstance = new ClientSocket();

	public TCPServerSocket(ServerSocket s, int port, DatabaseUtl db) {
		try {
			connectionSocket = s;
			this.port = port;
			this.db = db;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String input;
		BufferedReader reader = null;
		OutputStreamWriter writer = null;

		try {
			System.out.println("TCP Server Started!");
			Socket sock = connectionSocket.accept();
			for (;;) {
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				writer = new OutputStreamWriter(sock.getOutputStream());
				writer.write("Connected to the Server\n");
				writer.flush();
				input = reader.readLine();
				System.out.println("Input Received:" + input);
				
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
	public static void processGossip(String input, OutputStreamWriter writer) throws Exception {
		String[] splits = ModifyCommand.modifyRcvGossip(input);
		if (splits.length != 4 || splits[1] == null || splits[2] == null || splits[3] == null) {
			writer.write(Constant.ERROR_MESSAGE_1);
			writer.flush();
		} else {
			boolean flag = db.ifMsgExist(splits[3]);
			if (flag) {
				writer.write(Constant.ERROR_MESSAGE_3);
				writer.flush();
			} else {
				writer.write("OK! Msg sent to peers\n");
				writer.flush();
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
	public static void updatePeer(String input, OutputStreamWriter writer) throws Exception {
		String[] splits = ModifyCommand.modifyRecPeerComm(input);
		if (splits.length != 6 || splits[1] == null || splits[3] == null || splits[5] == null) {
			writer.write(Constant.ERROR_MESSAGE_1);
			writer.flush();
		} else {
			writer.write("OK! Peer updated\n");
			writer.flush();
			db.savePeer(splits);
		}

	}

	// returns all known peers
	public static void returnPeers(OutputStreamWriter writer) throws Exception {
		writer.write("OK!\n");
		writer.write(ModifyCommand.modifyPeersAllInfo(db.getPeersRecords()) + "\n");
		writer.flush();
	}

}