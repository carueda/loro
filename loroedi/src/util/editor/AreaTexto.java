package util.editor;

import java.io.*;
import java.awt.*;


////////////////////////////////////////////////////////
/**
 * Un editor opera sobre una instancia de esta clase.
 * Esta instancia debe avisarle al editor cuando su contenido
 * cambie por alguna accion del usuario. Para esto puede
 * ejecutar:
 *
 * <code>
 *		obtEditor().textModificado();
 * </code>
 * 
 * @author Carlos Rueda
 */
public abstract class AreaTexto 
{
	/** Mi editor. */
	protected Editor se;
	

	/////////////////////////////////////////////////////////////////
	/**
	 * Lleva el cursor a donde esta el texto iniciando la busqueda
	 * desde la posicion dada.
	 * Retorna la posicion si la cadena fue encontrada.
	 */
	public int buscar(String cadena, int desde)
	{
		String texto = getText();
		int pos = texto.indexOf(cadena, desde);

		if ( pos >= 0 )
		{
			select(pos, pos + cadena.length());
		}
		
		return pos;
	}
	/////////////////////////////////////////////////////////////////
	/**
	 */
	public abstract Component comoComponent();
	/////////////////////////////////////////////////////////////////
	/**
	 * copy
	 */
	public abstract void copy();

	/////////////////////////////////////////////////////////////////
	/**
	 * cut
	 */
	public abstract void cut();
	/////////////////////////////////////////////////////////////////
	/**
	 * Retorna la linea en donde esta el cursor.
	 */
	public abstract int getCaretPosition();
	/////////////////////////////////////////////
	/**
	 * 
	 */
	public abstract String getText() ;

	/////////////////////////////////////////////////////////////////
	/**
	 * Lleva el cursor a una linea.
	 */
	public abstract void irALinea(int num);



	/////////////////////////////////////////////////////////////////
	/**
	 * paste
	 */
	public abstract void paste();
	
	
	/////////////////////////////////////////////
	/**
	 * Pone el color de selección.
	 */
	public abstract void setSelectionColor(Color color);
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Selecciona un trozo.
	 */
	public abstract void select(int start, int end);
	/////////////////////////////////////////////////////////////////
	/**
	 * selectAll
	 */
	public abstract void selectAll();
	/////////////////////////////////////////////////////////////////
	/**
	 * Ubica la posicion del cursor.
	 */
	public abstract void setCaretPosition(int pos);

	/////////////////////////////////////////////
	/**
	 * 
	 */
	public abstract void setText(String t);
	/////////////////////////////////////////////
	/**
	 * Hace que el area de texto obtenga el foco
	 * de los eventos.
	 */
	public abstract void traerAlFrente() ;

	/////////////////////////////////////////////
	/**
	 * Crea un area de texto
	 */
	protected AreaTexto() 
	{
	}

	/////////////////////////////////////////////
	/**
	 * Crea un area de texto con editor asociado.
	 */
	protected AreaTexto(Editor se) 
	{
		this.se = se;
	}

	/////////////////////////////////////////////
	/**
	 * Pone el editor asociado a esta area de texto.
	 */
	public void ponEditor(Editor se) 
	{
		this.se = se;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Incrementa o decrementa el tamano del font actual
	 * de esta area de texto.
	 */
	public void changeFontSize(int inc)
	{
		Font font = getFont();
		int font_size = font.getSize();
		int font_style = font.getStyle();
		font_size += inc;
		if ( font_size <= 0 )		// 2001-08-11
		{
			// Ignore
			return;
		}
		Font new_font = new Font("monospaced", font_style, font_size);
		//new_font = font.deriveFont(font_style, font_size);
		//	deriveFont is 1.2.2 ?
		setFont(new_font);
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene el font.
	 */
	public abstract Font getFont();

	/////////////////////////////////////////////
	/**
	 * Obtiene el editor asociado a esta area de texto.
	 */
	public Editor obtEditor()
	{
		return se;
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Pone el font.
	 */
	public abstract void setFont(Font font);

	////////////////////////////////////////////////////////////////
	public abstract void setEditable(boolean editable);
}
