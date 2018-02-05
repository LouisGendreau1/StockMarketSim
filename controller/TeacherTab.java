package controller;

import java.io.IOException;
import java.sql.SQLException;

import application.Database;
import application.InternetConnectivity;
import application.MSML;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class TeacherTab extends AnchorPane {
	
	@FXML
	private TextField firstName;
	@FXML
	private TextField lastName;
	@FXML
	private PasswordField password;
	@FXML
	private PasswordField passwordRe;
	@FXML
	private TextField email;
	@FXML
	private TextField startingAmount;
	@FXML
	private Button submit;
	@FXML
	private ProgressIndicator progress;
	@FXML
	private Label emailFeedback;
	@FXML
	private Label passwordFeedback;
	@FXML
	private Label startingAmountFeedback;
	@FXML
	private Label allFieldFeedback;
	@FXML
	private Label passwordReFeedback;
	
	// Loads the TeacherTab fxml
	public TeacherTab() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TeacherTab.fxml"));
	    fxmlLoader.setRoot(this);
	    fxmlLoader.setController(this);
	    try {
	        fxmlLoader.load();
	    } catch (IOException exception) {
	        throw new RuntimeException(exception);
	    }
	}
	
	@FXML
	public void sendInfo(ActionEvent event) throws SQLException {
		emailFeedback.setVisible(false);
		passwordFeedback.setVisible(false);
		startingAmountFeedback.setVisible(false);
		allFieldFeedback.setVisible(false);
		passwordReFeedback.setVisible(false);
		// If all fields are filled
		if(firstName.getText().length() > 0 && lastName.getText().length() > 0 && email.getText().length() > 0 && password.getText().length() > 0 && passwordRe.getText().length() > 0 && startingAmount.getText().length() > 0) {
			if(emailLegit()) {
				if(passwordLegit()) {
					if(passwordReLegit()) {
						if(startingAmountLegit()) {
							progress.setVisible(true);
							submit.setVisible(false);
							// Starts a Thread to store infor in the database
							Thread t = new Thread();
							t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
								// When the Thread is finished, goes back to login
								@Override
								public void handle(WorkerStateEvent event) {
									try {
										Scene login = new Scene(FXMLLoader.load(getClass().getResource("/fxml/Login.fxml")));
										MSML.getStage().setScene(login);
										MSML.setScene(login);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							});
							t.start();
						}else {
							startingAmountFeedback.setVisible(true);
						}
					}else {
						passwordFeedback.setVisible(true);
					}
				}else {
					passwordFeedback.setVisible(true);
				}
			}else {
				emailFeedback.setVisible(true);
			}
		}else {
			allFieldFeedback.setVisible(true);
		}
	}
	
	// Check if the email is valid and does not exits in the database
	public boolean emailLegit() throws SQLException {
		if(email.getText().indexOf("@") != -1) {
			if(!email.getText().substring(email.getText().indexOf("@") + 1).contains("@")) {
				if(email.getText().substring(email.getText().indexOf("@")).contains(".")) {
					if(!InternetConnectivity.checkInternetConnectivity()) {
						this.setDisable(true);
					}else {
						Database.connectionDatabase();
						if(Database.emailExists(email.getText())) {
							return false;
						}else {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean passwordLegit() {
		if(password.getText().length() >= 8 && password.getText().length() <= 16) {
			if(password.getText().matches(".*\\d.*")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean passwordReLegit() {
		if(password.getText().equals(passwordRe.getText())) {
			return true;
		}
		return false;
	}
	
	public boolean startingAmountLegit() {
		try {
			Double.parseDouble(startingAmount.getText());
			if(Double.parseDouble(startingAmount.getText()) < 1000000001) {
				return true;
			}else {
				return false;
			}
		}catch(NumberFormatException e) {
			return false;
		}
	}
	
	public TeacherTab getOuter() {
		return this;
	}
	
	class Thread extends Service<Void> {
		// Sends the info to the database
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					if(!InternetConnectivity.checkInternetConnectivity()) {
						getOuter().setDisable(true);
					}else {
						Database.connectionDatabase();
						try {
							Database.newTeacher(firstName.getText(), lastName.getText(), email.getText(), password.getText(), Integer.parseInt(startingAmount.getText()));
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					this.succeeded();
					return null;
				}
			};
		}
	}
}
