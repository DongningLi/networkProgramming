package com.cse.np.asn;

import java.util.ArrayList;

/**
 * This is the class that should be extended by objects that are encoded in
 * arrays.
 * 
 * @author msilaghi
 *
 */
public abstract class ASNObjArrayable {
	public abstract Encoder getEncoder();

	public byte[] encode() {
		// System.out.println("will encode: " +this);
		return getEncoder().getBytes();
	}

	public abstract Object decode(Decoder dec) throws ASN1DecoderFail;

	public Encoder getEncoder(ArrayList<String> dictionary_GIDs) {
		ASN1_Util.printCallPath(
				"getEncoder: you need to implement getEncoder(dictionaries) for objects of type: " + this);
		return getEncoder();
	}

	/**
	 * Must be implemented whenever this object is encoded in a sequence
	 * (array/list)
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public abstract ASNObjArrayable instance() throws CloneNotSupportedException;

	/**
	 * 
	 * @param dictionary_GIDs
	 * @param dependants
	 *            : pass 0 for no dependents (ASNObj.DEPENDANTS_NONE) pass -1
	 *            for DEPENDANTS_ALL. Any positive number is decremented at each
	 *            level.
	 * 
	 *            Other custom schemas can be defined using remaining negative
	 *            numbers.
	 * @return
	 */
	public Encoder getEncoder(ArrayList<String> dictionary_GIDs, int dependants) {
		ASN1_Util.printCallPath(
				"getEncoder: you need to implement getEncoder(dictionaries, dependants) for objects of type: " + this);
		return getEncoder(dictionary_GIDs);
	}
}