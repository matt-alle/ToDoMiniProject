package toDoServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
	private static int clientNumber = 0;
//	private final Logger logger = Logger.getLogger("");
	private Socket socket;
	private static String SEPARATOR = "|";
	private String message = "";

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

			// Send back what was received:
			String message = in.readLine();
			out.print(message);

			// TODO: Split string in message-parts (splits every character...)
			String[] messageParts = message.split(SEPARATOR);
			System.out.println(messageParts[0]);
			// for (int i = 0; i < messageParts.length; i++) {
			// System.out.print(messageParts[i] + "///////");
			// }

			// TODO: Depending on message type -> do corresponding task
			switch (messageParts[0]) {
			case "Ping":
				//
				break;

			case "CreateLogin":
				//
				break;

			default:
				System.out.println("Unknown Message Type");
				out.print(false);
			}

			out.flush();
			socket.close();
		} catch (IOException e) {
			// logger.warning(e.toString());
		}
	}
	
	//private PrintWriter processMessage(String message) {
		// Maybe?
	// }

}
