SOURCE_FILES = \
com/cse/np/asn/ASN1_Util.java \
com/cse/np/asn/ASN1DecoderFail.java \
com/cse/np/asn/ASNLenRuntimeException.java \
com/cse/np/asn/ASNObj.java \
com/cse/np/asn/ASNObjArrayable.java \
com/cse/np/asn/Encoder.java \
com/cse/np/asn/Decoder.java \
com/cse/np/dao/GossipCon.java\
com/cse/np/dao/MsgBrodcastCon.java\
com/cse/np/dao/PeerCon.java\
com/cse/np/dao/PeersAnswerCon.java\
com/cse/np/dao/PeersQueryCon.java\
com/cse/np/util/Constant.java \
com/cse/np/util/DatabaseUtl.java \
com/cse/np/util/ModifyCommand.java \
com/cse/np/server/TCPChildServer.java\
com/cse/np/server/UDPChildServer.java\
com/cse/np/server/TCPServerSocket.java \
com/cse/np/server/UDPServerSocket.java \
com/cse/np/server/TCPClientSocket.java \
com/cse/np/server/UDPClientSocket.java \
com/cse/np/sc/RunClient.java \
com/cse/np/util/GetOpts.java \
com/cse/np/sc/MainFunction.java \
com/cse/np/sc/ClientMainFunction.java \
com/cse/np/util/RecMultiCast.java \

JAVAC = javac -sourcepath src/
JFLAGS = -encoding UTF-8

vpath %.class bin
vpath %.java src

build: $(SOURCE_FILES:.java=.class)

# pattern rule
%.class: %.java
	$(JAVAC) -cp .:lib/sqlite-jdbc-3.7.2.jar:lib/java-getopt-1.0.13.jar:bin -d bin $(JFLAGS) $<

clean:
	rm -frv bin/*