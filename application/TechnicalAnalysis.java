package application;

import java.util.ArrayList;

public abstract class TechnicalAnalysis {

	
		/**
		 * 
		 * @param graphPoints
		 * @param movingLength
		 * @return
		 */
		public static ArrayList<Double> SMAComputation(ArrayList<Double> graphPoints, int movingLength){
			double movingAverageValue;
			double totalMoving;
			
			if(movingLength > graphPoints.size()){
				//Error box with Java FX or error sound 
			}
			
			ArrayList<Double> movingAverage = new ArrayList<>();
			for(int i = movingLength; i < graphPoints.size(); i++){
				totalMoving = 0; 
				movingAverageValue = 0;
				for(int j = i - 1; j >= i - movingLength; j--){
					totalMoving += graphPoints.get(j);
					movingAverageValue = totalMoving / movingLength;
					
				}
				movingAverage.add(movingAverageValue);
			}
			return movingAverage;
		}
		
		
		/**
		 * 
		 * @param graphDataPoints
		 * @return
		 */
		public static double standardDeviation(ArrayList<Double> graphDataPoints){
			double totalBollinger = 0;
			double average;
			double series = 0;
			double standardDeviation;
			
			for(int i = 0; i < graphDataPoints.size(); i++){
				totalBollinger += graphDataPoints.get(i);
			}
			average = totalBollinger / graphDataPoints.size();
			
			for(int i = 0; i < graphDataPoints.size(); i++){
				series += Math.pow((graphDataPoints.get(i) - average), 2);
			}
			standardDeviation = Math.sqrt(series / graphDataPoints.size());
			return standardDeviation;
		}
		
		
		/**
		 * 
		 * @param graphPoints
		 * @param movingLength
		 * @return
		 */
		public static ArrayList<Double> standarDeviationSubset(ArrayList<Double> graphPoints, int movingLength){
			ArrayList<Double> subset = new ArrayList<>();
			ArrayList<Double> standardDevList = new ArrayList<>();
			
			for(int i = movingLength; i < graphPoints.size(); i++){
				for(int j = i - 1; j > i - movingLength; j--){
					subset.add(graphPoints.get(j));
					}
				standardDevList.add(TechnicalAnalysis.standardDeviation(subset));
			}
			return standardDevList; 
		}
		
		
		/**
		 * 
		 * @param graphData
		 * @param movingLength
		 * @param band
		 * @return
		 */
		public static ArrayList<Double> bollingerBands(ArrayList<Double> graphPoint, int movingLength, String band){
			ArrayList<Double> SMA = TechnicalAnalysis.SMAComputation(graphPoint, movingLength); 
			ArrayList<Double> upper = new ArrayList<>();
			ArrayList<Double> lower = new ArrayList<>(); 

			if(band == "upper"){
				for(int i = 0; i < SMA.size(); i++){
					upper.add(SMA.get(i) + 2 * ((TechnicalAnalysis.standarDeviationSubset(graphPoint, movingLength)).get(i)));
				}
				return upper;
			}
			if(band == "lower"){
				for(int i = 0; i < SMA.size(); i++){
					lower.add(SMA.get(i) - 2 * ((TechnicalAnalysis.standarDeviationSubset(graphPoint, movingLength)).get(i)));
				}
				return lower;
			}
			else{
				return null;
			}			
		}
}
