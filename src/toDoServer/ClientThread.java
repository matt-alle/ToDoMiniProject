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
	private static String SEPARATOR = "\\|";
	private String message = "";
	
	//TODO: somehow safe in a list.. but how?
	private ArrayList<ToDoEntry> toDoList;

	public ClientThread(Socket socket) {
		this.socket = socket;
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
			// does not really work -> remove later
			String reply = "";
			for (int i = 0; i < messageParts.length; i++) {
				reply += messageParts[i] + "\n";
			}
			// out.print(reply);

			// TODO: Depending on message type -> do corresponding task and reply
			switch (messageParts[0]) {
			case "Ping":
				System.out.println("casePing");
				out.print(true);
				break;

			case "CreateLogin":
				System.out.println("caseCreateLogin");
				break;

			case "Login":
				System.out.println("caseLogin");
				break;

			case "CreateToDo":
				System.out.println("caseCreateToDo");
				String title = messageParts[2];
				String priority = messageParts[3];
				String description = messageParts[4];
				ToDoEntry toDo = new ToDoEntry(title, priority, description, 1);
				toDoList.add(toDo);
				System.out.println("added: " + toDo.toString());
				System.out.println("ListLen: " + toDoList.size());
				System.out.println("from List: " + toDoList.get(0));
				break;

			case "ListToDos":
				System.out.println("caseListToDos");
				for (int i = 0; i < toDoList.size(); i++) {
					System.out.println("ToDo" + toDoList.get(i).getToDoID() + ": " + toDoList.get(i).toString());
				}
				break;

			case "GetToDo":
				System.out.println("caseGetToDo");
				break;

			case "Logout":
				System.out.println("caseLogout");
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

	// private PrintWriter processMessage(String message) {
	// Maybe?
	// }

}
