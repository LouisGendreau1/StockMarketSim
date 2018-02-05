package application;

import java.sql.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public abstract class Database {

	static Connection connect;

	/**
	 * 
	 */
	public static int connectionDatabase() {
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String url = "jdbc:mysql://mystockmarketlab.com:3306/louis_MyStockMarketLab";
			connect = DriverManager.getConnection(url, "louis_stock", "stockmarket123$");
		}
		catch(Exception ex){
			return -1;
		}
		return 1;
	}
	
	/**
	 * 
	 * @param email
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	public static String loginCheck(String email, String password) throws SQLException {
		PreparedStatement studentQuery = connect.prepareStatement("SELECT id FROM Student WHERE email = ? AND password = ?");
		studentQuery.setString(1, email); 
		studentQuery.setString(2, password);
		ResultSet studentSet = studentQuery.executeQuery();
			
		//Check student account
		String id;
		if(studentSet.isBeforeFirst()) {
			studentSet.next();
			id = "s" + studentSet.getInt("id");
		    studentSet.close();
		    studentQuery.close();
		    return id;
		}else {
			
			//Check teacher account
			PreparedStatement teacherQuery = connect.prepareStatement("SELECT id FROM Teacher WHERE email = ? AND password = ?");
			teacherQuery.setString(1, email); 
			teacherQuery.setString(2, password);
			
			ResultSet teacherSet = teacherQuery.executeQuery();
			if(teacherSet.isBeforeFirst()) {
				teacherSet.next();
				id = "t" + teacherSet.getInt("id");
				teacherSet.close();
				teacherQuery.close();
				return id;
			}
		}
		
	    return "x-1"; 
	}
	
	/**
	 * 
	 * @param firstname
	 * @param lastname
	 * @param teacherID
	 * @param email
	 * @param password
	 * @throws SQLException
	 */
	public static void newStudent(String firstname, String lastname, int teacherID, String email, String password) throws SQLException{

		PreparedStatement newStudent = connect.prepareStatement("INSERT INTO Student (FirstName, LastName, TeacherID, eMail, Password) VALUES (?, ?, ?, ?, ?)");
		newStudent.setString(1, firstname);
		newStudent.setString(2, lastname);
		newStudent.setInt(3, teacherID);
		newStudent.setString(4, email);
		newStudent.setString(5, password);
		newStudent.executeUpdate();
		newStudent.close();
		
		PreparedStatement idQuery = connect.prepareStatement("SELECT id FROM Student WHERE eMail = ?");
		idQuery.setString(1, email);
		ResultSet rs = idQuery.executeQuery();
		rs.next();
		
		PreparedStatement startingAmount = connect.prepareStatement("SELECT StartingAmount FROM Teacher WHERE id = ?");
		startingAmount.setInt(1, teacherID);
		ResultSet rs2 = startingAmount.executeQuery();
		rs2.next();
		
		PreparedStatement newPortfolio = connect.prepareStatement("INSERT INTO Portfolio (StudentID, Balance) VALUES (?, ?)");
		newPortfolio.setInt(1, rs.getInt("id"));
		newPortfolio.setDouble(2, rs2.getInt("StartingAmount"));
		newPortfolio.executeUpdate();
		
		idQuery.close();
		startingAmount.close();
        newPortfolio.close();
	}
	
	// Check if the Teacher exists
	public static boolean checkTeacherId(int teacherId) throws SQLException {
		
		PreparedStatement check = connect.prepareStatement("SELECT id FROM Teacher");
		ResultSet rs = check.executeQuery();
		while(rs.next()) {
			if(teacherId == rs.getInt("id")) {
				return true;
			}
		}
		return false;
	}
	
	// Check if the email is already in use
	public static boolean emailExists(String email) throws SQLException {
		PreparedStatement check = connect.prepareStatement("SELECT eMail FROM Student");
		ResultSet rs = check.executeQuery();
		while(rs.next()) {
			if(email.equals(rs.getString("eMail"))) {
				return true;
			}
		}
		check.close();
		PreparedStatement check2 = connect.prepareStatement("SELECT eMail FROM Teacher");
		ResultSet rs2 = check2.executeQuery();
		while(rs2.next()) {
			if(email.equals(rs2.getString("eMail"))) {
				return true;
			}
		}
		check2.close();
		return false;
	}
	
	/**
	 * 
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @param password
	 * @param startingAmount
	 * @throws SQLException
	 */
	public static void newTeacher(String firstname, String lastname,String email, String password, int startingAmount) throws SQLException{

		PreparedStatement newTeacher = connect.prepareStatement("INSERT INTO Teacher (FirstName, LastName, eMail, Password, StartingAmount) VALUES (?, ?, ?, ?, ?)");
		newTeacher.setString(1, firstname);
		newTeacher.setString(2, lastname);
		newTeacher.setString(3, email);
		newTeacher.setString(4, password);
		newTeacher.setInt(5, startingAmount);
		newTeacher.executeUpdate();
		newTeacher.close();
	}
	
	// Sends the balance
	public static double getBalance(int id) throws SQLException {
		
		double balance = 0;
		
		PreparedStatement getBalance = connect.prepareStatement("SELECT Balance FROM Portfolio WHERE StudentID = ?");
		getBalance.setInt(1, id);
		ResultSet rs = getBalance.executeQuery();
		rs.next();
		balance = rs.getDouble("Balance");
		return balance;
	}
	
	// Send the user name
	public static String getName(int id, int userType) throws SQLException {
		
		String name = "User";
		
		if(userType == 0) {
			PreparedStatement getName = connect.prepareStatement("SELECT FirstName FROM Student WHERE id = ?");
			getName.setInt(1, id);
			ResultSet rs = getName.executeQuery();
			rs.next();
			name = rs.getString("FirstName");
		}else if(userType == 1) {
			PreparedStatement getName = connect.prepareStatement("SELECT FirstName FROM Teacher WHERE id = ?");
			getName.setInt(1, id);
			ResultSet rs = getName.executeQuery();
			rs.next();
			name = rs.getString("FirstName");
		}
		return name;
	}
	
	public static String getLastName(int id, int userType) throws SQLException {
		
		String name = "User";
		
		if(userType == 0) {
			PreparedStatement getName = connect.prepareStatement("SELECT LastName FROM Student WHERE id = ?");
			getName.setInt(1, id);
			ResultSet rs = getName.executeQuery();
			rs.next();
			name = rs.getString("LastName");
		}else if(userType == 1) {
			PreparedStatement getName = connect.prepareStatement("SELECT LastName FROM Teacher WHERE id = ?");
			getName.setInt(1, id);
			ResultSet rs = getName.executeQuery();
			rs.next();
			name = rs.getString("LastName");
		}
		return name;
	}
	
	// Return the quantity of the stock in the user portfolio
	public static int getQuantity(int studentId, String stockSymbol) throws SQLException {
		int quantity = 0;
		
		PreparedStatement getPortfolioIDQuery = connect.prepareStatement("SELECT id FROM Portfolio WHERE StudentID = ?");
		getPortfolioIDQuery.setInt(1, studentId);
		int ID = 0;
		ResultSet portfolioID = getPortfolioIDQuery.executeQuery();
		if(portfolioID.next()){
			ID = portfolioID.getInt("id");
			getPortfolioIDQuery.close();
		}
		
		PreparedStatement getQuantity = connect.prepareStatement("SELECT Quantity FROM StockList WHERE Symbol = ? AND PortfolioID = ?");
		getQuantity.setString(1, stockSymbol);
		getQuantity.setInt(2, ID);
		ResultSet getQuantitySet = getQuantity.executeQuery();

		getQuantitySet.next();
		quantity = getQuantitySet.getInt("Quantity");
		getQuantity.close();
		
		return quantity;
	}
	
	// Return the adjusted value of the stock in the user portfolio
	public static double getAdjustedPrice(int studentId, String stockSymbol) throws SQLException {
		double adjustedPrice = 0;
		
		PreparedStatement getPortfolioIDQuery = connect.prepareStatement("SELECT id FROM Portfolio WHERE StudentID = ?");
		getPortfolioIDQuery.setInt(1, studentId);
		int ID = 0;
		ResultSet portfolioID = getPortfolioIDQuery.executeQuery();
		if(portfolioID.next()){
			ID = portfolioID.getInt("id");
			getPortfolioIDQuery.close();
		}
		
		PreparedStatement getAdjustedPrice = connect.prepareStatement("SELECT AdjustedPrice FROM StockList WHERE Symbol = ? AND PortfolioID = ?");
		getAdjustedPrice.setString(1, stockSymbol);
		getAdjustedPrice.setInt(2, ID);
		ResultSet getAdjustedPriceSet = getAdjustedPrice.executeQuery();

		getAdjustedPriceSet.next();
		adjustedPrice = getAdjustedPriceSet.getDouble("AdjustedPrice");
		getAdjustedPrice.close();
		
		return adjustedPrice;
	}
	
	// Send info to load the portfolio
	public static ArrayList<String> loadPortfolioTab(int id) throws SQLException{
		
		ArrayList<String> stocks = new ArrayList<String>();
		int portID = 0;
		
		PreparedStatement portfolioQuery = connect.prepareStatement("SELECT id FROM Portfolio WHERE StudentID = ?");
		portfolioQuery.setInt(1, id);
		ResultSet resultSet1 = portfolioQuery.executeQuery();
		resultSet1.next();
		portID = resultSet1.getInt("id"); 
		portfolioQuery.close();
		try {
		PreparedStatement getSymbol = connect.prepareStatement("SELECT Symbol FROM StockList WHERE PortfolioID = ? ORDER BY Symbol ASC");
		PreparedStatement getQuantity = connect.prepareStatement("SELECT Quantity FROM StockList WHERE Symbol = ? AND PortfolioID = ?");
		getSymbol.setInt(1, portID);
		ResultSet resultSet2 = getSymbol.executeQuery();
		
		if(resultSet2.isBeforeFirst()) {
			while(resultSet2.next()) {
				String s = resultSet2.getString("Symbol");
				getQuantity.setString(1, s);
				getQuantity.setInt(2, portID);
				ResultSet resultSet3 = getQuantity.executeQuery();
				resultSet3.next();
				int q = resultSet3.getInt("Quantity");
				stocks.add(q + "-" + s);
			}
		}
		
		getSymbol.close();
		getQuantity.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return stocks; 
	}
	
	public static int getStartingAmount(int teacherID) throws SQLException {
		
		PreparedStatement getStartingAmount = connect.prepareStatement("SELECT StartingAmount FROM Teacher WHERE id = ?");
		getStartingAmount.setInt(1, teacherID);
		ResultSet rs = getStartingAmount.executeQuery();
		rs.next();
		
		return rs.getInt("StartingAmount");
	}
	
	/**
	 * 
	 * @param studentID
	 * @param symbol
	 * @param ordertype
	 * @param ordertime
	 * @param askprice
	 * @param quantity
	 * @throws SQLException
	 */
	public static void buyStock(int studentID, String symbol, String orderTime, double askPrice, int quantity) throws SQLException{
		
		String orderType = "BUY";
		Boolean stockExists = false;
		double balance;
		int prevQuantity = 0;
		
		PreparedStatement portfolioQuery = connect.prepareStatement("SELECT id FROM Portfolio WHERE StudentID = ?");
		portfolioQuery.setInt(1, studentID);
		ResultSet rsPortfolioID = portfolioQuery.executeQuery();
		rsPortfolioID.next();
		int portfolioID = rsPortfolioID.getInt("id");
		portfolioQuery.close();
		
		PreparedStatement verifyQuery = connect.prepareStatement("SELECT Quantity FROM StockList WHERE PortfolioID = ? AND Symbol = ?");
		verifyQuery.setInt(1, portfolioID);
		verifyQuery.setString(2, symbol);
		try {
			ResultSet rsVerify = verifyQuery.executeQuery();
			rsVerify.next();
			prevQuantity = rsVerify.getInt("Quantity");
			stockExists = true;
		}catch(Exception e) {
			stockExists = false;
		}
		verifyQuery.close();
		
		if(stockExists) {
			PreparedStatement stockListUpdate = connect.prepareStatement("UPDATE StockList SET Quantity = ?, AdjustedPrice = ? WHERE PortfolioID = ? AND Symbol = ?");
			stockListUpdate.setInt(1, prevQuantity + quantity);
			PreparedStatement getCurPrice = connect.prepareStatement("SELECT AdjustedPrice FROM StockList WHERE Symbol = ?");
			getCurPrice.setString(1, symbol);
			ResultSet rsCurPrice = getCurPrice.executeQuery();
			rsCurPrice.next();
			double adjustedPrice = (prevQuantity * rsCurPrice.getDouble("AdjustedPrice") + quantity * askPrice) / (prevQuantity + quantity);
			DecimalFormat df = new DecimalFormat("0.00");
			adjustedPrice = Double.parseDouble(df.format(adjustedPrice));
			stockListUpdate.setDouble(2, adjustedPrice);
			stockListUpdate.setInt(3, portfolioID);
			stockListUpdate.setString(4, symbol);
			stockListUpdate.executeUpdate();
			stockListUpdate.close();
			getCurPrice.close();
		}else {
			PreparedStatement stockListUpdate = connect.prepareStatement("INSERT INTO StockList (Symbol, Quantity, AdjustedPrice, PortfolioID, Type) VALUES (?, ?, ?, ?, ?)");
			stockListUpdate.setString(1, symbol);
			stockListUpdate.setInt(2, quantity);
			stockListUpdate.setDouble(3, askPrice);
			stockListUpdate.setInt(4, portfolioID);
			stockListUpdate.setString(5, orderType);
			stockListUpdate.executeUpdate();
			stockListUpdate.close();
		}
		
		PreparedStatement transactionUpdate = connect.prepareStatement("INSERT INTO Transaction (StudentID, Symbol, Quantity, OrderType, OrderTime, AskPrice) VALUES (?, ?, ?, ?, ?, ?)");
		transactionUpdate.setInt(1, studentID);
		transactionUpdate.setString(2, symbol);
		transactionUpdate.setInt(3, quantity);
		transactionUpdate.setString(4, orderType);
		transactionUpdate.setString(5, orderTime);
		transactionUpdate.setDouble(6, askPrice);
		transactionUpdate.executeUpdate();
		transactionUpdate.close();
		
		PreparedStatement balanceQuery = connect.prepareStatement("SELECT Balance FROM Portfolio WHERE StudentID = ?");
		balanceQuery.setInt(1, studentID);
		ResultSet rsBalanceQuery = balanceQuery.executeQuery();
		rsBalanceQuery.next();
		balance = rsBalanceQuery.getDouble("Balance");
		balanceQuery.close();
		
		PreparedStatement balanceUpdate = connect.prepareStatement("UPDATE Portfolio SET Balance = ? WHERE StudentID = ?");
		balanceUpdate.setDouble(1, (balance - (quantity * askPrice)));
		balanceUpdate.setInt(2, studentID);
		balanceUpdate.executeUpdate();
		balanceUpdate.close();
	}		
	
	public static void historicalPortfolioValue(int id, String date, int value) throws SQLException{
		
		PreparedStatement portfolioIDQuery = connect.prepareStatement("SELECT id FROM Portfolio WHERE StudentID = ?");
		portfolioIDQuery.setInt(1, id);
		
		ResultSet portSet = portfolioIDQuery.executeQuery();
		
		int ID = 0;
		if(portSet.next()){
			ID = portSet.getInt("id");
		}
		
		PreparedStatement historyQuery = connect.prepareStatement("INSERT INTO HistoricalPortfolioValue (id, Date, Value, PortfolioID) VALUES (?, ?, ?, ?)");

		historyQuery.setString(1, "DEFAULT");
		historyQuery.setString(2, "date");
		historyQuery.setInt(3, value);
		historyQuery.setInt(3, ID);
		
		portfolioIDQuery.close();
		historyQuery.close();
		
	}
	
	public static int sellStock(int studentId, String stockName, int quantity, double ask, String date) throws NumberFormatException, Exception{
		
		int returnState = 0;
		
		PreparedStatement getPortfolioIDQuery = connect.prepareStatement("SELECT id FROM Portfolio WHERE StudentID = ?");
		getPortfolioIDQuery.setInt(1, studentId);
		int ID = 0;
		ResultSet portfolioID = getPortfolioIDQuery.executeQuery();
		portfolioID.next();
		ID = portfolioID.getInt("id");
		getPortfolioIDQuery.close();
		
		PreparedStatement checkQuantity = connect.prepareStatement("SELECT Quantity FROM StockList WHERE Symbol = ? AND PortfolioID = ? AND Type = ?");
		checkQuantity.setString(1, stockName);
		checkQuantity.setInt(2, ID);
		checkQuantity.setString(3, "BUY");
		ResultSet checkQuantitySet = checkQuantity.executeQuery();
		
		int stockQuantity;
		
		checkQuantitySet.next();
		stockQuantity = checkQuantitySet.getInt("Quantity");
		checkQuantity.close();	
	
		if(stockQuantity - quantity == 0) {
			
			PreparedStatement deleteRow = connect.prepareStatement("DELETE FROM StockList WHERE Symbol = ? AND PortfolioID = ? AND Type = ?");
			deleteRow.setString(1, stockName);
			deleteRow.setInt(2, ID);
			deleteRow.setString(3, "BUY");
			deleteRow.executeUpdate();
			deleteRow.close();
			returnState = -1;
		}else {
			
			PreparedStatement sellStock = connect.prepareStatement("UPDATE StockList SET Quantity = ? WHERE PortfolioID = ? AND Symbol = ? AND Type = ?");
			sellStock.setInt(1, (stockQuantity - quantity));
			sellStock.setInt(2, ID);
			sellStock.setString(3, stockName);
			sellStock.setString(4, "BUY");
			sellStock.executeUpdate();
			sellStock.close();
			returnState = stockQuantity - quantity;
		}
		
		PreparedStatement getBalanceBeforeSell = connect.prepareStatement("SELECT Balance FROM Portfolio WHERE StudentID = ?");
		double portfolioBalance = 0;
		getBalanceBeforeSell.setInt(1, studentId);
		ResultSet balanceSet = getBalanceBeforeSell.executeQuery();
		balanceSet.next();
		portfolioBalance = balanceSet.getDouble("Balance");
		getBalanceBeforeSell.close();
		
		PreparedStatement updateBalance = connect.prepareStatement("UPDATE Portfolio SET Balance = ? WHERE StudentID = ?");
		updateBalance.setDouble(1, portfolioBalance + quantity * ask);
		updateBalance.setDouble(2, studentId);
		updateBalance.executeUpdate();
		updateBalance.close();
		
		PreparedStatement updateTransaction = connect.prepareStatement("INSERT INTO Transaction (StudentID, Symbol, Quantity, OrderType, OrderTime, AskPrice) VALUES (?, ?, ?, ?, ?, ?)");
		updateTransaction.setInt(1, studentId);
		updateTransaction.setString(2, stockName);
		updateTransaction.setInt(3, quantity);
		updateTransaction.setString(4, "SELL");
		updateTransaction.setString(5, date);
		updateTransaction.setDouble(6, ask);
		updateTransaction.executeUpdate();
		updateTransaction.close();
		
		return returnState;
	}
	
	public static ArrayList<Entry> getHistory(int studentID) throws Exception {
		
		ArrayList<Entry> e = new ArrayList<Entry>();
	
		PreparedStatement dataQuery = connect.prepareStatement("SELECT id, Symbol, Quantity, OrderType, OrderTime, AskPrice FROM Transaction WHERE StudentID = ?");
		
		dataQuery.setInt(1, studentID);
		ResultSet dataSet = dataQuery.executeQuery();
		
		while(dataSet.next()){
			int id = dataSet.getInt("id");
			String symbol = dataSet.getString("Symbol");
			int quantity = dataSet.getInt("Quantity");
			String orderType = dataSet.getString("OrderType");
			String orderTime = dataSet.getString("OrderTime");
			double askPrice = dataSet.getDouble("AskPrice");
			e.add(new Entry(symbol, quantity, askPrice, orderTime, orderType, id));
		}
		return e;
	}
	
	// Return an array with the dates we have to compute the portfolio value
	public static ArrayList<Date> dateToComputeValue(String lastConnection) throws SQLException, ParseException {
		ArrayList<Date> daysBetween = new ArrayList<Date>(); 
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date today = getCurrentDate();
		Date lastDate = new Date((format.parse(lastConnection)).getTime());
		
		daysBetween = getDaysBetweenDates(lastDate, today);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -daysBetween.size() + 1);
		ArrayList<Date> dateSequence = new ArrayList<Date>();
		for(int i = 0; i < daysBetween.size() - 1; i++){

			// Removes the weekends from the ArrayList
			if(c.get(Calendar.DAY_OF_WEEK) == 7 || c.get(Calendar.DAY_OF_WEEK) == 1) {
				
			}else {
				dateSequence.add(daysBetween.get(i));
			}
			c.add(Calendar.DATE, 1);
		}
			
		return dateSequence;
	}
	

	// Return string of the current date
	public static Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}
	
	// Return an arraylist of all the dates between 2 dates
	public static ArrayList<Date> getDaysBetweenDates(Date startdate, Date enddate){
	   ArrayList<Date> dates = new ArrayList<Date>();
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(startdate);

	    while(calendar.getTime().before(enddate)) {
	        Date result = new Date(calendar.getTimeInMillis());
	        dates.add(result);
	        calendar.add(Calendar.DATE, 1);
	    }
	    
	    return dates;
	}
	
	
	public static double computeHistoricalValuePortfolio(Date date, int id) throws NumberFormatException, Exception{
		// Get portID
		int portfolioID = 0;
		PreparedStatement getPortID = connect.prepareStatement("SELECT id FROM Portfolio WHERE StudentID = ?");
		getPortID.setInt(1, id);
		ResultSet portID = getPortID.executeQuery();
		portID.next();
		portfolioID = portID.getInt("id");
		
		// Get balance of portfolio
		double balance = 0;
		PreparedStatement getBalance = connect.prepareStatement("SELECT Balance FROM Portfolio WHERE StudentID = ?");
		getBalance.setInt(1, id);
		ResultSet portfolioBalance = getBalance.executeQuery();
		portfolioBalance.next();
		balance = portfolioBalance.getDouble("Balance");
		
		PreparedStatement getStockInPortfolio = connect.prepareStatement("SELECT Symbol FROM StockList WHERE PortfolioID = ?");
		getStockInPortfolio.setInt(1, portfolioID);
		ResultSet stock = getStockInPortfolio.executeQuery();
		ArrayList<String> stockName = new ArrayList<String>();
		
		while(stock.next()){
			stockName.add(stock.getString("Symbol"));
		}
		
		String[][] stockAndQuantity = new String[2][stockName.size()];
		PreparedStatement getQuantity = connect.prepareStatement("SELECT Quantity FROM StockList WHERE Symbol = ? AND PortfolioID = ?");
		
		for(int i = 0; i < stockName.size(); i++){
			getQuantity.setString(1, stockName.get(i));
			getQuantity.setInt(2, portfolioID);
			ResultSet quantitySet = getQuantity.executeQuery();
			
			while(quantitySet.next()){
				stockAndQuantity[0][i] = stockName.get(i); 
				stockAndQuantity[1][i] = Integer.toString(quantitySet.getInt("Quantity")); 
			}
		}
		
		double portfolioValue = 0;
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		for(int i = 0; i < stockName.size(); i++){
			portfolioValue += XML.getGraphDataPointsUnique(stockAndQuantity[0][i], format.format(date), format.format(date)) * (Double.parseDouble(stockAndQuantity[1][i]));    
		}
		portfolioValue += balance;
		
		getPortID.close();
		getBalance.close();
		getStockInPortfolio.close();
		getQuantity.close();
		
		return portfolioValue;
		
	}
	
	
	// Call at every login to check if the program has to compute some historical portfolio values 
	public static void updateDatabaseWithNewValues(int id) throws NumberFormatException, Exception{
		//get last connection du user
		PreparedStatement getLastConnection = connect.prepareStatement("SELECT LastConnection FROM LastDateConnected WHERE StudentID = ?");
		getLastConnection.setInt(1, id);
		ResultSet lastDateSet = getLastConnection.executeQuery();
		
		if(lastDateSet.isBeforeFirst()) {
			lastDateSet.next();
			String lastConnection = lastDateSet.getString("LastConnection");
			
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date today = getCurrentDate();
			Date lastDate = new Date((format.parse(lastConnection)).getTime());
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			
			Calendar c = Calendar.getInstance();
			c.setTime(lastDate);
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			// Check if date operation needed
			if(lastConnection != null){
				if(lastDate != today && dayOfWeek != 7 && dayOfWeek != 1) {
					int portfolioID = 0;
					//get portfolio id
					PreparedStatement getPortID = connect.prepareStatement("SELECT id FROM Portfolio WHERE StudentID = ?");
					getPortID.setInt(1, id);
					ResultSet portID = getPortID.executeQuery();
					portID.next();
					portfolioID = portID.getInt("id");
					
					ArrayList<Date> dates = dateToComputeValue(lastConnection); // Return arraylist of date objects
					ArrayList<Double> values = new ArrayList<Double>();
					PreparedStatement updateTable = connect.prepareStatement("INSERT INTO HistoricalPortfolioValue (id, Date, Value, PortfolioID) VALUES (DEFAULT, ?, ?, ?)");
					
					for(int i = 0; i < dates.size(); i++) {
						values.add(computeHistoricalValuePortfolio(dates.get(i), id));
						updateTable.setString(1, format.format(dates.get(i)));
						updateTable.setDouble(2, values.get(i));
						updateTable.setInt(3, portfolioID);
						updateTable.executeUpdate();
					}
					getLastConnection.close();
					getPortID.close();
					updateTable.close();
				}
			}
		}
	}
	
	// Call this method every time the user logout 
	// If first time login, create a LastDateConnected, else modify the table
	public static void updateDateAtLogout(int id) throws SQLException{
		PreparedStatement getLastConnection = connect.prepareStatement("SELECT LastConnection FROM LastDateConnected WHERE StudentID = ?");
		getLastConnection.setInt(1, id);
		ResultSet lastDateSet = getLastConnection.executeQuery();
		
		if(lastDateSet.isBeforeFirst()) {
			PreparedStatement updateLastConnectionDate = connect.prepareStatement("UPDATE LastDateConnected SET LastConnection = ? WHERE StudentID = ?");
			updateLastConnectionDate.setString(1, dateToString(getCurrentDate()));
			updateLastConnectionDate.setInt(2, id);
			updateLastConnectionDate.executeUpdate();
			updateLastConnectionDate.close();

		}else {
			PreparedStatement addRowInTable = connect.prepareStatement("INSERT INTO LastDateConnected (id, StudentID, LastConnection) VALUES (DEFAULT, ?, ?)");
			addRowInTable.setInt(1, id);
			addRowInTable.setString(2, dateToString(getCurrentDate()));
			addRowInTable.executeUpdate();
			addRowInTable.close();

		}
		getLastConnection.close();
	}
	
	public static double getPortfolioValue(int userId) throws SQLException {
		
		double value = 0;
		
		// Get portID
		int portfolioID = 0;
		PreparedStatement getPortID = connect.prepareStatement("SELECT id FROM Portfolio WHERE StudentID = ?");
		getPortID.setInt(1, userId);
		ResultSet portID = getPortID.executeQuery();
		portID.next();
		portfolioID = portID.getInt("id");
		
		PreparedStatement getValue = connect.prepareStatement("SELECT Value FROM HistoricalPortfolioValue WHERE PortfolioID = ?");
		getValue.setInt(1, portfolioID);
		ResultSet rsVal = getValue.executeQuery();
		
		while(rsVal.next()) {
			value = rsVal.getDouble("Value");
		}
		
		return value;
	}
	
	public static ArrayList<Integer> getTeacherStudentList(int teacherID) throws SQLException {
		
		ArrayList<Integer> list = new ArrayList<Integer>();
	
		PreparedStatement getStudentID = connect.prepareStatement("SELECT id FROM Student WHERE TeacherID = ?");
		getStudentID.setInt(1, teacherID);
		ResultSet rs = getStudentID.executeQuery();
		
		if(rs.isBeforeFirst()) {
			while(rs.next()) {
				list.add(rs.getInt("id"));
			}
		}else {
			return list;
		}
		return list;
	}
	
	public static  String[][] getPortfolioValueGraph(int id) throws SQLException{
		
		int portfolioID = 0;
		PreparedStatement getPortID = connect.prepareStatement("SELECT id FROM Portfolio WHERE StudentID = ?");
		getPortID.setInt(1, id);
		ResultSet portID = getPortID.executeQuery();
		portID.next();
		portfolioID = portID.getInt("id");
		getPortID.close();
		
		PreparedStatement getAllHistoricalValues = connect.prepareStatement("SELECT Value FROM HistoricalPortfolioValue WHERE PortfolioID = ?");
		getAllHistoricalValues.setInt(1, portfolioID);
		ResultSet values = getAllHistoricalValues.executeQuery();
		ArrayList<Double> historicalValues = new ArrayList<Double>();
		
		if(values.isBeforeFirst()){
			while(values.next()){
				historicalValues.add(values.getDouble("Value"));
			}
			
			PreparedStatement getDate = connect.prepareStatement("SELECT Date FROM HistoricalPortfolioValue WHERE PortfolioID = ?");
			getDate.setInt(1, portfolioID);
			ResultSet dates = getDate.executeQuery();
			
			ArrayList<String> listOfDate = new ArrayList<String>();
			
			while(dates.next()){
				listOfDate.add(dates.getString("Date"));
			}
			
			String[][] valueAndDate = new String[2][historicalValues.size()];

			for(int i = 0; i < historicalValues.size(); i++){
				valueAndDate[0][i] = Double.toString(historicalValues.get(i));
				valueAndDate[1][i] = listOfDate.get(i);
			}
			return valueAndDate;
		}
		return null;	
	}
	
	public static Date lastDayOpen(){
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		
		if(dayOfWeek != 1 || dayOfWeek != 7){
			return new Date(cal.getTimeInMillis());
		}else if(dayOfWeek == 1 || dayOfWeek == 7){
			cal.add(Calendar.DATE, -2);
			dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);	
			if(dayOfWeek != 1 || dayOfWeek != 7){
				return new Date(cal.getTimeInMillis());
			}else{
				cal.add(Calendar.DATE, -3);
				dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
				return new Date(cal.getTimeInMillis());
			}
		}	
		return null;
	}

	public static int getTeacherID(int studentID) throws SQLException {
		
		PreparedStatement getTeacherID = connect.prepareStatement("SELECT TeacherId FROM Student WHERE id = ?");
		getTeacherID.setInt(1, studentID);
		ResultSet students = getTeacherID.executeQuery();
		students.next();
		return students.getInt("TeacherID");
	}
	
	public static void groupValueAverage(int teacherID) throws SQLException{
		
		PreparedStatement getStudentInGroup = connect.prepareStatement("SELECT id FROM Student WHERE TeacherID = ?");
		getStudentInGroup.setInt(1, teacherID);
		ResultSet students = getStudentInGroup.executeQuery();
		ArrayList<Integer> studentInGroup = new ArrayList<Integer>();
		
		if(students.isBeforeFirst()) {
			//student in a group, in this arrayList
			while(students.next()){
				studentInGroup.add(students.getInt("id"));
			}
			
			//get portfolioID of all students in teacher group 
			PreparedStatement getPortID = connect.prepareStatement("SELECT id FROM Portfolio WHERE StudentID = ?");
			ArrayList<Integer> portfolioID = new ArrayList<Integer>();
			
			for(int i = 0; i < studentInGroup.size(); i++){
				getPortID.setInt(1, studentInGroup.get(i));
				ResultSet portID = getPortID.executeQuery();
				portID.next();
				portfolioID.add(portID.getInt("id"));
			}
			
			
			ArrayList<Double> allStudentValues = new ArrayList<Double>();
			int numberOfStudents = 0;
	
			for(int i = 0; i < studentInGroup.size(); i++){
				PreparedStatement getValue = connect.prepareStatement("SELECT Value FROM HistoricalPortfolioValue WHERE PortfolioID = ? AND Date = ?");
				getValue.setInt(1, portfolioID.get(i));
				getValue.setString(2, dateToString(lastDayOpen()));
				ResultSet value = getValue.executeQuery();
				
				if(value.isBeforeFirst()){
					value.next();
					allStudentValues.add(value.getDouble("Value"));
					numberOfStudents++;					
				}
			}	
			
			double groupTotalAddition = 0; 
			double groupAverage = 0;
			
			for(int i = 0; i < allStudentValues.size(); i++){
				groupTotalAddition += allStudentValues.get(i); 
			}
			
			groupAverage = groupTotalAddition / numberOfStudents;
			PreparedStatement checkExist = connect.prepareStatement("SELECT AverageValue FROM HistoricalPortfolioValueGroupAverage WHERE Date = ? AND TeacherID = ?");
			checkExist.setString(1, dateToString(lastDayOpen()));
			checkExist.setInt(2, teacherID);
			ResultSet rs = checkExist.executeQuery();
			if(rs.isBeforeFirst()) {
				PreparedStatement putInTable = connect.prepareStatement("UPDATE HistoricalPortfolioValueGroupAverage SET AverageValue = ? WHERE Date = ?");
				putInTable.setDouble(1, groupAverage);
				putInTable.setString(2, dateToString(lastDayOpen()));
				putInTable.executeUpdate();
				putInTable.close();
			}else {			
				PreparedStatement putInTable = connect.prepareStatement("INSERT INTO HistoricalPortfolioValueGroupAverage (id, TeacherID, AverageValue, Date) VALUES (DEFAULT, ?, ?, ?)");
				putInTable.setInt(1, teacherID);
				putInTable.setDouble(2, groupAverage);
				putInTable.setString(3, dateToString(lastDayOpen()));
				putInTable.executeUpdate();
				putInTable.close();
			}
		}
	}
	
	public static String dateToString(Date d) {
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
		return f;
	}
	
	public static double getGroupAverage(int teacherID, Date date) throws SQLException {
		
		PreparedStatement getValue = connect.prepareStatement("SELECT Value FROM HistoricalPortfolioValueGroupAverage WHERE TeacherID = ? AND Date = ?");
		getValue.setInt(1, teacherID);
		getValue.setString(2, dateToString(date));
		ResultSet value = getValue.executeQuery();
		value.next();
		
		return value.getDouble("AverageValue");
	}
	
	public static  String[][] getPortfolioGroupGraph(int id) throws SQLException{
		
		PreparedStatement getAllHistoricalValues = connect.prepareStatement("SELECT AverageValue FROM HistoricalPortfolioValueGroupAverage WHERE TeacherID = ?");
		getAllHistoricalValues.setInt(1, id);
		ResultSet values = getAllHistoricalValues.executeQuery();
		ArrayList<Double> historicalValues = new ArrayList<Double>();
		
		if(values.isBeforeFirst()){
			while(values.next()){
				historicalValues.add(values.getDouble("AverageValue"));
			}
			
			PreparedStatement getDate = connect.prepareStatement("SELECT Date FROM HistoricalPortfolioValueGroupAverage WHERE TeacherID = ? ORDER BY Date ASC");
			getDate.setInt(1, id);
			ResultSet dates = getDate.executeQuery();
			
			ArrayList<String> listOfDate = new ArrayList<String>();
			
			while(dates.next()){
				listOfDate.add(dates.getString("Date"));
			}
			
			String[][] valueAndDate = new String[2][historicalValues.size()];

			for(int i = 0; i < historicalValues.size(); i++){
				valueAndDate[0][i] = Double.toString(historicalValues.get(i));
				valueAndDate[1][i] = listOfDate.get(i);
			}
			return valueAndDate;
		}
		return null;	
	}
}
