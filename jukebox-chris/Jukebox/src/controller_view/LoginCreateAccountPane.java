
/**
 * AUTHOR: Chris Castillo, Anisha Munjal 
 * FILE: LoginCreateAccountPane.java 
 * ASSIGNMENT: The Cashless Jukebox
 * COURSE: CSC 335 Spring 2023
 * PURPOSE: This class implements the LoginCreateAccountPane. It contains
 * methods for creating the pane that is used to collect credentials for
 * Jukebox accounts. 
 */

package controller_view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.JukeboxAccount;

public class LoginCreateAccountPane extends GridPane {

	private Label labelUsername = new Label("Username");
	private Label labelPassword = new Label("Password");
	private Button buttonLogin = new Button("Login");
	private Button buttonCreateAccount = new Button("Create Account");
	private TextField textUsername = new TextField();
	private PasswordField textPassword = new PasswordField();

	private GridPane pane;
	private JukeboxAccount account;

	Stage stage;

	/**
	 * Constructor for LoginCreateAccountPane class
	 * 
	 * @param stage, the Stage for adding LoginCreateAccountPane
	 */
	public LoginCreateAccountPane(Stage stage) {
		this.stage = stage;
		setupLoginPane();

		// create new scene
		Scene scene = new Scene(new StackPane(pane), 200, 100);

		// set title for the stage
		stage.setTitle("Login or Create Account");
		stage.setScene(scene);

		this.add(pane, 0, 0);

		// register handlers
		registerHandlers();
	}

	/**
	 * Get username and password from text files and authenticate user
	 * 
	 * @param newAccount, boolean true if new account is to be created
	 */
	private void authenticateUser(boolean newAccount) {
		String name = textUsername.getText();
		String password = textPassword.getText();

		// validate length of username and password entered by student
		if (name.length() == 0 || password.length() == 0) {
			return;
		}
		account = new JukeboxAccount(name, password, newAccount);

		if (account.getAccountAuthenticationStatus() == false) {
			account = null;
		}
	}

	/**
	 * Get current user that is logged in
	 * 
	 * @return JukeboxAccount, the Jukebox account of user that is logged in
	 */
	public JukeboxAccount getCurrentUserLoggedIn() {
		return account;
	}

	/**
	 * Register handlers for LoginCreateAccountPane
	 */
	private void registerHandlers() {
		buttonLogin.setOnAction((event) -> {
			// authenticate existing user
			authenticateUser(false);
		});

		buttonCreateAccount.setOnAction((event) -> {
			// create new Jukebox account
			authenticateUser(true);
		});
	}

	/**
	 * Setup layout for obtaining student user name and password
	 */
	private void setupLoginPane() {
		// create new GridPane
		pane = new GridPane();

		// set horizontal gap on the GridPane
		pane.setHgap(10);

		// set vertical gap on the GridPane
		pane.setVgap(10);

		// add UI controls to the GridPane
		pane.add(labelUsername, 1, 1);
		pane.add(labelPassword, 1, 2);
		pane.add(textUsername, 2, 1);
		pane.add(textPassword, 2, 2);
		pane.add(buttonLogin, 1, 3);
		pane.add(buttonCreateAccount, 2, 3);
	}
}