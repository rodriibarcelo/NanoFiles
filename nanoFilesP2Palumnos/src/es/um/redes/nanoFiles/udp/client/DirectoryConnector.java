package es.um.redes.nanoFiles.udp.client;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Cliente con métodos de consulta y actualización específicos del directorio
 */
public class DirectoryConnector {
	/**
	 * Puerto en el que atienden los servidores de directorio
	 */
	private static final int DIRECTORY_PORT = 6868;
	/**
	 * Tiempo máximo en milisegundos que se esperará a recibir una respuesta por el
	 * socket antes de que se deba lanzar una excepción SocketTimeoutException para
	 * recuperar el control
	 */
	private static final int TIMEOUT = 1000;
	/**
	 * Número de intentos máximos para obtener del directorio una respuesta a una
	 * solicitud enviada. Cada vez que expira el timeout sin recibir respuesta se
	 * cuenta como un intento.
	 */
	private static final int MAX_NUMBER_OF_ATTEMPTS = 5;

	/**
	 * Valor inválido de la clave de sesión, antes de ser obtenida del directorio al
	 * loguearse
	 */
	public static final int INVALID_SESSION_KEY = -1;
	/**
	 * Valor invalido de usuario servidor (no envia archivos)
	 */
	public static final boolean NO_ES_SERVIDOR = false;
	
	public static final String INVALID_NICKNAME = "";
	/**
	 * Socket UDP usado para la comunicación con el directorio
	 */
	private DatagramSocket socket;
	/**
	 * Dirección de socket del directorio (IP:puertoUDP)
	 */
	private InetSocketAddress directoryAddress;

	private int sessionKey = INVALID_SESSION_KEY;
	private String nickname = INVALID_NICKNAME;
	private boolean esServidor = NO_ES_SERVIDOR;
	private boolean successfulResponseStatus;
	private String errorDescription;

