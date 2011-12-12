package br.rj.eso.control;

import java.util.HashMap;
import java.util.Map;

public class DistanceHandler {
	private Map<Long,Double> DISTANCE = new HashMap<Long,Double>();
	private static DistanceHandler instance;
	private DistanceHandler(){}
	public static DistanceHandler getInstance(){
		if (instance == null) {
			instance = new DistanceHandler();
		}
		return instance;
	}
	public  void  handleDistance(long timestamp,double velocity){
		DISTANCE.put(timestamp,velocity);
	}
	public   Map<Long,Double> getDistance(){
		return DISTANCE;
	}
	public void reset(){
		DISTANCE = new HashMap<Long,Double>();
	}
}
