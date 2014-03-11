package org.gdesign.network.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class NetworkServerConsole {
	
	public static void updateMap(HashMap<String,String> map, NetworkServerSocketConnection c){
		String info = "";
		info += " lastPacketReceived="+ new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"\n";
		info += " threadID="+c.getId()+"\n";
		info += " threadState="+c.getState()+"\n";
		info += " connectionID="+c.getConnectionId()+"\n";
		info += " connectionIP="+c.getSocket().getInetAddress()+"\n";
		map.put(c.getUsername(), info);
		if (NetworkServer.DEBUG) printConsole(map);
	}
	
	public static void printConsole(HashMap<String,String> map){
		for (String s : map.keySet()){
			System.out.println(s);
			System.out.println(map.get(s));
		}
	}
}
