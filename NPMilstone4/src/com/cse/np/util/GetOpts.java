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

	RunClient cmf = new RunClient();

	public void processCmdInput(String[] args) {
		if (args.length == 0) {
			printUsage();
			System.exit(1);
		}

		String serverName = null;
		String portNumber = null;
		boolean ifTCP = true;
		String messageSent = null;
		String timeStamp = null;

		// well as a server ip and port: '-m "Tom eats Jerry"' and '-t
		// "2017-01-09-16-18-20-001Z"';
		// and -T or -U for TCP or UDP; and '-s "183.116.10.43"'; and port '-p
		// 2334'
		Getopt option = new Getopt("Dongning_Li_Haoge_Lin", args, "m:t:TUs:p:");

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
			default:
				printUsage();
				break;
			}
		}

		if (!serverName.equals(null) && !portNumber.equals(null)) {

			cmf.startClinetServer(messageSent, timeStamp, ifTCP, serverName, Integer.parseInt(portNumber));

		} else {

			String usage_1 = "Port number and server ip address is needed.";
			System.err.println(usage_1);
		}

	}

	private static void printUsage() {

		String usage_1 = "Command process fail.";
		System.err.println(usage_1);
	}

}