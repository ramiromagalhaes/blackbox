package br.rj.eso.control;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class GraphicGenerator {
	static final int width=280;
	static final int height=200;
	static final float initialX = 40;
	static final float initialY = 20;
	static final float finalX = 220;
	static final float finalY = 90;
	static final float axisY = finalY-initialY;
	//static final float changeIncrement = 0.5f;
	public Bitmap makeGraphic(Map<Long,Double> dados){
		
		Bitmap bm =Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas cvCanvas = new Canvas(bm);
		
		
		Paint pt = new Paint();
		
		pt.setColor(Color.BLACK);
		cvCanvas.drawARGB(255, 255, 255, 255);
		//Faço num for para que as linhas tenham 3 pixels de largura (de -1 a 1)
		for(int i=-1;i<1;i++){
			cvCanvas.drawLine(i+initialX, initialY, i+initialX, axisY*2+initialY, pt);
			cvCanvas.drawLine(initialX, i+finalY, finalX, i+finalY, pt);
		}
		Collection<Long> chavesOrdenadas = ordena(dados.keySet());
		int counter =0;
		float tempoInicialFicticio =0;
		Long tempoInicialReal = chavesOrdenadas.iterator().next();
		for(Long tempo : chavesOrdenadas){
			//Escrevo o tempo no eixo X
			String tSegundos = (tempoInicialFicticio+((tempo-tempoInicialReal)/1000.0))+"";
			if(tSegundos.contains(".")){
				try{
					tSegundos = tSegundos.substring(0, tSegundos.indexOf(".")+2);
				}catch(Exception e){}
			}
			cvCanvas.drawText(tSegundos, initialX+(30*counter), finalY+10, pt);
			
			
			//Escrevo o double no eixo Y
			double y = dados.get(tempo);
			
			cvCanvas.drawText(y+"", initialX-40, finalY-((float)y), pt);
			
			//Marco um ponto
			Paint ptLinha = new Paint();
			
			ptLinha.setColor(Color.RED);
			
			cvCanvas.drawLine(initialX+(30*counter),  finalY-((float)y), initialX+(30*counter)+2,  finalY-((float)y), ptLinha);
			
			counter++;
			
			
		}
		return bm;
		
	}
	
	private Collection<Long> ordena(Collection<Long> col){
		Object[] aux= col.toArray();
		Collection<Long> resultado = new LinkedList<Long>();
		for(int i=0;i<aux.length;i++){
			for(int j=0;j<aux.length;j++){
				if((Long)aux[i]<(Long)aux[j]){
					long tmp = (Long)aux[i];
					aux[i]=aux[j];
					aux[j]=tmp;
				}
			}
		}
		
		for(int i=0;i<aux.length;i++){
			resultado.add((Long)aux[i]);
		}
		return resultado;
	}
}
