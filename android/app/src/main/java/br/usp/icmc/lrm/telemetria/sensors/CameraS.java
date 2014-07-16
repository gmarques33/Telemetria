package br.usp.icmc.lrm.telemetria.sensors;

import java.util.ArrayList;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import br.usp.icmc.lrm.telemetria.exceptions.SensorStateException;

public class CameraS implements GenericSensor {

	private Camera mCamera;
	private PictureCallback mPicture;
	protected byte[] img = null;
	private GenericSensor.status status;
	private boolean stop;
	
	public CameraS() {
		this.status = GenericSensor.status.off;
		
		this.mPicture = new PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				img = data;
				if(!stop){
					mCamera.startPreview();
					mCamera.takePicture(null, null, mPicture);
				}
			}
		};
	}

	public static ArrayList<GenericSensor> getList(Context context) {

		ArrayList<GenericSensor> cameras = null;
		cameras = new ArrayList<GenericSensor>();

		//Android 2.2 or lower support only one camera
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA))
			cameras.add(new CameraS());

		return cameras;
	}
	
	@Override
	public status getStatus() {
		return status;
	}

	@Override
	public synchronized String getData() throws SensorStateException {
		if (this.status.equals(GenericSensor.status.on)) {
			if (this.img == null)
				return "0";
			return Integer.toString(this.img.length);
		}
		return "0";
	}

	public byte[] getBytes() {
		if (this.status.equals(GenericSensor.status.on))
			return this.img;
		return null;
	}

	@Override
	public synchronized void turnOn() throws SensorStateException {
		if (!this.status.equals(GenericSensor.status.on)) {
			try {
				this.stop = false;
				this.mCamera = Camera.open();
				this.mCamera.startPreview();
				this.mCamera.takePicture(null, null, this.mPicture);
				while(this.img == null);
				this.status = GenericSensor.status.on;
			} catch (Exception e) {
				this.status = GenericSensor.status.error;
				Log.e("Telemetria",
						"Error on turn on camera: "
								+ e.getMessage());
				throw new SensorStateException("0004");
			}
		}
	}

	@Override
	public synchronized void turnOn(int parameter) throws SensorStateException {
		turnOn();
	}

	@Override
	public synchronized void turnOff() {
		if (!this.status.equals(GenericSensor.status.off)) {
			this.status = GenericSensor.status.off;
			this.stop = true;
			try{
				this.mCamera.stopPreview();
				this.mCamera.release();
			}catch (Exception e) {
				Log.w("Telemetria", e.getMessage());
			}
			this.mCamera = null;
			this.img = null;
		}
	}
}
