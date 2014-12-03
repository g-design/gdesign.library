package org.gdesign.network.client.test;

import java.io.IOException;
import java.net.UnknownHostException;

import org.gdesign.network.client.NetworkClient;
import org.gdesign.network.client.NetworkListener;
import org.gdesign.network.protocol.NetworkProtocol;

public class NetworkClientTest {
	NetworkClient client;
	
	public NetworkClientTest(String serverip, int port, String username) throws UnknownHostException, ClassNotFoundException, IOException, InterruptedException{
		client = new NetworkClient(serverip, port, username);
		client.addNetworkListener(new NetworkListener() {
			
			@Override
			public Object messageReveiced(NetworkProtocol p) {
				System.out.println(p.toString());
				return p;
			}
			
			@Override
			public void connectionClosed() {
				System.out.println("Connection closed.");
			}
		});
	}
	
	public void sendMessage(String text) throws IOException{
		if (client.isConnected()) client.sendMessage(text);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			NetworkClientTest c = new NetworkClientTest("localhost", 1337, "agaida");
			c.sendMessage("test123");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		

	}

}
