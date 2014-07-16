package br.usp.icmc.lrm.telemetria.sensors;

import java.util.ArrayList;

import br.usp.icmc.lrm.telemetria.exceptions.SensorStateException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class Battery extends BroadcastReceiver implements GenericSensor {

	private Context context;
	private Integer scale;
	private Integer level;
	private Integer voltage;
	private Integer temp;
	private GenericSensor.status status;

	public static ArrayList<GenericSensor> getList(Context context) {

		ArrayList<GenericSensor> batteries = null;

		batteries = new ArrayList<GenericSensor>();
		batteries.add(new Battery(context));

		return batteries;
	}

	public Battery(Context context) {
		this.status = GenericSensor.status.off;
		this.context = context;
	}

	@Override
	public status getStatus() {
		return this.status;
	}

	@Override
	public synchronized void turnOn() {
		if (!this.status.equals(GenericSensor.status.on)) {
			IntentFilter filter = new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED);
			this.context.registerReceiver(this, filter);
			//Wait sensor to be ready
			while(this.level == null || this.scale == null || this.temp == null || this.voltage == null);
			this.status = GenericSensor.status.on;
		}
	}

	@Override
	public synchronized void turnOn(int accuracy) {
		turnOn();
	}

	@Override
	public synchronized void turnOff() {
		if (!this.status.equals(GenericSensor.status.off)) {
			this.status = GenericSensor.status.off;
			//Return to original values
			this.level = null;
			this.scale = null;
			this.temp = null;
			this.voltage = null;
			this.context.unregisterReceiver(this);
		}
	}

	@Override
	public String getData() throws SensorStateException {
		if (this.status.equals(GenericSensor.status.on)) {
			String data = null;
			try{
				data = this.level.toString() + " " + this.scale.toString() + " "
						+ this.temp.toString() + " " + this.voltage.toString();
				return data;
			}catch(NullPointerException e){
				Log.w("Telemetria", "OutOfSync");
				throw new SensorStateException("SensorStatus "
						+ this.status.toString());
			}
		}else
			throw new SensorStateException("SensorStatus "
					+ this.status.toString());
	}
	
	public byte[] getBytes(){
		return null;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		this.level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		this.scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		this.temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
		this.voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
	}

}
