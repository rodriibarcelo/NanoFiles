package es.um.redes.nanoFiles.udp.message;

import java.util.ArrayList;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Clase que modela los mensajes del protocolo de comunicación entre pares para
 * implementar el explorador de ficheros remoto (servidor de ficheros). Estos
 * mensajes son intercambiados entre las clases DirectoryServer y
 * DirectoryConnector, y se codifican como texto en formato "campo:valor".
 * 
 * @author rtitos
 *
 */
public class DirMessage {
	public static final int PACKET_MAX_SIZE = 65507; // 65535 - 8 (UDP header) - 20 (IP header)

	private static final char DELIMITER = ':'; // Define el delimitador
	private static final char DELIM_COMAS = ','; // Delimitador para RegisteredUsers
	private static final char END_LINE = '\n'; // Define el carácter de fin de línea

	/**
	 * Nombre del campo que define el tipo de mensaje (primera línea)
	 */
	private static final String FIELDNAME_OPERATION = "operation";
	/*
	 * TODO: Definir de manera simbólica los nombres de todos los campos que pueden
	 * aparecer en los mensajes de este protocolo (formato campo:valor)
	 */
	private static final String NICKNAME = "nickname";
	private static final String SESSION_KEY = "sessionkey";
	private static final String REGISTERED_USERS = "registeredusers";
	private static final String LOGOUTOK = "logoutok";
	private static final String PORT = "port";
	private static final String PORT_OK = "portok";
	private static final String LOOKUPADDR = "lookupaddr";
	private static final String FILENAME = "filename";
	private static final String FILEHASH = "filehash";
	private static final String FILESIZE = "filesize";
	private static final String PUBOK = "pubok";
	private static final String SERVOK = "servok";
	
	//private static final String LOGOUT = "logout";
	


	/**
	 * Tipo del mensaje, de entre los tipos definidos en PeerMessageOps.
	 */
	private String operation = DirMessageOps.OPERATION_INVALID;
	/*
	 *  Crear un atributo correspondiente a cada uno de los campos de los
	 * diferentes mensajes de este protocolo.
	 */
	private String nickname;
	private int sessionKey;
	private String[] registeredUsers;
	private boolean logoutOk;
	private int port;
	private boolean portOk;
	private String ipPuerto;
	private String[] filename;
	private String[] filehash;
	private String[] filesize;
	private boolean pubOk;
	private boolean servOk;

	public DirMessage(String op) {
		operation = op;
	}

	/**
	 * Constructor del mensaje para iniciar el login
	 */
	public DirMessage(String login, String nick) {
		operation = login;
		nickname = nick;
		ipPuerto = nick;
	}
	public DirMessage(String op, String nick, int sKey) {
		operation = op;
		nickname = nick;
		sessionKey = sKey;
	}
	/**
	 * Constructor del mensaje para recibir el login o enviar 
	 * la peticion para la lista de usuarios
	 */
	public DirMessage(String op, int sKey) {
		operation = op;
		sessionKey = sKey;
	}
	/**
	 * Constructor para recibir la lista de usuarios registrados
	 * @param registeredusers
	 * @param users
	 * @param esServer
	 */
	public DirMessage(String registeredusers, String[] users) {
		operation = registeredusers;
		registeredUsers = users;
		filename = users;
		
	}
	public DirMessage(String op, int sKey,  String[] fname) {
		operation = op;
		filename = fname;
		sessionKey = sKey;
		
	}
	
	
	/**
	 * Constructor para hacer el logout, que contiene dos campos, 
	 * nombre de la operacion y si el logout ha sido exitoso o no
	 * @param op
	 * @param outOk
	 */
	public DirMessage(String op, boolean outOk) {
		operation = op;
		logoutOk = outOk;
		portOk = outOk;
		pubOk = outOk;
		servOk = outOk;
	}
	
	public DirMessage(String op, int sKey, int puerto) {
		operation = op;
		sessionKey = sKey;
		port = puerto;
	}
	public DirMessage(String op, int sKey, String[] fname, String[] fhash) {
		sessionKey = sKey;
		operation = op;
		filename = fname;
		filehash = fhash;
	}
	public DirMessage(String op, String[] fname, String[] fhash) {
		operation = op;
		filename = fname;
		filehash = fhash;
	}
	public DirMessage(String op,  String[] fname, String[] fhash, String[] fsize) {
		operation = op;
		filename = fname;
		filehash = fhash;
		filesize = fsize;
	}
	public DirMessage(String op, int sKey,  String[] fname, String[] fhash, String[] fsize) {
		operation = op;
		filename = fname;
		filehash = fhash;
		filesize = fsize;
		sessionKey = sKey;
	}
	/*
	 *  Crear diferentes constructores adecuados para construir mensajes de
	 * diferentes tipos con sus correspondientes argumentos (campos del mensaje)
	 */

	public String getOperation() {
		return operation;
	}

	public void setNickname(String nick) {
		nickname = nick;
	}

