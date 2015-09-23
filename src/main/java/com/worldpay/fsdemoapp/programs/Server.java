package com.worldpay.fsdemoapp.programs;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.function.Consumer;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.worldpay.fsdemoapp.CipherSuiteTools;
import com.worldpay.fsdemoapp.SSLServerSocketFactoryFactory;
import com.worldpay.fsdemoapp.businesslogic.TimeServer;

/**
 * <p>
 * Command line server for use with {@link Client}.
 * </p>
 * <p>
 * Usage: <c>java Server [Cipher Suite Name]*</c>
 * </p>
 * <p>
 * Where:
 * <ul>
 * <li><em>Cipher Suite Name</em> is a name known to JSSE (either run {@link ShowCipherSuites} or consult
 * <a href="http://docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html">Java Cryptography Architecture Oracle Providers
 * Documentation for JDK 8</a>) that will be enabled.  If not specified then JRE defaults will be used.</li>
 * </p>
 */
public class Server {

	private static final Logger LOG = LoggerFactory.getLogger(Server.class);

	public static void main(final String[] args) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException,
					KeyStoreException, CertificateException, IOException {
		SSLServerSocketFactory sssf = new SSLServerSocketFactoryFactory().getServerSocketFactory();
		TimeServer server = new TimeServer(sssf, new Consumer<ServerSocket>() {
			public void accept(ServerSocket serverSocket) {
				if (serverSocket instanceof SSLServerSocket) {
					String[] suites = args.length == 0 ? CipherSuiteTools.getCipherSuitesWithForwardSecrecy() : args;
					LOG.info("Setting cipher suites to: {}", CipherSuiteTools.toDelimitedString(suites, ", "));
					((SSLServerSocket) serverSocket).setEnabledCipherSuites(suites);
				}
			}

		});
		server.run();
	}

}
