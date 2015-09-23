@echo off

rem Delete the existing keystores
del ServerPrivateKeyStore.jks
del ServerPublicCertificateKeyStore.jks

rem Add a key and certificate using DSA, RSA and EC key algorithms
call addKeyType.bat dsa
call addKeyType.bat rsa
call addKeyType.bat ec 571
