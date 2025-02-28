package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import es.um.redes.nanoFiles.util.FileInfo;

public class PeerMessage {




	private byte opcode;
	private long param1;
	private long param2;
	private int longitud;
	private byte[] valor;
	/*
	 * Añadir atributos y crear otros constructores específicos para crear
	 * mensajes con otros campos (tipos de datos)
	 * 
	 */
	


	public PeerMessage() {
		opcode = PeerMessageOps.OPCODE_INVALID_CODE;
	}

	public PeerMessage(byte op) {
		opcode = op;
	}
	public PeerMessage(byte op, long parametro1, long parametro2) {
		opcode=op;
		param1 = parametro1;
		param2 = parametro2;
		
	}
	public PeerMessage(byte op, int longi, byte[] val ) {
		opcode=op;
		longitud = longi;
		valor = val;
		
	}
	

	/*
	 * Crear métodos getter y setter para obtener valores de nuevos atributos,
	 * comprobando previamente que dichos atributos han sido establecidos por el
	 * constructor (sanity checks)
	 */
	public byte getOpcode() {
		return opcode;
	}
	public long getParam1() {
		return param1;
	}
	public long getParam2() {
		return param2;
	}
	public int getLongitud() {
		return longitud;
	}
	public byte[] getValor() {
		return valor;
	}
	public void setParam1(long parametro1) {
		param1 = parametro1;
	}
	public void setParam2(long parametro2) {
			param2 = parametro2;
		}
	public void setLongitud(int longitud) {
		this.longitud = longitud;
	}
	public void setValor (byte[] valor) {
		this.valor = valor;
	}

	/**
	 * Método de clase para parsear los campos de un mensaje y construir el objeto
	 * DirMessage que contiene los datos del mensaje recibido
	 * 
	 * @param data El array de bytes recibido
	 * @return Un objeto de esta clase cuyos atributos contienen los datos del
	 *         mensaje recibido.
	 * @throws IOException
	 */
	public static PeerMessage readMessageFromInputStream(DataInputStream dis) throws IOException {
		/*
		 * En función del tipo de mensaje, leer del socket a través del "dis" el
		 * resto de campos para ir extrayendo con los valores y establecer los atributos
		 * del un objeto DirMessage que contendrá toda la información del mensaje, y que
		 * será devuelto como resultado. NOTA: Usar dis.readFully para leer un array de
		 * bytes, dis.readInt para leer un entero, etc.
		 */
		PeerMessage message = new PeerMessage();
		byte opcode = dis.readByte();
		switch (opcode) {
		case PeerMessageOps.SEND_DOWNLOADFROM:
			int longi = dis.readInt();
			byte[] val = new byte[longi];
			dis.readFully(val); 
			message = new PeerMessage(opcode, longi, val);
				break;
		case PeerMessageOps.RECEIVE_DOWNLOADFROM:
			int longit = dis.readInt();
			byte[] valo = new byte[longit];
			dis.readFully(valo); 
			message = new PeerMessage(opcode, longit, valo);
			break;
		case PeerMessageOps.FAIL_DOWNLOADFROM:
			message = new PeerMessage(opcode);
			break;



		default:
			System.err.println("PeerMessage.readMessageFromInputStream doesn't know how to parse this message opcode: "
					+ PeerMessageOps.opcodeToOperation(opcode));
			System.exit(-1);
		}
		return message;
	}

	public void writeMessageToOutputStream(DataOutputStream dos) throws IOException {
		/*
		 * Escribir los bytes en los que se codifica el mensaje en el socket a
		 * través del "dos", teniendo en cuenta opcode del mensaje del que se trata y
		 * los campos relevantes en cada caso. NOTA: Usar dos.write para leer un array
		 * de bytes, dos.writeInt para escribir un entero, etc.
		 */

		dos.writeByte(opcode);
		switch (opcode) {
		case PeerMessageOps.SEND_DOWNLOADFROM:
			dos.writeInt(longitud);
			dos.write(valor);
				break;
		case PeerMessageOps.RECEIVE_DOWNLOADFROM:
			dos.writeInt(longitud);
			dos.write(valor);
				break;
		case PeerMessageOps.FAIL_DOWNLOADFROM:
			break;
			
				
			

		default:
			System.err.println("PeerMessage.writeMessageToOutputStream found unexpected message opcode " + opcode + "("
					+ PeerMessageOps.opcodeToOperation(opcode) + ")");
		}
	}





}
