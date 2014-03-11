package org.gdesign.network.client;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gdesign.network.protocol.NetworkProtocol;

public abstract class NetworkAdapter {
	
	private List<NetworkListener> 			listeners 	= new ArrayList<NetworkListener>();
	
	private Socket 							socket;
	private ObjectOutputStream 				oos;
	private ObjectInputStream 				ois;
	private boolean 						isAuthed	=	false;
	private String							clientName	=	null;
	private UUID							uniqueID	=   UUID.randomUUID();
	
	public NetworkAdapter(String ServerIP,int Port, String username) throws UnknownHostException, IOException, ClassNotFoundException{
		this.socket = new Socket(ServerIP, Port);
		if (socket != null) {
			this.oos = new ObjectOutputStream(this.socket.getOutputStream());
			this.ois = new ObjectInputStream(this.socket.getInputStream());
			NetworkProtocol pAuth = new NetworkProtocol(NetworkProtocol.COMMAND, username, NetworkProtocol.CMD_AUTH_WL);
			pAuth.setMessage(uniqueID.toString());
			pAuth.setStatus(0);
			if (authWithServer(pAuth)) receiveDataLoop();
			else clean();
		}
	}
	
	public NetworkAdapter(String ServerIP,int Port, String username, String password) throws UnknownHostException, IOException, ClassNotFoundException{
		this.socket = new Socket(ServerIP, Port);
		if (socket != null) {
			this.oos = new ObjectOutputStream(this.socket.getOutputStream());
			this.ois = new ObjectInputStream(this.socket.getInputStream());
			NetworkProtocol pAuth = new NetworkProtocol(NetworkProtocol.COMMAND, username, NetworkProtocol.CMD_AUTH_PASS);
			pAuth.setMessage(uniqueID.toString());
			pAuth.setObject(password);
			pAuth.setStatus(0);
			if (authWithServer(pAuth)) receiveDataLoop();
			else clean();
		}
	}
	
	public void addNetworkListener(NetworkListener nl){
		listeners.add(nl);
	}
	
	public UUID getUUID(){
		return this.uniqueID;
	}

	public Socket getSocket() {
		return this.socket;
	}
	

	public String getClientName() {
		return clientName;
	}

	private void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	public boolean isConnected(){
		if (isAuthed() && this.socket.isConnected()) return true;
		else return false;
	}
	
	public void sendData(Object protocol) throws IOException{
		if (this.oos != null){
			if (!socket.isOutputShutdown()){
				this.oos.writeObject(protocol);
				this.oos.flush();					
			}	
		}
	}
	
	public void receiveDataLoop(){
		Thread t = new Thread(new Runnable() {	
			@Override
			public void run() {
				while (true) {
					if (!socket.isInputShutdown()){
						try {
							NetworkProtocol p = (NetworkProtocol) ois.readObject();
							if (p != null) {
						       for (NetworkListener nl : listeners)
						            nl.messageReveiced(p);
							}
						} catch (ClassNotFoundException | IOException e) {
							for (NetworkListener nl : listeners)
					            nl.connectionClosed();
							break;
						}
					}
				}
			}
		});
		t.start();
	}
	
	private boolean authWithServer(NetworkProtocol auth) throws ClassNotFoundException, IOException{
		sendData(auth);
		if (this.ois != null){
			NetworkProtocol p = (NetworkProtocol) this.ois.readObject();
			if (p != null) {
				if (p.getType() == NetworkProtocol.MESSAGE && p.getMessage().compareTo(NetworkProtocol.AUTHENTICATION_SUCCESSFUL) == 0) {
					setClientName(p.getUsername());
					System.out.println("Connected to server. UUID:"+this.uniqueID);
					return this.isAuthed=true;
				} else if (p.getType() == NetworkProtocol.MESSAGE && p.getMessage().compareTo(NetworkProtocol.AUTHENTICATION_ERROR) == 0) {
					System.out.println(p.getObject().toString());
				}
			}
		} 
		throw new IOException("Permission denied. Connection refused.");
	}
	
	public boolean isAuthed(){
		return this.isAuthed;
	}
	
	public void clean() throws IOException{
		this.oos.close();
		this.ois.close();
		this.socket.close();
	}
	
}
