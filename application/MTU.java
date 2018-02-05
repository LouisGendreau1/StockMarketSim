package application;

import controller.Home;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class MTU {

	private int progress;
	
	// Start 499 Threads to update the MarketTab
	public MTU() {
		for(int i = 0; i < MSML.STOCK_NAME.length; i++) {
			Thread t = new Thread(i);
			t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent e) {
					progress++;
				}
			});
			t.start();
		}
	}
	
	public int getProgress() {
		return this.progress;
	}
	
	public void setProgress(int p) {
		this.progress = p;
	}

	private String getStockAsk(String stock) throws Exception {
		return XML.update(stock, "Ask");
	}
	
	private String getStockChange(String stock) throws Exception {
		return XML.update(stock, "PercentChange");
	}

	class Thread extends Service<Void> {
	
		int id;
		
		public Thread(int id) {
			this.id = id;
		}
		
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				
				@Override
				protected Void call() throws Exception {
					String ask = getStockAsk(Home.getStockInfo(id).getSymbol());
					String change = getStockChange(Home.getStockInfo(id).getSymbol());
					Home.getStockInfo(id).setAsk(Double.parseDouble(ask));
					Home.getStockInfo(id).setChange(change);
					this.succeeded();
					return null;
				}
			};
		}
	}
}
