package br.usp.icmc.lrm.telemetria.exceptions;

public class SensorStateException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SensorStateException() {
		super("SensorStateException");
	}
	
	public SensorStateException(String message){
		super(message);

	}
}
