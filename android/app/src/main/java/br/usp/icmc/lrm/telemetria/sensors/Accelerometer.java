package br.usp.icmc.lrm.telemetria.sensors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.usp.icmc.lrm.telemetria.exceptions.SensorStateException;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Accelerometer implements GenericSensor, SensorEventListener {

	private Sensor androidSensor = null;
	private final SensorManager sensorManager;
	private float[] values = { -500, -500, -500 };
	private GenericSensor.status status;

	public Accelerometer(Sensor androidSensor, Context context) {
		this.androidSensor = androidSensor;
		this.sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		this.status = GenericSensor.status.off;
	}

	public static ArrayList<GenericSensor> getList(Context context) {
		SensorManager sensorManager;
		ArrayList<GenericSensor> genericSensorList = null;
		List<Sensor> androidSensorList = null;

		genericSensorList = new ArrayList<GenericSensor>();
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		androidSensorList = sensorManager
				.getSensorList(Sensor.TYPE_ACCELEROMETER);

		for (Iterator<Sensor> iterator = androidSensorList.iterator(); iterator
				.hasNext();) {
			genericSensorList.add(new Accelerometer(iterator.next(), context));
		}

		return genericSensorList;
	}

	@Override
	public status getStatus() {
		return this.status;
	}

	@Override
	public String getData() throws SensorStateException {
		if (this.status.equals(GenericSensor.status.on)) {
			String data = null;

			data = Float.toString(this.values[0]) + " ";
			data += (Float.toString(this.values[1]) + " ");
			data += Float.toString(this.values[2]);

			return data;

		} else
			throw new SensorStateException("SensorStatus "
					+ this.status.toString());
	}

	public float[] getValues() {
		return this.values;
	}

	public byte[] getBytes() {
		return null;
	}

	@Override
	public void turnOn() throws SensorStateException {
		turnOn(SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public synchronized void turnOn(int parameter) throws SensorStateException {
		if (!this.status.equals(GenericSensor.status.on)) {
			if (this.sensorManager.registerListener(this, androidSensor,
					parameter)) {
				this.status = GenericSensor.status.on;
				// Wait accelerometer be ready
				while (this.values[0] == -500 && this.values[1] == -500
						&& this.values[2] == -500);
			} else {
				this.status = GenericSensor.status.error;
				throw new SensorStateException("0004");
			}
		}
	}

	@Override
	public synchronized void turnOff() {
		if (!this.status.equals(GenericSensor.status.off)) {
			this.status = GenericSensor.status.off;
			this.sensorManager.unregisterListener(this);
			// Return values to original state
			this.values[0] = this.values[1] = this.values[2] = -500;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		this.values = event.values;
	}

}
