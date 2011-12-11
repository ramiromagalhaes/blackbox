package br.rj.eso.listener;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.View.OnClickListener;
import br.rj.eso.ActivityHolder;
import br.rj.eso.BTActivity;
import br.rj.eso.ConnectionThread;

public class BTConClickListener implements OnClickListener {
	private BluetoothDevice device;
	private BluetoothAdapter adapter;

	public BTConClickListener(BluetoothDevice device, BluetoothAdapter adapter) {
		this.device = device;
		this.adapter = adapter;
	}

	@Override
	public void onClick(View v) {
		//Inicia a thread de conexão
		ConnectionThread ct = new ConnectionThread(device,adapter);
		ct.start();
		ActivityHolder.ACTIVITY.connected=ct;
		ActivityHolder.ACTIVITY.goToRunningPage();

	}

}
