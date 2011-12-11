package br.rj.eso;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import br.rj.eso.control.AccelerationHandler;
import br.rj.eso.control.GraphicGenerator;
import br.rj.eso.control.ParameterHandler;
import br.rj.eso.listener.BTConClickListener;



public class BTActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 2;
	public ConnectionThread connected;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        ActivityHolder.ACTIVITY=this;
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
    	
    	LinearLayout ll = (LinearLayout)this.findViewById(R.id.runningLL);
    	
    	//Espera o ParameterHandler terminar de colher os dados
    	while(ParameterHandler.RUNNING);
    	
    	
    	ImageView view = ((ImageView) this.findViewById(R.id.grafico));
		view.setImageBitmap(new GraphicGenerator().makeGraphic(AccelerationHandler.getInstance().getAccelerationX()));
    	
    }
}