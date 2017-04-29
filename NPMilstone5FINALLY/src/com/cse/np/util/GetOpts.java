package com.cse.np.util;

import com.cse.np.sc.RunClient;

import gnu.getopt.Getopt;

/**
 * The Class GetOpts.
 *
 * @author Haoge Lin & Dongning Li
 * 
 *         Extend code of Anita Devi(2015)
 * 
 */

public class GetOpts {

	static RunClient cmf = new RunClient();

	static String serverName = null;
	static String portNumber = null;
	static boolean ifTCP = true;
	static String messageSent = null;
	static String timeStamp = null;
	static String delayTime = null;

	
	public String processCmdInput(String[] args) {
		if (args.length == 0) {
			printUsage();
			System.exit(1);
		}

		Getopt option = new Getopt("Dongning_Li_Haoge_Lin", args, "m:t:TUs:p:D:");

		int c;
		while ((c = option.getopt()) != -1) {
			switch (c) {
			case 'm':
				messageSent = option.getOptarg();
				break;
			case 't':
				timeStamp = option.getOptarg();
				break;
			case 'T':
				break;
			case 'U':
				ifTCP = false;
				break;
			case 's':
				serverName = option.getOptarg();
				break;
			case 'p':
				portNumber = option.getOptarg();
				break;
			case 'D':
				delayTime = option.getOptarg();
				break;
			default:
				printUsage();
				break;
			}
		}

		if ((serverName != null) && (portNumber != null)) {

			startClient();
			
		} else if(!delayTime.equals(null)){

			return delayTime;
			
		}else{
			
			String usage_1 = "Port number and server ip address is needed.";
			System.err.println(usage_1);
		}
		
		return null;

	}
	
	private static void startClient(){
		
		cmf.startClinetServer(messageSent, timeStamp, ifTCP, serverName, Integer.parseInt(portNumber));
	}

	private static void printUsage() {

		String usage_1 = "Command process fail.";
		System.err.println(usage_1);
	}

}