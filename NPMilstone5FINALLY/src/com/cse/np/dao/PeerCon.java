package com.cse.np.dao;

import com.cse.np.asn.*;

/**
 * The Class PeerCon.
 *
 * Encode and decode the Peer message.
 * 
 */

public class PeerCon extends ASNObj {

	private String name;
	private int portNumber;
	private String ipAddress;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public PeerCon(String namea, int portNumbera, String ipAddressa) {
		super();
		this.name = namea;
		this.portNumber = portNumbera;
		this.ipAddress = ipAddressa;
	}

	public PeerCon() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Encoder getEncoder() {

		Encoder encoder = new Encoder().initSequence();
		encoder.addToSequence(new Encoder(name));
		encoder.addToSequence(new Encoder(portNumber));
		encoder.addToSequence(new Encoder(ipAddress, false));

		encoder = encoder.setASN1Type(Encoder.CLASS_APPLICATION, Encoder.PC_CONSTRUCTED, (byte) 2);

		return encoder;
	}

	@Override
	public PeerCon decode(Decoder decoder) throws ASN1DecoderFail {

		decoder = decoder.getContent();

		name = decoder.getFirstObject(true).getString();
		portNumber = decoder.getFirstObject(true).getInteger().intValue();
		ipAddress = decoder.getFirstObject(true).getString();

		return this;

	}

	public static byte getType() {

		return Encoder.buildASN1byteType(Encoder.CLASS_APPLICATION, Encoder.PC_CONSTRUCTED, (byte) 2);
	}

	@Override
	public ASNObj instance() throws CloneNotSupportedException {

		return new PeerCon();
	}

}
