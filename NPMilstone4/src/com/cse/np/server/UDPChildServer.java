package com.cse.np.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import com.cse.np.asn.Decoder;
import com.cse.np.dao.GossipCon;
import com.cse.np.dao.MsgBrodcastCon;
import com.cse.np.dao.PeerCon;
import com.cse.np.dao.PeersAnswerCon;
import com.cse.np.util.Constant;
import com.cse.np.util.DatabaseUtl;
import com.cse.np.util.ModifyCommand;

public class UDPChildServer implements Runnable {

	static DatabaseUtl db = new DatabaseUtl();
	static int port;
	static String input;
	static DatagramSocket serverSocket;
	private byte[] receiveData;
	ModifyCommand mdfcInstance = new ModifyCommand();

	static class KillThread extends TimerTask {
		@Override
		public void run() {
			serverSocket.close();
			System.out.println("client closed.");
		}
	}

	public UDPChildServer(DatagramSocket s, int port, DatabaseUtl db) throws SocketException {
		
		serverSocket = s;
		this.port = port;
		this.db = db;
	}

	@Override
	public void run() {
		try {
			
			// kill the thread, timeout = 20 seconds
			Timer timer = new Timer();
			timer.schedule(new KillThread(), 20000);
		
			while (true) {
				
				byte[] receiveData = new byte[1024];
				DatagramPacket p = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(p);
				
				String input = new String(p.getData(), 0, p.getLength());
				
				InetAddress ip = p.getAddress();
				int pt = p.getPort();

				Decoder decode = new Decoder(input.getBytes());
				
				if (input == null || input.isEmpty()) {
					byte[] msg = Constant.ERROR_MESSAGE_1.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, ip, pt);
					serverSocket.send(sendPacket);

				} else {

					if (decode.tagVal() == 1 && decode.typeClass() == 1) {

						GossipCon gcDecoded = new GossipCon().decode(decode);

						processGossip(gcDecoded.getHashedMsg(), gcDecoded.getTimeStamp(), gcDecoded.getMessage(), ip,
								pt);

					} else if (decode.tagVal() == 2) {// Peer Insertion

						PeerCon peerDecoded = new PeerCon().decode(decode);
						updatePeer(peerDecoded.getName(), peerDecoded.getPortNumber() + "", peerDecoded.getIpAddress(),
								ip, pt);

					} else if (decode.tagVal() == 3) {// Returns All Peers

						returnPeers(ip, pt);

					} else {

						byte[] msg = Constant.ERROR_MESSAGE_1.getBytes();
						DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, ip, pt);
						serverSocket.send(sendPacket);
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	// If the gossip already exists, discard it. Otherwise, store it into the
	// database and broadcast it.
	public static void processGossip(String hashedMsg, String timeStamp, String msgSent, InetAddress ip, int port)
			throws Exception {

		System.out.println("------");
		boolean flag = db.ifMsgExist(msgSent);
		System.out.println(flag);
		if (flag) {
			
			byte[] msg = Constant.ERROR_MESSAGE_3.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, ip, port);
			serverSocket.send(sendPacket);
		} else {

			// response message sent to client
			String msg = "OK! Message saves successful.";
			byte[] msg1 = msg.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(msg1, msg1.length, ip, port);
			serverSocket.send(sendPacket);

			// save the message and send to all known peers
			db.saveMsg(hashedMsg, timeStamp, msgSent);
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

			// if there is a peers, send to him.
			while (it.hasNext()) {

				Map hm = (Map) it.next();
				int multiPortNumber = Integer.parseInt((String) hm.get("portNumber"));
				dest = (String) hm.get("IPAddress");

				// send the message
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
	public static void updatePeer(String name, String portNumber, String IPAddress, InetAddress ip, int port)
			throws Exception {

		String msg = "OK! Peer updated";
		byte[] msg1 = msg.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(msg1, msg1.length, ip, port);
		serverSocket.send(sendPacket);

		db.savePeer(name, portNumber, IPAddress);

	}

	// returns all known peers
	public static void returnPeers(InetAddress ip, int port) throws Exception {

		ArrayList<PeerCon> ls = ModifyCommand.getPeersList(db.getPeersRecords());
		PeersAnswerCon pac = new PeersAnswerCon(ls);
		byte[] peerAnswerEncoded = pac.encode();

		DatagramPacket sendPacket = new DatagramPacket(peerAnswerEncoded, peerAnswerEncoded.length, ip, port);
		serverSocket.send(sendPacket);
	}

}
