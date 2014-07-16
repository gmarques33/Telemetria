package br.usp.icmc.lrm.telemetria.exceptions;

public class WrongOTLVersion extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	public WrongOTLVersion() {
		super("Wrong OTL Version");
	}
	
	public WrongOTLVersion(String message) {
		super("Wrong OTL Version: " + message);
	}

}
