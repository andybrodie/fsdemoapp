@echo off
setlocal

set debuglevel=ssl

java -Djavax.net.debug=%debuglevel% -Djdk.tls.ephemeralDHKeySize=2048 -cp target\SecurityTest-0.0.1-SNAPSHOT.jar;target\dependency\slf4j-api-1.6.1.jar;target\dependency\slf4j-simple-1.6.1.jar com.worldpay.fsdemoapp.programs.Server %1 %2
