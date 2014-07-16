package br.usp.icmc.lrm.telemetria.sensors;

import java.util.ArrayList;

import android.content.Context;
import android.location.LocationManager;

public class NetworkLocation extends Gps{

	public NetworkLocation(Context context) {
		super();
		this.context = context;
		this.locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
		this.provider = LocationManager.NETWORK_PROVIDER;
		this.status = GenericSensor.status.off;
	}
	
	public static ArrayList<GenericSensor> getList(Context context) {

		ArrayList<GenericSensor> networkLocation = null;
		networkLocation = new ArrayList<GenericSensor>();
		
		if( ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE)).getProvider(LocationManager.NETWORK_PROVIDER) != null)
			networkLocation.add(new NetworkLocation(context));
		
		return networkLocation;
	}
}
