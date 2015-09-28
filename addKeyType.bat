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

rem If the first argument isn't specified, set to RSA
if "%1" == "" (
	set keyalg=rsa
) else (
	set keyalg=%1
)

rem If the second argument isn't specified, set to 2048 bit)
if "%2" == "" (
	set keysize=2048
) else (
	set keysize=%2
)
@echo Creating %keysize% bit key using algorithm %keyalg%
call keytool_priv -genkeypair -keysize %keysize% -alias %keyalg%%keysize% -validity 360 -dname "CN=Test (%keyalg%-%keysize%), O=Worldpay, L=Cambridge, ST=Cambridgeshire, C=UK" -keypass keypass -keyalg %keyalg% 

rem Export the public certificate only from the keystore and put in to a .cer file
call keytool_priv -exportcert -alias %keyalg%%keysize% -file %keyalg%%keysize%.cer 

rem Import the public certificate in to the trust store
call keytool_pub -importcert -file %keyalg%%keysize%.cer -alias %keyalg%%keysize% -noprompt

rem Tidy up the exported certificate, we don't need it
del %keyalg%%keysize%.cer
