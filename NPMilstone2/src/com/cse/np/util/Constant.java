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
 * Extend code of Anita Devi(2015)
 * 
 */

public interface Constant {

	String DB_NAME = "NPMil2.db";
	String DRIVER_NAME = "org.sqlite.JDBC";
	String GOSSIP_TABLE = "MessageReceived";
	String PEER_TABLE = "PeersCatalog";
	String PEER = "PEER";
	String PEERS = "PEERS?\n";
	String GOSSIP = "GOSSIP";
	String ERROR_MESSAGE_1 = "Invalid input\n";
	String ERROR_MESSAGE_2 = "No record found\n";
	String ERROR_MESSAGE_3 = "DISCARDED\n";

	// constant of local time
	public static final TimeZone timeZone = TimeZone.getTimeZone("UTC");
	public static final Calendar calendar = Calendar.getInstance(timeZone);
	public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS'Z'");
	public static final Date now = new Date();
	public static final String LOCAL_TIME = simpleDateFormat.format(calendar.getTime());

}