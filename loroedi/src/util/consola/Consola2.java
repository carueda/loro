package util.consola;

import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.Label;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.Writer;
import java.io.Reader;

import loro.IEjecutor;

//////////////////////////////////////////////////////////////
/**
 * Ventana con consola de zonas separadas para entrada y
 * salida estándar.
 *
 * @author Carlos Rueda
 * @version 0.1 Abr/15/1999
 */
public class Consola2 extends Frame
implements ActionListener, ReaderListener, Consola
{
	/** El flujo para escribir en la consola. */
	private SalidaWriter sw;

	/** El flujo para leer de la consola. */
	private EntradaReader er;

	private Label stdin;
	Color colorStdin;

	/** El area de salida estandar. */
	TextArea sta;

	/** El area de entrada estandar. */
	TextArea eta;

/**
 * Crea una consola con título dado.
 */
public Consola2()
{
	super();

	setSize(600, 400);
	Font font = new Font("monospaced", Font.PLAIN, 14);

	sta = new TextArea();
	sta.setEditable(false);
	sta.setFont(font);
	eta = new TextArea(2, 80);
	eta.setFont(font);

	Panel stdoutp = new Panel(new BorderLayout());
	Panel sup = new Panel(new FlowLayout(FlowLayout.LEFT));
	sup.add(new Label("Salida estándar"));
	Button but = new Button("Borrar");
	but.addActionListener(this);
	sup.add(but);
	stdoutp.add(sup, "North");
	stdoutp.add(sta);

	Panel stdinp = new Panel(new BorderLayout());
	stdin = new Label("Entrada estándar");
	colorStdin = stdin.getBackground();
	stdinp.add(stdin, "North");
	stdinp.add(eta);

	add(stdinp, "South");
	add(stdoutp, "Center");

	sw = new SalidaWriter(sta);
	er = new EntradaReader(eta, sw);

	er.addReaderListener(this);
	
	addWindowListener
	(	new java.awt.event.WindowAdapter()
		{	public void windowClosing(java.awt.event.WindowEvent e)
			{
				if ( !er.isReading() )
					setVisible(false);
			}
		}
	);

	
}
	////////////////////////////////////////////////
	/**
	 * Despliega esta consola.
	 */
	public void abrir()
	{
		setVisible(true);
		requestFocus();
		eta.requestFocus();
	}
	///////////////////////////////////////////////////
	public void actionPerformed(ActionEvent _)
	{
		sw.limpiar();
	}
public void cerrar()
{
	try
	{	er.close();
		sw.close();
	}
	catch(Exception e)
	{
	}
	dispose();
}
	public void ejecucionTerminada()
	{
	}
/**
 * Un ejemplo de demostración.
 * Lo que se escribe por el área stdin, se escribe por stdout
 * hasta que se escriba "chao".
 */
public static void main(String[] _)
{
	Consola con = new Consola2();
	con.setTitle("Consola Eco");
	new Conector(con.obtReader(), con.obtWriter()).start();
	con.setVisible(true);
}
public Reader obtReader()
{
	return er;
}
public Writer obtWriter()
{
	return sw;
}

	public void reset()
	{
		er.reset();
		try
		{	sw.flush();
		}
		catch(java.io.IOException e)
		{
		}
	}
/**
 * Llamado cuando a un EntradaReader se le llama
 * su read(char[],int,int).
 */
public void waitingRead(boolean t)
{
	if ( t )
	{
		final String esperando = " -- ESPERANDO ENTRADA...";
		setTitle(getName()+ esperando);
		stdin.setBackground(Color.green);
		stdin.setText("Entrada estándar" + esperando);
		if ( !isShowing() )
		{
			setVisible(true);

			// Para obligar a actualizar scroll:
			sta.setCaretPosition(sta.getCaretPosition());
		}
		requestFocus();
		eta.requestFocus();
	}
	else
	{	
		setTitle(getName());
		stdin.setBackground(colorStdin);
		stdin.setText("Entrada estándar");
	}
	
}

	public void ponEjecutor(IEjecutor ejecutor)
	{
	}
}