	public DirectoryConnector(String address) {
		/*
		 * Convertir el nombre de host 'address' a InetAddress y guardar la
		 * dirección de socket (address:DIRECTORY_PORT) del directorio en el atributo
		 * directoryAddress, para poder enviar datagramas a dicho destino.
		 */
		try {
			directoryAddress = new InetSocketAddress(InetAddress.getByName(address), DIRECTORY_PORT);
		} catch (UnknownHostException e) {
			System.err.println("Error, host desconocido");
			System.exit(1);
		}
		/*
		 * Crea el socket UDP en cualquier puerto para enviar datagramas al
		 * directorio
		 */
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			System.err.println("Error al crear el socket " + e);
		}

	}

	/**
	 * Método para enviar y recibir datagramas al/del directorio
	 * 
	 * @param requestData los datos a enviar al directorio (mensaje de solicitud)
	 * @return los datos recibidos del directorio (mensaje de respuesta)
	 * @throws IOException 
	 */
	private byte[] sendAndReceiveDatagrams(byte[] requestData){
		byte responseData[] = new byte[DirMessage.PACKET_MAX_SIZE];
		byte response[] = null;
		try {
			socket.setSoTimeout(TIMEOUT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		if (directoryAddress == null) {
			System.err.println("DirectoryConnector.sendAndReceiveDatagrams: UDP server destination address is null!");
			System.err.println(
					"DirectoryConnector.sendAndReceiveDatagrams: make sure constructor initializes field \"directoryAddress\"");
			System.exit(-1);

		}
		if (socket == null) {
			System.err.println("DirectoryConnector.sendAndReceiveDatagrams: UDP socket is null!");
			System.err.println(
					"DirectoryConnector.sendAndReceiveDatagrams: make sure constructor initializes field \"socket\"");
			System.exit(-1);
		}
		/*
		 * TODO: Enviar datos en un datagrama al directorio y recibir una respuesta. El
		 * array devuelto debe contener únicamente los datos recibidos, *NO* el búfer de
		 * recepción al completo.
		 */
		DatagramPacket paqueteSend = new DatagramPacket(requestData, requestData.length, directoryAddress);
		try {
			socket.send(paqueteSend);
		} catch (IOException e) {
			System.out.println("Error en la entrada/salida, saliendo...");
			System.exit(1);
		}
		
		DatagramPacket paqueteReceive = new DatagramPacket(responseData, responseData.length);
		int i=0;
		boolean recibido = false;
		while(i<MAX_NUMBER_OF_ATTEMPTS && !recibido) {
			try {
				socket.receive(paqueteReceive);
				recibido = true;
			}catch(SocketTimeoutException e) {
					i++;
			} catch (IOException e) {
				System.err.println("Error en la entrada/salida, saliendo...");
				System.exit(1);
			}
		}
		if(!recibido) {
			System.err.println("Se ha llegado al numero maximo de intentos");
			System.exit(1);
		}
		String res = new String(responseData, 0, paqueteReceive.getLength());
		response = res.getBytes();
		
		/*
		 * Una vez el envío y recepción asumiendo un canal confiable (sin
		 * pérdidas) esté terminado y probado, debe implementarse un mecanismo de
		 * retransmisión usando temporizador, en caso de que no se reciba respuesta en
		 * el plazo de TIMEOUT. En caso de salte el timeout, se debe reintentar como
		 * máximo en MAX_NUMBER_OF_ATTEMPTS ocasiones.
		 */
		/*
		 * Las excepciones que puedan lanzarse al leer/escribir en el socket deben
		 * ser capturadas y tratadas en este método. Si se produce una excepción de
		 * entrada/salida (error del que no es posible recuperarse), se debe informar y
		 * terminar el programa.
		 */
		/*
		 * NOTA: Las excepciones deben tratarse de la más concreta a la más genérica.
		 * SocketTimeoutException es más concreta que IOException.
		 */



		if (response != null && response.length == responseData.length) {
			System.err.println("Your response is as large as the datagram reception buffer!!\n"
					+ "You must extract from the buffer only the bytes that belong to the datagram!");
		}
		return response;
	}

	/**
	 * Método para probar la comunicación con el directorio mediante el envío y
	 * recepción de mensajes sin formatear ("en crudo")
	 * 
	 * @return verdadero si se ha enviado un datagrama y recibido una respuesta
	 * @throws IOException 
	 */
	public boolean testSendAndReceive(){
		/*
		 *  Probar el correcto funcionamiento de sendAndReceiveDatagrams. Se debe
		 * enviar un datagrama con la cadena "login" y comprobar que la respuesta
		 * recibida es "loginok". En tal caso, devuelve verdadero, falso si la respuesta
		 * no contiene los datos esperados.
		 */
		boolean success = false;
		String log = "login";
		byte logB[] = log.getBytes();
		byte res[] = sendAndReceiveDatagrams(logB);
		String respuesta = new String(res);
		if(respuesta.equals("loginok"))
			success = true;
		return success;
	}

	public InetSocketAddress getDirectoryAddress() {
		return directoryAddress;
	}

	public int getSessionKey() {
		return sessionKey;
	}

	/**
	 * Método para "iniciar sesión" en el directorio, comprobar que está operativo y
	 * obtener la clave de sesión asociada a este usuario.
	 * 
	 * @param nickname El nickname del usuario a registrar
	 * @return La clave de sesión asignada al usuario que acaba de loguearse, o -1
	 *         en caso de error
	 */
	public boolean logIntoDirectory(String nickname) {
		assert (sessionKey == INVALID_SESSION_KEY);
		boolean success = false;
		this.nickname = nickname;
		// 1.Crear el mensaje a enviar (objeto DirMessage) con atributos adecuados
		// (operation, etc.) NOTA: Usar como operaciones las constantes definidas en la clase
		// DirMessageOps
		//  2.Convertir el objeto DirMessage a enviar a un string (método toString)
		//  3.Crear un datagrama con los bytes en que se codifica la cadena
		//  4.Enviar datagrama y recibir una respuesta (sendAndReceiveDatagrams).
		//  5.Convertir respuesta recibida en un objeto DirMessage (método
		// DirMessage.fromString)
		//  6.Extraer datos del objeto DirMessage y procesarlos (p.ej., sessionKey)
		//  7.Devolver éxito/fracaso de la operación
		DirMessage mensaje = new DirMessage(DirMessageOps.OPERATION_LOGIN, nickname);
		String login = mensaje.toString();
		byte[] loginBytes = login.getBytes();
		byte[] res = sendAndReceiveDatagrams(loginBytes);
		String respuestaCadena = new String(res);
		DirMessage respuesta = DirMessage.fromString(respuestaCadena);
		if(respuesta.getOperation().equals(DirMessageOps.OPERATION_LOGIN_OK)) {
			sessionKey = respuesta.getSessionKey();
			if(sessionKey != -1) {
				System.out.println("Login exitoso, clave de sesion: "+ sessionKey);
				success = true;
			}else {
				System.err.println("Error al hacer login, nombre de usuario en uso.");
			}
		}
		return success;
	}

	/**
	 * Método para obtener la lista de "nicknames" registrados en el directorio.
	 * Opcionalmente, la respuesta puede indicar para cada nickname si dicho peer
	 * está sirviendo ficheros en este instante.
	 * 
	 * @return La lista de nombres de usuario registrados, o null si el directorio
	 *         no pudo satisfacer nuestra solicitud
	 */
	public String[] getUserList() {
		String[] userlist = null;
		//  Ver TODOs en logIntoDirectory y seguir esquema similar
		DirMessage getList = new DirMessage(DirMessageOps.OPERATION_GET_USER_LIST, sessionKey);
		String mensaje = getList.toString();
		byte[] res = sendAndReceiveDatagrams(mensaje.getBytes());
		String respuesta = new String(res);
		DirMessage listaUsers = DirMessage.fromString(respuesta);
		userlist = listaUsers.getRegisteredUsers();
		return userlist;
	}

	/**
	 * Método para "cerrar sesión" en el directorio
	 * 
	 * @return Verdadero si el directorio eliminó a este usuario exitosamente
	 */
	public boolean logoutFromDirectory() {
		//  Ver TODOs en logIntoDirectory y seguir esquema similar
		DirMessage logout = new DirMessage(DirMessageOps.OPERATION_LOGOUT, sessionKey);
		String mensaje = logout.toString();
		byte [] menBytes =  mensaje.getBytes();
		byte[] res = sendAndReceiveDatagrams(menBytes);
		String respuesta = new String(res);
		DirMessage logoutOk = DirMessage.fromString(respuesta);
		return (logoutOk.getLogoutOk());
	}

	/**
	 * Método para dar de alta como servidor de ficheros en el puerto indicado a
	 * este peer.
	 * 
	 * @param serverPort El puerto TCP en el que este peer sirve ficheros a otros
	 * @return Verdadero si el directorio acepta que este peer se convierta en
	 *         servidor.
	 */
	public boolean registerServerPort(int serverPort) {
		//  Ver TODOs en logIntoDirectory y seguir esquema similar
		boolean success = false;
		DirMessage regPort = new DirMessage(DirMessageOps.OPERATION_REG_SERVER_PORT, sessionKey, serverPort);
		String msg = regPort.toString();
		byte[] enviado = msg.getBytes();
		byte[] recibido = sendAndReceiveDatagrams(enviado);
		String respuesta = new String(recibido);
		DirMessage res = DirMessage.fromString(respuesta);
		success = res.getPortOk();
		return success;
	}

	/**
	 * Método para obtener del directorio la dirección de socket (IP:puerto)
	 * asociada a un determinado nickname.
	 * 
	 * @param nick El nickname del servidor de ficheros por el que se pregunta
	 * @return La dirección de socket del servidor en caso de que haya algún
	 *         servidor dado de alta en el directorio con ese nick, o null en caso
	 *         contrario.
	 */
	public InetSocketAddress lookupServerAddrByUsername(String nick) {
		InetSocketAddress serverAddr = null;
		// Ver TODOs en logIntoDirectory y seguir esquema similar
		DirMessage enviado = new DirMessage(DirMessageOps.OPERATION_SEND_LOOKUP_ADDR, nick, sessionKey);
		byte[] env  =  enviado.toString().getBytes();
		byte[] res = sendAndReceiveDatagrams(env);
		String respuesta = new String(res);
		DirMessage ipP = DirMessage.fromString(respuesta);
		if(!ipP.getIpPuerto().equals("")) {
			String[] sep = ipP.getIpPuerto().split("-");
			serverAddr = new InetSocketAddress(sep[0],Integer.parseInt(sep[1]));
		}
		return serverAddr;
	}

	/**
	 * Método para publicar ficheros que este peer servidor de ficheros están
	 * compartiendo.
	 * 
	 * @param files La lista de ficheros que este peer está sirviendo.
	 * @return Verdadero si el directorio tiene registrado a este peer como servidor
	 *         y acepta la lista de ficheros, falso en caso contrario.
	 */
	public boolean publishLocalFiles(FileInfo[] files) {
		boolean success = false;

		//  Ver TODOs en logIntoDirectory y seguir esquema similar
		String[] nombreF = new String[files.length];
		String[] hashF = new String[files.length];
		String[] tamF =  new String[files.length];
		int i =0;
		for(FileInfo f : files) {
			nombreF[i] = f.fileName;
			hashF[i] = f.fileHash;
			tamF[i] = Long.toString(f.fileSize);
			i++;
		}
		DirMessage msg = new DirMessage(DirMessageOps.OPERATION_PUBLISH, sessionKey, nombreF, hashF, tamF);
		byte[] enviado = msg.toString().getBytes();
		byte[] recibido = sendAndReceiveDatagrams(enviado);
		String res = new String(recibido);
		DirMessage respuesta = DirMessage.fromString(res);
		if(respuesta.getOperation().equals(DirMessageOps.OPERATION_PUBLISH_OK)) {
			success = true;
			System.out.println("Ficheros publicados correctamente");
		}
		return success;
	}

	/**
	 * Método para obtener la lista de ficheros que los peers servidores han
	 * publicado al directorio. Para cada fichero se debe obtener un objeto FileInfo
	 * con nombre, tamaño y hash. Opcionalmente, puede incluirse para cada fichero,
	 * su lista de peers servidores que lo están compartiendo.
	 * 
	 * @return Los ficheros publicados al directorio, o null si el directorio no
	 *         pudo satisfacer nuestra solicitud
	 */
	public FileInfo[] getFileList() {
		FileInfo[] filelist = null;
		//  Ver TODOs en logIntoDirectory y seguir esquema similar
		DirMessage enviado = new DirMessage(DirMessageOps.OPERATION_FILELIST, sessionKey);
		byte[] env = enviado.toString().getBytes();
		byte[] rec = sendAndReceiveDatagrams(env);
		String reci = new String(rec);
		DirMessage recibido = DirMessage.fromString(reci);
		String[] nombreF = recibido.getFilename();
		String[] hashF =recibido.getFilehash();
		String[] tamF =  recibido.getFilesize();
		filelist = new FileInfo[recibido.getFilehash().length];
		for(int i=0;i<recibido.getFilename().length;i++) {
			filelist[i] = new FileInfo(hashF[i], nombreF[i], Long.parseLong(tamF[i]), "");
		}
		return filelist;
	}

	/**
	 * Método para obtener la lista de nicknames de los peers servidores que tienen
	 * un fichero identificado por su hash. Opcionalmente, puede aceptar también
	 * buscar por una subcadena del hash, en vez de por el hash completo.
	 * 
	 * @return La lista de nicknames de los servidores que han publicado al
	 *         directorio el fichero indicado. Si no hay ningún servidor, devuelve
	 *         una lista vacía.
	 */
	public String[] getServerNicknamesSharingThisFile(String fileHash) {
		String[] nicklist = null;
		//  Ver TODOs en logIntoDirectory y seguir esquema similar



		return nicklist;
	}
	public boolean unregisterFileServer() {
		boolean success = false;
		DirMessage msg = new DirMessage(DirMessageOps.OPERATION_UNREGISTER_SERVER, sessionKey);
		byte[] enviado = msg.toString().getBytes();
		byte[] rec = sendAndReceiveDatagrams(enviado);
		String recibido = new String(rec);
		DirMessage respuesta = DirMessage.fromString(recibido);
		success = respuesta.getOperation().equals(DirMessageOps.OPERATION_UNREGISTER_SERVER_OK);
		return success;
	}



}
