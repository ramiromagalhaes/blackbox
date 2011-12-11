package br.rj.eso.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import br.rj.eso.ConnectionThread;

import android.bluetooth.BluetoothSocket;

public class ParameterHandler {
	//Classe que cuida do recebimento dos dados
	//Indica se ainda está recebendo dados
	public static  boolean RUNNING=false;
	public void receiveParameters(BluetoothSocket socket) throws IOException{
		RUNNING=true;
		//Conecta ao socket
		socket.connect();
		//Instancia as classes que vão gravar os dados e chama o reset para apagar dados antigos que estejam gravados
		DistanceHandler vH = DistanceHandler.getInstance();
		vH.reset();
		AccelerationHandler aH = AccelerationHandler.getInstance();
		aH.reset();
		GPSHandler gpsH = GPSHandler.getInstance();
		gpsH.reset();
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		//Aguarda o recebimento de 2 enter, isso garante que não começará a gravar dados
		//no meio de um envio
		String enter;
		int enterCount=0;
		while(enterCount<2){
			//
			enter=br.readLine();
			if(enter!=null && enter.equals("")){
				enterCount++;
			}else{
				enterCount=0;
			}
		}
		
		//Loop de recebimento dos dados
		long lastTime = System.currentTimeMillis();
		long currentTime=0;
		while(ConnectionThread.RUNNING){
			while(currentTime-lastTime<500){
				//Espera ocupada de 0.5 s
				currentTime = System.currentTimeMillis();
			}
			
			lastTime = System.currentTimeMillis();
			
			String vel = br.readLine();
			
			String acX = br.readLine();
			String acY = br.readLine();
			String acZ = br.readLine();
			
			String gps = br.readLine();
			
			String timestampStr = br.readLine();
			
			//2 enter
			enter=br.readLine();
			enter=br.readLine();
			
			Long timestamp =Long.parseLong(timestampStr);
			
			vH.handleDistance(timestamp,Double.parseDouble(vel));
			aH.handleAcceleration(timestamp,parseAcceleration(acX),parseAcceleration(acY),parseAcceleration(acZ));
			gpsH.handleCoordinates(timestamp,gps);
			
		}
		socket.close();
		RUNNING=false;
	}
	public void receiveParametersTestMode() {
		RUNNING=true;
		DistanceHandler vH = DistanceHandler.getInstance();
		vH.reset();
		AccelerationHandler aH = AccelerationHandler.getInstance();
		aH.reset();
		GPSHandler gpsH = GPSHandler.getInstance();
		gpsH.reset();
		for(int i=0;i<10;i++){
			Long timestamp =System.currentTimeMillis();
			
			vH.handleDistance(timestamp,Math.random()*100);
			aH.handleAcceleration(timestamp,Math.random()*1000,Math.random()*1000,Math.random()*1000);
			gpsH.handleCoordinates(timestamp,Math.random()+"");
		}
		RUNNING=false;
	}
	private double parseAcceleration(String acc){
		try{
			return Double.parseDouble(acc);
		}catch(NumberFormatException e){
			return Double.parseDouble(acc.split(":")[1]);
		}
		
	}
}
