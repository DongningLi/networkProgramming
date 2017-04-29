package com.cse.np.dao;

import com.cse.np.asn.ASN1DecoderFail;
import com.cse.np.asn.ASNObj;
import com.cse.np.asn.Decoder;
import com.cse.np.asn.Encoder;

/**
 * The Class MsgBrodcastCon.
 *
 * Encode and decode the message gonna be broadcasted.
 * 
 */

public class MsgBrodcastCon extends ASNObj {

	private String message;

	public MsgBrodcastCon(String messagea) {

		super();
		this.message = messagea;

	}

	public MsgBrodcastCon() {
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public Encoder getEncoder() {

		Encoder encoder = new Encoder().initSequence().addToSequence(new Encoder(message));

		Encoder newEncoder = new Encoder().initSequence().addToSequence(encoder).setASN1Type(Encoder.CLASS_APPLICATION,
				Encoder.PC_CONSTRUCTED, (byte) 1);

		return newEncoder;
	}

	@Override
	public MsgBrodcastCon decode(Decoder decoder) throws ASN1DecoderFail {

		decoder = decoder.getContent();
		decoder = decoder.getContent();

		message = decoder.getFirstObject(true).getString();

		return this;
	}
}
