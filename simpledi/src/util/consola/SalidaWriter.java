package util.consola;

import java.awt.TextArea;
import java.io.Writer;
import java.io.IOException;

/**
 * Escritor sobre un TextArea.
 */
public class SalidaWriter extends Writer 
{
	TextArea ta;
	boolean closed;
/**
 * Salida constructor comment.
 */
public SalidaWriter(TextArea ta) 
{
	super();
	this.ta = ta;
	closed = false;
}
public void close()
throws IOException
{
	closed = true;
}
public void flush()
throws IOException
{
}
public void limpiar()
{
	ta.setText("");
}
public void write(char[] cbuf, int off, int len)
throws IOException
{
	if ( closed )
		throw new IOException("Closed");

	ta.append(new String(cbuf, off, len));
}
}