package toDoClient.appClasses;

import toDoClient.JavaFX_App_Template;
import toDoClient.ServiceLocator;
import toDoClient.abstractClasses.Controller;
import javafx.scene.control.TextField;

/**
 * Copyright 2015, FHNW, Prof. Dr. Brad Richards. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 * 
 * @author Brad Richards
 */

public class App_Controller extends Controller<App_Model, App_View> {
	ServiceLocator serviceLocator;
	private static String SEPARATOR = "|";
	private String message = "";

	private boolean ipValid;
	private boolean portValid;
	private boolean userNameValid;
	private boolean passwordValid;
	private boolean titleValid;
	private boolean descriptionValid;
	private boolean loggedIn = false; // Is the client currently logged in or not

	public App_Controller(final JavaFX_App_Template main, App_Model model, App_View view) {
		super(model, view);

		serviceLocator = ServiceLocator.getServiceLocator();
		serviceLocator.getLogger().info("Application controller initialized");

		// Disable at start:
		view.taskTitleTF.setDisable(true);
		view.taskDescriptionTA.setDisable(true);
		view.priorityCB.setDisable(true);
		view.saveTaskButton.setDisable(true);
		view.todoSelectionCB.setDisable(true);
		view.listToDosButton.setDisable(true);
		view.logInOutButton.setDisable(true);
		view.pingButton.setDisable(true);
		view.createNewAccountButton.setDisable(true);
		view.saveTaskButton.setDisable(true);
		view.changePasswordButton.setDisable(true);
		view.deleteToDoButton.setDisable(true);

		// Validate default-values at the beginning (if there are any)
		validateUserName(view.userNameTF.getText());
		validateIP(view.ipTF.getText());
		validatePort(view.portTF.getText());

		/**
		 * --------------------------------------------------------------------------------------------------
		 * Send messages to client on button-click
		 */
		view.pingButton.setOnAction(e -> {
			// If asked while logged in -> include token
			boolean addToken = loggedIn;
			if (addToken == true) {
				message = "Ping" + SEPARATOR + model.getToken();
			}
			if (addToken == false) {
				message = "Ping";
			}
			model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);

			if (model.getServerMessageParts()[1].equals("true"))
				view.statusLabel.setText("Connected to server");
			else
				view.statusLabel.setText("No connection to server");
		});

