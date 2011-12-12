package br.rj.eso.util;

import java.util.Collection;
import java.util.LinkedList;

public class Util {
	public static LinkedList<Long> ordena (Collection<Long> col){
		Object[] aux= col.toArray();
		LinkedList<Long> resultado = new LinkedList<Long>();
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
	public static double maior(Collection<Double> numeros){
		double maior=numeros.iterator().next();
		for(Double num : numeros){
			if(num>maior)maior=num;
		}
		return maior;
	}
	public static double menor(Collection<Double> numeros){
		double menor=numeros.iterator().next();
		for(Double num : numeros){
			if(num<menor)menor=num;
		}
		return menor;
	}
}
