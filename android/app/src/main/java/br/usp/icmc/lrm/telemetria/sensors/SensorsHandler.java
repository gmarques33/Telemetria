package br.usp.icmc.lrm.telemetria.sensors;

import java.util.ArrayList;

import android.content.Context;

public class SensorsHandler {

	private ArrayList<GenericSensor> sensorList;
	private Context context;
	
	public SensorsHandler(Context context){
		this.sensorList = new ArrayList<GenericSensor>();
		setContext(context);
		updateSensorsList();
	}
	
	public void updateSensorsList(){
		this.sensorList.addAll(Gps.getList(this.context));
		this.sensorList.addAll(NetworkLocation.getList(this.context));
		this.sensorList.addAll(Accelerometer.getList(this.context));
		this.sensorList.addAll(Battery.getList(this.context));
		this.sensorList.addAll(CameraS.getList(this.context));
	}

	public ArrayList<GenericSensor> getSensorList() {
		return sensorList;
	}
	
	public void get(){

	}
	
	public void setContext(Context context){
		this.context = context;
	}
}
