package com.cse.np.dao;

import com.cse.np.asn.ASN1DecoderFail;
import com.cse.np.asn.ASN1_Util;
import com.cse.np.asn.ASNObj;
import com.cse.np.asn.Decoder;
import com.cse.np.asn.Encoder;

/**
 * The Class GossipCon.
 *
 * Encode and Decode the gossip message.
 * 
 */
public class GossipCon extends ASNObj {

	private String hashedMsg;
	private String timeStamp;
	private String message;

	public GossipCon(String hashedMsga, String timeStampa, String messagea) {
		super();
		this.hashedMsg = hashedMsga;
		this.timeStamp = timeStampa;
		this.message = messagea;
	}

	public GossipCon() {
		// TODO Auto-generated constructor stub
	}

	public String getHashedMsg() {
		return hashedMsg;
	}

	public void setHashedMsg(String hashedMsg) {
		this.hashedMsg = hashedMsg;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public Encoder getEncoder() {

		byte[] hashedMsgByte = hashedMsg.getBytes();

		Encoder encoder = new Encoder().initSequence();
		encoder.addToSequence(new Encoder(hashedMsgByte));
		encoder.addToSequence(new Encoder(ASN1_Util.getCalendar(timeStamp)));
		encoder.addToSequence(new Encoder(message));

		Encoder newEncoder = new Encoder().initSequence().addToSequence(encoder).setASN1Type(Encoder.CLASS_APPLICATION,
				Encoder.PC_CONSTRUCTED, (byte) 1);

		return newEncoder;
	}

	@Override
	public GossipCon decode(Decoder decoder) throws ASN1DecoderFail {

		decoder = decoder.getContent();
		decoder = decoder.getContent();

		hashedMsg = decoder.getFirstObject(true).getString((byte) 4);

		timeStamp = decoder.getFirstObject(true).getGeneralizedTime(Encoder.TAG_GeneralizedTime);
		timeStamp = timeStamp.substring(0, 4) + "-" + timeStamp.substring(4, 6) + "-" + timeStamp.substring(6, 8) + "-"
				+ timeStamp.substring(8, 10) + "-" + timeStamp.substring(10, 12) + "-" + timeStamp.substring(12, 14)
				+ "-" + timeStamp.substring(15, 19);

		message = decoder.getFirstObject(true).getString();

		return this;

	}

}
