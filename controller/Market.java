package controller;

import java.io.IOException;

import application.MTU;
import application.StockInfo;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Market extends AnchorPane {

	@FXML
	private Accordion stocks;
	@FXML
	private TextField searchField;
	@FXML
	private AnchorPane barLength;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private RadioButton updateOption;
	
	MTU mtu;
	
	public Market() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Market.fxml"));
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
			    
			    // Implementation of the search engine
			    searchField.textProperty().addListener(new ChangeListener<String>(){
			    	
			    	double bar = 12690;
			    	
		            @Override
		            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		            	if(newValue.length() <= 0) {
		    				for(int i = 0; i < stocks.getPanes().size(); i++) {
		    					stocks.getPanes().get(i).setTranslateY(0);
		    					stocks.getPanes().get(i).setVisible(true);
		    					bar = 12690;
		    				}
		    			}else {
		    				bar = 0;
		    				int invisible = 0;
			        		for(int i = 0; i < stocks.getPanes().size(); i++) {
			        			stocks.getPanes().get(i).setVisible(true);
			        			if(stocks.getPanes().get(i).getText().contains(searchField.getText())) {
			        				stocks.getPanes().get(i).setTranslateY(-25 * invisible);
			        				bar += 25;
			        			}else {
			        				stocks.getPanes().get(i).setVisible(false);
			        				invisible++;
			        			}
			        		}
		    			}
		            	if(bar + 140 > 419) {
		            		barLength.setPrefHeight(bar + 140);
		            	}
		            }
		        });
			    
			    mtu = new MTU();
			    Timer t = new Timer();
			    t.start();
		    }
		});
		
		// Add a listener for opening the tab
	    for(int i = 0; i < stocks.getPanes().size(); i++) {
	    	stocks.getPanes().get(i).setId("white");
	    	int j = i;
	    	stocks.getPanes().get(j).expandedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if(newValue) {
						stocks.getPanes().get(j).setContent(new Stock(j));
					}
				}
	    	});
	    }
	}
	
	private void updateGui() throws NumberFormatException {
		for(int i = 0; i < stocks.getPanes().size(); i++) {
			StockInfo stock = Home.getStockInfo(i);
			Label value = new Label();
			value.setId("white");
			Label change = new Label();
			try {
				double ask = stock.getAsk();
				if(ask == 0) {
					value.setText(" - ");
				}else {
					value.setText(String.format("%.2f", ask));
				}
				value.setFont(Font.font("System", FontWeight.BOLD, 12));
				stocks.getPanes().get(i).setGraphicTextGap(getPositionForValue(i));
				stocks.getPanes().get(i).setGraphic(value);
				value.setContentDisplay(stocks.getPanes().get(i).getContentDisplay());
				value.setGraphicTextGap(getPositionForChange(i, value.getText()));
				String changeValue = stock.getChange();
				if(changeValue == "" || changeValue == null) {
					change.setText(" - ");
				}else {
					if(changeValue.charAt(0) == '-') {
						String s = "" + ((int)(Double.parseDouble(changeValue.substring(1, changeValue.indexOf('%'))) * 100)) / 100.0;
						if(s.substring(s.indexOf(".")).length() < 3) {
							s += "0";
						}
						change.setText(changeValue.charAt(0) + " " + s + "%");
						change.setTextFill(Color.RED);
					}else {
						String s = "" + ((int)(Double.parseDouble(changeValue.substring(0, changeValue.indexOf('%'))) * 100)) / 100.0;
						if(s.substring(s.indexOf(".")).length() < 3) {
							s += "0";
						}
						change.setText(changeValue.charAt(0) + "" + s + "%");
						change.setTextFill(Color.DARKGREEN);
					}
				}
				change.setFont(Font.font("System", FontWeight.BOLD, 12));
				value.setGraphic(change);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private double getPositionForValue(int i) {
		Text t = new Text();
		t.setText(stocks.getPanes().get(i).getText());
		double l = t.getLayoutBounds().getWidth();
		t.setVisible(false);
		return 370 - l;
	}
	
	private double getPositionForChange(int i, String s) {
		Text t = new Text();
		t.setText(s);
		double l = t.getLayoutBounds().getWidth();
		t.setVisible(false);
		return 260 - l;
	}
	
	public Accordion getStocks() {
		return stocks;
	}
	
	// Updates when the MTU finished
	class Timer extends AnimationTimer {
		
		@Override
		public void handle(long now) {
			
			if(updateOption.isSelected()) {
				progressBar.setProgress(mtu.getProgress() / 450.0);
				
				if(mtu.getProgress() > 450) {
					updateGui();
					mtu = new MTU();
				}
			}
		}
	}	
}
