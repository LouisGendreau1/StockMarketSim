package controller;

import java.io.IOException;
import java.util.ArrayList;

import application.Database;
import application.Entry;
import application.InternetConnectivity;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class StudentHistory extends AnchorPane {
	
	@FXML
	private TableView<Entry> historyTable;
	@FXML
	private TableColumn<Entry, Integer> idCol;
	@FXML
	private TableColumn<Entry, Integer> quantityCol;
	@FXML
	private TableColumn<Entry, Double> askCol;
	@FXML
	private TableColumn<Entry, String> timeCol;
	@FXML
	private TableColumn<Entry, String> typeCol;
	@FXML
	private TableColumn<Entry, String> symbolCol;
	
	public ObservableList<Entry> data = FXCollections.observableArrayList();
	
	public int userId;
	
	// Loads the HistoryTab fxml
	public StudentHistory(int id) {
		this.userId = id;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/StudentHistory.fxml"));
	    fxmlLoader.setRoot(this);
	    fxmlLoader.setController(this);
	    try {
	        fxmlLoader.load();
	    } catch (IOException exception) {
	        throw new RuntimeException(exception);
	    }
	    
	    // Increase Performance
	    Platform.runLater(new Runnable() {
		    public void run() {
		    	historyTable.getItems().clear();
		    	idCol.setCellValueFactory(new PropertyValueFactory<Entry, Integer>("Id"));
				quantityCol.setCellValueFactory(new PropertyValueFactory<Entry, Integer>("Quantity"));
				askCol.setCellValueFactory(new PropertyValueFactory<Entry, Double>("AskPrice"));
				timeCol.setCellValueFactory(new PropertyValueFactory<Entry, String>("OrderTime"));
				typeCol.setCellValueFactory(new PropertyValueFactory<Entry, String>("OrderType"));
				symbolCol.setCellValueFactory(new PropertyValueFactory<Entry, String>("Symbol"));
				historyTable.setItems(data);
				
				if(!InternetConnectivity.checkInternetConnectivity()) {
					Home.setConnected(false);
				}else {
					Database.connectionDatabase();
					ArrayList<Entry> entries = new ArrayList<Entry>();
					try {
						entries = Database.getHistory(userId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					for(int i = 0; i < entries.size(); i++) {
						data.add(entries.get(i));
					}
				}
		    }
	    });
	}
}

