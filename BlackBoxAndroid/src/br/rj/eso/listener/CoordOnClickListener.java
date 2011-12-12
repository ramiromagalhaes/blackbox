package br.rj.eso.listener;

import android.view.View;
import android.view.View.OnClickListener;
import br.rj.eso.ActivityHolder;

public class CoordOnClickListener implements OnClickListener{
	private int lat;
	private int lon;

	public CoordOnClickListener(String coord) {
		try{
			String latAux = coord.split(";")[0];
			latAux=latAux.replace("Lat:", "");
			double graus = Double.parseDouble(latAux.split("º")[0]);
			graus +=Double.parseDouble(latAux.split("º")[0].split("'")[0])/60;
			graus*=1000000;
			lat = (int)graus;
			
			String lonAux = coord.split(";")[1];
			lonAux=lonAux.replace("Lon:", "");
			
			graus = Double.parseDouble(lonAux.split("º")[0]);
			graus +=Double.parseDouble(lonAux.split("º")[0].split("'")[0])/60;
			graus*=1000000;
			lon = (int)graus;
			
		}catch(Exception e){
			lat=0;
			lon=0;
		}
	}

	@Override
	public void onClick(View v) {
		ActivityHolder.ACTIVITY.goToMapPage(lat, lon);

	}
}
