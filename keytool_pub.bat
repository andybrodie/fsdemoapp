@echo off

rem Performs an action on the client trust store, passing all the parameters to the keytool utility.
rem
rem Store password is always "storepass" (without quotes).
rem Store filename is always ServerPublicCertificateKeyStore.jks.

keytool -keystore ServerPublicCertificateKeyStore.jks -storepass storepass %*
