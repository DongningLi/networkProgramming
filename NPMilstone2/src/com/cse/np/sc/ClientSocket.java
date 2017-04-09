package com.cse.np.sc;

import com.cse.np.util.Constant;
import com.cse.np.util.ModifyCommand;

/**
 * The Class ClientSocket.
 *
 * @author Dongning Li
 * 
 */

public class ClientSocket {

	ModifyCommand mdfcInstance = new ModifyCommand();

	public String modifyInput(String gossip) {

		String encryptionMsg = mdfcInstance.getEncryption(gossip);
		String localT = Constant.LOCAL_TIME;

		String str = "GOSSIP:" + encryptionMsg + ":" + localT + ":" + gossip + "%";

		return str;

	}

}
