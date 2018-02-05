package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.MSML;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;

public class NewUser implements Initializable {

	@FXML
	private TabPane tabPane;
	@FXML
	private Button back;
	
	// Instantiate StudentTab and TeacherTab
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		tabPane.getTabs().get(0).setContent(new StudentTab());
		tabPane.getTabs().get(1).setContent(new TeacherTab());
	}
	
	// On click goes back to login
	public void backToLogin(ActionEvent event) {
		try {
			Scene login = new Scene(FXMLLoader.load(getClass().getResource("/fxml/Login.fxml")));
			MSML.getStage().setScene(login);
			MSML.setScene(login);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

