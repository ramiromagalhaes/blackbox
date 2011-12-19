package br.rj.eso;

import java.util.LinkedList;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import br.rj.eso.control.AccelerationHandler;
import br.rj.eso.control.DistanceHandler;
import br.rj.eso.control.GPSHandler;
import br.rj.eso.control.ParameterHandler;
import br.rj.eso.listener.BTConClickListener;
import br.rj.eso.listener.CoordOnClickListener;
import br.rj.eso.util.Util;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;




public class BTActivity extends MapActivity {
	private static final int REQUEST_ENABLE_BT = 2;
	public ConnectionThread connected;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        ActivityHolder.ACTIVITY=this;
        //TODO descomentar 
        //Tenta iniciar o Bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e("ERROR-BT", "Aparelho não suporta Bluetooth");
            return;
        }
        //Verifica se está habilitado, se não estiver habilita
        try{
	        if (!mBluetoothAdapter.isEnabled()) {
	            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	        }
        }catch(Exception e){
        	Log.e("ERROR-BT", e.getMessage());
        }
        
       
        //Muda a tela para a tela principal 
        setContentView(R.layout.main);
        //Recupera a View que contém todas as outras
        LinearLayout ll = (LinearLayout)this.findViewById(R.id.mainL);
        //Encontra aparelhos pareados
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		 if (pairedDevices.size() > 0) {
		     //Itera os aparelhos pareados
		     for (BluetoothDevice device : pairedDevices) {
		         Log.d("DEBUG-BT", device.getName() + "\n" + device.getAddress());
		       //Para cada aparelho cria um botão que ao ser clicado conecta 
		         Button bt = new Button(this);
		         String name = device.getName();
		         if(name==null || name.equals(""))name = device.getAddress();
		         bt.setText(name);
		         bt.setOnClickListener(new BTConClickListener(device,mBluetoothAdapter));
		         ll.addView(bt);
		     }
		 }else{
			 Log.d("DEBUG-BT", "Nao foi encontrado nenhum aparelho para parear");
		 }
        /*ParameterHandler ph = new ParameterHandler();
        ph.receiveParametersTestMode();
        goToResultPage();*/
    }
    public void goToRunningPage(){
    	
    	//Muda a página atual para a página "running" (ou seja, carregando dados do Arduino)
    	setContentView(R.layout.running);
    	
    	LinearLayout ll = (LinearLayout)this.findViewById(R.id.runningLL);
        
    	//Coloca um botão para parar o recebimento de dados e exibir os resultados
        Button bt = new Button(this);
        bt.setText("Stop");
        bt.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				if(connected!=null){
					connected.cancel();
					ActivityHolder.ACTIVITY.goToResultPage();
				}
			}});
        ll.addView(bt);
    	
    }
    public void goToResultPage(){
    	//Muda para a página de resultados
    	setContentView(R.layout.result);
    	
    	//LinearLayout ll = (LinearLayout)this.findViewById(R.id.resultLL);
    	
    	//Espera o ParameterHandler terminar de colher os dados
    	while(ParameterHandler.RUNNING);
    	
    	
    	TableLayout tabela = (TableLayout)this.findViewById(R.id.resultTable);
    	//tabela.setBackgroundResource(R.layout.testeborda);
    	TableRow cabecalho = new TableRow(this);
    	cabecalho.setBackgroundResource(R.layout.testeborda);
    	TextView tvTime = new TextView(this);
    	tvTime.setText("Tempo");
    	tvTime.setPadding(5, 0, 5, 0);
    	tvTime.setTextColor(Color.BLACK);
    	tvTime.setTextSize(25);
    	
    	TextView tvAccX = new TextView(this);
    	tvAccX.setText("Acc eixo X");
    	tvAccX.setPadding(5, 0, 5, 0);
    	tvAccX.setTextColor(Color.BLACK);
    	tvAccX.setTextSize(25);
    	
    	TextView tvAccY = new TextView(this);
    	tvAccY.setText("Acc eixo Y");
    	tvAccY.setPadding(5, 0, 5, 0);
    	tvAccY.setTextColor(Color.BLACK);
    	tvAccY.setTextSize(25);
    	
    	TextView tvAccZ = new TextView(this);
    	tvAccZ.setText("Acc eixo Z");
    	tvAccZ.setPadding(5, 0, 5, 0);
    	tvAccZ.setTextColor(Color.BLACK);
    	tvAccZ.setTextSize(25);
    	
    	TextView tvDistancia = new TextView(this);
    	tvDistancia.setText("Velocidade");
    	tvDistancia.setPadding(5, 0, 5, 0);
    	tvDistancia.setTextColor(Color.BLACK);
    	tvDistancia.setTextSize(25);
    	
    	TextView tvLoc = new TextView(this);
    	tvLoc.setText("Localizacao");
    	tvLoc.setPadding(5, 0, 5, 0);
    	tvLoc.setTextColor(Color.BLACK);
    	tvLoc.setTextSize(25);
    	
    	cabecalho.addView(tvTime);
    	cabecalho.addView(tvAccX);
    	cabecalho.addView(tvAccY);
    	cabecalho.addView(tvAccZ);
    	cabecalho.addView(tvDistancia);
    	cabecalho.addView(tvLoc);
    	
    	tabela.addView(cabecalho);
    	cabecalho.setPadding(0, 3, 0, 5);
    	
    	LinkedList<Long> tempos = Util.ordena(DistanceHandler.getInstance().getDistance().keySet());
    	
    	double maiorAcX = Util.maior(AccelerationHandler.getInstance().getAccelerationX().values());
    	double menorAcX = Util.menor(AccelerationHandler.getInstance().getAccelerationX().values());
    	
    	double maiorAcY = Util.maior(AccelerationHandler.getInstance().getAccelerationY().values());
    	double menorAcY = Util.menor(AccelerationHandler.getInstance().getAccelerationY().values());
    	
    	double maiorAcZ = Util.maior(AccelerationHandler.getInstance().getAccelerationZ().values());
    	double menorAcZ = Util.menor(AccelerationHandler.getInstance().getAccelerationZ().values());
    	
    	double maiorDist = Util.maior(DistanceHandler.getInstance().getDistance().values());
    	double menorDist = Util.menor(DistanceHandler.getInstance().getDistance().values());
    	for(Long tempo : tempos){
    		TableRow linha = new TableRow(this);
    		linha.setBackgroundResource(R.layout.testeborda);
    		String tSegundos = ((tempo-tempos.getFirst())/1000.0)+"";
			if(tSegundos.contains(".")){
				try{
					tSegundos = tSegundos.substring(0, tSegundos.indexOf(".")+2);
				}catch(Exception e){}
			}
    		TextView tvTempo = new TextView(this);
    		tvTempo.setText(tSegundos);
    		tvTempo.setTextSize(25);
    		tvTempo.setPadding(5, 0, 5,0);
    		tvTempo.setTextColor(Color.BLACK);
    		linha.addView(tvTempo);
    		
    		
    		TextView tvAx = new TextView(this);
    		double acx=AccelerationHandler.getInstance().getAccelerationX().get(tempo);
    		tvAx.setText(acx+"");
    		tvAx.setTextSize(25);
    		tvAx.setPadding(5, 0, 5,0);
    		if(acx==maiorAcX){
    			tvAx.setTextColor(Color.RED);
    		}else if(acx==menorAcX){
    			tvAx.setTextColor(Color.BLUE);
    		}else {
    			tvAx.setTextColor(Color.BLACK);
    		}
    		linha.addView(tvAx);
    		
    		
    		TextView tvAy = new TextView(this);
    		double acy = AccelerationHandler.getInstance().getAccelerationY().get(tempo);
    		tvAy.setText(acy+"");
    		tvAy.setTextSize(25);
    		tvAy.setPadding(5, 0, 5,0);
    		if(acy==maiorAcY){
    			tvAy.setTextColor(Color.RED);
    		}else if(acy==menorAcY){
    			tvAy.setTextColor(Color.BLUE);
    		}else {
    			tvAy.setTextColor(Color.BLACK);
    		}
    		linha.addView(tvAy);
    		
    		TextView tvAz = new TextView(this);
    		double acz = AccelerationHandler.getInstance().getAccelerationZ().get(tempo);
    		tvAz.setText(acz+"");
    		tvAz.setTextSize(25);
    		tvAz.setPadding(5, 0, 5,0);
    		if(acz==maiorAcZ){
    			tvAz.setTextColor(Color.RED);
    		}else if(acz==menorAcZ){
    			tvAz.setTextColor(Color.BLUE);
    		}else {
    			tvAz.setTextColor(Color.BLACK);
    		}
    		linha.addView(tvAz);
    		
    		TextView tvDist = new TextView(this);
    		double dist = DistanceHandler.getInstance().getDistance().get(tempo);
    		tvDist.setText(dist+"");
    		tvDist.setTextSize(25);
    		tvDist.setPadding(5, 0, 5,0);
    		if(dist==maiorDist){
    			tvDist.setTextColor(Color.RED);
    		}else if(dist==menorDist){
    			tvDist.setTextColor(Color.BLUE);
    		}else {
    			tvDist.setTextColor(Color.BLACK);
    		}
    		linha.addView(tvDist);
    		
    		TextView tvCoord = new TextView(this);
    		tvCoord.setText(GPSHandler.getInstance().getCoordinates().get(tempo));
    		tvCoord.setTextSize(25);
    		tvCoord.setPadding(5, 0, 5,0);
    		tvCoord.setTextColor(Color.BLACK);
    		tvCoord.setOnClickListener(new CoordOnClickListener(GPSHandler.getInstance().getCoordinates().get(tempo)));
    		linha.addView(tvCoord);
    		
    		linha.setPadding(0, 3, 0, 5);
    		
    		linha.setOnClickListener(new OnClickListener(){
				public void onClick(View linha) {
					TableLayout tl = (TableLayout)linha.getParent();
					for(int i=1;i<tl.getChildCount();i++){
						tl.getChildAt(i).setBackgroundResource(R.layout.testeborda);
					}
					linha.setBackgroundColor(Color.LTGRAY);
				}});
    		tabela.addView(linha);
    	}
    	
    	//ll.addView(tabela);
    	
    	/*ImageView view = ((ImageView) this.findViewById(R.id.grafico));
		view.setImageBitmap(new GraphicGenerator().makeGraphic(AccelerationHandler.getInstance().getAccelerationX()));*/
    	
    }
    public void goToMapPage(int lat, int lon){
    	setContentView(R.layout.mapa);
    	
    	MapView map = (MapView)this.findViewById(R.id.viewMapa);
    	map.setBuiltInZoomControls(false);
    	Log.d("DEBUG-BT", lat+","+lon);
    	map.getController().setCenter(new GeoPoint(lat,lon));
    	map.getController().setZoom(21);
    }
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}