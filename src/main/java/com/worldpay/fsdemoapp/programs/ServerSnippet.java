package com.worldpay.fsdemoapp.programs;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

/**
 * Contains the server snippets used in the presentation.
 */
public class ServerSnippet {

	public static void main(String[] args) throws Exception {
		new ServerSnippet().go();
	}

	public SSLServerSocketFactory createServerSocketFactory() throws Exception {
		// Create an SSLContext configured for TLS only
		SSLContext serverContext = SSLContext.getInstance("TLS");

		// Initialise with a set of private keys
		serverContext.init(loadServerKeys(), null, null);

		return serverContext.getServerSocketFactory();
	}

	public void go() throws Exception {
		// Create a SSLServerSocketFactory with configured Key Store
		SSLServerSocketFactory socketFac = createServerSocketFactory();

		// Create a server socket to use
		SSLServerSocket serverSocket = (SSLServerSocket) socketFac.createServerSocket(0, 0);

		// Configure the server socket with a cipher suite (optional)
		serverSocket.setEnabledCipherSuites(new String[] { "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256" });

		// Wait for a connection from a client
		serverSocket.accept();
	}

	public KeyManager[] loadServerKeys() throws Exception {
		// Create a key store in memory
		String defaultKeyStoreType = KeyStore.getDefaultType();
		KeyStore ksKeys = KeyStore.getInstance(defaultKeyStoreType);

		// Populate from the prepared keystore on disk
		InputStream storeInput = new FileInputStream("ServerPrivateKeyStore.jks");
		ksKeys.load(storeInput, "storepass".toCharArray());

		// Set up a default Key Manager Factory (works with PKCS)
		String kmfAlg = KeyManagerFactory.getDefaultAlgorithm();
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(kmfAlg);

		// Provide a password to the keys we want
		kmf.init(ksKeys, "keypass".toCharArray());

		return kmf.getKeyManagers();
	}

}
