package controller;

import java.io.IOException;
import java.sql.SQLException;

import application.MSML;
import application.StockInfo;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Portfolio extends AnchorPane {

	@FXML
	private Accordion stocks;
	@FXML
	private TextField searchField;
	@FXML
	private AnchorPane barLength;
	@FXML
	private TitledPane contentDisplayer;
	@FXML
	private ProgressIndicator loadingGui;
	
	int numberOfStock;
	
	public Portfolio() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Portfolio.fxml"));
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
		    	loadingGui.setVisible(true);
			    barLength.setPrefHeight(419);
			    Timer t = new Timer();
			    Thread th = new Thread();
			    th.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent event) {
						for(int i = 0; i < Home.getPortfolioContent().getContent().size(); i++) {
					    	TitledPane p = new TitledPane(Home.getPortfolioContent().getContent().get(i).getSymbol() + " (" + Home.getStockInfo(MSML.convertSymbolToId(Home.getPortfolioContent().getContent().get(i).getSymbol())).getName() + ")", null);
					    	p.setId("white");
					    	numberOfStock++;
					    	p.expandedProperty().addListener(new ChangeListener<Boolean>() {
								@Override
								public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
									if(newValue) {
										try {
											p.setContent(new StockInPortfolio(MSML.convertSymbolToId(p.getText().substring(0, p.getText().indexOf("(") - 1))));
										}catch (SQLException e) {
											
										}
									}
								}
					    	});
					    	stocks.getPanes().add(p);
					    }
						loadingGui.setVisible(false);
						if(numberOfStock * 26 + 140 > 419) {
							barLength.setPrefHeight(numberOfStock * 26 + 140);
						}
					}
				});
			    t.start();
			    th.start();

			    // Implementation of the search engine
			    searchField.textProperty().addListener(new ChangeListener<String>(){
			    	
			    	double bar = numberOfStock * 26;
			    	
		            @Override
		            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		            	if(newValue.length() <= 0) {
		    				for(int i = 0; i < stocks.getPanes().size(); i++) {
		    					stocks.getPanes().get(i).setTranslateY(0);
		    					stocks.getPanes().get(i).setVisible(true);
		    					bar = numberOfStock * 26;
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
		    }
		});	    
	}
	
	private void updateGui() throws NumberFormatException {
		for(int i = 0; i < stocks.getPanes().size(); i++) {
			StockInfo stock = Home.getStockInfo(MSML.convertSymbolToId(stocks.getPanes().get(i).getText().substring(0, stocks.getPanes().get(i).getText().indexOf("(") - 1)));
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
				stocks.getPanes().get(i).setContentDisplay(contentDisplayer.getContentDisplay());
				value.setContentDisplay(contentDisplayer.getContentDisplay());
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

	// Updates each 5 seconds
	class Timer extends AnimationTimer {
		
		long time = System.currentTimeMillis();
		
		@Override
		public void handle(long now) {
			if(System.currentTimeMillis() - time > 5000) {
				updateGui();
				time = System.currentTimeMillis();
			}
		}
	}
	
	class Thread extends Service<Void> {
		
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				
				@Override
				protected Void call() throws Exception {
					while(Home.getPortfolioContent().isLoading()) {
						
					}
					this.succeeded();
					return null;
				}
			};
		}
	}
}

