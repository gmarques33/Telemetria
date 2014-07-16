package br.usp.icmc.lrm.telemetria.sensors;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import br.usp.icmc.lrm.telemetria.LooperThread;
import br.usp.icmc.lrm.telemetria.exceptions.SensorStateException;

public class Gps implements GenericSensor, LocationListener {

	protected Context context;
	protected LocationManager locationManager;
	protected GenericSensor.status status;
	protected String provider;
	protected LooperThread looperThread;

	public Gps() {

	}

	public Gps(Context context) {
		this.context = context;
		this.locationManager = (LocationManager) this.context
				.getSystemService(Context.LOCATION_SERVICE);
		this.provider = LocationManager.GPS_PROVIDER;
		this.status = GenericSensor.status.off;
	}

	public static ArrayList<GenericSensor> getList(Context context) {

		ArrayList<GenericSensor> gps = null;
		gps = new ArrayList<GenericSensor>();

		if (((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE))
				.getProvider(LocationManager.GPS_PROVIDER) != null)
			gps.add(new Gps(context));

		return gps;
	}

	@Override
	public status getStatus() {
		return this.status;
	}

	@Override
	public synchronized void turnOn() {
		if (!this.status.equals(GenericSensor.status.on)) {
			this.looperThread = new LooperThread();
			this.looperThread.run();
			this.locationManager.requestLocationUpdates(this.provider, 0, 0,
					this, this.looperThread.getMyLooper());
			this.status = GenericSensor.status.on;
		}

	}

	public synchronized void turnOn(int accuracy) {
		turnOn();
	}

	@Override
	public synchronized void turnOff() {
		if (!this.status.equals(GenericSensor.status.off)) {
			this.status = GenericSensor.status.off;
			this.looperThread.stop();
			this.looperThread = null;
			this.locationManager.removeUpdates(this);
		}
	}

	@Override
	public String getData() throws SensorStateException {
		if (this.status.equals(GenericSensor.status.on)) {
			String data;
			Location location = null;
			location = this.locationManager.getLastKnownLocation(this.provider);
			if (location != null) {
				data = location.getLatitude()
						+ " "
						+ location.getLongitude()
						+ " "
						+ location.getAltitude()
						+ " "
						+ location.getAccuracy()
						+ " "
						+ (Calendar.getInstance().getTimeInMillis() - location
								.getTime());
			} else
				data = "0 0 0 0 -1";
			
			return data;
		}else
			throw new SensorStateException("SensorStatus "
					+ this.status.toString());
	}

	public byte[] getBytes() {
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String arg0) {
	}

	@Override
	public void onProviderEnabled(String arg0) {
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

}