	public String getNickname() {
		return nickname;
	}
	public int getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(int sesKey) {
		sessionKey = sesKey;
	}
	public String[] getRegisteredUsers() {
		return registeredUsers;
	}
	public void setRegisteredUsers(String[] users) {
		registeredUsers = users;
	}
	public boolean getLogoutOk() {
		return logoutOk;
	}
	public void setLogoutOk(boolean logOutOk) {
		logoutOk = logOutOk;
	}
	public void setPort(int puerto) {
		port = puerto;
	}
	public int getPort() {
		return port;
	}
	public void setPortOk(boolean pOk) {
		portOk = pOk;
	}
	public boolean getPortOk() {
		return portOk;
	}
	public String getIpPuerto() {
		return ipPuerto;
	}
	public void setIpPuerto(String ipPort) {
		ipPuerto = ipPort;
	}
	
	public String[] getFilename() {
		return filename;
	}

	public void setFilename(String[] filename) {
		this.filename = filename;
	}

	public String[] getFilehash() {
		return filehash;
	}

	public void setFilehash(String[] filehash) {
		this.filehash = filehash;
	}

	public String[] getFilesize() {
		return filesize;
	}

	public void setFilesize(String[] filesize) {
		this.filesize = filesize;
	}
	
	public boolean isPubOk() {
		return pubOk;
	}

	public void setPubOk(boolean pubOk) {
		this.pubOk = pubOk;
	}
	

	public boolean isServOk() {
		return servOk;
	}

	public void setServOk(boolean servOk) {
		this.servOk = servOk;
	}

	/**
	 * Método que convierte un mensaje codificado como una cadena de caracteres, a
	 * un objeto de la clase PeerMessage, en el cual los atributos correspondientes
	 * han sido establecidos con el valor de los campos del mensaje.
	 * 
	 * @param message El mensaje recibido por el socket, como cadena de caracteres
	 * @return Un objeto PeerMessage que modela el mensaje recibido (tipo, valores,
	 *         etc.)
	 */
	public static DirMessage fromString(String message) {
		/*
		 *  Usar un bucle para parsear el mensaje línea a línea, extrayendo para
		 * cada línea el nombre del campo y el valor, usando el delimitador DELIMITER, y
		 * guardarlo en variables locales.
		 */

		// System.out.println("DirMessage read from socket:");
		// System.out.println(message);
		String[] lines = message.split(END_LINE + "");
		// Local variables to save data during parsing
		DirMessage m = null;



		for (String line : lines) {
			int idx = line.indexOf(DELIMITER); // Posición del delimitador
			String fieldName = line.substring(0, idx).toLowerCase(); // minúsculas
			String value = line.substring(idx + 1).trim();

			switch (fieldName) {
			case FIELDNAME_OPERATION: {
				assert (m == null);
				m = new DirMessage(value);
				break;
			}
			case NICKNAME: {
				m = new DirMessage(m.getOperation(), value);
				break;
			}
			case SESSION_KEY: {
				if(m.getOperation().equals(DirMessageOps.OPERATION_SEND_LOOKUP_ADDR))
					m = new DirMessage(m.getOperation(), m.getNickname(),  Integer.parseInt(value));
				else
					m = new DirMessage(m.getOperation(), Integer.parseInt(value));
				break;
			} 
			case REGISTERED_USERS:{
				String[] users = value.split(",");
				m = new DirMessage(m.getOperation(), users);	
				break;
			}
			case FILENAME:
			{
				if(m.getOperation().equals(DirMessageOps.OPERATION_PUBLISH)) {
					String[] fileName = value.split(",");
					m = new DirMessage(m.getOperation(),m.getSessionKey(), fileName);
				}else if(m.getOperation().equals(DirMessageOps.OPERATION_FILELIST_OK)){
					String[] fileName = value.split(",");
					m = new DirMessage(m.getOperation(), fileName);
				}
				break;
			}
			case FILEHASH:
			{
				if(m.getOperation().equals(DirMessageOps.OPERATION_PUBLISH)) {
					String[] fileHash = value.split(",");
					m = new DirMessage(m.getOperation(),m.getSessionKey(), m.getFilename(), fileHash);
				}else if(m.getOperation().equals(DirMessageOps.OPERATION_FILELIST_OK)) {
					String[] fileHash = value.split(",");
					m = new DirMessage(m.getOperation(), m.getFilename(), fileHash);
				}
				break;
			}
			case FILESIZE:
			{
				
				if(m.getOperation().equals(DirMessageOps.OPERATION_PUBLISH)) {
					String[] fileSize = value.split(",");
					m = new DirMessage(m.getOperation(), m.getSessionKey(), m.getFilename(), m.getFilehash(), fileSize);
				}else if(m.getOperation().equals(DirMessageOps.OPERATION_FILELIST_OK)){
					String[] fileSize = value.split(",");
					m = new DirMessage(m.getOperation(), m.getFilename(), m.getFilehash(), fileSize);
				}
				break;
			}
			case PUBOK:{
				m = new DirMessage(m.getOperation(), Boolean.parseBoolean(value));
				break;
			}
			case PORT:
			{
				m = new DirMessage(m.getOperation(), m.getSessionKey(), Integer.parseInt(value));
				break;
			}
			case PORT_OK:
			{
				m = new DirMessage(m.getOperation(), Boolean.parseBoolean(value));
				break;
			}
			case SERVOK:
			{
				m = new DirMessage(m.getOperation(), Boolean.parseBoolean(value));
				break;
			}
			case LOGOUTOK:
			{
				m = new DirMessage(m.getOperation(), Boolean.parseBoolean(value));
				break;
			}
			case LOOKUPADDR:
			{
				m = new DirMessage(m.getOperation(), value);
				break;
			}
			default:
				System.err.println("PANIC: DirMessage.fromString - message with unknown field name " + fieldName);
				System.err.println("Message was:\n" + message);
				System.exit(-1);
			}
		}




		return m;
	}

