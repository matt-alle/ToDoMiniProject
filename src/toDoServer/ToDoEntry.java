package toDoServer;

public class ToDoEntry {

	private int toDoID;
	private static int highestID = 0;
	private String title;
	private String priority;
	private String description;
	private String user;

	private static int getNextID() {
		return highestID++;
	}

	public ToDoEntry(String title, String priority, String description, String user) {
		this.toDoID = getNextID();
		this.title = title;
		this.priority = priority;
		this.description = description;
		this.user = user;
	}

	public String getTitle() {
		return title;
	}

	public String getPriority() {
		return priority;
	}

	public String getDescription() {
		return description;
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

	public String getUser() {
		return this.user;
	}

	// only to restore data (had to remove "final")
	public void setID(int toDoID) {
		this.toDoID = toDoID;
	}

	@Override
	public String toString() {
		String toDo = this.title + "|" + this.priority + "|" + this.description;
		return toDo;
	}

}
