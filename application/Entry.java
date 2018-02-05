package application;

public class Entry {
	
	int id;
	String orderType;
	String orderTime;
	String symbol;
	double askPrice;
	int quantity;
	
	public Entry(String symbol, int quantity, double askPrice, String orderTime, String orderType, int id){
		this.id = id;
		this.symbol = symbol;
		this.orderType = orderType;
		this.orderTime = orderTime;
		this.askPrice = askPrice;
		this.quantity = quantity;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public double getAskPrice() {
		return askPrice;
	}

	public void setAskPrice(double askPrice) {
		this.askPrice = askPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
