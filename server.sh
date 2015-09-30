#!/bin/sh

# Runs the server Java application.  You must build the application before this will work, use "mvn package" to do so.
# This application opens up an ephemeral port and waits for a connection (port number assigned is output to console)

# Usage: server.sh [cipher suite name]*
# Where:
# 	[cipher suite name] is the name of zero or more cipher suites to force the client to use.
#
# See the source code Server.java for more informaton.
#
# We always pass the JVM parameter jdk.tls.ephemeralDHKeySize to use a 2048 bit Diffie-Hellman ephemeral public/private key pair for protecting the pre-master secret.

# Set the level of SSL/TLS debugging
debuglevel=ssl

# Build a class path from the built application, along with the SLF4J API and basic implementation
appClassPath=target/SecurityTest-0.0.1-SNAPSHOT.jar:target/dependency/slf4j-api-1.6.1.jar:target/dependency/slf4j-simple-1.6.1.jar 

java -Djavax.net.debug=$debuglevel -Djdk.tls.ephemeralDHKeySize=2048 -cp $appClassPath com.worldpay.fsdemoapp.programs.Server $*
