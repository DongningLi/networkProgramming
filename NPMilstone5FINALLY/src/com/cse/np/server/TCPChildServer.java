package com.cse.np.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.cse.np.asn.Decoder;
import com.cse.np.dao.*;
import com.cse.np.util.*;
import java.util.Timer;
import java.util.TimerTask;

public class TCPChildServer implements Runnable {

	static DatabaseUtl db = new DatabaseUtl();
	static int port;
	static Socket sock;
	ModifyCommand mdfcInstance = new ModifyCommand();
	static int delayTime = 172800;

	public static int getDelayTime() {
		return delayTime;
	}

	public static void setDelayTime(int delayTime) {
		TCPChildServer.delayTime = delayTime;
	}

	static class KillThread extends TimerTask {
		@Override
		public void run() {
			try {
				sock.close();
				System.out.println("Cilent close.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public TCPChildServer(Socket s, int port, DatabaseUtl db, int delayTime) {
		try {
			this.sock = s;
			this.port = port;
			this.db = db;
			this.delayTime = delayTime;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TCPChildServer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String input;
		BufferedReader reader = null;
		PrintWriter writer = null;

		try {
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			writer = new PrintWriter(sock.getOutputStream());

			Timer timer = new Timer();
			timer.schedule(new KillThread(), 500000);

			while (true) {

				input = reader.readLine();
				byte[] inputByte = input.getBytes();
				Decoder decode = new Decoder(inputByte);

				if (input == null || input.isEmpty()) {

					continue;

				} else {

					if (decode.tagVal() == 1 && decode.typeClass() == 1) { 

						GossipCon gcDecoded = new GossipCon().decode(decode);

						processGossip(gcDecoded.getHashedMsg(), gcDecoded.getTimeStamp(), gcDecoded.getMessage(),
								writer);

					} else if (decode.tagVal() == 2) {// Peer Insertion

						PeerCon peerDecoded = new PeerCon().decode(decode);
						updatePeer(peerDecoded.getName(), peerDecoded.getPortNumber() + "", peerDecoded.getIpAddress(),
								writer);

					} else if (decode.tagVal() == 3) {// Returns All Peers

						returnPeers(writer);

					} else if(decode.tagVal() == 4){
						
						Leave lcDecoded = new Leave().decode(decode);
						processLeave(lcDecoded.getName(), writer);
						
					} else{
						
					}
				}

			}
		} catch (Exception e) {

		} finally {
			try {
				reader.close();
				writer.close();
				sock.close();
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}

	// If the gossip already exists, discard it. Otherwise, store it into the
	// database and broadcast it.
	public static void processGossip(String hashedMsg, String timeStamp, String msgSent, PrintWriter writer)
			throws Exception {

		boolean flag = db.ifMsgExist(msgSent);

		if (flag) {
			writer.println(Constant.ERROR_MESSAGE_3);
			writer.flush();

		} else {

			db.saveMsg(hashedMsg, timeStamp, msgSent);

			// send the successful message to client
			writer.println("Save message successfully.");
			writer.flush();

			MsgBrodcastCon msgB = new MsgBrodcastCon(msgSent);
			byte[] gcEncoded = msgB.encode();
			String testSTRING = new String(gcEncoded);

			sendToPeers(testSTRING);
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

				// broatcast the message to group mates.
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
	public static void updatePeer(final String name, final String portNumber, final String IPAddress, PrintWriter writer)
			throws Exception {

		// response message
		writer.println("OK! Peer updated");
		writer.flush();
		
		Timer timer = new Timer(IPAddress);
		timer.schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				
				db.delPeer(name);
				System.out.println("Delete peer not seen for a while.");
				
				// broatcast the message to group mates.
				String msg = "You are deleted from peers.";
				MsgBrodcastCon msgB = new MsgBrodcastCon(msg);
				byte[] gcEncoded = msgB.encode();
				InetAddress group;
				try {
					group = InetAddress.getByName(IPAddress);
					MulticastSocket s = new MulticastSocket();
					DatagramPacket msgBroadcast = new DatagramPacket(gcEncoded, gcEncoded.length, group, Integer.parseInt(portNumber));

					s.send(msgBroadcast);

					s.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Thread.currentThread().stop();
			}

		}, delayTime * 1000);
		
		db.savePeer(name, portNumber, IPAddress);

	}

	// returns all known peers
	public static void returnPeers(PrintWriter writer) throws Exception {

		ArrayList<PeerCon> ls = ModifyCommand.getPeersList(db.getPeersRecords());
		PeersAnswerCon pac = new PeersAnswerCon(ls);
		byte[] peerAnswerEncoded = pac.encode();

		writer.println(new String(peerAnswerEncoded));
		writer.flush();
	}
	
	//delete peer from database and response
	private void processLeave(String name, PrintWriter writer) {

		db.delPeer(name);

		writer.println("delete peer successfully.");
		writer.flush();
		
	}

}
