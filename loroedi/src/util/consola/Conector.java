package util.consola;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.IOException;

/**
*	Este hilo se encarga de leer todo lo que venga por un flujo de 
*	entrada escribi�ndolo por otro flujo de salida.
*
*	La conexi�n se establece entre los flujos b�sicos InputStream
*	(para la entrada) y OutputStream (para la salida).
*	Seguramente usted har� extensiones de estas clases para sacar
*	provecho de los conectores.
*
*	El flujo de entrada tiene un manejo a trav�s de un objeto
*	java.io.BufferedReader; por lo tanto, en cada lectura, mediante
*	el m�todo readLine(), se recibir� un paquete completo, el cual 
*	normalmente es una l�nea.
*
*	Para el flujo de salida se utiliza un objeto java.io.PrintWriter.
*	Mediante su m�todo println() se escribe cada l�nea leida.
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
	* con este texto, se termina la ejecuci�n.
	*/
	String chao;

/**
 * Crea un conector con texto de despedida "chao".
 *
 * @param r		El flujo de donde se leer�n l�neas.
 * @param w		El flujo a donde se escribir�n las l�neas leidas.
 */
Conector(Reader r, Writer w)
{
	this(r, w, "chao");
}
/**
 * Crea un conector.
 *
 * @param r		El flujo de donde se leer�n l�neas.
 * @param w		El flujo a donde se escribir�n las l�neas leidas.
 * @param chao	Texto para terminaci�n de ejecuci�n.
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
 * Ejecuci�n del conector. Se trata de un ciclo en el que
 * se leen l�neas de la entrada y se escriben en la salida.
 * Si el texto de la l�nea concuerda con el de terminaci�n,
 * se termina la ejecuci�n.
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