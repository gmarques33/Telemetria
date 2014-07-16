package br.usp.icmc.lrm.telemetria.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;
import java.util.Iterator;

import android.content.Context;
import android.util.Log;
import br.usp.icmc.lrm.telemetria.exceptions.SensorStateException;
import br.usp.icmc.lrm.telemetria.exceptions.WrongMessageReceived;
import br.usp.icmc.lrm.telemetria.exceptions.WrongOTLVersion;
import br.usp.icmc.lrm.telemetria.sensors.GenericSensor;
import br.usp.icmc.lrm.telemetria.sensors.SensorsHandler;

class OTLClientHandler extends Thread {

	private final String otlVersion = "0001";

	private Socket client;
	private DataOutputStream out;
	private DataInputStream in;
	@SuppressWarnings("unused")
	private Context context;
	private SensorsHandler sensorHandler;
	private byte[] bytes = null;

	private String lastTimestamp;
	private String lastMessage;
	private String lastInfo;

	public OTLClientHandler(Socket socket, SensorsHandler sensorHandler) {
		client = socket;
		this.sensorHandler = sensorHandler;
	}

	public void sendByte(char byteToSend) throws IOException {
		this.out.writeByte(byteToSend);
	}

	public void sendBytes(byte[] buffer) throws IOException {
		this.out.write(buffer);
	}

	public void sendString(String stringToSend) throws IOException {
		this.out.writeBytes(stringToSend);
	}

	public char receiveByte() throws IOException {
		char rec = '0';
		rec = (char) this.in.readByte();
		return rec;
	}

	// Receive the message, parse and return the received string
	public String receiveMessage() throws IOException {
		String received = "";
		char c;
		do {
			c = receiveByte();
			received += c;
		} while (c != '\r');

		parseMessage(received);
		return received;
	}

	public void sendMessage(String message) throws IOException {

		String timestamp;
		Calendar cal = null;
		cleanGarbage();
		// Add timestamp to message
		cal = Calendar.getInstance();
		timestamp = String.valueOf(cal.getTimeInMillis());

		message = timestamp + " " + message;

		sendString(message);

	}

	private void parseMessage(String message) {

		this.lastTimestamp = "";
		this.lastMessage = "";
		this.lastInfo = "";

		int i;
		// Copy timestamp from received message
		for (i = 0; i < message.length() && message.charAt(i) != ' '; i++) {
			lastTimestamp += message.charAt(i);
		}
		i++;

		// Copy received message
		while (i < message.length() && message.charAt(i) != ' ') {
			lastMessage += message.charAt(i);
			i++;
		}
		i++;

		// Copy received information
		while (i + 1 < message.length()
				&& !(message.charAt(i) == '\n' && message.charAt(i + 1) == '\r')) {
			lastInfo += message.charAt(i);
			i++;
		}
	}

	private void initConnection() throws IOException, WrongOTLVersion,
			WrongMessageReceived {
		this.out = new DataOutputStream(this.client.getOutputStream());
		this.in = new DataInputStream(this.client.getInputStream());

		// Receive and send Hello message
		receiveMessage();

		if (!this.lastMessage.equals("hlo")) {
			sendMessage("err " + "0001" + '\n' + '\r');
			throw new WrongMessageReceived("Message received: "
					+ this.lastMessage);
		} else if (!this.lastInfo.equals(getOtlversion())) {
			sendMessage("err " + "0005" + '\n' + '\r');
			throw new WrongOTLVersion("Client version: " + this.lastInfo
					+ " Required version: " + getOtlversion());
		} else
			sendMessage("hlo " + getOtlversion() + '\n' + '\r');

	}

	private void maintainConnection() throws IOException {

		do {
			receiveMessage();
			if (getLastMessage().equals("lst")) {
				treatLst();
			} else if (getLastMessage().equals("get")) {
				treatGet();
			} else if (getLastMessage().equals("ton")) {
				treatTon();
			} else if (getLastMessage().equals("off")) {
				treatOff();
			} else if (getLastMessage().equals("msg")) {
				treatMsg();
			} else if (getLastMessage().equals("gtb")) {
				treatGtb();
			} else if (getLastMessage().equals("cls")) {
				closeConnection();
			} else {
				Log.e("Telemetria",
						"Error on OTLClientHandler - WrongMessageReceived: "
								+ this.lastMessage);
				sendMessage("err 0001" + '\n' + '\r');
			}
		} while (!getLastMessage().equals("cls"));

	}

	private void closeConnection() throws IOException {
		sendMessage("bye 0" + '\n' + '\r');
		this.client.close();
	}

	private void treatLst() throws IOException {

		String message;
		String sensorType = "";
		GenericSensor sensor;
		int i = 0;

		message = "lst " + this.sensorHandler.getSensorList().size() + "\n";
		for (Iterator<GenericSensor> iterator = this.sensorHandler
				.getSensorList().iterator(); iterator.hasNext();) {
			sensor = (GenericSensor) iterator.next();

			if (sensorType.equals(sensor.getClass().getSimpleName()))
				i++;
			else {
				sensorType = sensor.getClass().getSimpleName();
				i = 0;
			}

			message += (sensorType + " " + i + '\n');
		}
		message += '\r';

		sendMessage(message);
	}

