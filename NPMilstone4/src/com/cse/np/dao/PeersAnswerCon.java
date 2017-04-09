package com.cse.np.dao;

import java.util.ArrayList;
import com.cse.np.asn.*;

/**
 * The Class PeersAnswerCon.
 *
 * Encode and Decode the peers inforamtion as the answer to PeerQuery
 * 
 */

public class PeersAnswerCon extends ASNObj {

	// PeersAnswer ::= [1] EXPLICIT SEQUENCE OF Peer
	ArrayList<PeerCon> peers;

	public PeersAnswerCon(ArrayList<PeerCon> peersa) {

		super();
		this.peers = peersa;
	}

	public PeersAnswerCon() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<PeerCon> getPeers() {
		return peers;
	}

	public void setPeers(ArrayList<PeerCon> peers) {
		this.peers = peers;
	}

	@Override
	public Encoder getEncoder() {

		Encoder encoder = new Encoder().initSequence().addToSequence(Encoder.getEncoder(peers));

		Encoder newEncoder = new Encoder().initSequence().addToSequence(encoder).setASN1Type((byte) 1);

		return newEncoder;
	}

	@Override
	public PeersAnswerCon decode(Decoder decoder) throws ASN1DecoderFail {

		decoder = decoder.getContent();
		decoder = decoder.getContent();
		peers = decoder.getFirstObject(true).getSequenceOfAL(PeerCon.getType(), new PeerCon());

		return this;
	}

}
