package org.gdesign.network.client;

import org.gdesign.network.protocol.NetworkProtocol;

public interface NetworkListener {

	public Object messageReveiced(NetworkProtocol p);
	public void connectionClosed();
	
}
