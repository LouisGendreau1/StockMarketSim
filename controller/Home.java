package controller;

import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

import application.UserGraph;
import application.MarketTime;
import application.PortfolioContent;
import controller.Login;
import application.ClassGraph;
import application.Database;
import application.InternetConnectivity;
import application.MSML;
import application.StockInfo;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Home implements Initializable {
	
	private static StockInfo[] stockInfo;
	private static PortfolioContent portfolioContent;
	private static ArrayList<Integer> list;
	
	@FXML
	private TabPane tabPane;
	@FXML
	private Text nameId;
	@FXML
	public Text balance;
	@FXML
	private Label portfolio;
	@FXML
	private Label smStatus;
	@FXML
	private Label connectedGUI;
	@FXML
	private LineChart<String, Double> userGraph;
	@FXML
	private LineChart<String, Double> classGraph;
	
	static final String OPEN = "OPEN";
	static final String CLOSED = "CLOSED";
	static final String WEEKEND = "CLOSE ON WEEKENDS";
	
	static boolean connected = true;
	static String smStatusVal;
	static boolean forcePortfolioUpdate = false;
	static int userId;
	static int studentClicked = 0;
	
	public static double balanceValue;
	
	static boolean updateStudentClicked = false;
	static boolean forceHistoryUpdate = false;
	static boolean forceSmStatusUpdate = false;
	
	Portfolio p;
	Node h;
	News n;
	Market m;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Smooths the GUI while loading
		Platform.runLater(new Runnable() {
		    public void run() {
		    	stockInfo = new StockInfo[499];
		    	try {
					updateSmStatus();
				} catch (ParseException e) {
					e.printStackTrace();
				}
		    	// Create the 499 stockInfo container, sets stock names and loads database info
				Home.userId = Login.getId();
				
				for(int i = 0; i < stockInfo.length; i++) {
					stockInfo[i] = new StockInfo(i);
					stockInfo[i].setName(Login.getStockNames()[i]);
					if(i == userId) {
						stockInfo[i].setId(userId);
					}
				}
				
				if(!InternetConnectivity.checkInternetConnectivity()) {
					setConnected(false);
				}else {
					Database.connectionDatabase();
					try {
						nameId.setText("Welcome, " + Database.getName(userId, Login.getUserType()) + " (" + userId + ")");
						portfolio.setText("0.0");
						// If the user is a Student
						if(Login.getUserType() == 0) {
							try {
								portfolioContent = new PortfolioContent(userId);
							} catch (SQLException e) {
								e.printStackTrace();
							}
							Thread t = new Thread();
							t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
								@Override
								public void handle(WorkerStateEvent event) {
									try {
										if(Login.userType == 0) {
											userGraph.getData().add((new UserGraph(userId)).getSeries());
											portfolio.setText(String.format("%.2f", Database.getPortfolioValue(userId)));
											classGraph.getData().add(new ClassGraph(Database.getTeacherID(userId)).getSeries());
										}else if(Login.userType == 1) {
											classGraph.getData().add(new ClassGraph(userId).getSeries());
										}
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
							});
							t.start();
							double d = Math.floor((Database.getBalance(userId) * 100)) / 100.0;
							balanceValue = d;
							balance.setText(String.format("%.2f", d));
							
							n = new News();
							p = new Portfolio();
							m = new Market();
							h = new History();
							tabPane.getTabs().get(0).setContent(n);
							tabPane.getTabs().get(1).setContent(p);
							tabPane.getTabs().get(2).setContent(m);
							tabPane.getTabs().get(3).setContent(h);
						
						// If the user is a Teacher
						}else if(Login.getUserType() == 1) {
							list = Database.getTeacherStudentList(userId);
							balanceValue = Math.floor((Database.getStartingAmount(userId) * 100)) / 100.0;
							balance.setText(String.format("%.2f", balanceValue));
							n = new News();
							tabPane.getTabs().get(0).setContent(n);
							m = new Market();
							tabPane.getTabs().remove(1);
							tabPane.getTabs().get(1).setContent(m);
							h = new THistory();
							tabPane.getTabs().get(2).setContent(h);
							
						}
					} catch (SQLException e) {
						
					} catch (Exception e) {
						
					}
				}
				Timer t = new Timer();
				t.start();
				MSML.getStage().setResizable(true);
		    }
		});
		
	}

	// Check if stock market is open
	public static void updateSmStatus() throws ParseException {
		forceSmStatusUpdate = true;
		if(MarketTime.getDateString(0) == "0") {
			smStatusVal = WEEKEND;
		}
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	String s = sdf.format(cal.getTime());
    	if(Integer.parseInt(s.substring(0, 2)) >= 10) {
    		smStatusVal = OPEN;
    	}else if(Integer.parseInt(s.substring(0, 2)) == 9) {
    		if(Integer.parseInt(s.substring(3, 5)) > 50) {
    			smStatusVal = OPEN;
    		}else {
    			smStatusVal = CLOSED;
    		}
    	}else {
    		smStatusVal = CLOSED;
    	}
	}

	public static void updateStudentClicked(int id) {
		studentClicked = id;
		updateStudentClicked = true;
	}
	
	public static StockInfo getStockInfo(int stockId) {
		return stockInfo[stockId];
	}
	
	public static int getId() {
		return userId;
	}

	public static boolean getConnectd() {
		return connected;
	}
	
	public static void setConnected(boolean c) {
		connected = c;
	}
	
	public static PortfolioContent getPortfolioContent() {
		return portfolioContent;
	}
	
	public static void updateBalance() throws SQLException {
		if(!InternetConnectivity.checkInternetConnectivity()) {
			setConnected(false);
		}else {
			Database.connectionDatabase();
			balanceValue = Math.floor((Database.getBalance(userId) * 100)) / 100.0;
		}
	}
	
	public static void updatePortfolio() {
		portfolioContent.reloadPortfolio();
		forcePortfolioUpdate = true;
	}
	
	public static void updateHistory() {
		History.data = FXCollections.observableArrayList();
		forceHistoryUpdate = true;
	}
	
	public static ArrayList<Integer> getList() {
		return list;
	}

	class Timer extends AnimationTimer {

		@Override
		public void handle(long now) {
			if(updateStudentClicked) {
				userGraph.getData().clear();
				Thread t = new Thread();
				t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent event) {
						try {
							if(Login.userType == 0) {
								userGraph.getData().add((new UserGraph(userId)).getSeries());
								portfolio.setText(String.format("%.2f", Database.getPortfolioValue(userId)));
								classGraph.getData().add(new ClassGraph(Database.getTeacherID(userId)).getSeries());
							}else if(Login.userType == 1) {
								classGraph.getData().add(new ClassGraph(userId).getSeries());
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				});
				t.start();
				updateStudentClicked = false;
				try {
					userGraph.getData().add(new UserGraph(studentClicked).getSeries());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(!connected) {
				connectedGUI.setVisible(true);
				n.setDisable(true);
				m.setDisable(true);
				if(Login.getUserType() == 0) {
					p.setDisable(true);
					h.setDisable(true);
				}
			}
			if(forceSmStatusUpdate) {
				forceSmStatusUpdate = false;
				smStatus.setText(smStatusVal);
				if(smStatusVal == OPEN) {
					smStatus.setTextFill(Color.DARKGREEN);
				}else {
					smStatus.setTextFill(Color.RED);
				}
			}
			// Updates the balance real-time
			if(Double.parseDouble(balance.getText()) != balanceValue) {
				balance.setText(String.format("%.2f", balanceValue));
			}
			// Reload a new portfolio with new stocks
			if(forcePortfolioUpdate) {
				forcePortfolioUpdate = false;
				p = new Portfolio();
				tabPane.getTabs().get(1).setContent(p);
			}
			if(forceHistoryUpdate) {
				forceHistoryUpdate = false;
				h = new History();
				tabPane.getTabs().get(3).setContent(h);
			}
		}
	}
	
	class Thread extends Service<Void> {
		
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				
				@Override
				protected Void call() {
					try {
						if(Login.userType == 0) {
							Database.updateDatabaseWithNewValues(userId);
							Database.groupValueAverage(Database.getTeacherID(userId));
						}else if(Login.userType == 1) {
							Database.groupValueAverage(userId);
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
					this.succeeded();
					return null;
				}
			};
		}
	}
}
