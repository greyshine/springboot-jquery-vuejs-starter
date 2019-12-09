package de.greyshine.vuespringexample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Spielwiese {

	public static void main(String[] args) throws IOException {
		
		ServerSocket ss = new ServerSocket(8080);
		System.out.println("started...");
		Socket s = ss.accept();
		
		System.out.println( s );
		
		s.close();
		ss.close();
		
	}
	
}
