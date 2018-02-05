package controller;

import java.io.IOException;
import java.sql.SQLException;

import application.Database;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class THistory extends AnchorPane {

	@FXML
	private Accordion histories;
	@FXML
	private TextField searchField;
	@FXML
	private AnchorPane barLength;
	@FXML
	private TitledPane contentDisplayer;
	
	int numberOfHistory;
	
	public THistory() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/THistory.fxml"));
	    fxmlLoader.setRoot(this);
	    fxmlLoader.setController(this);
	    try {
	        fxmlLoader.load();
	    } catch (IOException exception) {
	        throw new RuntimeException(exception);
	    }

	 // Smooths the GUI while loading
		Platform.runLater(new Runnable() {
		    public void run() {
			    barLength.setPrefHeight(419);
			    Database.connectionDatabase();
				for(int i = 0; i < Home.getList().size(); i++) {
			    	TitledPane p;
					try {
						p = new TitledPane(Database.getName(Home.getList().get(i), 0) + " " + Database.getLastName(Home.getList().get(i), 0) + " (" + Home.getList().get(i) + ")", null);
						p.setId("white");
						p.expandedProperty().addListener(new ChangeListener<Boolean>() {
							@Override
							public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
								if(newValue) {
									StudentHistory s = new StudentHistory(Integer.parseInt(p.getText().substring(p.getText().indexOf("(") + 1, p.getText().length() - 1)));
									p.setContent(s);
									Home.updateStudentClicked(s.userId);
								}
							}
				    	});
						histories.getPanes().add(p);
					} catch (SQLException e) {
						e.printStackTrace();
					}
			    	numberOfHistory++;
			    }
				if(numberOfHistory * 26 + 140 > 419) {
					barLength.setPrefHeight(numberOfHistory * 26 + 140);
				}

			    // Implementation of the search engine
			    searchField.textProperty().addListener(new ChangeListener<String>(){
			    	
			    	double bar = numberOfHistory * 26;
			    	
		            @Override
		            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		            	if(newValue.length() <= 0) {
		    				for(int i = 0; i < histories.getPanes().size(); i++) {
		    					histories.getPanes().get(i).setTranslateY(0);
		    					histories.getPanes().get(i).setVisible(true);
		    					bar = numberOfHistory * 26;
		    				}
		    			}else {
		    				bar = 0;
		    				int invisible = 0;
			        		for(int i = 0; i < histories.getPanes().size(); i++) {
			        			histories.getPanes().get(i).setVisible(true);
			        			if(histories.getPanes().get(i).getText().contains(searchField.getText())) {
			        				histories.getPanes().get(i).setTranslateY(-25 * invisible);
			        				bar += 25;
			        			}else {
			        				histories.getPanes().get(i).setVisible(false);
			        				invisible++;
			        			}
			        		}
		    			}
		            	if(bar + 140 > 419) {
		            		barLength.setPrefHeight(bar + 140);
		            	}
		            }
		        });
			    updateGui();
		    }
		});
		
	}
	
	private void updateGui() throws NumberFormatException {
		for(int i = 0; i < histories.getPanes().size(); i++) {
			Label balance = new Label();
			balance.setId("white");
			Label value = new Label();
			value.setId("white");
			Database.connectionDatabase();
			try {
				double d = Database.getBalance(Home.getList().get(i));
				if(d == 0) {
					balance.setText(" - ");
				}else {
					balance.setText(String.format("%.2f", d));
				}
				balance.setFont(Font.font("System", FontWeight.BOLD, 12));
				histories.getPanes().get(i).setGraphicTextGap(getPositionForValue(i));
				histories.getPanes().get(i).setGraphic(balance);
				histories.getPanes().get(i).setContentDisplay(contentDisplayer.getContentDisplay());
				balance.setContentDisplay(contentDisplayer.getContentDisplay());
				balance.setGraphicTextGap(getPositionForChange(i, balance.getText()));
				double k = Database.getPortfolioValue(Home.getList().get(i));
				if(k == 0) {
					value.setText(" - ");
				}else {
					value.setText(String.format("%.2f", k));
				}
				value.setFont(Font.font("System", FontWeight.BOLD, 12));
				balance.setGraphic(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private double getPositionForValue(int i) {
		Text t = new Text();
		t.setText(histories.getPanes().get(i).getText());
		double l = t.getLayoutBounds().getWidth();
		t.setVisible(false);
		return 350 - l;
	}
	
	private double getPositionForChange(int i, String s) {
		Text t = new Text();
		t.setText(s);
		double l = t.getLayoutBounds().getWidth();
		t.setVisible(false);
		return 260 - l;
	}
}

