package org.gdesign.network.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.UUID;

import org.gdesign.network.protocol.NetworkProtocol;

public class NetworkServerSocketConnection extends Thread implements Runnable {

	private NetworkServer server 	= null;
	private Socket socket			= null;
	private String username 		= null;
	private String password			= null;
	private UUID   connectionId 	= null;
	private ObjectInputStream in	= null;
	private ObjectOutputStream out	= null;
	private int status				= -1;
	private boolean isConnected  	= false;
	
	public NetworkServerSocketConnection(NetworkServer server, Socket socket) throws IOException, ClassNotFoundException{
		this.server = server;
		this.socket = socket;
		this.in = new ObjectInputStream(socket.getInputStream());
		this.out = new ObjectOutputStream(socket.getOutputStream());
		this.start();
	}
	
	
	private boolean authWithServer(NetworkServer.AUTHMODE mode) throws ClassNotFoundException, IOException{
		NetworkProtocol p = (NetworkProtocol) in.readObject();
		switch (mode) {
		case WHITELIST:
			if (p.getType() == NetworkProtocol.COMMAND && p.getCommand() == NetworkProtocol.CMD_AUTH_WL){
				this.username = p.getUsername();
				this.connectionId = UUID.fromString(p.getMessage());
				this.status = p.getStatus();
				if (server.authWhitelist(this.username)){
					out.writeObject(new NetworkProtocol(NetworkProtocol.MESSAGE, username, NetworkProtocol.AUTHENTICATION_SUCCESSFUL));
					out.flush();
					isConnected = true;
				} else {
					NetworkProtocol err = new NetworkProtocol(NetworkProtocol.MESSAGE, username, NetworkProtocol.AUTHENTICATION_ERROR);
					err.setObject("Authentication via whitelist failed.");
					out.writeObject(err);
					out.flush();				
				}
				
			}
			break;
		case PASSWORD:
			if (p.getType() == NetworkProtocol.COMMAND && p.getCommand() == NetworkProtocol.CMD_AUTH_PASS){
				this.username = p.getUsername();
				this.password = p.getObject().toString();
				this.connectionId = UUID.fromString(p.getMessage());
				this.status = p.getStatus();
				if (server.authPassword(this.username, this.password)) {
					out.writeObject(new NetworkProtocol(NetworkProtocol.MESSAGE, username, NetworkProtocol.AUTHENTICATION_SUCCESSFUL));
					out.flush();
					isConnected = true;
				} else {
					NetworkProtocol err = new NetworkProtocol(NetworkProtocol.MESSAGE, username, NetworkProtocol.AUTHENTICATION_ERROR);
					err.setObject("Authentication via password failed.");
					out.writeObject(err);
					out.flush();				
				}
			} else {
				NetworkProtocol err = new NetworkProtocol(NetworkProtocol.MESSAGE, username, NetworkProtocol.AUTHENTICATION_ERROR);
				err.setObject("Server authentification mode is set to: "+server.getAuthMode());
				out.writeObject(err);
				out.flush();
			}
			break;		
		default:
			NetworkProtocol err = new NetworkProtocol(NetworkProtocol.MESSAGE, username, NetworkProtocol.AUTHENTICATION_ERROR);
			err.setObject("Authentication mode unknown.");
			out.writeObject(err);
			out.flush();
			break;
		}
		if (isConnected) this.socket.setSoTimeout(NetworkProtocol.TIMEOUT);
		else if (server.DEBUG) System.out.println("Client authentication failed.");
		return isConnected;
	}
	
	@Override
	public void run() {
		super.run();
		try {
			if (authWithServer(server.getAuthMode())) {
				server.clientAuthed(this);
				while (isConnected) {
					NetworkProtocol protocol = (NetworkProtocol) this.in.readObject();
					if (protocol != null){
						switch (protocol.getType()) {
						case NetworkProtocol.STATUS:
							this.status = protocol.getStatus();
							break;
						default:
							break;
						}
						server.messageReceived(this, protocol);
					}
				}				
			} else closeConnection();
		} catch (ClassNotFoundException | IOException e) {
			if (e instanceof SocketTimeoutException || e instanceof SocketException) {
				closeConnection();
			}
			else {
				System.err.println("Protocol error. "+e);
				closeConnection();
			}
		}
	}
	
	public void closeConnection(){
		try {
			this.isConnected = false;
			this.in.close();
			this.out.close();
			this.socket.close();
			this.server.delClient(this);
			this.interrupt();
		} catch (IOException e) {
			System.err.println(e.toString());
		}
	}
	
	public boolean isConnected(){
		return this.isConnected;
	}

	public String getUsername() {
		return username;
	}
	
	public UUID getConnectionId() {
		return connectionId;
	}
	
	public Socket getSocket() {
		return socket;
	}

	public void setConnectionId(UUID connectionId) {
		this.connectionId = connectionId;
	}
	
	public void sendMessage(NetworkProtocol protocol) throws IOException{
		out.writeObject(protocol);
		out.flush();
	}
	
	public String toString(){
		String returnString = "";
		returnString += "--------------------------------------"+"\n";
		returnString += "Server:" + server.toString()+"\n";
		returnString += "Socket: [ip="  + socket.getInetAddress() +",localport="+socket.getLocalPort()+",remotePort="+socket.getPort()+"]\n";
		returnString += "Username:"  + username.toString()+"\n";
		returnString += "Status:"  + status+"\n";
		returnString += "ConnectionID:"  + (connectionId != null ? connectionId.toString() : "x")+"\n";
		returnString += "---------------------------------------"+"\n";
		return returnString;
	}
	
}
