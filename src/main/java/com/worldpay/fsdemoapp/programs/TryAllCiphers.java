package com.worldpay.fsdemoapp.programs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

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
 * <p>
 * Command line utility to run the server and client in a single command with <em>all</em> available cipher suites, reporting back on which ones
 * succeeded and which ones failed.
 * </p>
 * <p>
 * This is a useful utility for determining which algorithm of public key, contained in the server certificate, enable which cipher suites.
 * </p>
 */
public class TryAllCiphers {

	private static final Logger LOG = LoggerFactory.getLogger(TimeServer.class);

	public static void main(final String[] args) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException,
					KeyStoreException, CertificateException, IOException {
		TryAllCiphers test = new TryAllCiphers();
		test.run();
	}

	private static String toReadableList(List<String> strings) {
		StringBuilder sb = new StringBuilder();
		for (String string : strings) {
			sb.append('\t');
			sb.append(string);
			sb.append('\n');
		}
		return sb.toString();
	}

	/**
	 * Start a server with only the suite specified being enabled.
	 *
	 * @param sssf the server socket factory to use with {@link TimeServer}.
	 * @param suite the name of the cipher suite to enable. All others will be disabled.
	 * @return the ephemeral port that the server is listening on (pass this to the client).
	 */
	private int createServer(final SSLServerSocketFactory sssf, final String suite) throws KeyManagementException, UnrecoverableKeyException,
					NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {

		TimeServer server = new TimeServer(sssf, new Consumer<ServerSocket>() {

			public void accept(ServerSocket serverSocket) {
				((SSLServerSocket) serverSocket).setEnabledCipherSuites(new String[] { suite });
			}
		});
		Thread serverThread = new Thread(server);
		serverThread.start();
		server.waitUntilReady();
		return server.getLocalPort();
	}

	private void run() throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException,
					IOException {
		SSLServerSocketFactory sssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		List<String> suites = Arrays.asList(sssf.getSupportedCipherSuites());
		List<String> workingCiphers = new ArrayList<String>();
		List<String> failedCiphers = new ArrayList<String>();
		sssf = new SSLServerSocketFactoryFactory().getServerSocketFactory();
		SSLSocketFactory ssf = new SSLSocketFactoryFactory().getSocketFactory();

		for (String suite : suites) {
			int port = createServer(sssf, suite);
			boolean result = runClient(ssf, port, suite);
			(result ? workingCiphers : failedCiphers).add(suite);
		}

		LOG.info("Successful Ciphers ({} / {})\n{}", new Object[] { workingCiphers.size(), suites.size(), toReadableList(workingCiphers) });
		LOG.info("Failed Ciphers ({} / {})\n{}:", new Object[] { failedCiphers.size(), suites.size(), toReadableList(failedCiphers) });
	}

	/**
	 * Start a client with only the suite specified being enabled.
	 *
	 * @param ssf the socket factory to use with {@link TimeServer}.
	 * @param suite the name of the cipher suite to enable. All others will be disabled.
	 * @return the ephemeral port that the server is listening on (pass this to the client).
	 * @param port the port to connect to (use returned value from {@link #createServer(SSLServerSocketFactory, String)}.
	 * @param suite the cipher suite name to force.
	 * @return true if the SSL/TLS handshake was successful, false otherwise.
	 */
	private boolean runClient(SSLSocketFactory ssf, int port, final String suite) {
		TimeClient client = new TimeClient("localhost", port, ssf, new Consumer<Socket>() {
			public void accept(Socket socket) {
				((SSLSocket) socket).setEnabledCipherSuites(new String[] { suite });
			}
		}, new Consumer<Socket>() {
			public void accept(Socket socket) {
				LOG.info("Agreed cipher suite: {}", ((SSLSocket) socket).getSession().getCipherSuite());
			}
		});
		try {
			client.run();
			return true;
		} catch (Exception e) {
			// Any errors imply that the a cipher suite could not be agreed, so return a failure for this configured suite.
			return false;
		}
	}

}
