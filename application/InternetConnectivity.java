package application;

import java.net.*;

public abstract class InternetConnectivity {

	public static void main(String[] args){
		
		checkInternetConnectivity();
		
	}
	
	public static boolean checkInternetConnectivity(){
		
		Socket socket = new Socket();
		InetSocketAddress address = new InetSocketAddress("www.google.ca", 80); 
		
		try{
			socket.connect(address, 5000);
			return true;
		}
		catch(Exception e){
			return false;
		}
		finally{
			try{
				socket.close();
			}
			catch(Exception e){
				
			}
		}
	}
}

