package org.gdesign.network.client;


import java.io.IOException;
import java.net.UnknownHostException;

import org.gdesign.network.protocol.NetworkProtocol;

public class NetworkClient extends NetworkAdapter{

	public NetworkClient(String serverIP, int Port, String username) throws UnknownHostException, IOException, InterruptedException, ClassNotFoundException {
		super(serverIP, Port, username);
	}
	
	public NetworkClient(String serverIP, int Port, String username, String password) throws UnknownHostException, IOException, InterruptedException, ClassNotFoundException {
		super(serverIP, Port, username, password);
	}
	
	public void sendStatus(int status) throws IOException{
		this.sendData(new NetworkProtocol(NetworkProtocol.STATUS,super.getClientName(),status));
	}
	
	public void sendMessage(String message) throws IOException{
		this.sendData(new NetworkProtocol(NetworkProtocol.MESSAGE,super.getClientName(),message));
	}
	
	public void sendCommand(int command) throws IOException{
		this.sendData(new NetworkProtocol(NetworkProtocol.COMMAND,super.getClientName(),command));
	}
	
	public void sendObject(Object object) throws IOException{
		this.sendData(new NetworkProtocol(NetworkProtocol.OBJECT,super.getClientName(),object));
	}
		
}
