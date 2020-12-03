package toDoMiniProject.createAccount;

import toDoMiniProject.abstractClasses.View;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CreateAccount_View extends View<CreateAccount_Model> {

	Label accAreaTitle;
	Label taskAreaTitle;

	TextField userNameTF;

	PasswordField passwordField;

	Button createNewAccountButton;
	Button cancelButton;

	public CreateAccount_View(Stage stage, CreateAccount_Model model) {
		super(stage, model);
	}

	@Override
	protected Scene create_GUI() {
		// TODO make it able to switch languages
		
		VBox root = new VBox();
		root.getChildren().add(createAccountArea());

		Scene scene = new Scene(root);
		// scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
		return scene;
	}

	private GridPane createAccountArea() {
		GridPane pane = new GridPane();

		userNameTF = new TextField();
		userNameTF.setPromptText("User Name / E-Mail");
		passwordField = new PasswordField();
		passwordField.setPromptText("Password");
		createNewAccountButton = new Button("Create new Account");
		cancelButton = new Button("Cancel");

		pane.add(userNameTF, 0, 0);
		pane.add(passwordField, 1, 0);
		pane.add(createNewAccountButton, 0, 1);
		pane.add(cancelButton, 1, 1);

		return pane;
	}

}