package toDoServer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ServerModel {

	private ArrayList<User> userList = new ArrayList<>();
	private ArrayList<ToDoEntry> toDoList = new ArrayList<>();
	private User currentUser; // temporary store the user who is currently logged in

	private static String USERS = "Users.sav";
	private static String TODO = "ToDo.sav";
	private static String SEPARATOR = ";";

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

	/**
	 * Save and restore server data
	 */
	
	// TODO read saved files

	public void writeSaveFileUsers() {
		File file = new File(USERS);
		try (FileWriter fileOut = new FileWriter(file)) {
			for (User user : userList) {
				String line = writeUser(user);
				fileOut.write(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("saved");
	}

	// Token is not saved - every user is logged out if the server crashes
	public String writeUser(User user) {
		String line = user.getUserID() + SEPARATOR + user.getUserName() + SEPARATOR + user.getUserPassword() + "\n";
		return line;
	}

	public void writeSaveFileToDo() {
		File file = new File(TODO);
		try (FileWriter fileOut = new FileWriter(file)) {
			for (ToDoEntry todo : toDoList) {
				String line = writeToDo(todo);
				fileOut.write(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("saved");
	}

	public String writeToDo(ToDoEntry todo) {
		String line = todo.getToDoID() + SEPARATOR + todo.getTitle() + SEPARATOR + todo.getPriority() + SEPARATOR
				+ todo.getDescription() + SEPARATOR + todo.getUser() + "\n";
		return line;
	}
}
