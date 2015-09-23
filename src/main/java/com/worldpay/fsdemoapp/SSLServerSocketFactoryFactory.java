package com.worldpay.fsdemoapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Creates a ready to use {@link SSLServerSocketFactory} instance configured with the public certificate and private key of the server so that the TLS
 * handshake completes successfully.
 * </p>
 * <p>
 * Once an instance has been created, load a set of certificates with {@link #loadServerKeys(String)} before calling {@link #getServerSocketFactory()}
 * </p>
 */
public class SSLServerSocketFactoryFactory {

	private static final Logger LOG = LoggerFactory.getLogger(SSLServerSocketFactoryFactory.class);

	/**
	 * <p>
	 * Load the private keys for the server from a keystore file.
	 * </p>
	 * <p>
	 * Store password must be <c>storepass</c>.
	 * </p>
	 *
	 * @param keyStoreFile the filename of the keystore file.
	 * @return a set of key managers that will contain the server key.
	 * @throws NoSuchAlgorithmException thrown if the default cipher suite is not available.
	 * @throws IOException thrown if there is a problem reading the keystore from the disk.
	 * @throws CertificateException thrown if there is a problem with a certificate used.
	 * @throws KeyStoreException thrown if there is a problem accessing the keystore containing a certificate.
	 * @throws UnrecoverableKeyException thrown if the key could not be recovered from the keystore (usually means password is wrong).
	 */
	public static KeyManager[] loadServerKeys(String keyStoreFile)
					throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		// Key store for your own private key and signing certificates
		try {
			InputStream keyStoreResource = new FileInputStream(keyStoreFile);
			char[] keyStorePassphrase = "storepass".toCharArray();
			String defaultKeyStoreType = KeyStore.getDefaultType();
			LOG.debug("Default keystore type is {}", defaultKeyStoreType);
			KeyStore ksKeys = KeyStore.getInstance(defaultKeyStoreType);
			ksKeys.load(keyStoreResource, keyStorePassphrase);

			// KeyManager decides which key material to use.

			String defaultKeyManagerFactoryAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
			LOG.info(defaultKeyManagerFactoryAlgorithm);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(defaultKeyManagerFactoryAlgorithm);
			kmf.init(ksKeys, "keypass".toCharArray());

			return kmf.getKeyManagers();
		} catch (FileNotFoundException fnfe) {
			// If they keystore doesn't exist, then return empty so we can try out what works with NO keystore
			LOG.error("Unable to find store file {}", keyStoreFile);
			return new KeyManager[0];
		}
	}

	/**
	 * <p>
	 * Set up the server socket factory. This needs:
	 * </p>
	 * <ol>
	 * <li>To limit the acceptable cipher suites to only those supporting FS.</li>
	 * <li>Configure the server certificate that will be presented to each client.</li>
	 * </ol>
	 * <p>
	 * We assume other defaults are "sensible" (i.e. disable insecure protocols and cipher suites.
	 * </p>
	 *
	 * @throws NoSuchAlgorithmException thrown if the default cipher suite is not available.
	 * @throws IOException thrown if there is a problem reading the keystore from the disk.
	 * @throws CertificateException thrown if there is a problem with a certificate used.
	 * @throws KeyStoreException thrown if there is a problem accessing the keystore containing a certificate.
	 * @throws KeyManagementException thrown if there is an issue using the selected key.
	 * @throws UnrecoverableKeyException thrown if the key could not be recovered from the keystore (usually means password is wrong).
	 */

	public SSLServerSocketFactory getServerSocketFactory() throws NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException,
					KeyStoreException, CertificateException, IOException {
		SSLContext serverContext = SSLContext.getInstance("TLS");
		// The server certificate to issue to each client is stored in a KeyManager (because it needs the private key).
		serverContext.init(loadServerKeys("ServerPrivateKeyStore.jks"), null, null);
		return serverContext.getServerSocketFactory();
	}

}
