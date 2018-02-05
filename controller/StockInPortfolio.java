package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import application.StockInfo;
import controller.Home;
import application.InternetConnectivity;
import application.MSML;
import application.SSU;
import application.Database;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class StockInPortfolio extends StackPane {

	@FXML
	private AnchorPane gui;
	@FXML
	private ProgressIndicator loadingBar;
	@FXML
	private ProgressIndicator sellProgress;
	@FXML
	private Label name;
	@FXML
	private Label ask;
	@FXML
	private Label change;
	@FXML
	private Label bid;
	@FXML
	private Label open;
	@FXML
	private Label high;
	@FXML
	private Label low;
	@FXML
	private Label vol;
	@FXML
	private Label mktCap;
	@FXML
	private Label close;
	@FXML
	private Label yearHigh;
	@FXML
	private Label yearLow;
	@FXML
	private Label PERatio;
	@FXML
	private Label total;
	@FXML
	private Button plus;
	@FXML
	private Button minus;
	@FXML
	private TextField amount;
	@FXML
	private Button sell;
	@FXML
	private Label amountOwned;
	@FXML
	private Label totalOwned;
	@FXML
	private Label adjustedPrice;
	@FXML
	private Label profitLoss;
	@FXML
	private Label percent;
	
	int stockId;
	StockInfo stock;
	SSU ssu;
	String symbol;
	String prev = "";
	int quantity;
	double value;
	
	// Load the fxml and create variables
	public StockInPortfolio(int stockId) throws SQLException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/StockInPortfolio.fxml"));
	    fxmlLoader.setRoot(this);
	    fxmlLoader.setController(this);
	    this.stockId = stockId;
	    stock = Home.getStockInfo(stockId);
	    ssu = new SSU(stockId);
	    if(!InternetConnectivity.checkInternetConnectivity()) {
			Home.setConnected(false);
		}else {
		    Database.connectionDatabase();
		    quantity = Database.getQuantity(Home.getId(), MSML.STOCK_NAME[stockId]);
		    value = Database.getAdjustedPrice(Home.getId(), MSML.STOCK_NAME[stockId]);
		}
	    Timer t = new Timer();
	    t.start();
	    try {
	        fxmlLoader.load();
	    } catch (IOException exception) {
	        throw new RuntimeException(exception);
	    }
	    name.setText(" - ");
		ask.setText(" - ");
		change.setText(" - ");
		bid.setText(" - ");
		open.setText(" - ");
		high.setText(" - ");
		low.setText(" - ");
		vol.setText(" - ");
		mktCap.setText(" - ");
		close.setText(" - ");
		yearHigh.setText(" - ");
		yearLow.setText(" - ");
		PERatio.setText(" - ");
		amount.setText("0");
		total.setText("0.0");
		amountOwned.setText("0");
		totalOwned.setText("0.0");
		adjustedPrice.setText("0.0");
		profitLoss.setText("0.0");
		percent.setText("(+0.0%)");
		gui.setVisible(false);
		loadingBar.setVisible(true);
		
		// Add behavior when amount text field is changed
		amount.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	if(!oldValue.equalsIgnoreCase(newValue)) {
            		try {
            			total.setTextFill(Color.WHITE);
            			if(amount.getText().length() > 0) {
	        				if(Integer.parseInt(amount.getText()) > 0 && Integer.parseInt(amount.getText()) <= quantity) {
	        					
	        				}else {
	        					amount.setText(quantity + "");
	        					total.setText("0.0");
	        				}
	        				total.setText(String.format("%.2f", Integer.parseInt(amount.getText()) * Double.parseDouble(ask.getText())));
        					double k = (stock.getAsk() - value) * Integer.parseInt(amount.getText());
        					profitLoss.setText(String.format("%.2f", k));
        					double d = (stock.getAsk() - value) / value;
        					if(d < 0) {
        						percent.setText("(" + String.format("%.2f", d) + "%)");
        						profitLoss.setTextFill(Color.RED);
        						percent.setTextFill(Color.RED);
        					}else {
        						percent.setText("(+" + String.format("%.2f", d) + "%)");
        						profitLoss.setTextFill(Color.DARKGREEN);
        						percent.setTextFill(Color.DARKGREEN);
        					}
            			}else {
            				amount.setText("0");
            				total.setText("0.0");
            			}
        			}catch(NumberFormatException e) {
        				amount.setText("0");
        				total.setText("0.0");
        			}
            	}
            }
        });
	}
	
	@FXML
	public void add(ActionEvent event) {
		total.setTextFill(Color.WHITE);
		try {
			if(amount.getText().length() == 0) {
				amount.setText("1");
			}else if(Integer.parseInt(amount.getText()) <= quantity){
				amount.setText(Integer.parseInt(amount.getText()) + 1 + "");
			}else {
				amount.setText(quantity + "");
			}
			total.setText(String.format("%.2f", Integer.parseInt(amount.getText()) * Double.parseDouble(ask.getText())));
			double k = (stock.getAsk() - value) * Integer.parseInt(amount.getText());
			profitLoss.setText(String.format("%.2f", k));
			double d = (stock.getAsk() - value) / value;
			if(d < 0) {
				percent.setText("(" + String.format("%.2f", d) + "%)");
				profitLoss.setTextFill(Color.RED);
				percent.setTextFill(Color.RED);
			}else {
				percent.setText("(+" + String.format("%.2f", d) + "%)");
				profitLoss.setTextFill(Color.DARKGREEN);
				percent.setTextFill(Color.DARKGREEN);
			}
		}catch(NumberFormatException e) {
			
		}
	}
	
	@FXML
	public void subtract(ActionEvent event) {
		total.setTextFill(Color.WHITE);
		try {
			if(Integer.parseInt(amount.getText()) > 0) {
				amount.setText(Integer.parseInt(amount.getText()) - 1 + "");
				total.setText(String.format("%.2f", Integer.parseInt(amount.getText()) * Double.parseDouble(ask.getText())));
				double k = (stock.getAsk() - value) * Integer.parseInt(amount.getText());
				profitLoss.setText(String.format("%.2f", k));
				double d = (stock.getAsk() - value) / value;
				if(d < 0) {
					percent.setText("(" + String.format("%.2f", d) + "%)");
					profitLoss.setTextFill(Color.RED);
					percent.setTextFill(Color.RED);
				}else {
					percent.setText("(+" + String.format("%.2f", d) + "%)");
					profitLoss.setTextFill(Color.DARKGREEN);
					percent.setTextFill(Color.DARKGREEN);
				}
			}
		}catch(NumberFormatException e) {
			
		}
	}
	
	// Add the stock to the StockList table
	@FXML
	public void sellStock(ActionEvent event) throws SQLException {
		if(Double.parseDouble(total.getText()) > 0) {
			sellProgress.setVisible(true);
			sell.setVisible(false);
			Thread t = new Thread();
			t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					sellProgress.setProgress(1);
					sellProgress.setVisible(false);
					sell.setVisible(true);
					sellProgress.setProgress(-1);
					try {
						Home.updateBalance();
						Home.updateHistory();
						if(t.deleteStock) {
							Home.updatePortfolio();
						}else {
							updateGui();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			});
			t.start();
		}else {
			total.setTextFill(Color.RED);
		}
	}
	
	// Update the GUI with the new values of the stock
	public void updateGui() {
		if(stock.getName() != "" && stock.getName() != null) {
			name.setText(stock.getName());
		}
		if(stock.getAsk() != 0) {
			ask.setText(String.format("%.2f", stock.getAsk()));
		}if(stock.getChange() != "" && stock.getChange() != null) {
			change.setText(stock.getChange().charAt(0) + String.format("%.2f", Double.parseDouble(stock.getChange().substring(1, stock.getChange().length() - 1))) + stock.getChange().charAt(stock.getChange().length() -1));
			if(change.getText().charAt(0) == '+') {
				change.setTextFill(Color.DARKGREEN);
			}else {
				change.setTextFill(Color.RED);
			}
		}
		if(stock.getBid() != 0) {
			bid.setText(stock.getBid() + "");
		}
		if(stock.getMktCap() != "" && stock.getMktCap() != null) {
			mktCap.setText(stock.getMktCap());
		}
		if(stock.getOpen() != 0) {
			open.setText(stock.getOpen() + "");
		}
		if(stock.getClose() != 0) {
			close.setText(stock.getClose() + "");
		}
		if(stock.getHigh() != 0) {
			high.setText(stock.getHigh() + "");
		}
		if(stock.getYearHigh() != 0) {
			yearHigh.setText(stock.getYearHigh() + "");
		}
		if(stock.getLow() != 0) {
			low.setText(stock.getLow() + "");
		}
		if(stock.getYearLow() != 0) {
			yearLow.setText(stock.getYearLow() + "");
		}
		if(stock.getVolume() != 0) {
			vol.setText(stock.getVolume() + "");
		}
		if(stock.getPERatio() != 0) {
			PERatio.setText(stock.getPERatio() + "");
		}
		amountOwned.setText(quantity + "");
		totalOwned.setText((quantity * stock.getAsk()) + "");
		adjustedPrice.setText(String.format("%.2f", value));
	}
	
	// Updates when the SSU finished
	class Timer extends AnimationTimer {

		@Override
		public void handle(long now) {
			if(ssu.getDone()) {
				ssu = new SSU(stockId);
				ssu.setDone(false);
				updateGui();
				gui.setVisible(true);
				loadingBar.setVisible(false);
			}
		}
	}
	
	// Sends info for the Sell command
	class Thread extends Service<Void> {

		boolean deleteStock = false;
		
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					Date d = new Date();
					String f = "";
					String s = d.toString();
					f = s.substring(24);
					if(s.substring(4, 6).equals("Jan")) {
						f += "-01";
					}else if(s.substring(4, 7).equals("Feb")) {
						f += "-02";
					}
					else if(s.substring(4, 7).equals("Mar")) {
						f += "-03";
					}
					else if(s.substring(4, 7).equals("Apr")) {
						f += "-04";
					}
					else if(s.substring(4, 7).equals("May")) {
						f += "-05";
					}
					else if(s.substring(4, 7).equals("Jun")) {
						f += "-06";
					}
					else if(s.substring(4, 7).equals("Jan")) {
						f += "-07";
					}
					else if(s.substring(4, 7).equals("Jul")) {
						f += "-08";
					}
					else if(s.substring(4, 7).equals("Aug")) {
						f += "-09";
					}
					else if(s.substring(4, 7).equals("Sep")) {
						f += "-10";
					}
					else if(s.substring(4, 7).equals("Oct")) {
						f += "-11";
					}
					else if(s.substring(4, 7).equals("Dec")) {
						f += "-12";
					}
					f += "-" + s.substring(8, 10);
					if(!InternetConnectivity.checkInternetConnectivity()) {
						Home.setConnected(false);
					}else {
						Database.connectionDatabase();
						try {
							int i = Database.sellStock(Home.getId(), stock.getSymbol(), Integer.parseInt(amount.getText()), Double.parseDouble(ask.getText()), f);
							if(i < 0) {
								deleteStock = true;
							}else {
								quantity = i;
							}
						}catch(NumberFormatException e) {
							
						}
					}
					this.succeeded();
					return null;
				}
			};
		}
	}
}
