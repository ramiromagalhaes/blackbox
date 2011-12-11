package br.rj.eso.control;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class AccelerationHandler {
	private Map<Long,Double> ACCELERATION_X = new HashMap<Long,Double>();
	private Map<Long,Double> ACCELERATION_Y = new HashMap<Long,Double>();
	private Map<Long,Double> ACCELERATION_Z = new HashMap<Long,Double>();
	private static AccelerationHandler instance;
	private AccelerationHandler(){}
	public static AccelerationHandler getInstance(){
		if (instance == null) {
			instance = new AccelerationHandler();
		}
		return instance;
	}
	public  void  handleAcceleration(long timestamp,double x,double y, double z){
		ACCELERATION_X.put(timestamp,x);
		ACCELERATION_Y.put(timestamp,y);
		ACCELERATION_Z.put(timestamp,z);
	}
	public   Map<Long,Double> getAccelerationX(){
		return ACCELERATION_X;
	}
	public   Map<Long,Double> getAccelerationY(){
		return ACCELERATION_Y;
	}
	public   Map<Long,Double> getAccelerationZ(){
		return ACCELERATION_Z;
	}
	public void reset(){
		ACCELERATION_X = new HashMap<Long,Double>();
		ACCELERATION_Y = new HashMap<Long,Double>();
		ACCELERATION_Z = new HashMap<Long,Double>();
	}
}
