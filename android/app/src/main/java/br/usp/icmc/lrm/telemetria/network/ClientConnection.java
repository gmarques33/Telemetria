package br.usp.icmc.lrm.telemetria.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ClientConnection {

	private String address;
	private Socket socket;
	DataOutputStream out;
	DataInputStream in;
/*
 * A string address é composta por ip:porta
 */
	public void connect(String address) throws Exception {
		setAddress(address);
		connect();
	}

	public void connect() throws Exception {
		InetAddress internetAddress;
		String server;
		int port;

		// Parse string
		String[] values = this.address.split("[:]");
		server = values[0];
		port = Integer.parseInt(values[1]);

		// Open Socket, Writer and Reader
		internetAddress = InetAddress.getByName(server);
		this.socket = new Socket(internetAddress, port);
		this.out = new DataOutputStream(socket.getOutputStream());
		this.in = new DataInputStream(socket.getInputStream());
	}
	
	public void sendByte(char byteToSend) throws IOException {
		if (isConnected())
			this.out.writeByte(byteToSend);
	}
	
	public void sendString(String stringToSend) throws IOException{
		if (isConnected())
			this.out.writeBytes(stringToSend);
	}

	public char receiveByte() throws IOException {
		char rec = '0';
		if (isConnected())
			rec = (char) in.readByte();
		return rec;
	}
	
	public String receiveString() throws IOException{
		String received = "";
		char c;
		if(isConnected())
			do {
				c = receiveByte();
				received += c;
			} while (c != '\r');
		return received;
	}

	// Limpa o lixo que tiver na conexao

	public void cleanGarbage() {
		int garbage;
		try {
			do {
				garbage = this.in.available();
				this.in.skip(garbage);
			} while (garbage > 0);
		} catch (Exception e) {

		}
	}
	
	public DataInputStream getIn(){
		return in;
	}

	public Integer available() {
		Integer avaliable = null;
		try {
			avaliable = this.in.available();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return avaliable;
	}

	public Boolean isConnected() {
		if (socket == null)
			return false;
		else
			return this.socket.isConnected();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}