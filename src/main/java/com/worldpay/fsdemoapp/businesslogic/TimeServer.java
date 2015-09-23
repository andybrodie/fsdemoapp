package com.worldpay.fsdemoapp.businesslogic;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.function.Consumer;

import javax.net.ServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple server that sends the current time to any client that connects before closing the connection. It is blissfully unaware of TLS.
 */
public class TimeServer implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(TimeServer.class);

	/**
	 * Whether the server is ready to accept connections and {@link #getLocalPort()} has been assigned.
	 */
	private boolean isReady;

	/**
	 * Stores the port that is assigned when creating the server socket.
	 */
	private int localPort;

	/**
	 * Used to protect the {@link #isReady} field.
	 */
	private Object lockObject = new Object();

	/**
	 * If set, this will be invoked after the server socket has been opened, allowing the callback to configure the socket.
	 */
	private Consumer<ServerSocket> serverSocketCallback;

	/**
	 * The constructor provided socket factory.
	 */
	private ServerSocketFactory socketFactory;

	public TimeServer(ServerSocketFactory socketFactory, Consumer<ServerSocket> serverSocketCallback) {
		this.socketFactory = socketFactory;
		this.serverSocketCallback = serverSocketCallback;
	}

	public int getLocalPort() throws IllegalStateException {
		if (this.localPort == 0) {
			throw new IllegalStateException();
		}
		return this.localPort;
	}

	public void run() {
		try {
			ServerSocket serverSocket;
			serverSocket = this.socketFactory.createServerSocket(0, 0);
			this.localPort = serverSocket.getLocalPort();
			setReady();

			// Execute the callback that allows us to configure SSL
			if (this.serverSocketCallback != null) {
				this.serverSocketCallback.accept(serverSocket);
			}

			LOG.info("Waiting for connection on port {}", this.localPort);
			Socket socket = serverSocket.accept();
			LOG.info("Accepted client, writing data");
			Writer writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
			String textToSend = new Date().toString();
			writer.write(textToSend);
			writer.flush();
			LOG.info("Sent \"{}\"", textToSend);
			socket.close();
			serverSocket.close();
			LOG.info("Closed connection and listener socket");
			LOG.info("Server completed");
		} catch (IOException e) {
			LOG.error("IOException occurred, terminating", e);
		}

	}

	/**
	 * Notifies any waiting threads that this server object is now ready for connections.
	 */
	private void setReady() {
		synchronized (this.lockObject) {
			this.isReady = true;
			this.lockObject.notify();
		}
		LOG.info("TimeServer is ready to accept connections");
	}

	/**
	 * Causes the calling thread to wait until the server is ready to accept connections.
	 */
	public void waitUntilReady() {
		synchronized (this.lockObject) {
			while (!this.isReady) {
				try {
					this.lockObject.wait();
				} catch (InterruptedException e) {
					LOG.info("Interrupted, that was strange!  Going back to wait for isReady to be set to true");
				}
			}
		}
	}

}
