package toDoServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {
	private Integer port = 50001;
//	private final Logger logger = Logger.getLogger("");

	/**
	 * Initialize serverModel when the server is started and pass it on to every
	 * Thread. Every client updates the same user/todo list
	 */
	private ServerModel serverModel = new ServerModel();

	public static void main(String[] args) {
		Server server = new Server();
		server.start();
		// server.waitForStop();
	}

	@Override
	public void run() {
		try (ServerSocket listener = new ServerSocket(port, 10, null)) {
			// logger.info("Listening on port " + port);

			while (true) {
				Socket socket = listener.accept();
				ClientThread client = new ClientThread(socket, serverModel);
				client.start();
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	};

	
	public void setPort(Integer port) {
		this.port = port;
	}

}
