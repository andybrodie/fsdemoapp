@echo off
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
call keytool_priv -genkeypair -keysize %keysize% -alias %keyalg%%keysize% -validity 360 -dname "CN=Andy Brodie (%keyalg%-%keysize%), OU=Gateway Solution Design, O=Worldpay, L=Cambridge, ST=Cambridgeshire, C=UK" -keypass keypass -keyalg %keyalg% 
call keytool_priv -exportcert -alias %keyalg%%keysize% -file %keyalg%%keysize%.cer 
call keytool_pub -importcert -file %keyalg%%keysize%.cer -alias %keyalg%%keysize% -noprompt
del %keyalg%%keysize%.cer
