#!/bin/sh

# Erases the existing server key store and client trust store and recreates them using all the available key algorithms.

# Delete the existing keystores
rm ServerPrivateKeyStore.jks
rm ServerPublicCertificateKeyStore.jks

# Add a key and certificate using DSA, RSA and EC key algorithms
./addKeyType.sh dsa
./addKeyType.sh rsa
# EC only supports key sizes of 571 bits.
./addKeyType.sh ec 571
