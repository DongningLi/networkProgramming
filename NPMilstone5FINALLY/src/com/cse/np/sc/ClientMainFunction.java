package com.cse.np.sc;

import com.cse.np.util.GetOpts;

/**
 * The Class ClientMainFunction.
 *
 * Run client thread when neccessary.
 * 
 */

public class ClientMainFunction extends Thread {

	static GetOpts option;
	static String[] args;

	ClientMainFunction(String[] _args) {

		option = new GetOpts();
		args = _args;
	}

	public void run() {

		option.processCmdInput(args);

	}

}
