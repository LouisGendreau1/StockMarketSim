package application;

import controller.Login;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class INU {

	private static double completed;
	int id;
	
	// Start a Thread to update the info
	public INU() {
		for(int i = 0; i < MSML.STOCK_NAME.length; i++) {
			id = i;
			Thread t = new Thread(i);
			t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent e) {
					completed++;
				}
			});
			t.setOnFailed(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent e) {
					t.reset();
				}
			});
			t.start();
		}
	}
	
	public double getPercentComplete() {
		return completed / 499.0;
	}

	private String getStockName(String stock) throws Exception {
		return XML.update(stock, "Name");
	}
	
	class Thread extends Service<Void> {
	
		int id;
		
		public Thread(int i) {
			this.id = i;
		}
		
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				
				@Override
				protected Void call() throws Exception {
					String name = getStockName(MSML.STOCK_NAME[id]);
					Login.getStockNames()[id] = name;
					this.succeeded();
					return null;
				}
			};
		}
	}
}
