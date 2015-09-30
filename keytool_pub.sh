#!/bin/sh

# Performs an action on the client trust store, passing all the parameters to the keytool utility.
#
# Store password is always "storepass" (without quotes).
# Store filename is always ServerPublicCertificateKeyStore.jks.

$JAVA_HOME/bin/keytool -keystore ServerPublicCertificateKeyStore.jks -storepass storepass ${1+"$@"}
