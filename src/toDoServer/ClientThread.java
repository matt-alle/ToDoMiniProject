package toDoServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread extends Thread {
	private static int clientNumber = 0;
//	private final Logger logger = Logger.getLogger("");
	private Socket socket;
	private ServerModel serverModel;
	private static String SEPARATOR = "\\|";
	private String message = "";
	private String user = "";

	// Initialize to have easy access to current user and todo. No further usage
//	private User user;// thisUser = new User("1", "2", "3");
//	private ToDoEntry toDo;// thisToDo = new ToDoEntry("1", "2", "3", "4");

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
			System.out.println("user: " + user);
			// TODO: Depending on message type -> do corresponding task and reply
			// TODO: Synchronize work on lists to avoid conflicts between different users?
			switch (messageParts[0]) {

			case "Ping":
				System.out.println("casePing");
				out.print("Result|true");
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
					User user = new User(userName, userPassword, null);
					// this.thisUser = user;
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
							serverModel.getUserList().get(i).setUserToken(token); // temporarily add token to user list
							user = serverModel.getUserList().get(i).getUserName(); // save name of the logged in user
						}
					// System.out.println(messageParts[1] +
					// serverModel.getUserList().get(i).getUserName());
					// System.out.println(messageParts[2] +
					// serverModel.getUserList().get(i).getUserPassword());
				}
				System.out.println("size " + serverModel.getUserList().size());
				if (correctLogin)
					out.print("Result|true|" + token);
				else
					out.print("Result|false");
				break;

			case "CreateToDo":
				System.out.println("caseCreateToDo");
				String title = messageParts[2];
				String priority = messageParts[3];
				String description = messageParts[4];
				ToDoEntry toDo = new ToDoEntry(title, priority, description, user);
				// this.thisToDo = toDo;
				serverModel.getToDoList().add(toDo);
				boolean success = true; // TODO: true if...
				// get ID of last, just created entry:
				// int id = serverModel.getToDoList().get(serverModel.getToDoList().size() -
				// 1).getToDoID();
				// out.print("Result|" + success + "|" + this.toDo.getToDoID());
				break;

			case "ListToDos":
				System.out.println("caseListToDos");
				for (int i = 0; i < serverModel.getToDoList().size(); i++) {
					System.out.println(serverModel.getToDoList().get(i).toString());
				}
				out.print("Result|true|0|1");
				break;

			case "GetToDo":
				System.out.println("caseGetToDo");
				out.print("Result|true|0|Shop|High|Buy food");
				break;

			case "Logout":
				System.out.println("caseLogout");
				// delete the token for this user
				// this.user.setUserToken(null);
				out.print("Result|true");
				break;

			default:
				System.out.println("Unknown Message Type");
				out.print("Unknown Message Type");
			}

			out.flush();
			socket.close();
		} catch (IOException e) {
			// logger.warning(e.toString());
		}
	}

	public String createToken() {
		String token = "Token";
		// TODO
		return token;
	}

	// private PrintWriter processMessage(String message) {
	// Maybe?
	// }

}
