SOURCE_FILES = \
com/cse/np/util/Constant.java \
com/cse/np/util/DatabaseUtl.java \
com/cse/np/util/GetOpts.java \
com/cse/np/util/ModifyCommand.java \
com/cse/np/sc/ClientSocket.java \
com/cse/np/sc/TCPServerSocket.java \
com/cse/np/sc/UDPServerSocket.java \
com/cse/np/sc/MainFunction.java \

JAVAC = javac 
JFLAGS = -encoding UTF-8

vpath %.class bin
vpath %.java src

build: $(SOURCE_FILES:.java=.class)

# pattern rule
%.class: %.java
	$(JAVAC) -cp .:lib/sqlite-jdbc-3.7.2.jar:lib/java-getopt-1.0.13.jar:bin -d bin $(JFLAGS) $<

clean:
	rm -frv bin/*