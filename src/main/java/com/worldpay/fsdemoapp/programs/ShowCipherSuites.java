package com.worldpay.fsdemoapp.programs;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.net.ssl.SSLServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Command line utility to show a full list of available cipher suites for use with JSSE.
 * </p>
 * <p>
 * Usage: <code>java com.worldpay.fsdemoapp.programs.ShowCipherSuites</code>
 * </p>
 * <p>
 * The output is a list of cipher suite names. Any cipher suite proceeded by a <strong>*</strong> indicates tht the suite is enabled by default.
 * </p>
 * <p>
 * The cipher suite names can then be used as parameters to {@link Server} or {@link Client} to force the use of specific cipher suites.
 * </p>
 */
public class ShowCipherSuites {

	private static final Logger LOG = LoggerFactory.getLogger(ShowCipherSuites.class);

	/**
	 * Logs all the cipher suites in a list. If the suite name is preceded by an asterisk then this means it is in the set of default suites.
	 */
	public static void dumpCipherSuites() {
		SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

		String[] defaultCiphers = ssf.getDefaultCipherSuites();
		String[] availableCiphers = ssf.getSupportedCipherSuites();

		TreeMap<String, Boolean> ciphers = new TreeMap<String, Boolean>();

		LOG.info("Found " + defaultCiphers.length + " default ciphers");
		LOG.info("Found " + availableCiphers.length + " available ciphers");
		for (String availableCipher : availableCiphers) {
			ciphers.put(availableCipher, Boolean.FALSE);
		}

		for (String defaultCipher : defaultCiphers) {
			ciphers.put(defaultCipher, Boolean.TRUE);
		}

		StringBuilder sb = new StringBuilder("Default\tCipher\n");
		for (Entry<String, Boolean> entry : ciphers.entrySet()) {
			Map.Entry<String, Boolean> cipher = entry;

			if (Boolean.TRUE.equals(cipher.getValue())) {
				sb.append('*');
			} else {
				sb.append(' ');
			}

			sb.append('\t');
			sb.append(cipher.getKey());
			sb.append('\n');
		}
		LOG.info(sb.toString());

	}

	/**
	 * Entry point that just dumps all the cipher suites that are available and terminates.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		ShowCipherSuites.dumpCipherSuites();
	}
}
