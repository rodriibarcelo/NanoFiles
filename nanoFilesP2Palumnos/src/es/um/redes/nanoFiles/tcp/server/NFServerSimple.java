package es.um.redes.nanoFiles.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class NFServerSimple {

	private static final int SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS = 1000;
	private static final String STOP_SERVER_COMMAND = "fgstop";
	private static final int PORT = 10000;
	private ServerSocket serverSocket = null;

	public NFServerSimple() throws IOException  {
		/*
		 *  Crear una direción de socket a partir del puerto especificado
		 */
		InetSocketAddress addr = new InetSocketAddress(PORT);
		/*
		 *  Crear un socket servidor y ligarlo a la dirección de socket anterior
		 */
		boolean usado = true;
		int i = 0;
		try {
		serverSocket = new ServerSocket();
		serverSocket.bind(addr);
		} catch(IOException e) {
			while(usado) {
				addr = new InetSocketAddress(PORT+i);
				try {
				serverSocket = new ServerSocket();
				serverSocket.bind(addr);
				usado = false;
				}catch(IOException f) {
					i++;
				}
				
			}
		}
		serverSocket.setSoTimeout(SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS);
	}
	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	/**
	 * Método para ejecutar el servidor de ficheros en primer plano. Sólo es capaz
	 * de atender una conexión de un cliente. Una vez se lanza, ya no es posible
	 * interactuar con la aplicación a menos que se implemente la funcionalidad de
	 * detectar el comando STOP_SERVER_COMMAND (opcional)
	 * 
	 */
	
	public void run() throws IOException {
		/*
		 *  Comprobar que el socket servidor está creado y ligado
		 */
		if(serverSocket == null || !serverSocket.isBound())
			System.err.println("Socket no creado o no ligado.");
		else {
			System.out.println("Server is listening on port " + serverSocket.getLocalPort());
			boolean stopServer = false;
			while(!stopServer) {
				try {
					
						
				/*
				 * Usar el socket servidor para esperar conexiones de otros peers que
				 * soliciten descargar ficheros
				 */
						Socket  soc = serverSocket.accept();
				/*
				 * Al establecerse la conexión con un peer, la comunicación con dicho
				 * cliente se hace en el método NFServerComm.serveFilesToClient(socket), al cual
				 * hay que pasarle el socket devuelto por accept
				 */
						NFServerComm.serveFilesToClient(soc);
						
				}catch (SocketTimeoutException e) {
					BufferedReader standardInput = new BufferedReader(new InputStreamReader(System.in));
					String input = "";
					if(standardInput.ready()) {
						input = standardInput.readLine();
						if(input.equals(STOP_SERVER_COMMAND)) {
							serverSocket.close();
							stopServer = true;
						}
					}
				}
			}
		}
		System.out.println("NFServerSimple stopped. Returning to the nanoFiles shell...");
	}
}
