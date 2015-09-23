package com.worldpay.fsdemoapp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.net.ssl.SSLServerSocketFactory;

/**
 * Utility methods for dealing with cipher suites.
 */
public class CipherSuiteTools {

	/**
	 * Matches only cipher suite names that start with TLS (protocol), followed by Diffie-Hellman key exchange algorithm for ephemeral keys (DHE).
	 * Both "plain" DHE and Elliptic Curve (EC prefix) are acceptable. We ignore <c>DH_anon</c>.
	 */
	private final static Pattern cipherSuiteRegex = Pattern.compile("^TLS_(EC)?DHE");

	/**
	 * Returns all of the cipher suites that have forward secrecy key exchange algorithms configured.
	 *
	 * @return a string array (hopefully not empty!) of cipher names that provide PFS.
	 */
	public static String[] getCipherSuitesWithForwardSecrecy() {
		SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

		List<String> fsCiphers = new ArrayList<String>();

		String[] defaultCiphers = ssf.getDefaultCipherSuites();
		for (String cipher : defaultCiphers) {
			if (cipherSuiteRegex.matcher(cipher).find()) {
				fsCiphers.add(cipher);
			}
		}

		return fsCiphers.toArray(new String[0]);
	}

	/**
	 * Converts the input string array to a single string, each item in the array separated by the separator passed.
	 * 
	 * @param elements An ordered list of elements to convert to a single string. Must not be null, may be empty.
	 * @param separator Inserted between each element in the <c>elements</c> array. Must not be null, may be empty.
	 * @return a String, possibly empty, never null.
	 */
	public static String toDelimitedString(final String[] elements, final String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < elements.length; i++) {
			sb.append(elements[i]);
			if (i < (elements.length - 1)) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

}
