package com.cse.np.server;

import java.net.SocketException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.cse.np.asn.ASN1DecoderFail;
import com.cse.np.asn.Decoder;
import com.cse.np.dao.GossipCon;
import com.cse.np.dao.Leave;
import com.cse.np.dao.PeerCon;
import com.cse.np.dao.PeersAnswerCon;
import com.cse.np.dao.PeersQueryCon;
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
		
		try {
			
			ProcessBuilder builder = new ProcessBuilder("./scripts/checkLeave.sh");
			Process p = builder.start();
			BufferedReader clientInput1 = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader clientInput2 = new BufferedReader(new InputStreamReader(System.in));
			String sendToSever;
			address = InetAddress.getByName(serverName);
			DatagramSocket socket = new DatagramSocket(); // 创建套接字

			byte[] sendBuf;
			DatagramPacket sendPacket;
			byte[] recBuf = new byte[1024];
			DatagramPacket recPacket = null;

			if (messageSent != null) {

				if (timeStamp == null) {

					timeStamp = mdfcInstance.getLocalTime();
				}

				GossipCon gc = new GossipCon(mdfcInstance.getHahsed(messageSent), timeStamp, messageSent);
				sendBuf = gc.encode();
				sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
				socket.send(sendPacket);

				recPacket = new DatagramPacket(recBuf, recBuf.length);
				socket.receive(recPacket);
				String recMsg = new String(recBuf, 0, recPacket.getLength());
				System.out.println(recMsg);
			}

			while (true) {
				
				while ((input = clientInput1.readLine())!= null) {
					
					String[] splits = ModifyCommand.modifyRcvGossip(input);

					if (splits[0].equals(Constant.PEER)) {

						String[] splits2 = ModifyCommand.modifyRecPeerComm(input);
						String name = splits2[1];
						String portNumber = splits2[3];
						String IPAddress = splits2[5];

						PeerCon pc = new PeerCon(name, Integer.parseInt(portNumber), IPAddress);
						sendBuf = pc.encode();

					} else if (splits[0].equals(Constant.PEERS)) { 

						PeersQueryCon pqc = new PeersQueryCon();
						sendBuf = pqc.encode();

					} else if(splits[0].equals(Constant.LEAVE)){
						
						String[] splits2 = ModifyCommand.modifyRecPeerComm(input);
						String name = splits2[1];
						Leave lc = new Leave(name);
						sendBuf = lc.encode();
						
					} else{

						GossipCon gc = new GossipCon(mdfcInstance.getHahsed(input),mdfcInstance.getLocalTime(), input);
						sendBuf = gc.encode();
					}
					
					sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
					socket.send(sendPacket);

					recPacket = new DatagramPacket(recBuf, recBuf.length);
					socket.receive(recPacket);
					String inputFromServer = new String(recBuf, 0, recPacket.getLength());
					String recMsg = null;
					Decoder decodeaaa = new Decoder(inputFromServer.getBytes());
					
					if (decodeaaa.tagVal() == 1 && decodeaaa.typeClass() == 0) {

						PeersAnswerCon pac = new PeersAnswerCon();
						PeersAnswerCon pacDecoded = pac.decode(decodeaaa);

						String msgSent = "PEERS";
						int peersNumber = pacDecoded.getPeers().size();
						msgSent = msgSent + "|" + peersNumber + "|";

						for (int i = 0; i < pacDecoded.getPeers().size(); i++) {

							String name = pacDecoded.getPeers().get(i).getName();
							String port = pacDecoded.getPeers().get(i).getPortNumber() + "";
							String ipAdress = pacDecoded.getPeers().get(i).getIpAddress();

							msgSent = msgSent + name + ":PORT=" + port + ":IP=" + ipAdress + "|";

						}
						recMsg = msgSent + "%";

					}else{
						
						recMsg = inputFromServer;
					}
					
					System.out.println(recMsg);
					
				}
				
				
				input = clientInput2.readLine();
				String[] splits = ModifyCommand.modifyRcvGossip(input);

				if (splits[0].equals(Constant.PEER)) {

					String[] splits2 = ModifyCommand.modifyRecPeerComm(input);
					String name = splits2[1];
					String portNumber = splits2[3];
					String IPAddress = splits2[5];

					PeerCon pc = new PeerCon(name, Integer.parseInt(portNumber), IPAddress);
					sendBuf = pc.encode();

				} else if (splits[0].equals(Constant.PEERS)) { 

					PeersQueryCon pqc = new PeersQueryCon();
					sendBuf = pqc.encode();

				} else if(splits[0].equals(Constant.LEAVE)){
					
					String[] splits2 = ModifyCommand.modifyRecPeerComm(input);
					String name = splits2[1];
					Leave lc = new Leave(name);
					sendBuf = lc.encode();
					
				} else{

					GossipCon gc = new GossipCon(mdfcInstance.getHahsed(input),mdfcInstance.getLocalTime(), input);
					sendBuf = gc.encode();
				}
				
				sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
				socket.send(sendPacket);

				recPacket = new DatagramPacket(recBuf, recBuf.length);
				socket.receive(recPacket);
				String inputFromServer = new String(recBuf, 0, recPacket.getLength());
				String recMsg = null;
				Decoder decodeaaa = new Decoder(inputFromServer.getBytes());
				
				if (decodeaaa.tagVal() == 1 && decodeaaa.typeClass() == 0) {

					PeersAnswerCon pac = new PeersAnswerCon();
					PeersAnswerCon pacDecoded = pac.decode(decodeaaa);

					String msgSent = "PEERS";
					int peersNumber = pacDecoded.getPeers().size();
					msgSent = msgSent + "|" + peersNumber + "|";

					for (int i = 0; i < pacDecoded.getPeers().size(); i++) {

						String name = pacDecoded.getPeers().get(i).getName();
						String port = pacDecoded.getPeers().get(i).getPortNumber() + "";
						String ipAdress = pacDecoded.getPeers().get(i).getIpAddress();

						msgSent = msgSent + name + ":PORT=" + port + ":IP=" + ipAdress + "|";

					}
					recMsg = msgSent + "%";

				}else{
					
					recMsg = inputFromServer;
				}
				
				System.out.println(recMsg);
				
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ASN1DecoderFail e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