	private void treatGet() throws IOException {

		String message;
		String sensorData;
		String sensorInfo[];
		GenericSensor sensor;

		try {
			sensor = locateSensor();
			sensorInfo = getLastInfo().split(" ");
			sensorData = sensor.getData();

			message = sensorInfo[0] + sensorInfo[1] + " " + sensorData;

			this.bytes = sensor.getBytes();

		} catch (WrongMessageReceived e) {
			// Send error message
			message = "err 0001";
		} catch (SensorStateException e) {
			// Send error message
			message = "err ";
			if(e.getMessage().equals(GenericSensor.status.off.toString()))
				message += "0006";
			else if(e.getMessage().equals(GenericSensor.status.busy.toString()))
				message += "0007";
			else if(e.getMessage().equals(GenericSensor.status.error.toString()))
				message += "0008";
			else if(e.getMessage().equals(GenericSensor.status.unknown.toString()))
				message += "0007";
		}
		// Send message
		message += '\n';
		message += '\r';
		sendMessage(message);

	}

	private void treatMsg() throws IOException {
		sendMessage("msg notImplemented" + '\n' + '\r');
	}

	private void treatTon() throws IOException {
		String message;
		String sensorInfo[];
		GenericSensor sensor;

		try {
			sensor = locateSensor();
			sensorInfo = this.lastInfo.split(" ");

			sensor.turnOn();

			message = "ton " + sensorInfo[0] + sensorInfo[1];

		} catch (WrongMessageReceived e) {
			// Send error message
			message = "err 0001";
		} catch (SensorStateException e) {
			// Send error message
			message = "err ";
			if(e.getMessage().equals(GenericSensor.status.off.toString()))
				message += "0006";
			else if(e.getMessage().equals(GenericSensor.status.busy.toString()))
				message += "0007";
			else if(e.getMessage().equals(GenericSensor.status.error.toString()))
				message += "0008";
			else if(e.getMessage().equals(GenericSensor.status.unknown.toString()))
				message += "0007";
		}
		
		message += '\n';
		message += '\r';

		sendMessage(message);
	}

	private void treatOff() throws IOException {
		String message;
		String sensorInfo[];
		GenericSensor sensor;

		try {
			sensor = locateSensor();
			sensorInfo = this.lastInfo.split(" ");

			sensor.turnOff();

			message = "off " + sensorInfo[0] + sensorInfo[1];

		} catch (WrongMessageReceived e) {
			// Send error message
			message = "err 0001";
		}
		// Send message
		message += '\n';
		message += '\r';
		sendMessage(message);
	}

	private void treatGtb() throws IOException {
		if (this.bytes != null)
			sendBytes(this.bytes);
		else
			sendMessage("err 0010");
	}

	private GenericSensor locateSensor() throws WrongMessageReceived {

		// Parse info to get sensorType and id
		String sensorInfo[];
		Boolean found = false;
		int i = 0;
		GenericSensor sensor = null;

		sensorInfo = this.lastInfo.split(" ");
		if (sensorInfo.length < 2)
			throw new WrongMessageReceived("0002");

		// Locate sensor
		for (Iterator<GenericSensor> iterator = this.sensorHandler
				.getSensorList().iterator(); !found && iterator.hasNext();) {
			sensor = (GenericSensor) iterator.next();

			if (sensorInfo[0].toLowerCase().equals(
					sensor.getClass().getSimpleName().toLowerCase())) {
				if (Integer.valueOf(sensorInfo[1]).intValue() == i)
					found = true;
				i++;
			}
		}
		if (!found)
			throw new WrongMessageReceived("0003");

		return sensor;
	}

	public String getOtlversion() {
		return otlVersion;
	}

	public String getLastTimestamp() {
		return lastTimestamp;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public String getLastInfo() {
		return lastInfo;
	}

	// Clean the garbage on conection
	public void cleanGarbage() {
		int garbage;
		try {
			do {
				garbage = in.available();
				in.skip(garbage);
			} while (garbage > 0);
		} catch (Exception e) {

		}
	}

	@Override
	public void run() {
		try {
			initConnection();
			maintainConnection();
		} catch (IOException e) {
			Log.e("Telemetria",
					"Error on OTLClientHandler - IOException: "
							+ e.getMessage());
		} catch (WrongMessageReceived e) {
			Log.e("Telemetria",
					"Error on OTLClientHandler - WrongMessageReceived: "
							+ e.getMessage());
		} catch (WrongOTLVersion e) {
			Log.e("Telemetria", "Error on OTLClientHandler - WrongOTLVersion: "
					+ e.getMessage());
		}
	}
}