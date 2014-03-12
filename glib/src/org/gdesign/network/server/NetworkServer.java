package org.gdesign.network.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gdesign.network.protocol.NetworkProtocol;

public class NetworkServer {

	public static int TIMEOUT = 600000;
	public static enum AUTHMODE {WHITELIST,PASSWORD};
	
	private List<NetworkServerListener> listener = new ArrayList<NetworkServerListener>(); 
	
	public boolean DEBUG = false;
	
	private HashMap<String , Integer> whitelist = new HashMap<>();
	private HashMap<String , String> passdb = new HashMap<>();
	
	private List<NetworkServerSocketConnection> clients = new ArrayList<NetworkServerSocketConnection>();
	private HashMap<String, String> connectionInfo = new HashMap<>();
	
	private AUTHMODE authmode = null;
	
	public NetworkServer(final int Port, AUTHMODE authmode){		
		this.authmode = authmode;
		Thread t = new Thread(new Runnable() {
			
			ServerSocket server;
			@Override
			public void run() {
					try {
						server = new ServerSocket(Port);
						if (DEBUG) System.out.println("Server started. Listening on port: "+server.getLocalPort());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					while (!server.isClosed()){
						try {
							if (DEBUG) System.out.println("Waiting for client");
							newClient(server.accept());
						} catch (IOException e) {
							
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
	
			}
		});
		t.start();
	}
	
	public AUTHMODE getAuthMode(){
		return this.authmode;
	}
	
	public boolean authWhitelist(String username){
		if (whitelist.get(username) != null && !isAlreadyConnected(username)) return true;
		else return false;
	}
	
	public boolean authPassword(String username, String password){
		if (passdb.get(username).compareTo(password) == 0 && !isAlreadyConnected(username)) return true;
		else return false;
	}
	
	public void broadcast(String text) throws IOException{
		if (DEBUG) System.out.println("Broadcasting message: "+text);
		NetworkProtocol broadcastMessage = new NetworkProtocol(NetworkProtocol.MESSAGE,"SERVERBROADCAST", text);
	     for (NetworkServerSocketConnection s : clients) {
		    	 s.sendMessage(broadcastMessage);
	     }
	}
	
	public void addNetworkServerListerner(NetworkServerListener l){
		this.listener.add(l);
	}
	
	public void addUserToWhitelist(String username,int level){
		whitelist.put(username, level);
	}

	public void addUserToPasswordDB(String username,String password){
		passdb.put(username, password);
	}
	
	public synchronized void delClient(NetworkServerSocketConnection c){
		if (DEBUG) System.out.println("Removing client from list: " +c.getSocket().getInetAddress()+":"+c.getSocket().getPort());
		for (NetworkServerListener l : listener){
			l.clientDisconnected(c);
		}
		connectionInfo.remove(c.getUsername());
		clients.remove(c);
	}
	
	private void newClient(Socket s) throws IOException, ClassNotFoundException{
		if (DEBUG) System.out.println("New client connected: "+s.getInetAddress()+":"+s.getPort());
		clients.add(new NetworkServerSocketConnection(this, s));
	}
	
	private boolean isAlreadyConnected(String username){
		for (NetworkServerSocketConnection con : clients) {
			if (con.getUsername().compareTo(username) == 0 && con.isConnected()) {
				if (DEBUG) System.out.println("New already connected. Connection refused. "+con.toString());
				con.closeConnection();
				return true; 
			}
		}
		return false;
	}
		
	public void clientAuthed(NetworkServerSocketConnection socket){
		if (DEBUG) System.out.println("Client authenticated.\n"+socket.toString());
		for (NetworkServerListener l : listener) { 
			l.clientAuthenticated(socket);
			NetworkServerConsole.updateMap(connectionInfo, socket, DEBUG);
		}
	}
	
	public void messageReceived(NetworkServerSocketConnection socket, Object protocol){
		if (DEBUG) System.out.println("Message received from " +socket.getConnectionId() +": "+ protocol.toString());
		for (NetworkServerListener l : listener){
			l.messageReceived(socket, protocol);
			NetworkServerConsole.updateMap(connectionInfo, socket, DEBUG);
		}
	}
	
	public void disconnectClient(String clientName){
		NetworkServerSocketConnection c = null;
		for (NetworkServerSocketConnection s : clients){
			if (clientName.equals(s.getUsername())) c = s;
		}
		if (DEBUG) System.out.println("Client disconnected: "+c.toString());
		c.closeConnection();
	}
	
	public List<NetworkServerSocketConnection> getConnectedClients(){
		return this.clients;
	}
	
	public String listClients(){
		String t = "";
		for (String s : connectionInfo.keySet()){
			t+=" "+s+"\n";
		}
		if (t=="") t+= "no client connected.\n";
		return t;
	}
	
	public String listWhitelist(){
		String t = "";
		for (String s : whitelist.keySet()){
			t+=" "+s+"\n";
		}
		if (t=="") t+= "whitelist is empty.\n";
		return t;
	}
	
	public void enableDebug(boolean enabled){
		this.DEBUG = enabled;
	}
	
	
	public String listClientDetails(String name){
		String t = connectionInfo.get(name)+"\n";
		return t;
	}
	
}