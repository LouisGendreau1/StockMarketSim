package application;

import java.util.ArrayList;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public class GraphSeries {

	int stockId;
	XYChart.Series<String,Double> series;
	XYChart.Series<String,Double> SMASeries;
	XYChart.Series<String,Double> bollingerSeries;
	XYChart.Series<String,Double> bollingerSeries2;
	
	public GraphSeries(int id, int timespan) {
		this.stockId = id;
		ArrayList<String> sourceList = new ArrayList<String>();
		ArrayList<Double> SMAList = new ArrayList<Double>();
		ArrayList<Double> bollingerList = new ArrayList<Double>();
		ArrayList<Double> bollingerList2 = new ArrayList<Double>();
		series = new XYChart.Series<>();
		SMASeries = new XYChart.Series<>(); // Simple moving average series
		bollingerSeries = new XYChart.Series<>(); // Bollinger Bands series
		bollingerSeries2 = new XYChart.Series<>(); // Bollinger Bands series
		
		int sourceindex = 0;
		int analysisindex = 0;
		
		if(stockId == -1) {
			try {
				sourceList = XML.getGraphDataPoints("^GSPC", MarketTime.getDateStringXML(timespan), MarketTime.getDateString(1));
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			try {
				sourceList = XML.getGraphDataPoints(MSML.STOCK_NAME[stockId], MarketTime.getDateStringXML(timespan), MarketTime.getDateString(1));
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 0; i < sourceList.size(); i++){
			SMAList.add(Double.parseDouble(sourceList.get(i)));
			bollingerList.add(Double.parseDouble(sourceList.get(i)));
			bollingerList2.add(Double.parseDouble(sourceList.get(i)));
		}
		
		SMAList = TechnicalAnalysis.SMAComputation(SMAList, 5);
		bollingerList = TechnicalAnalysis.bollingerBands(bollingerList, 5, "upper");
		bollingerList2 = TechnicalAnalysis.bollingerBands(bollingerList2, 5, "lower");
		for(int i = 0; i < timespan; i++) {
			try {
				if(!MarketTime.getDateString(timespan - i).equals("0")){
					series.getData().add(new Data<String, Double>(MarketTime.getDateString(timespan - i).substring(5), Double.parseDouble(sourceList.get(sourceindex))));
					if(i > 6){
						SMASeries.getData().add(new Data<String, Double>(MarketTime.getDateString(timespan - i).substring(5), (SMAList.get(analysisindex))));
						bollingerSeries.getData().add(new Data<String, Double>(MarketTime.getDateString(timespan - i).substring(5), (bollingerList.get(analysisindex))));
						bollingerSeries2.getData().add(new Data<String, Double>(MarketTime.getDateString(timespan - i).substring(5), (bollingerList2.get(analysisindex))));
						analysisindex++;
					}
					sourceindex++;
				}
			} 
			catch (Exception e) {
				
			}
		}
	}
	
	public Series<String, Double> getSeries() {
		return series;
	}
	
	public XYChart.Series<String,Double> getSMASeries() {
		return SMASeries;
	}
	
	public XYChart.Series<String,Double> getBollingerSeries() {
		return bollingerSeries;
	}
	
	public XYChart.Series<String,Double> getBollingerSeries2() {
		return bollingerSeries2;
	}
}
