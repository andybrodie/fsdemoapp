@echo off

rem Runs the server Java application.  You must build the application before this will work, use "mvn package" to do so.
rem This application opens up an ephemeral port and waits for a connection (port number assigned is output to console)

rem Usage: server [cipher suite name]*
rem Where:
rem 	[cipher suite name] is the name of zero or more cipher suites to force the client to use.
rem
rem See the source code Server.java for more informaton.
rem
rem We always pass the JVM parameter jdk.tls.ephemeralDHKeySize to use a 2048 bit Diffie-Hellman ephemeral public/private key pair for protecting the pre-master secret.

setlocal

rem Set the level of SSL/TLS debugging
set debuglevel=ssl

rem Build a class path from the built application, along with the SLF4J API and basic implementation
set appClassPath=target\SecurityTest-0.0.1-SNAPSHOT.jar;target\dependency\slf4j-api-1.6.1.jar;target\dependency\slf4j-simple-1.6.1.jar 

java -Djavax.net.debug=%debuglevel% -Djdk.tls.ephemeralDHKeySize=2048 -cp %appClassPath% com.worldpay.fsdemoapp.programs.Server %*
