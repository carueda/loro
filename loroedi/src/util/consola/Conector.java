package util.consola;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.IOException;

/**
*	Este hilo se encarga de leer todo lo que venga por un flujo de 
*	entrada escribiéndolo por otro flujo de salida.
*
*	La conexión se establece entre los flujos básicos InputStream
*	(para la entrada) y OutputStream (para la salida).
*	Seguramente usted hará extensiones de estas clases para sacar
*	provecho de los conectores.
*
*	El flujo de entrada tiene un manejo a través de un objeto
*	java.io.BufferedReader; por lo tanto, en cada lectura, mediante
*	el método readLine(), se recibirá un paquete completo, el cual 
*	normalmente es una línea.
*
*	Para el flujo de salida se utiliza un objeto java.io.PrintWriter.
*	Mediante su método println() se escribe cada línea leida.
*
*	La tarea termina cuando el mensaje recibido es "chao".
*
*	@author Carlos Rueda
*	@version 1.0 Junio/03/1998
*/
public class Conector extends Thread
{
	BufferedReader br;
	PrintWriter pw;

	/** 
	* Texto de despedida. Si una linea leida coincide
	* con este texto, se termina la ejecución.
	*/
	String chao;

/**
 * Crea un conector con texto de despedida "chao".
 *
 * @param r		El flujo de donde se leerán líneas.
 * @param w		El flujo a donde se escribirán las líneas leidas.
 */
Conector(Reader r, Writer w)
{
	this(r, w, "chao");
}
/**
 * Crea un conector.
 *
 * @param r		El flujo de donde se leerán líneas.
 * @param w		El flujo a donde se escribirán las líneas leidas.
 * @param chao	Texto para terminación de ejecución.
 */
Conector(Reader r, Writer w, String chao)
{
	super("Conector");
	br = new BufferedReader(r);
	pw = new PrintWriter(w);
	this.chao = chao;
}
public static void main(String[] args) 
throws java.io.IOException
{
	new Conector
	(	new java.io.InputStreamReader(System.in), 
		new java.io.OutputStreamWriter(System.out)
	).start();
}
/**
 * Ejecución del conector. Se trata de un ciclo en el que
 * se leen líneas de la entrada y se escriben en la salida.
 * Si el texto de la línea concuerda con el de terminación,
 * se termina la ejecución.
 */
public void run()
{
	String linea;

	try
	{	while ( (linea = br.readLine()) != null ) 
		{	
			pw.println(linea);
			pw.flush();
			if (linea.equals(chao))
			{
				break;
			}
		}
	}
	catch(IOException e)
	{
	}
}
}