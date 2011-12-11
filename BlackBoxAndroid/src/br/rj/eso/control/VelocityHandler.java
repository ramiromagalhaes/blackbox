package br.rj.eso.control;

import java.util.HashMap;
import java.util.Map;

public class VelocityHandler {
	private Map<Long,Double> VELOCITY = new HashMap<Long,Double>();
	private static VelocityHandler instance;
	private VelocityHandler(){}
	public static VelocityHandler getInstance(){
		if (instance == null) {
			instance = new VelocityHandler();
		}
		return instance;
	}
	public  void  handleVelocity(long timestamp,double velocity){
		VELOCITY.put(timestamp,velocity);
	}
	public   Map<Long,Double> getVelocity(){
		return VELOCITY;
	}
	public void reset(){
		VELOCITY = new HashMap<Long,Double>();
	}
}
