package toDoServer;

public class User {

	private int userID;
	private static int highestID = 0;
	private String userName;
	private String userPassword;
	private String userToken;

	private static int getNextID() {
		return highestID++;
	}

	public User(String userName, String userPassword, String userToken) {
		this.userID = getNextID();
		this.userName = userName;
		this.userPassword = userPassword;
		this.userToken = userToken;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public int getUserID() {
		return userID;
	}

	// only to restore data (had to remove "final")
	public void setID(int userID) {
		this.userID = userID;
	}

	@Override
	public String toString() {
		String toDo = this.userName + "|" + this.userPassword + "|" + this.userToken;
		return toDo;
	}

}
