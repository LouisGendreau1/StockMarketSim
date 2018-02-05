package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.Database;
import application.INU;
import application.InternetConnectivity;
import application.MSML;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class Login implements Initializable {

	@FXML
	private TextField email;
	@FXML
	private PasswordField password;
	@FXML
	private Text newUser;
	@FXML
	private Button login;
	@FXML
	private Text wrongEntries;
	@FXML
	private ProgressIndicator progressGUI;
	@FXML
	private Label noConnection;
	
	private static String[] stockNames = new String[499];
	
	String s;
	private static int id = -1;
	public static int userType;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		wrongEntries.setVisible(false);
		progressGUI.setProgress(-1);
	}
	
	// On click, check identifications
	@FXML
	public void logIn(ActionEvent event) throws SQLException {
		wrongEntries.setVisible(false);
		login.setVisible(false);
		progressGUI.setVisible(true);
		Thread t = new Thread();
		t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				if(s == "n") {
					noConnection.setVisible(true);
				}
				if(s.charAt(0) == 's') {
					Login.userType = 0;
				}else if(s.charAt(0) == 't') {
					Login.userType = 1;
				}
				if(Login.id != -1) {
					INUThread th = new INUThread();
					th.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							Scene home;
							try {
								home = new Scene(FXMLLoader.load(getClass().getResource("/fxml/Home.fxml")));
								MSML.getStage().setScene(home);
								MSML.setScene(home);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
					th.start();
				}else {
					login.setVisible(true);
					progressGUI.setVisible(false);
					wrongEntries.setVisible(true);
				}
			}
		});
		t.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				progressGUI.setVisible(false);
				login.setVisible(false);
				noConnection.setVisible(true);
			}
		});
		t.start();
	}
	
	// On click, goes to the NewUser screen
	@FXML
	public void newUser(MouseEvent event) {
		try {
			Scene newUser = new Scene(FXMLLoader.load(getClass().getResource("/fxml/NewUser.fxml")));
			MSML.getStage().setScene(newUser);
			MSML.setScene(newUser);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Changes the cursor to HAND on the newUser label
	@FXML
	public void mouseInNewUser(MouseEvent event) {
		newUser.setUnderline(true);
		MSML.getScene().setCursor(Cursor.HAND);
	}
	
	// Changes the cursor to DEFAULT on nothing
	@FXML
	public void mouseOutNewUser(MouseEvent event) {
		newUser.setUnderline(false);
		MSML.getScene().setCursor(Cursor.DEFAULT);
	}
	
	public static int getId() {
		return id;
	}
	
	public static int getUserType() {
		return userType;
	}
	
	public static String[] getStockNames() {
		return Login.stockNames;
	}

	// Check the indentifications
	class Thread extends Service<Void> {
		
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				
				@Override
				protected Void call() {
					if(!InternetConnectivity.checkInternetConnectivity()) {
						this.getState();
						this.failed();
						return null;
					}
					try {
						Database.connectionDatabase();
						s = Database.loginCheck(email.getText(), password.getText());
						Login.id = Integer.parseInt(s.substring(1));
						this.succeeded();
					}catch(Exception e) {
						s = "n";
					}
					return null;
				}
			};
		}
	}
	
	class INUThread extends Service<Void> {
		
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				
				@Override
				protected Void call() throws Exception {
					INU inu = new INU();
					while(inu.getPercentComplete() < .9) {
						
					}
					this.succeeded();
					return null;
				}
			};
		}
	}
	
}
