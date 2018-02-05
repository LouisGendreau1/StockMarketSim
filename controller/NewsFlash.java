package controller;

import java.io.IOException;
import java.util.Random;

import application.XML;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class NewsFlash extends AnchorPane {

	@FXML
	private Label name;
	@FXML
	private Label value;
	@FXML
	private Label change;
	
	private static String stockSymbol;
	
	public NewsFlash(String t) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/NewsFlash.fxml"));
	    fxmlLoader.setRoot(this);
	    fxmlLoader.setController(this);
	    try {
	        fxmlLoader.load();
	    } catch (IOException exception) {
	        throw new RuntimeException(exception);
	    }
	    if(t.equals("Random")) {
	    	Random rand = new Random();
	    	int r = rand.nextInt(212) + 702;
	    	stockSymbol = "^YHOh" + r;
	    }else if(t.equals(("Bank"))) {
	    	stockSymbol = "^IXBK";
	    }else if(t.equals(("Financial"))) {
	    	stockSymbol = "^IXFN";
	    }else if(t.equals(("Industrial"))) {
	    	stockSymbol = "^IXID";
	    }else if(t.equals(("Insurance"))) {
	    	stockSymbol = "^IXIS";
	    }else if(t.equals(("Computer"))) {
	    	stockSymbol = "^IXK";
	    }else if(t.equals(("Transportation"))) {
	    	stockSymbol = "^IXTR";
	    }else if(t.equals(("Telecommunication"))) {
	    	stockSymbol = "^IXUT";
	    }else if(t.equals(("Biotech"))) {
	    	stockSymbol = "^NBI";
	    }
	    
	    String n;
		try {
			n = XML.update(stockSymbol, "Name");
		    if(n != "" || n != null) {
		    	name.setText(n);
		    }else {
		    	name.setText(" - ");
		    }
		    String v = XML.update(stockSymbol, "PreviousClose");
		    if(v != "" || v != null) {
		    	value.setText(v);
		    }else {
		    	value.setText(" - ");
		    }
		    String c = XML.update(stockSymbol, "PercentChange");
		    if(c != "" || c != null) {
		    	change.setText(c);
		    	try {
			    	if(c.charAt(0) == '-') {
			    		change.setTextFill(Color.RED);
			    	}else {
			    		change.setTextFill(Color.DARKGREEN);
			    	}
		    	}catch(Exception e) {
		    		
		    	}
		    }else {
		    	change.setText(" - ");
		    }
		} catch (Exception e) {
			
		}
	    
	}
}
