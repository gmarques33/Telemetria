package br.usp.icmc.lrm.telemetria.sensors;

import br.usp.icmc.lrm.telemetria.exceptions.SensorStateException;


public interface GenericSensor {
	
	public enum status {

        on, off, busy, error, unknown
        
    };
    
	public abstract status getStatus();
	
	public abstract String getData() throws SensorStateException;
	
	public abstract byte[] getBytes();
	
	public abstract void turnOn() throws SensorStateException;
	
	public abstract void turnOn(int parameter) throws SensorStateException;
	
	public abstract void turnOff();
		
}
