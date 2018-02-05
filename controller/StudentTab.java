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

public class StudentTab extends AnchorPane {
	
	@FXML
	private TextField firstName;
	@FXML
	private TextField lastName;
	@FXML
	private PasswordField password;
	@FXML
	private TextField teacherId;
	@FXML
	private PasswordField passwordRe;
	@FXML
	private TextField email;
	@FXML
	private Button submit;
	@FXML
	private ProgressIndicator progress;
	@FXML
	private Label emailFeedback;
	@FXML
	private Label passwordReFeedback;
	@FXML
	private Label classFeedback;
	@FXML
	private Label allFieldFeedback;
	@FXML
	private Label passwordFeedback;
	
	// Loads the StudentTab fxml
	public StudentTab() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/StudentTab.fxml"));
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
		classFeedback.setVisible(false);
		allFieldFeedback.setVisible(false);
		passwordReFeedback.setVisible(false);
		passwordFeedback.setVisible(false);
		// If all fields are filled
		if(firstName.getText().length() > 0 && lastName.getText().length() > 0 && email.getText().length() > 0 && password.getText().length() > 0 && passwordRe.getText().length() > 0) {
			if(emailLegit()) {
				if(passwordLegit()) {
					if(passwordReLegit()) {
						if(teacherIdLegit()) {
							progress.setVisible(true);
							submit.setVisible(false);
							// Starts a Thread to store the info in the database
							Thread t = new Thread();
							t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
								// When the Thread is finished, goes back to login
								@Override
								public void handle(WorkerStateEvent event) {
									try {
										Scene login = new Scene(FXMLLoader.load(getClass().getResource("/fxml/Login.fxml")));
										MSML.getStage().setScene(login);
										MSML.setScene(login);
									}catch (IOException e) {
										e.printStackTrace();
									}
								}
							});
							t.start();
						}else {
							classFeedback.setVisible(true);
						}
					}else {
						passwordReFeedback.setVisible(true);
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
	
	// Checks if the field is an integer and if the teacher exists in the database
	public boolean teacherIdLegit() throws SQLException {
		try {
			Integer.parseInt(teacherId.getText());
			if(!InternetConnectivity.checkInternetConnectivity()) {
				this.setDisable(true);
			}else {
				Database.connectionDatabase();
				if(Database.checkTeacherId(Integer.parseInt(teacherId.getText()))) {
					return true;
				}
			}
		}catch(NumberFormatException e) {
			
		}
		return false;
	}
	
	public StudentTab getOuter() {
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
							Database.newStudent(firstName.getText(), lastName.getText(), Integer.parseInt(teacherId.getText()), email.getText(), password.getText());
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
