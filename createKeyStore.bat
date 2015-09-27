@echo off

rem Erases the existing server key store and client trust store and recreates them using all the available key algorithms.

rem Delete the existing keystores
del ServerPrivateKeyStore.jks
del ServerPublicCertificateKeyStore.jks

rem Add a key and certificate using DSA, RSA and EC key algorithms
call addKeyType.bat dsa
call addKeyType.bat rsa

rem EC only supports key sizes of 571 bits.
call addKeyType.bat ec 571
