package com.worldpay.fsdemoapp.programs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.function.Consumer;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.worldpay.fsdemoapp.SSLServerSocketFactoryFactory;
import com.worldpay.fsdemoapp.SSLSocketFactoryFactory;
import com.worldpay.fsdemoapp.businesslogic.TimeClient;
import com.worldpay.fsdemoapp.businesslogic.TimeServer;

/**
 * Command line entry point that runs both a server and client using the default {@link ServerSocket} and {@link Socket} and then again using TLS via
 * {@link SSLServerSocket} and {@link SSLSocket}.
 */
public class CombinedServerAndClient {

	private static final Logger LOG = LoggerFactory.getLogger(CombinedServerAndClient.class);

	public static void main(String[] args) throws Exception {
		CombinedServerAndClient program = new CombinedServerAndClient();
		program.demoWithoutSSL();
		program.demoWithSSL();
	}

	/**
	 * Executes a simple client/server demo using sockets created by the factories passed.
	 *
	 * @param serverSocketFactory the factory for creating the server (listening) socket.
	 * @param socketFactory the factory for creating the client socket.
	 */
	private void demo(ServerSocketFactory serverSocketFactory, SocketFactory socketFactory) {
		TimeServer server = new TimeServer(serverSocketFactory, null);
		Thread serverThread = new Thread(server, "Server");
		serverThread.start();

		// Make sure the client thread doesn't get kicked off until he server socket is open and ready (as has assigned a port)
		server.waitUntilReady();

		TimeClient client = new TimeClient("localhost", server.getLocalPort(), socketFactory, null, new Consumer<Socket>() {
			public void accept(Socket socket) {
				if (socket instanceof SSLSocket) {
					SSLSocket sslSocket = (SSLSocket) socket;
					LOG.info("Using {}", sslSocket.getSession().getCipherSuite());
				} else {
					LOG.info("Using plain sockets");
				}
			}
		});
		Thread clientThread = new Thread(client,"Client");

		clientThread.start();

		// Tidy up the server and client
		try {
			clientThread.join();
			serverThread.join();
		} catch (InterruptedException e) {
			// Ignore any unexpected interruptions.
		}
	}

	/**
	 * Runs the server and client using {@link #demo(ServerSocketFactory, SocketFactory)} configured with the default (plaintext) {@link ServerSocket}
	 * and {@link Socket} factories.
	 */
	private void demoWithoutSSL() {
		LOG.info("Running demo with plain sockets");
		demo(ServerSocketFactory.getDefault(), SocketFactory.getDefault());
		LOG.info("Completed demo with plain sockets");
	}

	/**
	 * Calls {@link #demo(ServerSocketFactory, SocketFactory)} using SSL.
	 *
	 * @throws NoSuchAlgorithmException thrown if the default cipher suite is not available.
	 * @throws IOException thrown if there is a problem reading the keystore from the disk.
	 * @throws CertificateException thrown if there is a problem with a certificate used.
	 * @throws KeyStoreException thrown if there is a problem accessing the keystore containing a certificate.
	 * @throws KeyManagementException thrown if there is an issue using the selected key.
	 * @throws UnrecoverableKeyException thrown if the key could not be recovered from the keystore (usually means password is wrong).
	 */
	private void demoWithSSL() throws InterruptedException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, CertificateException,
					IOException, UnrecoverableKeyException {
		LOG.info("Running demo with TLS");
		SSLServerSocketFactory sssf = new SSLServerSocketFactoryFactory().getServerSocketFactory();
		SSLSocketFactory ssf = new SSLSocketFactoryFactory().getSocketFactory();
		
		demo(sssf, ssf);
		LOG.info("Completed demo with TLS");
	}

}
