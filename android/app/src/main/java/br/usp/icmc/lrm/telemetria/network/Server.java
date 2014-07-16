package br.usp.icmc.lrm.telemetria.network;

import java.io.IOException;
import java.net.ServerSocket;

import android.content.Context;
import android.util.Log;
import br.usp.icmc.lrm.telemetria.sensors.SensorsHandler;

public class Server implements Runnable {

	private ServerSocket serverSocket;
	private Integer clientsServed;
	private Integer port;
	@SuppressWarnings("unused")
	private Context context;
	private Thread myThread;
	private SensorsHandler sensorHandler;

	public Server(Integer port, Context context) throws IOException {
		this.port = port;
		this.clientsServed = 0;
		this.context = context;
		this.serverSocket = new ServerSocket(getPort());
		this.sensorHandler = new SensorsHandler(context);
		
		this.myThread = new Thread(this);
		this.myThread.start();
	}

	public Integer getPort() {
		return port;
	}

	public Integer getClientsServed() {
		return clientsServed;
	}

	@Override
	public void run() {
		while (this.myThread != null) {
			try {
				new OTLClientHandler(this.serverSocket.accept(), this.sensorHandler).start();
				this.clientsServed++;
			} catch (IOException e) {
				Log.e("Telemetria", "Error on Server Accept: " + e.getMessage());
			} 
		}
	}
	
	public Thread getThread(){
		return this.myThread;
	}
	
	public void stop(){
		try {
			this.serverSocket.close();
			this.serverSocket = null;
			this.myThread.interrupt();
			this.myThread = null;
		} catch (IOException e) {
			Log.e("Telemetria", "Error on Server Stop: " + e.getMessage());
		}
	}

}