package toDoServer;

import java.util.ArrayList;

public class ServerModel {

	private ArrayList<User> userList = new ArrayList<>();
	private ArrayList<ToDoEntry> toDoList = new ArrayList<>();
	private User currentUser; // temporary store the user who is currently logged in

	public ServerModel() {
		System.out.println("Initialized new serverModel");
	}

	public ArrayList<User> getUserList() {
		return userList;
	}

	public ArrayList<ToDoEntry> getToDoList() {
		return toDoList;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public User getCurrentUser() {
		return this.currentUser;
	}

	// TODO: store data in a file (every xx minutes and when the server is stopped)
}
