package com.cse.np.dao;

import com.cse.np.asn.ASN1DecoderFail;
import com.cse.np.asn.ASNObj;
import com.cse.np.asn.Decoder;
import com.cse.np.asn.Encoder;

public class Leave extends ASNObj {
	
    //Leave ::= [APPLICATION 4] EXPLICIT SEQUENCE {name UTF8String}

	private String name;

	public Leave(String name) {
		
		super();
		this.name = name;
	}

	public Leave() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Encoder getEncoder() {

		Encoder encoder = new Encoder().initSequence();
		encoder.addToSequence(new Encoder(name));

		Encoder newEncoder = new Encoder().initSequence().addToSequence(encoder).setASN1Type(Encoder.CLASS_APPLICATION,
				Encoder.PC_CONSTRUCTED, (byte) 4);

		return newEncoder;
	}

	public Leave decode(Decoder decoder) throws ASN1DecoderFail {

		decoder = decoder.getContent();
		decoder = decoder.getContent();

		name = decoder.getFirstObject(true).getString();

		return this;

	}
	
}