	/**
	 * Método que devuelve una cadena de caracteres con la codificación del mensaje
	 * según el formato campo:valor, a partir del tipo y los valores almacenados en
	 * los atributos.
	 * 
	 * @return La cadena de caracteres con el mensaje a enviar por el socket.
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
		/*
		 * TODO: En función del tipo de mensaje, crear una cadena con el tipo y
		 * concatenar el resto de campos necesarios usando los valores de los atributos
		 * del objeto.
		 */
		switch(operation) {
			case DirMessageOps.OPERATION_LOGIN:{
				sb.append(NICKNAME + DELIMITER + nickname + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_LOGIN_OK:{
				sb.append(SESSION_KEY + DELIMITER + Integer.toString(sessionKey) + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_GET_USER_LIST:{
				sb.append(SESSION_KEY + DELIMITER + Integer.toString(sessionKey) + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_SEND_USER_LIST:{
				StringBuffer usuarios = new StringBuffer();
				for(String str : registeredUsers) 
					usuarios.append(str+DELIM_COMAS);
				usuarios.deleteCharAt(usuarios.length() - 1);
				sb.append(REGISTERED_USERS + DELIMITER + usuarios + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_LOGOUT:
			{
				sb.append(SESSION_KEY + DELIMITER + Integer.toString(sessionKey) + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_LOGOUT_OK:
			{
				sb.append(LOGOUTOK + DELIMITER + logoutOk + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_REG_SERVER_PORT:
			{
				sb.append(SESSION_KEY + DELIMITER + Integer.toString(sessionKey) + END_LINE);
				sb.append(PORT + DELIMITER + Integer.toString(port) + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_REG_SERVER_PORT_OK:
			{
				sb.append(PORT_OK + DELIMITER + Boolean.toString(portOk) + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_PUBLISH:
			{
				StringBuffer fname = new StringBuffer();
				for(String str : filename) 
					fname.append(str+DELIM_COMAS);
				fname.deleteCharAt(fname.length() - 1);
				StringBuffer fhash = new StringBuffer();
				for(String str : filehash) 
					fhash.append(str+DELIM_COMAS);
				fhash.deleteCharAt(fhash.length() - 1);
				StringBuffer fsize = new StringBuffer();
				for(String str : filesize) 
					fsize.append(str+DELIM_COMAS);
				fsize.deleteCharAt(fsize.length() - 1);
				sb.append(SESSION_KEY + DELIMITER + Integer.toString(sessionKey) + END_LINE);
				sb.append(FILENAME + DELIMITER + fname + END_LINE);
				sb.append(FILEHASH + DELIMITER + fhash + END_LINE);
				sb.append(FILESIZE + DELIMITER + fsize + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_PUBLISH_OK:{
				sb.append(PUBOK + DELIMITER + Boolean.toString(pubOk) + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_UNREGISTER_SERVER:{
				sb.append(SESSION_KEY + DELIMITER + Integer.toString(sessionKey) + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_UNREGISTER_SERVER_OK:{
				sb.append(SERVOK + DELIMITER + Boolean.toString(servOk) + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_SEND_LOOKUP_ADDR:
			{
				sb.append(NICKNAME + DELIMITER + nickname + END_LINE);
				sb.append(SESSION_KEY + DELIMITER + Integer.toString(sessionKey) + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_RCV_LOOKUP_ADDR:
			{
				sb.append(LOOKUPADDR + DELIMITER + ipPuerto + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_FILELIST:
			{
				sb.append(SESSION_KEY + DELIMITER + Integer.toString(sessionKey) + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_FILELIST_OK:
			{
				StringBuffer fname = new StringBuffer();
				for(String str : filename) 
					fname.append(str+DELIM_COMAS);
				fname.deleteCharAt(fname.length() - 1);
				StringBuffer fhash = new StringBuffer();
				for(String str : filehash) 
					fhash.append(str+DELIM_COMAS);
				fhash.deleteCharAt(fhash.length() - 1);
				StringBuffer fsize = new StringBuffer();
				for(String str : filesize) 
					fsize.append(str+DELIM_COMAS);
				fsize.deleteCharAt(fsize.length() - 1);
				sb.append(FILENAME + DELIMITER + fname + END_LINE);
				sb.append(FILEHASH + DELIMITER + fhash + END_LINE);
				sb.append(FILESIZE + DELIMITER + fsize + END_LINE);
				break;
			}
				
		}


		sb.append(END_LINE); // Marcamos el final del mensaje
		return sb.toString();
	}
}
