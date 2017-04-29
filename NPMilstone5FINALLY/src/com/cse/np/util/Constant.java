package com.cse.np.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Interface Constant: stores constants for database and error messages Date:
 * 
 * @author Dongning Li & Haoge Lin
 * 
 *         Extend code of Anita Devi(2015)
 * 
 */

public interface Constant {

	int PORTNUMBER = 2345;
	String IPAddress = "127.0.0.1";

	String DB_NAME = "NPProject.db";
	String DRIVER_NAME = "org.sqlite.JDBC";
	String GOSSIP_TABLE = "MessageReceived";
	String PEER_TABLE = "PeersCatalog";
	String PEER = "Peer";
	String PEERS = "PeersQuery";
	String GOSSIP = "Gossip";
	String ERROR_MESSAGE_1 = "Invalid input.";
	String ERROR_MESSAGE_2 = "No record found.";
	String ERROR_MESSAGE_3 = "DISCARDED!";
	String LEAVE = "Leave";

}