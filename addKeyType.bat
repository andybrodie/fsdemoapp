@echo off

rem Creates a new certificate and private key and adds it to the server's keystore using keytool_priv,. 
rem then exports a certificate and stores it in the client's truststore using keytool_pub.bat)

rem Usage: addKeyType <algorithm> <keysize>
rem Where:
rem 	<algorithm> is the algorithm to use to create the key (rsa, dsa or ec).  Defaults to rsa.
rem 	<keysize> is the size of the key, defaults to 2048
rem
rem The key password will always be "keypass" (without quotes).
rem
rem See Java Keytool documentation for more information.

setlocal

if "%1" == "" (
	set keyalg=rsa
) else (
	set keyalg=%1
)

if "%2" == "" (
	set keysize=2048
) else (
	set keysize=%2
)
@echo Creating %keysize% bit key using algorithm %keyalg%
call keytool_priv -genkeypair -keysize %keysize% -alias %keyalg%%keysize% -validity 360 -dname "CN=Test (%keyalg%-%keysize%), O=Worldpay, L=Cambridge, ST=Cambridgeshire, C=UK" -keypass keypass -keyalg %keyalg% 
call keytool_priv -exportcert -alias %keyalg%%keysize% -file %keyalg%%keysize%.cer 
call keytool_pub -importcert -file %keyalg%%keysize%.cer -alias %keyalg%%keysize% -noprompt
del %keyalg%%keysize%.cer
