package com.cse.np.util;

import gnu.getopt.Getopt;

/**
 * The Class GetOpts.
 *
 * @author Haoge Lin & Dongning Li
 * 
 * Extend code of Anita Devi(2015)
 * 
 */

public class GetOpts {

	public String[] processCmdInput(String[] args) {
		if (args.length == 0) {
			printUsage();
			System.exit(1);
		}
		String[] input = new String[2];
		Getopt option = new Getopt("Dongning_Li_Haoge_Lin", args, "p:");
		int c;
		while ((c = option.getopt()) != -1) {
			switch (c) {
			case 'p':
				input[0] = option.getOptarg();// port
				break;
			default:
				printUsage();
				break;
			}
		}
		
		return input;
	}

	private static void printUsage() {
		String usage_1 = "Usage: -p port.";
		System.err.println(usage_1);
	}

}