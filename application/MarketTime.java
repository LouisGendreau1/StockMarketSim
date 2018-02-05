package application;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * MyStockMarketLab Project, Computer Science and Mathematics
 * @author LouisGendreau, PhilippeLavigueur, GabrielBelanger
 * The purpose of this class is to be able to work on a 5 day/week
 * system instead of a 7 day/week system.
 *
 *
 */

public abstract class MarketTime {

	/**
	 * 
	 * @param period
	 * @return
	 * @throws ParseException
	 */
	public static String getDateString(int period) throws ParseException {
		String dateStr;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -period);    
        dateStr = dateFormat.format(cal.getTime());
        
        Date date = dateFormat.parse(dateStr);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		
		if(dayOfWeek == 1){
			return "0";
		}
		else if(dayOfWeek == 7){
			return "0";
		}
		else{
			return dateStr;
		}
	}
	
	public static String getDateStringXML(int period) throws ParseException {
		String dateStr;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -period);    
        dateStr = dateFormat.format(cal.getTime());
		
		return dateStr;
	}
}
