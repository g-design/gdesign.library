package org.gdesign.network.server;

public interface NetworkServerListener {
	public void clientAuthenticated(NetworkServerSocketConnection s);
	public void messageReceived(NetworkServerSocketConnection s, Object p);
	public void clientDisconnected(NetworkServerSocketConnection s);
}
