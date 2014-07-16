package br.usp.icmc.lrm.telemetria;

import android.os.Looper;

//Solucao criativa para problemas complexos
public class LooperThread extends Thread{
		
	private Looper myLooper = null;
	
	public LooperThread(){
		
	}
	
	public void run() {
		Looper.prepare();
		myLooper = Looper.myLooper();
	}
	
	public Looper getMyLooper(){
		while(myLooper == null);
		return this.myLooper;
	}
	
}
