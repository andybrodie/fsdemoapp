package com.worldpay.fsdemoapp.programs;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Contains the client snippets used in the presentation.
 */
public class ClientSnippet {

	public static void main(String[] args) throws Exception {
		new ClientSnippet().go();
	}

	public SSLSocketFactory createSSLSocketFactory() throws Exception {
		// Create an SSLContext configured for TLS v1.2 only
		SSLContext clientContext = SSLContext.getInstance("TLSv1.2");

		// Initialise with a set of certificates that we will trust
		clientContext.init(null, loadServerCertificates(), null);

		return clientContext.getSocketFactory();
	}

	private void go() throws Exception {
		// Create a SSLServerSocketFactory with configured certificates to trust
		SSLSocketFactory socketFac = createSSLSocketFactory();

		// Create a socket, but don't connect
		SSLSocket socket = (SSLSocket) socketFac.createSocket();

		// Configure the socket with a cipher suite (optional)
		socket.setEnabledCipherSuites(new String[] { "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256" });

		// Connect to a listening server
		socket.connect(new InetSocketAddress("hostname", 10000));
	}

	public TrustManager[] loadServerCertificates() throws Exception {
		// Create an empty key store in memory
		InputStream trustStoreIS = new FileInputStream("ServerPublicCertificateKeystore.jks");
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

		// Populate from the prepared key store on disk, containing certificates
		trustStore.load(trustStoreIS, "storepass".toCharArray());

		// Create the "trust manager" that SSLSocket uses check certificates with
		String defaultTrustManagerAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(defaultTrustManagerAlgorithm);
		tmf.init(trustStore);

		return tmf.getTrustManagers();
	}
}
