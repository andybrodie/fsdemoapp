package com.worldpay.fsdemoapp.programs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.function.Consumer;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.worldpay.fsdemoapp.CipherSuiteTools;
import com.worldpay.fsdemoapp.SSLSocketFactoryFactory;
import com.worldpay.fsdemoapp.businesslogic.TimeClient;

/**
 * <p>
 * Command line client for use with {@link Server}.
 * </p>
 * <p>
 * Usage: <c>java Client &lt;port&gt; [Cipher Suite Name]*</c>
 * </p>
 * <p>
 * Where:
 * <ul><li><em>port</em> is the port to connect to a running server ({@link Server} will output this after starting).
 * <li><em>Cipher Suite Name</em> is a name known to JSSE (either run {@link ShowCipherSuites} or consult 
 * <a href="http://docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html">Java Cryptography Architecture Oracle Providers
 * Documentation for JDK 8</a>) that will be enabled.  If not specified then JRE defaults will be used.</li>
 * </p>
 */
public class Client {

	private static final Logger LOG = LoggerFactory.getLogger(Client.class);

	/**
	 * Initialises an {@link SSLSocketFactoryFactory} and passes it to a new {@link TimeClient} instance along with callbacks
	 * that display the cipher suite information at various points during configuration and communication with the {@link Server}.
	 * @param args command line arguments (see class description).
	 */
	public static void main(final String[] args) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException,
					IOException, UnrecoverableKeyException {

		SocketFactory sslSocketFactory = new SSLSocketFactoryFactory().getSocketFactory();
		TimeClient client =
						new TimeClient(InetAddress.getLocalHost().getHostName(), Integer.parseInt(args[0]), sslSocketFactory, new Consumer<Socket>() {
							public void accept(Socket socket) {
								if (socket instanceof SSLSocket) {
									String[] suites = args.length == 1 ? CipherSuiteTools.getCipherSuitesWithForwardSecrecy()
													: Arrays.copyOfRange(args, 1, args.length);
									LOG.info("Setting available cipher suites to: {}", CipherSuiteTools.toDelimitedString(suites, ", "));
									((SSLSocket) socket).setEnabledCipherSuites(suites);
								}
							}
						}, new Consumer<Socket>() {
							public void accept(Socket socket) {
								if (socket instanceof SSLSocket) {
									LOG.info("Agreed cipher suite: {}", ((SSLSocket) socket).getSession().getCipherSuite());
								}
							}
						});
		client.run();
	}

}
