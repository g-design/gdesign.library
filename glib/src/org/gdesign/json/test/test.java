package org.gdesign.json.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class test {
	
	public static final String APIKEY = "a430ea2e-7b6b-4aee-86ff-0638ed002817";//System.getenv("APIKEY");
	
	   public static String getHTML(String urlToRead, boolean proxyEnabled) {
		      URL url;
		      HttpURLConnection conn;
		      Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("proxy.zeb.de", 8080));
		      BufferedReader rd;
		      String line;
		      String result = "";
		      try {
		         url = new URL(urlToRead);
		         if (proxyEnabled) conn = (HttpURLConnection) url.openConnection(proxy);
		         else conn = (HttpURLConnection) url.openConnection();
		         rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		         while ((line = rd.readLine()) != null) {
		            result += line;
		         }
		         rd.close();
		      } catch (IOException e) {
		         e.printStackTrace();
		      } catch (Exception e) {
		         e.printStackTrace();
		      }
		      return result;
		   }
	
	public static void main(String[] args) {

		//System.out.println(getHTML("https://prod.api.pvp.net/api/lol/euw/v1.2/stats/by-summoner/20304080/summary?api_key="+APIKEY,true));
	}

}
