package toDoServer;

public class ToDoEntry {

	private final int toDoID;
	private static int highestID = 0;
	private String title;
	private String priority;
	private String description;
	private int userID;

	private static int getNextID() {
		return highestID++;
	}

	public ToDoEntry(String title, String priority, String description, int userID) {
		this.toDoID = getNextID();
		this.title = title;
		this.priority = priority;
		this.description = description;
	}

	public int getToDoID() {
		return toDoID;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		String toDo = this.title + "|" + this.priority + "|" + this.description;
		return toDo;
	}

}
