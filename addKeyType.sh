#!/bin/sh

# Creates a new certificate and private key and adds it to the server's keystore using keytool_priv,. 
# then exports a certificate and stores it in the client's truststore using keytool_pub.bat)

# Usage: addKeyType <algorithm> <keysize>
# Where:
# 	<algorithm> is the algorithm to use to create the key (rsa, dsa or ec).  Defaults to rsa.
# 	<keysize> is the size of the key, defaults to 2048
#
# The key password will always be "keypass" (without quotes).
#
# See Java Keytool documentation for more information.

# If the first argument isn't specified, set to RSA
if [ -z "$1" ]; then keyalg=rsa; else keyalg="$1"; fi

# If the second argument isn't specified, set to 2048 bit)
if [ -z "$2" ]; then keysize=2048; else keysize="$2"; fi

echo Creating ${keysize} bit key using algorithm "${keyalg}"
cn="CN=Test ($keyalg-$keysize), O=Worldpay, L=Cambridge, ST=Cambridgeshire, C=UK"
./keytool_priv.sh -genkeypair -keysize "${keysize}" -alias "${keyalg}${keysize}" -validity 360 -dname "$cn" -keypass keypass -keyalg "${keyalg}"

# Export the public certificate only from the keystore and put in to a .cer file
./keytool_priv.sh -exportcert -alias "${keyalg}${keysize}" -file "${keyalg}${keysize}".cer 

# Import the public certificate in to the trust store
./keytool_pub.sh -importcert -file "${keyalg}${keysize}".cer -alias "${keyalg}${keysize}" -noprompt

# Tidy up the exported certificate, we don't need it
rm "${keyalg}${keysize}".cer
