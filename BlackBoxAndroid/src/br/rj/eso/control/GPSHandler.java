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
		COORDINATES.put(timestamp,this.parse(coordinates));
	}
	private String parse(String coord){
		try{
			String aux[]=coord.split(",");
			String resultado="";
			String graus = aux[3];
			String minutos="0";
			if(aux[3].contains(".")){
				minutos=aux[3].substring(aux[3].indexOf(".")-2, aux[3].length());
				graus = aux[3].replace(minutos, "");
				
			}
			minutos+="'";
			graus+="º";
			
			resultado="Lat:"+graus+minutos+aux[4]+";";
			
			graus = aux[5];
			minutos="0'";
			if(aux[5].contains(".")){
				minutos=aux[5].substring(aux[5].indexOf(".")-2, aux[5].length());
				graus = aux[5].replace(minutos, "");
				
			}
			minutos+="'";
			graus+="º";
			resultado+="Lon:"+graus+minutos+aux[6];
			return resultado;
		}catch(Exception e){
			return coord;
		}
		
	}
	public  Map<Long,String> getCoordinates(){
		return COORDINATES;
	}
	public void reset(){
		COORDINATES = new HashMap<Long,String>();
	}
}
