package toDoClient.appClasses;

import java.util.Locale;
import java.util.logging.Logger;

import toDoClient.ServiceLocator;
import toDoClient.abstractClasses.View;
import toDoClient.commonClasses.Translator;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Copyright 2015, FHNW, Prof. Dr. Brad Richards. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 * 
 * @author Brad Richards
 */
public class App_View extends View<App_Model> {
	Menu menuFile;
	Menu menuFileLanguage;
	Menu menuHelp;

	Label accAreaTitle;
	Label taskAreaTitle;
	Label statusLabel;

	TextField ipTF;
	TextField portTF;
	TextField userNameTF;
	TextField taskTitleTF;

	PasswordField passwordField;

	TextArea taskDescriptionTA;

	Button logInOutButton;
	Button createNewAccountButton;
	Button saveTaskButton;
	Button pingButton;
	Button listToDosButton;
	Button getToDoButton;

	private enum priority { // maybe move later
		LOW, MEDIUM, HIGH
	};

	ComboBox<priority> priorityCB;

	public App_View(Stage stage, App_Model model) {
		super(stage, model);
		ServiceLocator.getServiceLocator().getLogger().info("Application view initialized");
	}

	@Override
	protected Scene create_GUI() {
		ServiceLocator sl = ServiceLocator.getServiceLocator();
		Logger logger = sl.getLogger();

		MenuBar menuBar = new MenuBar();
		menuFile = new Menu();
		menuFileLanguage = new Menu();
		menuFile.getItems().add(menuFileLanguage);

		for (Locale locale : sl.getLocales()) {
			MenuItem language = new MenuItem(locale.getLanguage());
			menuFileLanguage.getItems().add(language);
			language.setOnAction(event -> {
				sl.getConfiguration().setLocalOption("Language", locale.getLanguage());
				sl.setTranslator(new Translator(locale.getLanguage()));
				updateTexts();
			});
		}

		menuHelp = new Menu();
		menuBar.getMenus().addAll(menuFile, menuHelp);

		VBox root = new VBox();
		root.getChildren().add(menuBar);

		root.getChildren().add(createAccountArea());
		root.getChildren().add(createTaskArea());

		updateTexts();

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
		return scene;
	}

	private GridPane createAccountArea() {
		GridPane pane = new GridPane();

		ipTF = new TextField("127.0.0.1"); // change later
		// ipTF.setId("ipTF");
		ipTF.setPromptText("IP Address");
		portTF = new TextField("50001"); // change later
		portTF.setPromptText("Port");
		userNameTF = new TextField("aa.bb@cc.dd");
		// userNameTF.setPromptText();
		passwordField = new PasswordField();
		passwordField.setPromptText("Password");
		logInOutButton = new Button("Log In");
		createNewAccountButton = new Button();
		pingButton = new Button("Ping");
		statusLabel = new Label("   ");

		pane.add(ipTF, 0, 0);
		pane.add(portTF, 1, 0);
		pane.add(pingButton, 2, 0);
		pane.add(userNameTF, 0, 1);
		pane.add(passwordField, 1, 1);
		pane.add(logInOutButton, 2, 1);
		pane.add(statusLabel, 3, 1);
		pane.add(createNewAccountButton, 0, 2);

		return pane;
	}

	private GridPane createTaskArea() {
		GridPane pane = new GridPane();

		taskAreaTitle = new Label("Tasks:");
		taskTitleTF = new TextField();
		taskTitleTF.setPromptText("Enter Title");
		priorityCB = new ComboBox<>();
		priorityCB.setPromptText("Select Priority");
		taskDescriptionTA = new TextArea();
		taskDescriptionTA.setPromptText("Enter Task Description");
		saveTaskButton = new Button("Save Task");
		listToDosButton = new Button("List To Do's");
		getToDoButton = new Button("Get To Do");

		priorityCB.getItems().addAll(priority.values());

		pane.add(taskAreaTitle, 0, 0);
		pane.add(taskTitleTF, 0, 1);
		pane.add(priorityCB, 1, 1);
		pane.add(taskDescriptionTA, 0, 2);
		pane.add(saveTaskButton, 0, 3);
		pane.add(listToDosButton, 1, 3);
		pane.add(getToDoButton, 2, 3);

		return pane;
	}

	protected void updateTexts() {
		Translator t = ServiceLocator.getServiceLocator().getTranslator();

		// The menu entries
		menuFile.setText(t.getString("program.menu.file"));
		menuFileLanguage.setText(t.getString("program.menu.file.language"));
		menuHelp.setText(t.getString("program.menu.help"));

		// Buttons
		createNewAccountButton.setText(t.getString("button.createNewAccount"));

		// TextFields
		userNameTF.setPromptText(t.getString("textField.username"));

		// TODO: for all texts

		stage.setTitle(t.getString("program.name"));
	}

}