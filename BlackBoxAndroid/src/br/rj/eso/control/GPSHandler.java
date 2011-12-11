package br.rj.eso.control;

import java.util.HashMap;
import java.util.Map;

public class GPSHandler {
	private Map<Long,String> COORDINATES = new HashMap<Long,String>();
	private static GPSHandler instance;
	private GPSHandler(){}
	public static GPSHandler getInstance(){
		if (instance == null) {
			instance = new GPSHandler();
		}
		return instance;
	}
	public  void  handleCoordinates(long timestamp,String coordinates){
		COORDINATES.put(timestamp,coordinates);
	}
	public  Map<Long,String> getCoordinates(){
		return COORDINATES;
	}
	public void reset(){
		COORDINATES = new HashMap<Long,String>();
	}
}
