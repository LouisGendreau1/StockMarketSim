package application;

import controller.Home;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class SSU {

	private int id;
	private boolean done = false;
	long time = System.currentTimeMillis();
	
	// Start a Thread to update the info
	public SSU(int id) {
		this.id = id;
		Home.getStockInfo(id).setSymbol(id);
		Thread t = new Thread();
		t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent e) {
				done = true;
			}
		});
		t.start();
	}
	
	public boolean getDone() {
		return this.done;
	}
	
	public void setDone(boolean b) {
		this.done = b;
	}
	
	private String getStockName(String stock) throws Exception {
		return XML.update(stock, "Name");
	}
	
	private String getStockAsk(String stock) throws Exception {
		return XML.update(stock, "Ask");
	}
	
	private String getStockChange(String stock) throws Exception {
		return XML.update(stock, "PercentChange");
	}
	
	private String getStockBid(String stock) throws Exception {
		return XML.update(stock, "Bid");
	}
	
	private String getStockMarketCap(String stock) throws Exception {
		return XML.update(stock, "MarketCapitalization");
	}
	
	private String getStockOpen(String stock) throws Exception {
		return XML.update(stock, "Open");
	}
	
	private String getStockClose(String stock) throws Exception {
		return XML.update(stock, "PreviousClose");
	}
	
	private String getStockHigh(String stock) throws Exception {
		return XML.update(stock, "DaysHigh");
	}
	
	private String getStockYearHigh(String stock) throws Exception {
		return XML.update(stock, "YearHigh");
	}
	
	private String getStockLow(String stock) throws Exception {
		return XML.update(stock, "DaysLow");
	}

	private String getStockYearLow(String stock) throws Exception {
		return XML.update(stock, "YearLow");
	}
	
	private String getStockVolume(String stock) throws Exception {
		return XML.update(stock, "Volume");
	}
	
	private String getStockPERatio(String stock) throws Exception {
		return XML.update(stock, "PERatio");
	}
	
	class Thread extends Service<Void> {
	
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				
				long time = System.currentTimeMillis();
				
				@Override
				protected Void call() throws Exception {
					String name = getStockName(Home.getStockInfo(id).getSymbol());
					if(checkTime()) {
						this.succeeded();
					}
					String ask = getStockAsk(Home.getStockInfo(id).getSymbol());
					if(checkTime()) {
						this.succeeded();
					}
					String change = getStockChange(Home.getStockInfo(id).getSymbol());
					if(checkTime()) {
						this.succeeded();
					}
					String bid = getStockBid(Home.getStockInfo(id).getSymbol());
					if(checkTime()) {
						this.succeeded();
					}
					String mktCap = getStockMarketCap(Home.getStockInfo(id).getSymbol());
					if(checkTime()) {
						this.succeeded();
					}
					String open = getStockOpen(Home.getStockInfo(id).getSymbol());
					if(checkTime()) {
						this.succeeded();
					}
					String close = getStockClose(Home.getStockInfo(id).getSymbol());
					if(checkTime()) {
						this.succeeded();
					}
					String high = getStockHigh(Home.getStockInfo(id).getSymbol());
					if(checkTime()) {
						this.succeeded();
					}
					String yearHigh = getStockYearHigh(Home.getStockInfo(id).getSymbol());
					if(checkTime()) {
						this.succeeded();
					}
					String low = getStockLow(Home.getStockInfo(id).getSymbol());
					if(checkTime()) {
						this.succeeded();
					}
					String yearLow = getStockYearLow(Home.getStockInfo(id).getSymbol());
					if(checkTime()) {
						this.succeeded();
					}
					String volume = getStockVolume(Home.getStockInfo(id).getSymbol());
					if(checkTime()) {
						this.succeeded();
					}
					String PERatio = getStockPERatio(Home.getStockInfo(id).getSymbol());
					if(checkTime()) {
						this.succeeded();
					}
					try {
						Home.getStockInfo(id).setName(name);
					}catch(NumberFormatException e) {
						
					}
					try {
						Home.getStockInfo(id).setAsk(Double.parseDouble(ask));
					}catch(NumberFormatException e) {
						
					}
						Home.getStockInfo(id).setChange(change);
					try {
						Home.getStockInfo(id).setBid(Double.parseDouble(bid));
					}catch(NumberFormatException e) {
						
					}
					try {
						Home.getStockInfo(id).setMktCap(mktCap);
					}catch(NumberFormatException e) {
						
					}
					try {
						Home.getStockInfo(id).setOpen(Double.parseDouble(open));
					}catch(NumberFormatException e) {
						
					}
					try {
						Home.getStockInfo(id).setClose(Double.parseDouble(close));
					}catch(NumberFormatException e) {
						
					}
					try {
						Home.getStockInfo(id).setHigh(Double.parseDouble(high));
					}catch(NumberFormatException e) {
						
					}
					try {
						Home.getStockInfo(id).setYearHigh(Double.parseDouble(yearHigh));
					}catch(NumberFormatException e) {
						
					}
					try {
						Home.getStockInfo(id).setLow(Double.parseDouble(low));
					}catch(NumberFormatException e) {
						
					}
					try {
						Home.getStockInfo(id).setYearLow(Double.parseDouble(yearLow));
					}catch(NumberFormatException e) {
						
					}
					try {
						Home.getStockInfo(id).setVolume(Integer.parseInt(volume));
					}catch(NumberFormatException e) {
						
					}
					try {
						Home.getStockInfo(id).setPERatio(Double.parseDouble(PERatio));
					}catch(NumberFormatException e) {
						
					}
					this.succeeded();
					return null;
				}
				
				// After 15 seconds of loading, outputs the info
				public boolean checkTime() {
					if(System.currentTimeMillis() - time > 8000) {
						return true;
					}
					return false;
				}
			};
		}
	}
}
