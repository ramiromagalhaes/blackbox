package br.rj.eso;

import java.io.IOException;
import java.util.UUID;

import br.rj.eso.control.ParameterHandler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ConnectionThread extends Thread {
	//Thread que trata da conexão com o Arduino
	 	private final BluetoothSocket mmSocket;
	    private final BluetoothAdapter mBluetoothAdapter;
	    //UUID padrão para conexão genérica
	    private static final UUID DEFAULT_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	    
	    public static boolean RUNNING=false;
	 
	    public ConnectionThread(BluetoothDevice device, BluetoothAdapter mBluetoothAdapter) {
	        BluetoothSocket tmp = null;
	        this.mBluetoothAdapter = mBluetoothAdapter;
	 
	        try {
	            tmp = device.createRfcommSocketToServiceRecord(DEFAULT_UUID);
	        } catch (IOException e) { }
	        mmSocket = tmp;
	    }
	    
	    public void run() {
	    	RUNNING=true;
	        // Cancel discovery because it will slow down the connection
	        mBluetoothAdapter.cancelDiscovery();
	 
	            ParameterHandler pHandler = new ParameterHandler();
	            try {
					pHandler.receiveParameters( mmSocket);
				} catch (IOException e) {
					RUNNING=false;
					Log.e("ERROR-BT", e.getMessage());
				}
	    }
	 

		/** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	    	RUNNING=false; 
	    }
}
