package toDoServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ServerModel {

	private ArrayList<User> userList = new ArrayList<>();
	private ArrayList<ToDoEntry> toDoList = new ArrayList<>();

	private static String USERS = "Users.sav";
	private static String TODO = "ToDo.sav";
	private static String SEPARATOR = ";;;";

	public ServerModel() {
		System.out.println("Initialized new serverModel");
	}

	public ArrayList<User> getUserList() {
		return userList;
	}

	public ArrayList<ToDoEntry> getToDoList() {
		return toDoList;
	}


	/**
	 * Save and restore server data
	 * ------------------------------------------------------------------------------------------------------------------
	 */
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
		// System.out.println("saved");
	}

	public String writeToDo(ToDoEntry todo) {
		String line = todo.getToDoID() + SEPARATOR + todo.getTitle() + SEPARATOR + todo.getPriority() + SEPARATOR
				+ todo.getDescription() + SEPARATOR + todo.getUser() + "\n";
		return line;
	}

	public void readSaveFileUser() {
		File file = new File(USERS);
		String data = "";
		try (BufferedReader fileIn = new BufferedReader(new FileReader(file))) {
			String line = fileIn.readLine();
			while (line != null) {
				User user = readUser(line);
				userList.add(user);
				line = fileIn.readLine();
			}
		} catch (FileNotFoundException e) {
			data = "Save file does not exist";
		} catch (IOException e) {
			data = e.getClass().toString();
		}
	}

	public User readUser(String line) {
		String[] attributes = line.split(SEPARATOR);
		int userID = -999;
		String userName = "-";
		String userPassword = "-";
		try {
			userID = Integer.valueOf(attributes[0]);
			userName = attributes[1];
			userPassword = attributes[2];
		} catch (Exception e) {
			userName = "-Error in Line-"; // TODO error handling
		}
		User user = new User(userName, userPassword, null);
		user.setID(userID); // restore ID
		return user;
	}

	public void readSaveFileToDo() {
		File file = new File(TODO);
		String data = "";
		try (BufferedReader fileIn = new BufferedReader(new FileReader(file))) {
			String line = fileIn.readLine();
			while (line != null) {
				ToDoEntry todo = readToDo(line);
				toDoList.add(todo);
				line = fileIn.readLine();
			}
		} catch (FileNotFoundException e) {
			data = "Save file does not exist";
		} catch (IOException e) {
			data = e.getClass().toString();
		}
	}

	public ToDoEntry readToDo(String line) {
		String[] attributes = line.split(SEPARATOR);
		int todoID = -999;
		String todoTitle = "-";
		String todoPriority = "-";
		String todoDescription = "-";
		String todoUser = "-";
		try {
			todoID = Integer.valueOf(attributes[0]);
			todoTitle = attributes[1];
			todoPriority = attributes[2];
			todoDescription = attributes[3];
			todoUser = attributes[4];
		} catch (Exception e) {
			todoTitle = "-Error in Line-";
		}
		ToDoEntry todo = new ToDoEntry(todoTitle, todoPriority, todoDescription, todoUser);
		todo.setID(todoID); // restore ID
		return todo;
	}
}
