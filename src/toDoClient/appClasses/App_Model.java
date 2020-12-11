package toDoClient.appClasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import toDoClient.ServiceLocator;
import toDoClient.abstractClasses.Model;

/**
 * Copyright 2015, FHNW, Prof. Dr. Brad Richards. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 * 
 * @author Brad Richards
 */
public class App_Model extends Model {
	ServiceLocator serviceLocator;
	private int value;
	private String token = "";
	private String[] serverMessage = new String[10];
	String ip;
	int port;

	Socket socket;
	BufferedReader in;
	OutputStreamWriter out;

	public App_Model() {
		value = 0;

		serviceLocator = ServiceLocator.getServiceLocator();
		serviceLocator.getLogger().info("Application model initialized");
	}

	public int getValue() {
		return value;
	}

	public int incrementValue() {
		value++;
		serviceLocator.getLogger().info("Application model: value incremented to " + value);
		return value;
	}

	/**
	 * Create Client Socket and send message to server
	 */
	public void sendMessageToServer(String ip, int port, String message) {

		try {
			// (Try to) create socket, reader and writer only if:
			// - no socket with this IP and port already exist
			// - the IP or port entered by the user have been changed
			if (socket == null || (!ip.equals(this.ip) || this.port != port)) {
				socket = new Socket(ip, port);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new OutputStreamWriter(socket.getOutputStream());
			}

			// Save IP and port of currently running socket
			this.ip = ip;
			this.port = port;

			out.write(message + "\n");
			out.flush();

			System.out.print("Client: " + message + "\n");

			String reply = in.readLine();
			System.out.println("Server: " + reply);

			// Split server message
			if (reply != null) {
				String replyParts[] = reply.split("\\|");
				serverMessage = replyParts;
			}

		} catch (Exception e) {
			e.printStackTrace();
			serverMessage[0] = "Result"; // To display "no connection to server" status
			serverMessage[1] = "false";
		}

	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String[] getServerMessageParts() {
		return serverMessage;
	}

}
