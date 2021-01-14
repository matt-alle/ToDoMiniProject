package toDoServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientThread extends Thread {

	private final Logger logger = Logger.getLogger("");
	private Socket socket;
	private ServerModel serverModel;
	private static String SEPARATOR = "\\|";
	BufferedReader in;
	PrintWriter out;

	// Store User Name and Token of this Client Thread
	private String currentToken = "";
	private String currentUserName = "";

	public ClientThread(Socket socket, ServerModel serverModel) {
		this.socket = socket;
		this.serverModel = serverModel;
	}

	@Override
	public void run() {
		try {

			// Create reader and writer only once
			if (in == null) {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream());
			}

			logger.info("Request from client " + socket.getInetAddress().toString() + "for server "
					+ socket.getLocalAddress().toString());

			// Process messages as long as they are not null
			String message = in.readLine();
			while (message != null) {
				// Split message in parts to process it
				String[] messageParts = message.split(SEPARATOR);
				// TODO: Synchronize work on lists to avoid conflicts between different users?
				try {
					switch (messageParts[0]) {

					case "Ping":
						if (socket.isConnected()) {
							// Check token if user logged in
							try {
								if (messageParts[1].equals(this.currentToken)) {
									out.print("Result|true");
								} else
									out.print("Result|false");
								// Catch if messageParts[1] does not exist (user logged out - no token)
							} catch (ArrayIndexOutOfBoundsException e) {
								if (currentUserName == "") {
									out.print("Result|true");
								} else
									out.print("Result|false");
							}
						} else
							out.print("Result|false");
						break;

					case "CreateLogin":
						String userName = messageParts[1];
						String userPassword = messageParts[2];

						// Check it user name is already taken
						// -> if not, create and save user account
						boolean freeUserName = true;
						for (int i = 0; i < serverModel.getUserList().size(); i++) {
							if (messageParts[1].equals(serverModel.getUserList().get(i).getUserName()))
								freeUserName = false;
						}
						if (freeUserName) {
							// Create and add user without token
							User user = new User(userName, userPassword, null);
							serverModel.getUserList().add(user);
						}

						out.print("Result|" + freeUserName);
						break;

					case "Login":
						// Check if user and password exist and respond
						String token = createToken();
						boolean correctLogin = false;
						try {
							// Only if user is not logged in already
							if (this.currentToken == "") {
								for (int i = 0; i < serverModel.getUserList().size(); i++) {
									// check user name
									if (messageParts[1].equals(serverModel.getUserList().get(i).getUserName())) {
										// check password
										if (messageParts[2]
												.equals(serverModel.getUserList().get(i).getUserPassword())) {
											correctLogin = true;
											// temporarily add token to user list
											serverModel.getUserList().get(i).setUserToken(token);
											// store current user name and token for this thread
											this.currentUserName = messageParts[1];
											this.currentToken = serverModel.getUserList().get(i).getUserToken();
										}
									}
								}
							}
							if (correctLogin)
								out.print("Result|true|" + token);
							else
								out.print("Result|false");
						} catch (Exception e) {
							out.print("Result|false");
						}

						break;

					case "ChangePassword":
						// Check if token is valid
						if (messageParts[1].equals(this.currentToken)) {
							int l = 0;
							boolean changed = false;
							String newPassword = messageParts[2];
							while (l < serverModel.getUserList().size() && !changed) {
								if (serverModel.getUserList().get(l).getUserName().equals(this.currentUserName)) {
									serverModel.getUserList().get(l).setUserPassword(newPassword);
									changed = true;
								}
								l++;
							}
							if (changed)
								out.print("Result|true");
							else
								out.print("Result|false");
						} else
							out.print("Result|false");
						break;

					case "CreateToDo":
						if (messageParts[1].equals(this.currentToken)) {
							String title = messageParts[2];
							String priority = messageParts[3];
							String description = messageParts[4];
							ToDoEntry toDo = new ToDoEntry(title, priority, description, this.currentUserName);
							serverModel.getToDoList().add(toDo);
							out.print("Result|true|" + toDo.getToDoID());
						} else
							out.print("Result|false");

						break;

					case "ListToDos":
						if (messageParts[1].equals(this.currentToken)) {
							String todoList = "Result|true";
							boolean listFound = false;
							// create a string with the IDs of all the existing tasks
							for (int i = 0; i < serverModel.getToDoList().size(); i++) {
								if (currentUserName.equals(serverModel.getToDoList().get(i).getUser())) {
									todoList += ("|" + serverModel.getToDoList().get(i).getToDoID());
									listFound = true;
								}
							}
							// If no ToDo's for this user found -> return false
							if (listFound)
								out.print(todoList);
							else
								out.print("Result|false");
						} else
							out.print("Result|false");
						break;

					case "GetToDo":
						if (messageParts[1].equals(this.currentToken)) {
							try {
								int todoID = Integer.valueOf(messageParts[2]);
								boolean found = false;
								int i = 0;
								while (i < serverModel.getToDoList().size() && !found) {
									// ID has to match and logged in user must be the creator of the todo entry
									if (todoID == serverModel.getToDoList().get(i).getToDoID() && serverModel
											.getToDoList().get(i).getUser().equals(this.currentUserName)) {
										out.print("Result|true|" + todoID + "|"
												+ serverModel.getToDoList().get(i).toString());
										found = true;
									}
									i++;
								}
								if (!found)
									out.print("Result|false");
							} catch (Exception ex) {
								out.print("Result|false");
							}
						} else
							out.print("Result|false");
						break;

					case "DeleteToDo":
						if (messageParts[1].equals(this.currentToken)) {
							int id = Integer.valueOf(messageParts[2]);
							boolean deleted = false;
							int k = 0;
							// Search the todo with the corresponding ID and delete it
							while (k < serverModel.getToDoList().size() && !deleted) {
								if (serverModel.getToDoList().get(k).getToDoID() == id) {
									serverModel.getToDoList().remove(k);
									deleted = true;
								}
								k++;
							}
							if (deleted)
								out.print("Result|true");
							else
								out.print("Result|false");
						} else
							out.print("Result|false");
						break;

					case "Logout":
						try {
							// Only if user is logged in
							if (this.currentToken != "") {
								// Delete the token for this user
								this.currentToken = "";
								this.currentUserName = "";
								out.print("Result|true");
							} else
								out.print("Result|false");
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
					out.print("\n");
					out.flush();
					logger.warning(ex.toString());
				}

				// At the end to immediately stop if the message becomes null
				message = in.readLine();
			}

		} catch (

		IOException e) {
			out.print("Result|false");
			out.flush();
			logger.warning(e.toString());
		}
		// Save data if client is terminated
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
