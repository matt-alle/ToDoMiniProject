package toDoClient.appClasses;

import java.util.Locale;
import java.util.logging.Logger;

import toDoClient.ServiceLocator;
import toDoClient.abstractClasses.View;
import toDoClient.commonClasses.Translator;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollBar;
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

	Label taskAreaTitle;
	Label statusTitle;
	Label statusLabel;
	Label selectID;

	TextField ipTF;
	TextField portTF;
	TextField userNameTF;
	TextField taskTitleTF;

	PasswordField passwordField;

	TextArea taskDescriptionTA;
	TextArea todoDisplayTA;

	Button logInOutButton;
	Button createNewAccountButton;
	Button saveTaskButton;
	Button pingButton;
	Button listToDosButton;
	Button changePasswordButton;
	Button deleteToDoButton;

	ComboBox<priority> priorityCB;
	ComboBox<Integer> todoSelectionCB;

	ScrollBar scrollBar;

	private enum priority { // maybe move later
		LOW, MEDIUM, HIGH
	};

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
		root.setId("root");
		root.getChildren().add(menuBar);

		root.getChildren().add(createAccountArea());
		root.getChildren().add(createTaskArea());

		root.setSpacing(15);

		updateTexts();

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
		return scene;
	}

	private GridPane createAccountArea() {
		GridPane pane = new GridPane();

		ipTF = new TextField("127.0.0.1");
		portTF = new TextField("50001");
		userNameTF = new TextField();
		passwordField = new PasswordField();
		logInOutButton = new Button();
		createNewAccountButton = new Button();
		pingButton = new Button();
		changePasswordButton = new Button();
		statusTitle = new Label();
		statusLabel = new Label();

		createNewAccountButton.setId("createNewAccountButton");
		statusLabel.setId("statusLabel");
		changePasswordButton.setId("changePasswordButton");

		pane.add(ipTF, 0, 0);
		pane.add(portTF, 1, 0);
		pane.add(pingButton, 2, 0);
		pane.add(userNameTF, 0, 1);
		pane.add(passwordField, 1, 1);
		pane.add(logInOutButton, 2, 1);
		pane.add(createNewAccountButton, 0, 2);
		pane.add(changePasswordButton, 1, 2);
		pane.add(statusTitle, 2, 2);
		pane.add(statusLabel, 2, 3);

		GridPane.setMargin(ipTF, new Insets(5, 5, 5, 5));
		GridPane.setMargin(portTF, new Insets(5, 5, 5, 5));
		GridPane.setMargin(userNameTF, new Insets(5, 5, 5, 5));
		GridPane.setMargin(passwordField, new Insets(5, 5, 5, 5));
		GridPane.setMargin(pingButton, new Insets(5, 5, 5, 5));
		GridPane.setMargin(logInOutButton, new Insets(5, 5, 5, 5));
		GridPane.setMargin(changePasswordButton, new Insets(5, 5, 5, 5));
		GridPane.setMargin(createNewAccountButton, new Insets(5, 5, 5, 5));
		GridPane.setMargin(statusTitle, new Insets(5, 5, 5, 0));
		// GridPane.setMargin(statusLabel, new Insets(0, 0, 0, 0));

		pingButton.setPrefWidth(70);
		logInOutButton.setPrefWidth(70);
		createNewAccountButton.setPrefWidth(140);
		changePasswordButton.setPrefWidth(120);

		return pane;
	}

	private GridPane createTaskArea() {
		GridPane pane = new GridPane();

		taskAreaTitle = new Label();
		taskTitleTF = new TextField();
		// todoIDTF = new TextField();
		priorityCB = new ComboBox<>();
		taskDescriptionTA = new TextArea();
		taskDescriptionTA.setWrapText(true);
		todoDisplayTA = new TextArea();
		todoDisplayTA.setWrapText(true);
		todoDisplayTA.setEditable(false);
		saveTaskButton = new Button();
		listToDosButton = new Button();
		deleteToDoButton = new Button();
		scrollBar = new ScrollBar();
		selectID = new Label();
		todoSelectionCB = new ComboBox<>();

		priorityCB.getItems().addAll(priority.values());

		pane.add(taskAreaTitle, 0, 0);
		pane.add(taskTitleTF, 0, 1, 3, 1);
		pane.add(priorityCB, 3, 1);
		pane.add(taskDescriptionTA, 0, 2, 4, 1);
		pane.add(saveTaskButton, 0, 3);
		pane.add(listToDosButton, 0, 4);
		pane.add(selectID, 1, 4);
		pane.add(todoSelectionCB, 2, 4);
		pane.add(deleteToDoButton, 3, 4);
		pane.add(todoDisplayTA, 0, 5, 4, 1);

		GridPane.setMargin(taskAreaTitle, new Insets(5, 5, 5, 5));
		GridPane.setMargin(taskTitleTF, new Insets(5, 5, 5, 5));
		GridPane.setMargin(priorityCB, new Insets(5, 5, 5, 5));
		GridPane.setMargin(taskDescriptionTA, new Insets(5, 5, 5, 5));
		GridPane.setMargin(todoDisplayTA, new Insets(5, 5, 5, 5));
		GridPane.setMargin(saveTaskButton, new Insets(5, 5, 5, 5));
		GridPane.setMargin(listToDosButton, new Insets(20, 5, 5, 5));
		GridPane.setMargin(deleteToDoButton, new Insets(20, 5, 5, 35));
		GridPane.setMargin(selectID, new Insets(20, 0, 5, 90));
		GridPane.setMargin(todoSelectionCB, new Insets(20, 5, 5, 0));
		GridPane.setMargin(priorityCB, new Insets(5, 5, 5, 0));

		taskTitleTF.setPrefWidth(300);
		priorityCB.setPrefWidth(90);
		saveTaskButton.setPrefWidth(140);
		listToDosButton.setPrefWidth(140);
		deleteToDoButton.setPrefWidth(120);
		selectID.setPrefWidth(50);
		todoSelectionCB.setPrefWidth(60);
		taskDescriptionTA.setPrefHeight(100);
		todoDisplayTA.setPrefWidth(120);
		todoDisplayTA.setPrefHeight(200);

		return pane;
	}

	protected void updateTexts() {
		Translator t = ServiceLocator.getServiceLocator().getTranslator();

		// The menu entries
		menuFile.setText(t.getString("program.menu.file"));
		menuFileLanguage.setText(t.getString("program.menu.file.language"));
		menuHelp.setText(t.getString("program.menu.help"));

		// Labels
		taskAreaTitle.setText(t.getString("label.taskAreaTitle"));
		statusTitle.setText(t.getString("label.statusTitle"));
		statusLabel.setText(t.getString("label.statusLabel"));
		selectID.setText(t.getString("label.selectID"));

		// Text Fields
		ipTF.setPromptText(t.getString("textField.ipTF"));
		portTF.setPromptText(t.getString("textField.portTF"));
		userNameTF.setPromptText(t.getString("textField.userNameTF"));
		taskTitleTF.setPromptText(t.getString("textField.taskTitleTF"));

		// Text Area
		taskDescriptionTA.setPromptText(t.getString("textArea.taskDescriptionTA"));

		// Buttons
		logInOutButton.setText(t.getString("button.logInOutButton"));
		createNewAccountButton.setText(t.getString("button.createNewAccount"));
		saveTaskButton.setText(t.getString("button.saveTaskButton"));
		pingButton.setText(t.getString("button.pingButton"));
		listToDosButton.setText(t.getString("button.listToDosButton"));
		changePasswordButton.setText(t.getString("button.changePasswordButton"));
		deleteToDoButton.setText(t.getString("button.deleteToDoButton"));

		stage.setTitle(t.getString("program.name"));
	}

}