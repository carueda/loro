package util.consola;

import java.awt.TextArea;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.io.Reader;
import java.io.IOException;
import java.io.Writer;
import java.io.PrintWriter;

/**
 * Un Reader sobre un TextArea.
 *
 * @author Carlos Rueda
 * @version 0.1 Abr/15/1999
 */
public class EntradaReader extends Reader
implements KeyListener
{
	/** El TextArea de donde se toman los caracteres. */
	TextArea ta;

	/** Ha sido tecleado ENTER? */
	boolean enter;

	/** Ha sido cerrado este reader? */
	boolean closed;

	/** Texto aún no leido. */
	String texto;

	/** Se está en read()?. */
	boolean reading;

	/** Objeto interesado en ser avisado cuando read. */
	ReaderListener rl;


	/** Para hacer eco. */
	PrintWriter pwEco;
/**
 * Crea un Reader asociado a un TextArea.
 *
 * @param ta	EL TextArea de donde se lee.
 * @param weco	Writer para hacer eco.
 */
public EntradaReader(TextArea ta, Writer weco) 
{
	super();
	this.ta = ta;

	if ( weco != null )
		pwEco = new PrintWriter(weco);
		
	ta.addKeyListener(this);
	reset();
}
/**
 * Asocia un ReaderListener a este Reader.
 */
public void addReaderListener(ReaderListener rl) 
{
	this.rl = rl;
}
public void close()
throws IOException
{
	if ( closed )
		return;

	ta.removeKeyListener(this);
	closed = true;
}
/**
 * Dice si se está leyendo en este momento.
 */
public boolean isReading() 
{
	return reading;
}
/**
 * Actualiza el texto no leido cuando se teclea
 * un ENTER.
 */
public void keyPressed(KeyEvent e)
{
	synchronized(lock)
	{
		if ( !enter 
		&&    e.getKeyCode() == KeyEvent.VK_ENTER )
		{	
			enter = true;
			e.consume();		// read() hará el resto.

			// Actualice texto con TextArea
			texto += ta.getText() + "\n";

			// Limpie el TextArea
			ta.setText("");

			// Avise que ha habido ENTER
			notifyAll();
		}
	}
}
public synchronized void keyPressed_COPIA(KeyEvent e)
{
//System.out.println("keyPressed" +e);
	if ( !enter )
	{	if ( e.getKeyCode() == KeyEvent.VK_ENTER )
		{	enter = true;
			e.consume();		// read() hará el resto.
			notifyAll();
		}
	}
}
public void keyReleased(KeyEvent e)
{
}
public void keyTyped(KeyEvent e)
{
}
/**
 * Hace una lectura como se especifica en Reader.
 * Internamente, espera a que se teclee un ENTER
 * en el TextArea (KeyPressed lo notifica de ésto),
 * y toma del texto completo no leido para la
 * lectura.
 */
public int read(char[] cbuf, int off, int len)
throws IOException
{
	synchronized(lock)
	{
		if ( closed )
			throw new IOException("Closed");

		while ( !enter )
		{
			reading = true;

			if ( rl != null )
				rl.waitingRead(true);
				
			try
			{	wait();
			}
			catch(Exception e)
			{
				reading = false;
				if ( rl != null )
					rl.waitingRead(false);
				System.out.println("Exception->" +e.getMessage());
				throw new IOException(e.getMessage());
			}
		}
				
		// Llego del notifyAll() del keyPressed().
		// texto contiene lo aún no leido

		int texLen = texto.length();
		int num_leidos = Math.min(texLen, len);

		// Ponga los caracteres en el cbuf
		texto.getChars(0, num_leidos, cbuf, off);
		
		if ( texLen == num_leidos )
			enter = false;

		// Haga eco:
		if ( pwEco != null )
			pwEco.print(texto.substring(0, num_leidos));


		// Actualice texto y TextArea con lo no leido
		texto = texto.substring(num_leidos);
		ta.setText(texto);

		reading = false;
		if ( rl != null )
			rl.waitingRead(false);
		
		return num_leidos;
	}
}
public synchronized int read_COPIA(char[] cbuf, int off, int len)
throws IOException
{
	if ( closed )
		throw new IOException("Closed");

//System.out.println("read");
	while ( !enter )
	{
//System.out.println("pre-wait");
		try
		{	wait();
		}
		catch(Exception e)
		{	System.out.println("Exception->" +e.getMessage());
			throw new IOException(e.getMessage());
		}
//System.out.println("pos-wait");
	}
			
	/*
		Llego del notifyAll() del keyPressed().
		El textArea no tiene el cambio de linea del ENTER.
		Así que agrego un '\n'. (Algún BufferedReader asociado
		conmigo podrá tomar su linea oportunamente.)
	*/

	ta.append("\n");
	String tex = ta.getText();

	int texLen = tex.length();
	int num_leidos = Math.min(texLen, len);

	tex.getChars(0, num_leidos, cbuf, off);
	if ( texLen == num_leidos )
		enter = false;

	ta.setText(tex.substring(num_leidos));

	return num_leidos;
}
/**
 * Reinicia este Reader.
 */
public void reset() 
{
	synchronized(lock)
	{
		enter = false;
		closed = false;
		texto = "";
		ta.setText("");
	}
}
}