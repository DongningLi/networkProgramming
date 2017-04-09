package com.cse.np.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.cse.np.asn.Decoder;
import com.cse.np.dao.GossipCon;
import com.cse.np.dao.PeerCon;
import com.cse.np.dao.PeersAnswerCon;
import com.cse.np.dao.PeersQueryCon;
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
		byte[] sentEncoded;

		try {
			System.out.println("TCP Client Started!");

			// get the input from console
			BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));
			reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			writer = new PrintWriter(connectionSocket.getOutputStream());

			// if message conatians with the command line at first
			if (messageSent != null) {

				if (timeStamp == null) {

					timeStamp = Constant.LOCAL_TIME;
				}

				input = mdfcInstance.modifyInput(messageSent, timeStamp);
				GossipCon gc = new GossipCon(mdfcInstance.getHahsed(messageSent), timeStamp, messageSent);
				sentEncoded = gc.encode();
				writer.println(new String(sentEncoded));
				writer.flush();

				inputFromServer = reader.readLine();
				System.out.println(inputFromServer);
			}

			for (;;) {

				// get the user input continuously.
				input = clientInput.readLine();

				if (input != null) {

					String[] splits = ModifyCommand.modifyRcvGossip(input);

					if (splits[0].equals(Constant.PEER)) {

						String[] splits2 = ModifyCommand.modifyRecPeerComm(input);
						String name = splits2[1];
						String portNumber = splits2[3];
						String IPAddress = splits2[5];

						PeerCon pc = new PeerCon(name, Integer.parseInt(portNumber), IPAddress);
						sentEncoded = pc.encode();

					} else if (splits[0].equals(Constant.PEERS)) { // default
																	// consider
																	// as gossip

						PeersQueryCon pqc = new PeersQueryCon();
						sentEncoded = pqc.encode();

					} else {

						GossipCon gc = new GossipCon(mdfcInstance.getHahsed(input), Constant.LOCAL_TIME, input);
						sentEncoded = gc.encode();
					}

					writer.println(new String(sentEncoded));
					writer.flush();

					inputFromServer = reader.readLine();
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
						inputFromServer = msgSent + "%";

					}

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