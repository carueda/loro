package util.consola;

import java.io.Reader;
import java.io.Writer;

import loro.IEjecutor;

//////////////////////////////////////////////
/**
 */
public interface Consola 
{
	public void setVisible(boolean v);
	public boolean isShowing();
	public void abrir();
	public void cerrar();
	public void reset();
	public void setTitle(String t);
	public Reader obtReader();
	public Writer obtWriter();

	public void ejecucionTerminada();



	public void ponEjecutor(IEjecutor ejecutor);
}