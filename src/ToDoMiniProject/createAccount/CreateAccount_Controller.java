package ToDoMiniProject.createAccount;

import ToDoMiniProject.JavaFX_App_Template;
import ToDoMiniProject.ServiceLocator;
import ToDoMiniProject.abstractClasses.Controller;

public class CreateAccount_Controller extends Controller<CreateAccount_Model, CreateAccount_View> {
	ServiceLocator serviceLocator;
	private boolean userNameValid;
	private boolean passwordValid;

	public CreateAccount_Controller(final JavaFX_App_Template main, CreateAccount_Model model,
			CreateAccount_View view) {
		super(model, view);

		// Check and save entered data and return to main window
		view.createNewAccountButton.setOnAction(e -> {
			// TODO: Do something with entered information
			System.out.println(view.userNameTF.getText());
			System.out.println(view.passwordField.getText());
			view.stop();
			main.showStage();
		});

		view.cancelButton.setOnAction(e -> {
			view.stop();
			main.showStage();
		});

		// If "create account" window is closed, return to the main window
		view.getStage().setOnCloseRequest(e -> {
			view.stop();
			main.showStage();
		});
		
		// TODO: Add input validation (somehow move it from App_Cotroller to abstract class?)
	}

}
