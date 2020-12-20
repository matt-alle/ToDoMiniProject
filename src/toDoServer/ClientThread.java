package toDoServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ClientThread extends Thread {

	private final Logger logger = Logger.getLogger("");
	private Socket socket;
	private ServerModel serverModel;
	private static String SEPARATOR = "\\|";
	BufferedReader in;
	PrintWriter out;

	public ClientThread(Socket socket, ServerModel serverModel) {
		this.socket = socket;
		this.serverModel = serverModel;
	}

	@Override
	public void run() {
		try {

			// create reader and writer only once
			if (in == null) {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream());
			}

			logger.info("Request from client " + socket.getInetAddress().toString() + "for server "
					+ socket.getLocalAddress().toString());

			// process messages as long as they are not null
			String message = in.readLine();
			while (message != null) {
				// Split message in parts to process it
				String[] messageParts = message.split(SEPARATOR);
				// TODO: Synchronize work on lists to avoid conflicts between different users?
				try {
					switch (messageParts[0]) {

					case "Ping":
						if (socket.isConnected())
							out.print("Result|true");
						else
							out.print("Result|false");
						break;

					case "CreateLogin":
						String userName = messageParts[1];
						String userPassword = messageParts[2];

						// TODO also disable if only user name is the same?

						// Check it user name is already taken
						// -> if not, create and save user account
						boolean freeUserName = true;
						for (int i = 0; i < serverModel.getUserList().size(); i++) {
							if (messageParts[1].equals(serverModel.getUserList().get(i).getUserName()))
								freeUserName = false;
						}
						if (freeUserName) {
							// Create and add user without token (TODO encrypt password?)
							User user = new User(userName, userPassword, null);
							serverModel.getUserList().add(user);
						}

						out.print("Result|" + freeUserName);
						break;

					case "Login":
						// Check if user and password exist and respond
						String token = createToken();
						boolean correctLogin = false;
						for (int i = 0; i < serverModel.getUserList().size(); i++) {
							// check user name
							if (messageParts[1].equals(serverModel.getUserList().get(i).getUserName()))
								// check password
								if (messageParts[2].equals(serverModel.getUserList().get(i).getUserPassword())) {
									correctLogin = true;
									// temporarily add token to user list
									serverModel.getUserList().get(i).setUserToken(token);
									User currentUser = serverModel.getUserList().get(i);
									// store current user
									serverModel.setCurrentUser(currentUser);
								}
						}
						if (correctLogin)
							out.print("Result|true|" + token);
						else
							out.print("Result|false");

						break;

					case "CreateToDo":
						// check if token is valid (TODO for the other parts aswell? or not at all?)
						if (messageParts[1].equals(serverModel.getCurrentUser().getUserToken())) {
							String title = messageParts[2];
							String priority = messageParts[3];
							String description = messageParts[4];
							ToDoEntry toDo = new ToDoEntry(title, priority, description,
									serverModel.getCurrentUser().getUserName());
							serverModel.getToDoList().add(toDo);
							out.print("Result|true|" + toDo.getToDoID());
						} else
							out.print("Result|false");

						break;

					case "ListToDos":
						String todoList = "Result|true";
						boolean listFound = false;
						// create a string with the IDs of all the existing tasks
						for (int i = 0; i < serverModel.getToDoList().size(); i++) {
							if (serverModel.getCurrentUser().getUserName()
									.equals(serverModel.getToDoList().get(i).getUser())) {
								todoList += ("|" + serverModel.getToDoList().get(i).getToDoID());
								listFound = true;
							}
						}
						// If no ToDo's for this user found -> return false
						if (listFound)
							out.print(todoList);
						else
							out.print("Result|false");
						break;

					case "GetToDo":
						// TODO: chose by rank in list of user except ID?
						int todoID = Integer.valueOf(messageParts[2]); // TODO error handling (crashes if field is
																		// empty)
						boolean found = false;
						int i = 0;
						while (i < serverModel.getToDoList().size() && !found) {
							// ID has to match and logged in user must be the creator of the todo entry
							if (todoID == serverModel.getToDoList().get(i).getToDoID() && serverModel.getToDoList()
									.get(i).getUser().equals(serverModel.getCurrentUser().getUserName())) {
								out.print("Result|true|" + serverModel.getToDoList().get(i).toString());
								found = true;
							}
							i++;
						}
						if (!found)
							out.print("Result|false");
						break;

					case "Logout":
						try {
							// Delete the token for this user
							serverModel.getCurrentUser().setUserToken(null);
							serverModel.setCurrentUser(null);
							out.print("Result|true");
						} catch (Exception e) {
							out.print("Result|false");
						}
						break;

					default:
						out.print("Result|false");
					}

					// go to the next line after every output
					out.print("\n");
					out.flush();

					// if anything goes wrong -> send "false"
				} catch (Exception ex) {
					out.print("Result|false");
					out.flush();
					logger.warning(ex.toString());
				}

				// At the end to immediately stop if the message becomes null
				message = in.readLine();
			}

		} catch (IOException e) {
			out.print("Result|false");
			out.flush();
			logger.warning(e.toString());
		}
		// TODO: move somewhere else? (here saves after client is closed)
		serverModel.writeSaveFileUsers();
		serverModel.writeSaveFileToDo();
	}

	public String createToken() {
		String token = "";
		char[] characters = new char[36];
		// fill array with letters (A-Z) and numbers (0-9)
		for (int i = 0; i < 36; i++) {
			characters[i] = (char) ('A' + i);
			if (i > 25)
				characters[i] = (char) ('0' + (i - 26));
		}
		// create a random string of letters and numbers
		for (int i = 0; i < 30; i++) {
			int randomInt = (int) (Math.random() * 35 - 0.5) + 1;
			token += characters[randomInt];
		}
		return token;
	}

}
