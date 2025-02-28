package es.um.redes.nanoFiles.tcp.server;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class NFServerThread extends Thread {
	/*
	 *  Esta clase modela los hilos que son creados desde NFServer y cada uno
	 * de los cuales simplemente se encarga de invocar a
	 * NFServerComm.serveFilesToClient con el socket retornado por el método accept
	 * (un socket distinto para "conversar" con un cliente)
	 */
	private Socket soc;
	public NFServerThread(Socket socket) {
		soc = socket;
	}
	
	
	public void run() {
		try {
			
			NFServerComm.serveFilesToClient(soc);
			
		}catch(EOFException e) {
			System.err.println("Excepcion de final de archivo");
			e.printStackTrace();
		}catch (IOException e) {
			System.err.println("Excepcion entrada/salida");
			e.printStackTrace();
		}
	}
	


}
