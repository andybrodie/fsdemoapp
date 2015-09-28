@echo off

rem Performs an action on the server key store, passing all the parameters to the keytool utility.

rem Store password is always "storepass" (without quotes).
rem Store filename is always ServerPrivateKeyStore.jks

keytool -keystore ServerPrivateKeyStore.jks -storepass storepass %* 
