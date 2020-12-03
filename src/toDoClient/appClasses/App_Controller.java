package toDoClient.appClasses;

import toDoClient.JavaFX_App_Template;
import toDoClient.ServiceLocator;
import toDoClient.abstractClasses.Controller;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;

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
	private boolean loggedIn; // Is the client currently logged in or not

	public App_Controller(final JavaFX_App_Template main, App_Model model, App_View view) {
		super(model, view);
		loggedIn = false;
		// TODO - if I want
		// Open a new window to create new account; hide main window
		// view.createNewAccountButton.setOnAction(e -> {
		// view.getStage().hide();
		// main.startCreateAccount();
		// });

		/**
		 * --------------------------------------------------------------------------------------------------
		 * Send messages to client on button-click
		 */
		view.pingButton.setOnAction(e -> {
			// TODO: if-statement: if asked while logged in -> include token
			message = "Ping";
			try {
				model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);
			} catch (Exception ex) {
				System.out.println("No connection to Server");
			}
			// TODO: what to do if wrong ip/port? how to handle everything but "true"?
		});

		// TODO
		view.createNewAccountButton.setOnAction(e -> {
			message = "CreateLogin" + SEPARATOR + view.userNameTF.getText() + SEPARATOR + view.passwordField.getText();
			model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);
		});

		// Depending on logIn status: button does login or logout action
		// TODO: DOES NOT WORK CORRECTLY!
		boolean decider = loggedIn;
		if (decider = false) {
			view.logInOutButton.setOnAction(e -> {
				message = "Login" + SEPARATOR + view.userNameTF.getText() + SEPARATOR + view.passwordField.getText();
				System.out.println(loggedIn);
				model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);
				model.setToken(model.getServerMessageParts()[2]); // save the token which wasreceived from the server
				loggedIn = switchLoginLogoutGUI(loggedIn);
			});
		} else {
			System.out.println("was true...");
			view.logInOutButton.setOnAction(e -> {
				message = "Logout";
				System.out.println(loggedIn);
				model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);
				loggedIn = switchLoginLogoutGUI(loggedIn);
				model.setToken(null); // delete token
			});
		}

		
		/**
		 * view.logInOutButton.setOnAction(e -> { boolean decider = loggedIn;
		 * System.out.println("decider: " + decider); if (decider = false) { message =
		 * "Login" + SEPARATOR + view.userNameTF.getText() + SEPARATOR +
		 * view.passwordField.getText(); System.out.println(loggedIn);
		 * model.sendMessageToServer(view.ipTF.getText(),
		 * Integer.valueOf(view.portTF.getText()), message);
		 * model.setToken(model.getServerMessageParts()[2]); // save the token which was
		 * received from the server loggedIn = switchLoginLogoutGUI(loggedIn);
		 * System.out.println("u2?"); } if (decider = true) { System.out.println("was
		 * true..."); message = "Logout"; System.out.println(loggedIn);
		 * model.sendMessageToServer(view.ipTF.getText(),
		 * Integer.valueOf(view.portTF.getText()), message); loggedIn =
		 * switchLoginLogoutGUI(loggedIn); model.setToken(null); // delete token } });
		 */

		view.saveTaskButton.setOnAction(e -> {
			message = "CreateToDo" + SEPARATOR + model.getToken() + SEPARATOR + view.taskTitleTF.getText() + SEPARATOR
					+ view.priorityCB.getSelectionModel().getSelectedItem() + SEPARATOR
					+ view.taskDescriptionTA.getText();
			model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);
		});

		view.listToDosButton.setOnAction(e -> {
			message = "ListToDos" + SEPARATOR + model.getToken();
			model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);
		});

		view.getToDoButton.setOnAction(e -> {
			message = "GetToDo" + SEPARATOR + model.getToken() + SEPARATOR + "testID"; // get ID from a TextField or by
																						// selecting from list?
			model.sendMessageToServer(view.ipTF.getText(), Integer.valueOf(view.portTF.getText()), message);
		});

		/**
		 * End of messaging
		 * ----------------------------------------------------------------------------------------------------------------------------------------
		 */

		serviceLocator = ServiceLocator.getServiceLocator();
		serviceLocator.getLogger().info("Application controller initialized");

		view.logInOutButton.setDisable(true);
		view.userNameTF.textProperty().addListener((observable, oldValue, newValue) -> validateUserName(newValue));
		view.passwordField.textProperty().addListener((observable, oldValue, newValue) -> validatePassword(newValue));
		view.ipTF.textProperty().addListener((observable, oldValue, newValue) -> validateIP(newValue));
		view.portTF.textProperty().addListener((observable, oldValue, newValue) -> validatePort(newValue));
	}

//	public void buttonClick() {
//		model.incrementValue();
//		String newText = Integer.toString(model.getValue());
//
//		view.lblNumber.setText(newText);
//	}

	// Change GUI depending on if user is logged in or logged out
	public boolean switchLoginLogoutGUI(boolean loggedIn) {
		// if user already logged in, do:
		boolean result;
		if (loggedIn) {
			view.logInOutButton.setText("Log In");
			view.ipTF.setDisable(false);
			view.portTF.setDisable(false);
			view.userNameTF.setDisable(false);
			view.passwordField.setDisable(false);
			view.createNewAccountButton.setDisable(false);
			// ToDo area
			view.taskTitleTF.setDisable(true);
			// view.taskDescriptionTA.setDisable(true);
			// view.priorityCB.setDisable(true);
			view.saveTaskButton.setDisable(true);
			// view.displayTaskButton.setDisable(true);
			// view.listToDosButton.setDisable(true);
			result = false;
		}
		// if user not logged in, do:
		else {
			view.logInOutButton.setText("Log Out");
			view.ipTF.setDisable(true);
			view.portTF.setDisable(true);
			view.userNameTF.setDisable(true);
			view.passwordField.setDisable(true);
			view.createNewAccountButton.setDisable(true);
			// ToDo area
			view.taskTitleTF.setDisable(false);
			// view.taskDescriptionTA.setDisable(false);
			// view.priorityCB.setDisable(false);
			view.saveTaskButton.setDisable(false);
			// view.displayTaskButton.setDisable(false);
			// view.listToDosButton.setDisable(false);
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

	// TODO: specify more
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
	}

	private void validateTitle() {
		// TODO: 3-20 characters
		// other solution for description?
	}

	// maybe
	private void logInStatus(boolean tf) {
		// TODO: based on user logged in or out: activate/deactivate controls
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
		view.logInOutButton.setDisable(!valid);
	}

	/**
	 * End of input validation
	 * ----------------------------------------------------------------------------------------------------------------------------------------
	 */

}
