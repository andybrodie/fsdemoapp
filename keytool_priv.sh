#!/bin/sh

# Performs an action on the server key store, passing all the parameters to the keytool utility.

# Store password is always "storepass" (without quotes).
# Store filename is always ServerPrivateKeyStore.jks

$JAVA_HOME/bin/keytool -keystore ServerPrivateKeyStore.jks -storepass storepass ${1+"$@"}
