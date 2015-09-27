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

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Creates a ready to use {@link SSLSocketFactory} instance configured with the public certificate of the server so that the TLS handshake completes
 * successfully.
 * </p>
 * <p>
 * Once an instance has been created, load a set of certificates with {@link #loadServerCertificates(String)} before calling
 * {@link #getSocketFactory()}.
 */
public class SSLSocketFactoryFactory {

	private static final Logger LOG = LoggerFactory.getLogger(SSLSocketFactoryFactory.class);

	/**
	 * Loads the public certificate of the server in to a {@link TrustManager} so that the client trusts the server when it connects.
	 *
	 * @param certificateStoreFile the file to load the certificate from.
	 */
	public static TrustManager[] loadServerCertificates(String certificateStoreFile)
					throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		try {
			InputStream trustStoreIS = new FileInputStream(certificateStoreFile);
			char[] trustStorePassphrase = "storepass".toCharArray();
			KeyStore ksTrust = KeyStore.getInstance("JKS");
			ksTrust.load(trustStoreIS, trustStorePassphrase);

			// TrustManager decides which certificate authorities to use.
			String defaultTrustManagerAlgorithm = TrustManagerFactory.getDefaultAlgorithm();

			LOG.info("Default trust manager algorithm is {}", defaultTrustManagerAlgorithm);
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(defaultTrustManagerAlgorithm);
			tmf.init(ksTrust);

			TrustManager[] tm = tmf.getTrustManagers();
			LOG.info("Loaded {} trust managers", tm.length);

			return tm;
		} catch (FileNotFoundException fnfe) {
			// If they keystore doesn't exist, then return empty so we can try out what works with NO keystore
			LOG.error("Unable to find store file {}", certificateStoreFile);
			return new TrustManager[0];
		}

	}

	/**
	 * <p>
	 * Set up the client socket factory. This only needs to set itself up to trust the server's certificate. Cipher suite selection (limiting to PFS)
	 * will be done on the server side (only PFS suites will be offered).
	 * </p>
	 * <p>
	 * Also forces TLS 1.2 protocol only.
	 * </p>
	 *
	 * @throws NoSuchAlgorithmException thrown if the default cipher suite is not available.
	 * @throws IOException thrown if there is a problem reading the keystore from the disk.
	 * @throws CertificateException thrown if there is a problem with a certificate used.
	 * @throws KeyStoreException thrown if there is a problem accessing the keystore containing a certificate.
	 * @throws KeyManagementException thrown if there is an issue using the selected key.
	 * @throws UnrecoverableKeyException thrown if the key could not be recovered from the keystore (usually means password is wrong).
	 */
	public SSLSocketFactory getSocketFactory()
					throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, CertificateException, IOException {
		SSLContext clientContext = SSLContext.getInstance("TLSv1.2");
		// The client certificate needs a trust manager configured with the server certificate.
		clientContext.init(null, loadServerCertificates("ServerPublicCertificateKeystore.jks"), null);
		return clientContext.getSocketFactory();
	}

}
