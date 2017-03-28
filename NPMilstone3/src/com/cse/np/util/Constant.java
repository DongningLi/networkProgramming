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

	String DB_NAME = "NPMil3.db";
	String DRIVER_NAME = "org.sqlite.JDBC";
	String GOSSIP_TABLE = "MessageReceived";
	String PEER_TABLE = "PeersCatalog";
	String PEER = "PEER";
	String PEERS = "PEERS?\\n";
	String GOSSIP = "GOSSIP";
	String ERROR_MESSAGE_1 = "Invalid input.";
	String ERROR_MESSAGE_2 = "No record found.";
	String ERROR_MESSAGE_3 = "DISCARDED!";

	// constant of local time
	public static final TimeZone timeZone = TimeZone.getTimeZone("UTC");
	public static final Calendar calendar = Calendar.getInstance(timeZone);
	public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS'Z'");
	public static final Date now = new Date();
	public static final String LOCAL_TIME = simpleDateFormat.format(calendar.getTime());

}