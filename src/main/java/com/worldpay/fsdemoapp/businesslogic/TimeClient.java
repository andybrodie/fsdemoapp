package com.worldpay.fsdemoapp.businesslogic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.Consumer;

import javax.net.SocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A simple client for the TimeServer.  It has no knowledge of TLS.
 * </p>
 */
public class TimeClient implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(TimeClient.class);

	/**
	 * If set, this will be invoked after the socket has been created but before connected. This allows configuration of the socket.
	 */
	private Consumer<Socket> postConnectCallback;

	/**
	 * If set, this will be invoked after the socket has been connected, allowing the callback to examine the connected socket.
	 */
	private Consumer<Socket> preConnectCallback;

	/**
	 * The host name of the server.
	 */
	private String serverHost;

	/**
	 * The serverPort that the server is listening on.
	 */
	private int serverPort;

	/**
	 * The socket factory that will create sockets for us.
	 */
	private SocketFactory socketFactory;

	public TimeClient(String serverHost, int port, SocketFactory socketFactory, Consumer<Socket> preConnectCallback,
					Consumer<Socket> postConnectCallback) {
		this.serverHost = serverHost;
		this.serverPort = port;
		this.socketFactory = socketFactory;
		this.preConnectCallback = preConnectCallback;
		this.postConnectCallback = postConnectCallback;
	}

	public void run() {

		Socket socket;
		LOG.info("Creating socket to {}:{}", this.serverHost, this.serverPort);

		try {
			socket = this.socketFactory.createSocket();

			if (this.preConnectCallback != null) {
				this.preConnectCallback.accept(socket);
			}

			socket.connect(new InetSocketAddress(this.serverHost, this.serverPort));

			if (this.postConnectCallback != null) {
				this.postConnectCallback.accept(socket);
			}

			LOG.info("Reading data from server");
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			char[] input = new char[1024];
			int readCount;
			while ((readCount = isr.read(input)) != -1) {
				LOG.info("Received: \"{}\"", new String(input, 0, readCount));
			}
			socket.close();
			LOG.info("Closed connection");
			LOG.info("Client completed");
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

}
