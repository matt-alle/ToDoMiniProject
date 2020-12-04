package toDoServer;

import java.util.ArrayList;

public class ServerModel {

	private ArrayList<User> userList = new ArrayList<>();
	private ArrayList<ToDoEntry> toDoList = new ArrayList<>();

	public ServerModel() {
		System.out.println("Initialized new serverModel");
	}

	public ArrayList<User> getUserList() {
		return userList;
	}

	public ArrayList<ToDoEntry> getToDoList() {
		return toDoList;
	}

	// TODO: store data in a file (every xx minutes and when the server is stopped)
}
