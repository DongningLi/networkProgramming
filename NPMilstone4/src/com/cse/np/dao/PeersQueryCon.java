package com.cse.np.dao;

import com.cse.np.asn.ASN1DecoderFail;
import com.cse.np.asn.ASNObj;
import com.cse.np.asn.Decoder;
import com.cse.np.asn.Encoder;

/**
 * The Class PeersQueryCon.
 *
 * Encode and decode the Peers Query.
 * 
 */

public class PeersQueryCon extends ASNObj {

	// PeersQuery ::= [APPLICATION 3] IMPLICIT NULL
	private String peerQuery;

	public String getPeerQuery() {
		return peerQuery;
	}

	public void setPeerQuery(String peerQuery) {
		this.peerQuery = peerQuery;
	}

	@Override
	public Encoder getEncoder() {

		Encoder encoder = Encoder.getNullEncoder();
		encoder = encoder.setASN1Type(Encoder.CLASS_APPLICATION, Encoder.PC_PRIMITIVE, (byte) 3);

		return encoder;
	}

	@Override
	public PeersQueryCon decode(Decoder decoder) throws ASN1DecoderFail {

		peerQuery = decoder.getFirstObject(true).getString();

		return this;
	}

}
