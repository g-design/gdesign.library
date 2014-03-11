package org.gdesign.network.server.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.gdesign.network.protocol.NetworkProtocol;
import org.gdesign.network.server.NetworkServer;
import org.gdesign.network.server.NetworkServerListener;
import org.gdesign.network.server.NetworkServerSocketConnection;

public class NetworkServerTest {
	
	private NetworkServer flServer;
	private HashMap<NetworkServerSocketConnection, Integer> clientStates = new HashMap<>();
	
	public NetworkServerTest(int port) throws FileNotFoundException, IOException{
		flServer = new NetworkServer(port, NetworkServer.AUTHMODE.WHITELIST);
		flServer.addUserToWhitelist("testuser", 0);	
		flServer.addNetworkServerListerner(new NetworkServerListener() {
		
			@Override
			public void messageReceived(NetworkServerSocketConnection s, Object p) {
				if (p instanceof NetworkProtocol) {
					NetworkProtocol n = (NetworkProtocol) p;
					switch (n.getType()){
						case NetworkProtocol.COMMAND :
							break;
						case NetworkProtocol.STATUS :
							clientStates.put(s, n.getStatus());
							break;
						case NetworkProtocol.MESSAGE :
							break;
						case NetworkProtocol.OBJECT :
							break;
					}
				}
			}
			
			@Override
			public void clientAuthenticated(NetworkServerSocketConnection s) {
				broadcastStatus(s,0);
				clientStates.put(s, 0);
			}

			@Override
			public void clientDisconnected(NetworkServerSocketConnection s) {
				broadcastStatus(s,0);
				clientStates.remove(s);
			}
		});	
		
	}
	
	private void broadcastStatus(NetworkServerSocketConnection c, int status ){
		try {
			for (NetworkServerSocketConnection con : clientStates.keySet()){
				if (!con.equals(c)){
					con.sendMessage(new NetworkProtocol(NetworkProtocol.STATUS, c.getUsername(), status));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getUserState(String uname){
		for (NetworkServerSocketConnection c : clientStates.keySet()){
			if (c.getUsername().equals(uname)) return clientStates.get(c);
		}
		return 0;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new NetworkServerTest(1337);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
