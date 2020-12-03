package toDoMiniProject.server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
	private Integer port = 50001;
//	private final Logger logger = Logger.getLogger("");

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
				ClientThread client = new ClientThread(socket);
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
