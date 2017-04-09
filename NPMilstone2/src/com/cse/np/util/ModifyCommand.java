package com.cse.np.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class ModifyCommand: handles the messages
 * 
 * @author Dongning Li & Haoge Lin
 * 
 */

public class ModifyCommand {

	public String[] modifyMsgRec(String gossip) {

		String[] str = gossip.split("\\:|%");

		return str;
	}

	// returns the msg about all known peers
	public static String modifyPeersAllInfo(List list) {
		String msgSent = "PEERS";
		int peersNumber = list.size();
		msgSent = msgSent + "|" + peersNumber + "|";
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Map hm = (Map) it.next();
			msgSent = msgSent + hm.get("name") + ":PORT=" + hm.get("portNumber") + ":IP=" + hm.get("IPAddress") + "|";
		}
		msgSent = msgSent + "%";
		return msgSent;
	}

	// split the peer msg into parts
	public static String[] modifyRecPeerComm(String peerInfo) {
		String[] str = peerInfo.split("\\:|=|%");
		return str;
	}

	// split the gossip msg into parts
	public static String[] modifyRcvGossip(String gossip) {
		String[] str = gossip.split(":|%");
		return str;
	}

	public String getEncryption(String str) {

		String[] linestrSet = new String[3];

		try {

			Process process = Runtime.getRuntime().exec("chmod a+x ./script/encryptGossip.sh");
			process.waitFor();
			process = Runtime.getRuntime().exec("bash ./script/encryptGossip.sh " + str);
			InputStreamReader ins = new InputStreamReader(process.getInputStream());
			BufferedReader br = new BufferedReader(ins);

			String linestr = null;
			int i = 0;

			while ((linestr = br.readLine()) != null) {

				linestrSet[i] = linestr;
				i++;
			}

			br.close();
			ins.close();
			process.waitFor();

		} catch (Exception e) {

			e.printStackTrace();
		}

		return linestrSet[1];

	}

}