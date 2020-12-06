package toDoServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread extends Thread {

//	private final Logger logger = Logger.getLogger("");
	private Socket socket;
	private ServerModel serverModel;
	private static String SEPARATOR = "\\|";

	public ClientThread(Socket socket, ServerModel serverModel) {
		this.socket = socket;
		this.serverModel = serverModel;
	}

	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream());) {

			// logger.info("Request from client " + socket.getInetAddress().toString() + "
			// for server "
			// + socket.getLocalAddress().toString());

			// Split message in parts to process it
			String message = in.readLine();
			String[] messageParts = message.split(SEPARATOR);
			// TODO: Depending on message type -> do corresponding task and reply
			// TODO: Synchronize work on lists to avoid conflicts between different users?
			try {
				switch (messageParts[0]) {

				case "Ping":
					System.out.println("casePing");
					out.print("Result|true \r\n");
					out.flush();
					break;

				case "CreateLogin":
					System.out.println("caseCreateLogin");
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
						// create and add user without token
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
						if (messageParts[1].equals(serverModel.getUserList().get(i).getUserName())) // check user name
							if (messageParts[2].equals(serverModel.getUserList().get(i).getUserPassword())) { // check
																												// password
								correctLogin = true;
								serverModel.getUserList().get(i).setUserToken(token); // temporarily add token to user
																						// list
								User currentUser = serverModel.getUserList().get(i);
								serverModel.setCurrentUser(currentUser); // store current user
							}
						// System.out.println(messageParts[1] +
						// serverModel.getUserList().get(i).getUserName());
						// System.out.println(messageParts[2] +
						// serverModel.getUserList().get(i).getUserPassword());
					}
					if (correctLogin)
						out.print("Result|true|" + token);
					else
						out.print("Result|false");

					// list all users (remove later)
					for (int i = 0; i < serverModel.getUserList().size(); i++) {
						System.out.println(serverModel.getUserList().get(i).toString());
					}

					break;

				case "CreateToDo":
					System.out.println("caseCreateToDo");
					String title = messageParts[2];
					String priority = messageParts[3];
					String description = messageParts[4];
					ToDoEntry toDo = new ToDoEntry(title, priority, description,
							serverModel.getCurrentUser().getUserName());
					serverModel.getToDoList().add(toDo);
					out.print("Result|true|" + toDo.getToDoID());
					boolean success = true; // TODO: true if...
					break;

				case "ListToDos":
					System.out.println("caseListToDos");
					// remove later
					for (int i = 0; i < serverModel.getToDoList().size(); i++) {
						System.out.println(serverModel.getToDoList().get(i).toString());
					}
					String todoList = "Result|true";
					// create a string with the IDs of all the existing tasks
					for (int i = 0; i < serverModel.getToDoList().size(); i++) {
						if (serverModel.getCurrentUser().getUserName()
								.equals(serverModel.getToDoList().get(i).getUser()))
							todoList += ("|" + serverModel.getToDoList().get(i).getToDoID());
					}
					out.print(todoList);
					break;

				case "GetToDo":
					// TODO: chose by rank in list of user except ID?
					System.out.println("caseGetToDo");
					int todoID = Integer.valueOf(messageParts[2]); // TODO error handling
					boolean found = false;
					int i = 0;
					while (i < serverModel.getToDoList().size() && !found) {
						if (todoID == serverModel.getToDoList().get(i).getToDoID() && serverModel.getToDoList().get(i)
								.getUser() == serverModel.getCurrentUser().getUserName()) {
							out.print("Result|true|" + serverModel.getToDoList().get(i).toString());
							found = true;
						}
						i++;
					}
					if (!found)
						out.print("Result|false");
					break;

				case "Logout":
					System.out.println("caseLogout");
					// delete the token for this user
					serverModel.getCurrentUser().setUserToken(null);
					serverModel.setCurrentUser(null);
					out.print("Result|true");
					break;

				default:
					System.out.println("Unknown Message Type");
					out.print("Result|false");
				}
				out.flush();
				socket.close();

				// if anything goes wrong -> send "false"
			} catch (Exception ex) {
				out.print("Result|false");
				ex.toString();
			}

			out.flush();
			socket.close();

		} catch (IOException e) {
			System.out.println("Server: something was caught");
			// logger.warning(e.toString());
		}
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
			int random = (int) (Math.random() * 35 - 0.5) + 1;
			token += characters[random];
		}
		return token;
	}

	// private PrintWriter processMessage(String message) {
	// Maybe?
	// }

}
