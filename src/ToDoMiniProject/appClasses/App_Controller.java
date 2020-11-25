package ToDoMiniProject.appClasses;

import ToDoMiniProject.JavaFX_App_Template;
import ToDoMiniProject.ServiceLocator;
import ToDoMiniProject.abstractClasses.Controller;
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
	private boolean ipValid;
	private boolean portValid;
	private boolean userNameValid;
	private boolean passwordValid;

	public App_Controller(final JavaFX_App_Template main, App_Model model, App_View view) {
		super(model, view);

		// register ourselves to listen for button clicks
		view.btnClick.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				buttonClick();
			}
		});

		// register ourselves to handle window-closing event
		view.getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				Platform.exit();
			}
		});

		// Open a new window to create new account; hide main window
		view.createNewAccountButton.setOnAction(e -> {
			view.getStage().hide();
			main.startCreateAccount();
		});

		serviceLocator = ServiceLocator.getServiceLocator();
		serviceLocator.getLogger().info("Application controller initialized");

		view.logInButton.setDisable(true);
		view.userNameTF.textProperty().addListener((observable, oldValue, newValue) -> validateUserName(newValue));
		view.passwordField.textProperty().addListener((observable, oldValue, newValue) -> validatePassword(newValue));
		view.ipTF.textProperty().addListener((observable, oldValue, newValue) -> validateIP(newValue));
		view.portTF.textProperty().addListener((observable, oldValue, newValue) -> validatePort(newValue));
	}

	public void buttonClick() {
		model.incrementValue();
		String newText = Integer.toString(model.getValue());

		view.lblNumber.setText(newText);
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
		if (newValue.length() >= 8 && newValue.length() <= 20) {
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
		view.logInButton.setDisable(!valid);
	}

	/**
	 * ----------------------------------------------------------------------------------------------------------------------------------------
	 */

}
