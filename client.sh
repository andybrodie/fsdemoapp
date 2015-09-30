#!/bin/sh

# Runs the client Java application.  You must build the application before this will work, use "mvn package" to do so.

# Usage: client <port> [cipher suite name]*
# Where:
# 	<port> is the port to connect to a running server on this host (see Server.bat)
# 	[cipher suite name] is the name of zero or more cipher suites to force the client to use.
#
# See the source code Client.java for more informaton.

# Set the level of SSL/TLS debugging
debuglevel=all

# Build a class path from the built application, along with the SLF4J API and basic implementation
appClassPath=target/SecurityTest-0.0.1-SNAPSHOT.jar:target/dependency/slf4j-api-1.6.1.jar:target/dependency/slf4j-simple-1.6.1.jar

java -Djavax.net.debug=$debuglevel -Djdk.tls.ephemeralDHKeySize=2048 -cp $appClassPath com.worldpay.fsdemoapp.programs.Client $*
