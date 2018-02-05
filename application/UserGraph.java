package application;

import java.sql.SQLException;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public class UserGraph {

	XYChart.Series<String,Double> series;
	
	public UserGraph(int id) throws SQLException {
		
		Database.connectionDatabase();
		String[][] s = Database.getPortfolioValueGraph(id);
		
		series = new XYChart.Series<>();
		if(s != null) {
			for(int i = 0; i < s[0].length; i++){
				try {
					series.getData().add(new Data<String, Double>(s[1][i], Double.parseDouble(s[0][i])));
				} 
				catch (Exception e) {
					
				}
			}
		}
	}
	
	public Series<String, Double> getSeries() {
		return series;
	}
}
