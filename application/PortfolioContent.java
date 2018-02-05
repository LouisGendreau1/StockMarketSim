package application;

import java.sql.SQLException;
import java.util.ArrayList;

import controller.Home;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class PortfolioContent {

	private int userId;
	private ArrayList<StockInfo> content = new ArrayList<StockInfo>();
	private boolean loading = true;
	
	public PortfolioContent(int userId) throws SQLException {
		this.userId = userId;
		Thread t = new Thread();
		t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				loading = false;
			}
		});
		t.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				Home.setConnected(false);
			}
		});
		loading = true;
		t.start();
	}
	
	public ArrayList<StockInfo> getContent() {
		return this.content;
	}
	
	public boolean isLoading() {
		return this.loading;
	}
	
	public void reloadPortfolio() {
		content.clear();
		Thread t = new Thread();
		t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				loading = false;
			}
		});
		t.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				Home.setConnected(false);
			}
		});
		loading = true;
		t.start();
	}
	
	class Thread extends Service<Void> {
		
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				
				@Override
				protected Void call() throws Exception {
					if(!InternetConnectivity.checkInternetConnectivity()) {
						this.getState();
						this.failed();
						return null;
					}
					Database.connectionDatabase();
					ArrayList<String> temp;
					temp = Database.loadPortfolioTab(userId);
					for(int i = 0; i < temp.size(); i++) {
						StockInfo p = new StockInfo(temp.get(i).substring(temp.get(i).indexOf("-") + 1));
						p.setUserQuantity(Integer.parseInt(temp.get(i).substring(0, temp.get(i).indexOf("-"))));
						content.add(p);
					}
					this.succeeded();
					return null;
				}
			};
		}
	}
	
}
