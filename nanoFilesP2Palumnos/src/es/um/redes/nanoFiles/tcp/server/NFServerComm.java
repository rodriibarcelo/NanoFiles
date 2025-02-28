package es.um.redes.nanoFiles.tcp.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.Scanner;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFServerComm {

	public static synchronized void serveFilesToClient(Socket socket) throws EOFException, IOException  {
		/*
		 *  Crear dis/dos a partir del socket
		 */
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		/*
		 *  Mientras el cliente esté conectado, leer mensajes de socket,
		 * convertirlo a un objeto PeerMessage y luego actuar en función del tipo de
		 * mensaje recibido, enviando los correspondientes mensajes de respuesta.
		 * 
		 */

		//while(socket.isConnected()) {
		
			PeerMessage recibido = PeerMessage.readMessageFromInputStream(dis);
			if(recibido.getOpcode() == PeerMessageOps.SEND_DOWNLOADFROM) {
				FileInfo[] db =NanoFiles.db.getFiles();
				boolean encontrado = false;
				String hash = new String(recibido.getValor());
				for(FileInfo fic : db) {
					if(fic.fileHash.equals(hash)) 
						encontrado = true;
				}
				if(encontrado) {
					String path = NanoFiles.db.lookupFilePath(hash);
					File file = new File(path);
					byte[] archivoLeido = new byte[(int) file.length()];
					DataInputStream fis = new DataInputStream(new FileInputStream(file));
					fis.readFully(archivoLeido);
					fis.close();
					PeerMessage enviado = new PeerMessage(PeerMessageOps.RECEIVE_DOWNLOADFROM, archivoLeido.length, archivoLeido);
					enviado.writeMessageToOutputStream(dos);
				}else {
					PeerMessage enviado = new PeerMessage(PeerMessageOps.FAIL_DOWNLOADFROM);
					enviado.writeMessageToOutputStream(dos);
				}
				
			}
	
		//}
		
		/*
		 *  Para servir un fichero, hay que localizarlo a partir de su hash (o
		 * subcadena) en nuestra base de datos de ficheros compartidos. Los ficheros
		 * compartidos se pueden obtener con NanoFiles.db.getFiles(). El método
		 * FileInfo.lookupHashSubstring es útil para buscar coincidencias de una
		 * subcadena del hash. El método NanoFiles.db.lookupFilePath(targethash)
		 * devuelve la ruta al fichero a partir de su hash completo.
		 */



	}




}