		view.createNewAccountButton.setOnAction(e -> {
			message = "CreateLogin" + SEPARATOR + view.userNameTF.getText() + SEPARATOR + view.passwordField.getText();
			model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);
			if (model.getServerMessageParts()[1].equals("true"))
				view.statusLabel.setText("Account created");
			else
				view.statusLabel.setText("User name already taken");
		});

		// Depending on logIn-status: button goes from logged out to logged in or vice
		// versa
		view.logInOutButton.setOnAction(e -> {
			boolean logInOutSwitch = loggedIn;
			try {
				if (logInOutSwitch == false) {
					message = "Login" + SEPARATOR + view.userNameTF.getText() + SEPARATOR
							+ view.passwordField.getText();
					model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);
					// if login was successful -> save the token which was received from the server
					// and adapt GUI
					if (model.getServerMessageParts()[1].equals("true")) {
						model.setToken(model.getServerMessageParts()[2]);
						loggedIn = switchLoginLogoutGUI(loggedIn);
						view.statusLabel.setText("Login successful");
					} else {
						view.statusLabel.setText("Invalid user name or password");
					}
				}
				if (logInOutSwitch == true) {
					message = "Logout";
					model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);
					loggedIn = switchLoginLogoutGUI(loggedIn);
					model.setToken(null); // delete token
					view.statusLabel.setText("Logged out");
					view.taskTitleTF.clear();
					view.taskDescriptionTA.clear();
				}
			} catch (Exception ex) {
				System.out.println("trouble");
			}
		});

		view.changePasswordButton.setOnAction(e -> {
			message = "ChangePassword" + SEPARATOR + model.getToken() + SEPARATOR + view.passwordField.getText();
			model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);
			if (model.getServerMessageParts()[1].equals("true"))
				view.statusLabel.setText("Password Changed");
		});

		view.saveTaskButton.setOnAction(e -> {
			if (view.taskDescriptionTA.getText() == "")
				view.taskDescriptionTA.setText("-");
			message = "CreateToDo" + SEPARATOR + model.getToken() + SEPARATOR + view.taskTitleTF.getText() + SEPARATOR
					+ view.priorityCB.getSelectionModel().getSelectedItem() + SEPARATOR
					+ view.taskDescriptionTA.getText();
			model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);
			view.taskTitleTF.clear();
			view.taskDescriptionTA.clear();
		});

		view.listToDosButton.setOnAction(e -> {
			message = "ListToDos" + SEPARATOR + model.getToken();
			model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);
			String todoList = "";

			// Update ComboBox data with ID of this user (only available/updated after
			// todo's are listed)
			view.todoSelectionCB.getItems().clear();
			for (int i = 2; i < model.getServerMessageParts().length; i++) {
				todoList += model.getServerMessageParts()[i] + " / ";
				view.todoSelectionCB.getItems().add(Integer.valueOf(model.getServerMessageParts()[i]));
			}
			view.todoDisplayTA.setText("ToDo ID's of " + view.userNameTF.getText() + ":\n\n" + todoList);
		});

		view.todoSelectionCB.setOnAction(e -> {
			// Only execute if an ID is selected
			if (view.todoSelectionCB.getSelectionModel().getSelectedItem() != null) {
				message = "GetToDo" + SEPARATOR + model.getToken() + SEPARATOR
						+ view.todoSelectionCB.getSelectionModel().getSelectedItem();
				model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);
				String todo = "";
				String line = "###################################################################\n";
				for (int i = 2; i < model.getServerMessageParts().length; i++) {
					todo += line + model.getServerMessageParts()[i] + "\n";
				}
				view.todoDisplayTA.setText(todo);
			}
		});

		view.deleteToDoButton.setOnAction(e -> {
			// Only execute if a todo ID is selected
			if (view.todoSelectionCB.getSelectionModel().getSelectedItem() != null) {
				message = "DeleteToDo" + SEPARATOR + model.getToken() + SEPARATOR
						+ view.todoSelectionCB.getSelectionModel().getSelectedItem();
				model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);
				view.todoDisplayTA.setText(model.getServerMessageParts()[1]);
			}
		});

		/**
		 * End of messaging
		 * ----------------------------------------------------------------------------------------------------------------------------------------
		 */

		view.userNameTF.textProperty().addListener((observable, oldValue, newValue) -> validateUserName(newValue));
		view.passwordField.textProperty().addListener((observable, oldValue, newValue) -> validatePassword(newValue));
		view.ipTF.textProperty().addListener((observable, oldValue, newValue) -> validateIP(newValue));
		view.portTF.textProperty().addListener((observable, oldValue, newValue) -> validatePort(newValue));
		view.taskTitleTF.textProperty().addListener((observable, oldValue, newValue) -> validateTitle(newValue));
		view.taskDescriptionTA.textProperty()
				.addListener((observable, oldValue, newValue) -> validateDescription(newValue));
	}

	// Change GUI depending on if user is logged in or logged out
	public boolean switchLoginLogoutGUI(boolean loggedIn) {
		// If user already logged in, do:
		boolean result;
		if (loggedIn) {
			// Login area:
			view.logInOutButton.setText("Log In");
			view.ipTF.setDisable(false);
			view.portTF.setDisable(false);
			view.userNameTF.setDisable(false);
			// view.passwordField.setDisable(false);
			view.createNewAccountButton.setDisable(false);
			view.changePasswordButton.setDisable(true);
			// ToDo area:
			view.taskTitleTF.setDisable(true);
			view.taskDescriptionTA.setDisable(true);
			view.priorityCB.setDisable(true);
			view.saveTaskButton.setDisable(true);
			view.todoSelectionCB.setDisable(true);
			view.listToDosButton.setDisable(true);
			view.deleteToDoButton.setDisable(true);
			view.todoDisplayTA.clear();
			result = false;
		}
		// If user not logged in, do:
		else {
			// Login area:
			view.logInOutButton.setText("Log Out");
			view.ipTF.setDisable(true);
			view.portTF.setDisable(true);
			view.userNameTF.setDisable(true);
			// view.passwordField.setDisable(true);
			view.createNewAccountButton.setDisable(true);
			view.changePasswordButton.setDisable(false);
			// ToDo area:
			view.taskTitleTF.setDisable(false);
			view.taskDescriptionTA.setDisable(false);
			view.priorityCB.setDisable(false);
			// (is already disables through change listener)
			// view.saveTaskButton.setDisable(false);
			view.todoSelectionCB.setDisable(false);
			view.listToDosButton.setDisable(false);
			view.deleteToDoButton.setDisable(false);
			result = true;
		}
		return result;
	}

	/**
	 * Input validation:
	 * ----------------------------------------------------------------------------------------------------------------------------
	 */
	private void validateUserName(String newValue) {
		boolean valid = false;
		// Split on '@': must give us two not-empty parts
		String[] addressParts = newValue.split("@");
		if (addressParts.length == 2 && !addressParts[0].isEmpty() && !addressParts[1].isEmpty()) {
			// We want to split the domain on '.', but split does not give us an empty
			// string, if the split-character is the last character in the string. So we
			// first ensure that the string does not end with '.'
			if (addressParts[1].charAt(addressParts[1].length() - 1) != '.') {
				// Split domain on '.': must give us at least two parts.
				// Each part must be at least two characters long
				String[] domainParts = addressParts[1].split("\\.");
				if (domainParts.length >= 2) {
					valid = true;
					for (String s : domainParts) {
						if (s.length() < 2)
							valid = false;
					}
				}
			}
		}

		TextField textField = view.userNameTF;
		updateTextColor(textField, valid);

		userNameValid = valid;
		enableDisableButton();
	}

	private void validatePassword(String newValue) {
		boolean valid = false;
		if (newValue.length() >= 3 && newValue.length() <= 20) {
			valid = true;
		}

		passwordValid = valid;
		enableDisableButton();
	}

	private void validateIP(String newValue) {
		boolean valid = false;
		// Split on '.'
		String[] ipParts = newValue.split("\\.");
		// must have exactly 4 parts and no point at beginning or end
		if (ipParts.length == 4 && newValue.charAt(0) != '.' && newValue.charAt(newValue.length() - 1) != '.') {
			valid = true;
			for (int i = 0; i < 4; i++) {
				// check if parts are between 1 and 4 long
				if (ipParts[i].length() > 3) {
					valid = false;
				}
			}
			for (int k = 0; k < newValue.length(); k++) {
				// check if it's only numbers
				if ((newValue.charAt(k) < '0' || newValue.charAt(k) > '9') && newValue.charAt(k) != '.') {
					valid = false;
				}
			}
		}

		TextField textField = view.ipTF;
		updateTextColor(textField, valid);

		ipValid = valid;
		enableDisableButton();
		enableDisablePingButton();
	}

	private void validatePort(String newValue) {
		boolean valid = false;
		boolean onlyNumbers = true;

		if (newValue.length() != 0) {

			for (int k = 0; k < newValue.length(); k++) {
				// check if it's only numbers
				if ((newValue.charAt(k) < '0' || newValue.charAt(k) > '9')) {
					onlyNumbers = false;
				}
			}

			if (onlyNumbers && Integer.parseInt(newValue) > 0 && Integer.parseInt(newValue) < 65536) {
				valid = true;
			}
		}

		TextField textField = view.portTF;
		updateTextColor(textField, valid);

		portValid = valid;
		enableDisableButton();
		enableDisablePingButton();
	}

	private void validateTitle(String newValue) {
		boolean valid = false;
		if (newValue.length() >= 3 && newValue.length() <= 20)
			valid = true;

		titleValid = valid;
		enableDisableSaveTaskButton();
	}

	private void validateDescription(String newValue) {
		boolean valid = false;
		if (newValue.length() >= 0 && newValue.length() <= 255)
			valid = true;

		descriptionValid = valid;
		enableDisableSaveTaskButton();
	}

	// Changes text color in selected text field depending on "valid" value
	private void updateTextColor(TextField textField, boolean valid) {
		textField.getStyleClass().remove("addressNotOK");
		textField.getStyleClass().remove("addressOK");
		if (valid) {
			textField.getStyleClass().add("addressOK");
		} else {
			textField.getStyleClass().add("addressNotOK");
		}
	}

	private void enableDisableButton() {
		boolean valid = userNameValid && passwordValid && ipValid && portValid;
		if (!loggedIn) {
			view.logInOutButton.setDisable(!valid);
			view.createNewAccountButton.setDisable(!valid);
		}
		if (loggedIn)
			view.changePasswordButton.setDisable(!valid);
	}

	private void enableDisablePingButton() {
		boolean valid = ipValid && portValid;
		view.pingButton.setDisable(!valid);
	}

	private void enableDisableSaveTaskButton() {
		boolean valid = titleValid && descriptionValid;
		view.saveTaskButton.setDisable(!valid);
	}

	/**
	 * End of input validation
	 * ----------------------------------------------------------------------------------------------------------------------------------------
	 */

}
