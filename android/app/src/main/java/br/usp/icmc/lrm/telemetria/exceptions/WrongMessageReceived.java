package br.usp.icmc.lrm.telemetria.exceptions;

public class WrongMessageReceived extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public WrongMessageReceived() {
		super("Wrong message received");
	}
	
	public WrongMessageReceived(String message){
		super(message);

	}

}
