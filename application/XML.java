package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import controller.Login;


public abstract class XML extends Login {
	
	/** 
	 * Variables for stock data
	 * */
	static Document xmlDocStock;
	static Node resultsStock;
	static NodeList nodeListStock;
	static String xmlStockString = null;
	static ArrayList<String> infoListStock = new ArrayList<String>();
	
	/** 
	 * Variables for graph data 
	 * */
	static Document xmlDocGraph;
	static Node resultsGraph;
	static NodeList nodeListGraph;
	static String xmlGraphString = null;
	static ArrayList<String> infoListGraph = new ArrayList<String>();	
	
	/**
	 * 
	 * @param stock
	 * @param fieldToUpdate
	 * @return
	 * @throws Exception
	 */
	public static String update(String stock, String fieldToUpdate) throws Exception {
		loadDriver(); //load the SQL driver
		xmlStockString = getXMLUniqueStock(stock); //all the XML File in a String
		xmlDocStock = loadXMLFromString(xmlStockString);
		resultsStock = xmlDocStock.getFirstChild().getFirstChild();
		nodeListStock = resultsStock.getChildNodes();
		infoListStock.clear();
		infoListStock = getXMLStockInfos(nodeListStock);
		
		try {
			if(fieldToUpdate == "Name"){
				return (String) infoListStock.get(6);
			}
			else if(fieldToUpdate == "Ask"){
				return (String) infoListStock.get(12);
				}
			else if(fieldToUpdate == "PercentChange"){
				return (String) infoListStock.get(11);		
			}
			else if(fieldToUpdate == "Bid"){
				return (String) infoListStock.get(0);
			}
			else if(fieldToUpdate == "MarketCapitalization"){
				return (String) infoListStock.get(5);
			}
			else if(fieldToUpdate == "Open"){
				return (String) infoListStock.get(7);
			}
			else if(fieldToUpdate == "PreviousClose"){
				return (String) infoListStock.get(8);
			}
			else if(fieldToUpdate == "DaysHigh"){
				return (String) infoListStock.get(2);
			}
			else if(fieldToUpdate == "YearHigh"){
				return (String) infoListStock.get(4);
			}
			else if(fieldToUpdate == "DaysLow"){
				return (String) infoListStock.get(1);
			}
			else if(fieldToUpdate == "YearLow"){
				return (String) infoListStock.get(3);
			}
			else if(fieldToUpdate == "Volume"){
				return (String) infoListStock.get(10);
			}
			else if(fieldToUpdate == "PERatio"){
				return (String) infoListStock.get(9);
			}
		}catch(IndexOutOfBoundsException e) {
			
		}
		return null;
	}
	
	/**
	 * 
	 * @param stock
	 * @param dataPeriod
	 * @return
	 */
	public static ArrayList<String> getGraphDataPoints(String stock, String startDate, String endDate) throws Exception{
		loadDriver(); 
		xmlGraphString = getXMLGraphData(stock, startDate, endDate); // All the XML File in a String
		xmlDocGraph = loadXMLFromString(xmlGraphString);
		resultsGraph = xmlDocGraph.getFirstChild().getFirstChild();
		nodeListGraph = resultsGraph.getChildNodes();
		infoListGraph.clear();
		infoListGraph = getXMLStockInfos(nodeListGraph);
		// Need to reverse the ArrayList to have data in the desired order (past to present)
		Collections.reverse(infoListGraph);
		return infoListGraph; 
	}	
	
	/**
	 * 
	 * @param stringFromXML
	 * @return
	 */
	public static String getPercentChange(String stringFromXML) {
		String[] list = stringFromXML.split(" ");
		
		if(list.length != 3){
			return stringFromXML;
		}
		else{
			return list[2];
		}
	}
	
	/**
	 * 
	 * @param nodeList
	 * @return
	 */
	public static ArrayList<String> getXMLStockInfos(NodeList nodeList){
		ArrayList<String> infoList = new ArrayList<String>();
		for(int i = 0; i < nodeList.getLength(); i++) { 
			NodeList n = nodeList.item(i).getChildNodes();
			for(int j = 0; j < n.getLength(); j++) { 
				  infoList.add(n.item(j).getTextContent());
			}
		}
		return infoList;
	}
	
	/**
	 * 
	 * @param stock
	 * @return
	 */
	public static String getXMLUniqueStock(String stock){
		String xmlString = null;
		try{
			xmlString = getURLContent("https://query.yahooapis.com/v1/public/yql?q=select" + 
						"%20Name%2C%20Bid%2C%20DaysLow%2C%20DaysHigh%2C%20YearLow%2C%20Ye" +
						"arHigh%2C%20MarketCapitalization%2C%20Open%2C%20PreviousClose%2C" +
						"%20PercentChange%2C%20Ask%2C%20Volume%2C%20PERatio%20from%20yaho" +
						"o.finance.quotes%20where%20symbol%3D%22" + stock + "%22&env=stor" +
						"e%3A%2F%2Fdatatables.org%2Falltableswithkeys");
		}
		catch(Exception ex){
			
		}
		return xmlString;
	}
	
	/**
	 * 
	 * @param stock
	 * @return
	 */
	public static String getXMLGraphData(String stock, String startDate, String endDate){
		String xmlString = null;
		try{
			xmlString = getURLContent("https://query.yahooapis.com/v1/public/yql?q=select%20"
					+ "Adj_Close%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D"
					+ "%20%22" + stock + "%22%20and%20startDate%20%3D%20%22" + startDate + "%22%20and%20endDate"
					+ "%20%3D%20%22" + endDate + "%22&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys");
		}
		catch(Exception ex){
			
		}
		return xmlString;
	}
	
	public static double getGraphDataPointsUnique(String stock, String startDate, String endDate) throws Exception{
		loadDriver();
		xmlGraphString = getXMLGraphData(stock, startDate, endDate); //all the XML File in a String
		xmlDocGraph = loadXMLFromString(xmlGraphString);
		resultsGraph = xmlDocGraph.getFirstChild().getFirstChild();
		nodeListGraph = resultsGraph.getChildNodes();
		infoListGraph.clear();
		infoListGraph = getXMLStockInfos(nodeListGraph);
		// Need to reverse the ArrayList to have data in the desired order (past to present)
		Collections.reverse(infoListGraph);
		return Double.parseDouble(infoListGraph.get(0)); 
	}
	
	/**
	 * 
	 */
	public static void loadDriver(){
		try {
		    Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
		    throw new RuntimeException("Cannot find the driver in the classpath!", e);
		}
	}
	
	/**
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static Document loadXMLFromString(String xml) throws Exception{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xml));
	    return (Document) builder.parse(is);
	}

	/**
	 * 
	 * @param p_sURL
	 * @return
	 */
	public static String getURLContent(String p_sURL){
	    URL oURL;
	    URLConnection oConnection;
	    BufferedReader oReader;
	    String sLine;
	    StringBuilder sbResponse;
	    String sResponse = null;

	    try{
	        oURL = new URL(p_sURL);
	        oConnection = oURL.openConnection();
	        oReader = new BufferedReader(new InputStreamReader(oConnection.getInputStream()));
	        sbResponse = new StringBuilder();

	        while((sLine = oReader.readLine()) != null){
	            sbResponse.append(sLine);
	        }
	        sResponse = sbResponse.toString();
	    }
	    catch(Exception e){

	    }
	    return sResponse;
	}
}
