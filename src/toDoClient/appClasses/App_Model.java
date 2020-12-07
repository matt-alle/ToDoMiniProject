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
	private String token = "token_test";
	private String[] serverMessage = new String[10];

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
		try (Socket socket = new Socket(ip, port);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());) {
			out.write(message + "\n");
			out.flush();

			System.out.print("Client: " + message + "\n");

			// -------WTF...
			// String reply = ""; // Anything except null
			// while (reply != null) { // Will be null if the server closes the socket
			// try {
			// reply = in.readLine();
			// System.out.println("Server: " + reply);
			// } catch (IOException e) {
			// reply = null; // end loop if we have a communications error
			// System.out.println("Exc");
			// }
			// }

			// try {
			// String replyParts[] = reply.split("\\|");
			// serverMessage = replyParts;
			// int l = replyParts.length;
			// for (int i = 0; i < l; i++) {
			// System.out.println("parts: " + replyParts[i]);
			// }
			// } catch (Exception e) {
			// System.out.println(e.toString());
			// }
			// --

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
