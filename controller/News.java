package controller;

import java.io.IOException;
import java.util.ArrayList;

import application.GraphSeries;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class News extends AnchorPane {
	
	@FXML
	private GridPane groundedFlash;
	@FXML
	private GridPane movingFlash;
	@FXML
	private LineChart<String, Double> graph;
	
	private boolean done = false;
	
	ArrayList<NewsFlash> temp = new ArrayList<NewsFlash>();
	
	public News() throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/News.fxml"));
	    fxmlLoader.setRoot(this);
	    fxmlLoader.setController(this);
	    try {
	        fxmlLoader.load();
	    } catch (IOException exception) {
	        throw new RuntimeException(exception);
	    }
	    
	 // Smooths the GUI while loading
 		Platform.runLater(new Runnable() {
 		    public void run() {
 		    	graph.setCreateSymbols(false);
 		    	GraphSeries g = new GraphSeries(-1, 30);
 		    	graph.getData().clear();
 		    	graph.getData().add(g.getSeries());
 		    	Timer t = new Timer();
 			    t.start();
 			    try {
	 			    groundedFlash.add(new NewsFlash("Bank"), 0, 0);
	 			    groundedFlash.add(new NewsFlash("Financial"), 0, 1);
	 			    groundedFlash.add(new NewsFlash("Industrial"), 0, 2);
	 			    groundedFlash.add(new NewsFlash("Insurance"), 0, 3);
	 			    groundedFlash.add(new NewsFlash("Computer"), 1, 0);
	 			    groundedFlash.add(new NewsFlash("Transportation"), 1, 1);
	 			    groundedFlash.add(new NewsFlash("Telecommunication"), 1, 2);
	 			    groundedFlash.add(new NewsFlash("Biotech"), 1, 3);
 			    }catch(Exception e) {
 			    	
 			    }
 			    loadNewsFlash();
 		    }
 		});
	}
	
	public void loadNewsFlash() {
		done = false;
		temp.clear();
		Thread th = new Thread();
	    th.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				int k = 0;
				for(int i = 0; i < 4; i++) {
					for(int j = 0; j < 2; j++) {
						movingFlash.add(temp.get(k), i, j);
						k++;
					}
				}
				done = true;
			}
	    });
	    th.start();
	}
	
	class Timer extends AnimationTimer {
		
		long time = System.currentTimeMillis();
		
		@Override
		public void handle(long now) {
			if(System.currentTimeMillis() - time > 10000 && done) {
				loadNewsFlash();
				time = System.currentTimeMillis();
			}
		}
	}
	
	class Thread extends Service<Void> {

		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					for(int i = 0; i < 4; i++) {
				    	for(int j = 0; j < 2; j++) {
				    		temp.add(new NewsFlash("Random"));
				    	}
				    }
					this.succeeded();
					return null;
				}
			};
		}
	}
}
