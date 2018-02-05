package application;

public class StockInfo {

	// Data Dump
	private int id;
	private String symbol;
	private String name;
	private double ask;
	private String change;
	private double bid;
	private String mktCap;
	private double open;
	private double close;
	private double high;
	private double low;
	private double yearHigh;
	private double yearLow;
	private int volume;
	private double PERatio;
	private int userQuantity;
	
	public StockInfo(int id) {
		this.setId(id);
		this.setSymbol(MSML.STOCK_NAME[id]);
	}
	
	public StockInfo(String symbol) {
		this.setSymbol(symbol);
		for(int i = 0; i < MSML.STOCK_NAME.length; i++) {
			if(symbol.equals(MSML.STOCK_NAME[i])) {
				this.setId(i);
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public void setSymbol(int id) {
		this.setSymbol(MSML.STOCK_NAME[id]);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getAsk() {
		return ask;
	}

	public void setAsk(double ask) {
		this.ask = ask;
	}

	public String getChange() {
		return change;
	}

	public void setChange(String change) {
		this.change = change;
	}

	public double getBid() {
		return bid;
	}

	public void setBid(double bid) {
		this.bid = bid;
	}

	public String getMktCap() {
		return mktCap;
	}

	public void setMktCap(String mktCap) {
		this.mktCap = mktCap;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getYearHigh() {
		return yearHigh;
	}

	public void setYearHigh(double yearHigh) {
		this.yearHigh = yearHigh;
	}

	public double getYearLow() {
		return yearLow;
	}

	public void setYearLow(double yearLow) {
		this.yearLow = yearLow;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public double getPERatio() {
		return PERatio;
	}

	public void setPERatio(double pERatio) {
		PERatio = pERatio;
	}

	public int getUserQuantity() {
		return userQuantity;
	}

	public void setUserQuantity(int userQuantity) {
		this.userQuantity = userQuantity;
	}
	
}
