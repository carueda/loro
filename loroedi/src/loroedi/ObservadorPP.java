package loroedi;

import loro.Loro;
import loro.IObservadorPP;
import loro.ISymbolTable;
import loro.IUnidad;
import loro.Rango;
import loro.arbol.INodo;

import loroedi.gui.editor.UEditor;
import loroedi.gui.editor.UEditorListener;
import loroedi.gui.project.SymbolTableWindow;
import loroedi.Preferencias;

import java.awt.Color;

/////////////////////////////////////////////////////////////////////
/**
 * Atiende eventos de ejecuci�n paso-a-paso.
 *
 * @author Carlos Rueda
 */
public class ObservadorPP implements IObservadorPP
{
	static final String TITLE_PREFIX = "[Paso-a-Paso] ";
	static final Color enterColor = new Color(0xccffcc);
	static final Color exitColor  = new Color(0xffcccc);
	
	private UEditor editor;
	private SymbolTableWindow symbolTableWindow;
	private int level = 0;
	
	/** Para control step-over un elemento. */
	private INodo nodeToReturn = null;
	private INodo currentNode = null;
	private boolean onExit = false;
	
	private PPControl ppcontrol = new PPControl();
	
	///////////////////////////////////////////////////////////////////////
	public void nextStep(boolean stepInto)
	throws InterruptedException
	{
		if ( stepInto || onExit )
		{
			// Si es "step-into", o se est� ahora mismo a punto de
			// terminar un exit(), se pone ejecuci�n normal, es decir,
			// paso a paso; en el caso de estar en exit(), esto permite
			// pasar a la siguiente acci�n en el mismo nivel o en
			// uno menor.
			nodeToReturn = null;
			ppcontrol.setActive(true);
		}
		else
		{
			// es decir, "step-over"; entonces se registra el
			// actual nodo como al que hay que regresar y se
			// deja libre el ppcontrol. Ver exit().
			nodeToReturn = currentNode;
			ppcontrol.setActive(false);
		}
		ppcontrol.setSignal(0);				
	}

	///////////////////////////////////////////////////////////////////////
	public void resume()
	{
		nodeToReturn = null;
		ppcontrol.setActive(false);
	}
	
	//////////////////////////////////////////////////////////////////////
	public int enter(INodo n, ISymbolTable symbTab, String src)
	throws InterruptedException
	{
		if ( n instanceof IUnidad.IAlgoritmo )
		{
			return 0;
			
			// Explicaci�n:
			// LoroEjecutor *hace* efectivamente la visita 
			// un nodo-algoritmo, pero en cambio maneja directamente
			// el caso de las especificaciones y clases.
			// Por lo tanto, no se hace nada aqu� ya que se hara
			// el enter al nodo n.
		}
		
		currentNode = n;
		String prefix = getPrefix(level++);
		
		setup(symbTab);
		String nodeDescr = Loro.getNodeDescription(n);
		editor.setTitle(TITLE_PREFIX + nodeDescr);
		editor.getMessageArea().println(prefix+ "->" +nodeDescr);
		if ( src != null )
		{
			editor.setText(src);
			Rango r = n.obtRango();
			if ( r != null )
				editor.select(r.obtPosFin(), r.obtPosIni());
			else
				editor.selectAll();
		}
		editor.setSelectionColor(enterColor);
		symbolTableWindow.setSymbolTable(symbTab);
		
		onExit = false;
		ppcontrol.getSignal();
		
		return 0;
	}
	
	//////////////////////////////////////////////////////////////////////
	public int exit(INodo n, ISymbolTable symbTab, String src, String result)
	throws InterruptedException
	{
		currentNode = n;
		String prefix = getPrefix(--level);
		
		if ( currentNode == nodeToReturn )
		{
			nodeToReturn = null;
			ppcontrol.setActive(true);
		}
		
		String nodeDescr = Loro.getNodeDescription(n);
		editor.setTitle(TITLE_PREFIX + nodeDescr);
		editor.getMessageArea().print(prefix+ "<-" +nodeDescr);
		if ( result != null )
			editor.getMessageArea().print(" Result = " +result);
		editor.getMessageArea().println("");
		
		if ( src != null )
		{
			editor.setText(src);
			Rango r = n.obtRango();
			if ( r != null )
				editor.select(r.obtPosFin(), r.obtPosIni());
			else
				editor.selectAll();
		}
		editor.setSelectionColor(exitColor);
		symbolTableWindow.setSymbolTable(symbTab);
		
		onExit = true;
		ppcontrol.getSignal();
		
		return 0;
	}
	
	//////////////////////////////////////////////////////////////////////
	public int push(IUnidad u, ISymbolTable symbTab, String src)
	throws InterruptedException
	{
		setup(symbTab);
		editor.setTitle(TITLE_PREFIX + u);
		editor.getMessageArea().println("Entrando a " +u);
		if ( src != null )
		{
			editor.setText(src);
			Rango r = u.obtRango();
			if ( r != null )
				editor.select(r.obtPosFin(), r.obtPosIni());
			else
				editor.selectAll();
		}
		editor.setSelectionColor(enterColor);
		symbolTableWindow.setSymbolTable(symbTab);
		
		onExit = false;
		ppcontrol.getSignal();

		return 0;
	}

	//////////////////////////////////////////////////////////////////////
	public int pop(IUnidad u, ISymbolTable symbTab, String src)
	throws InterruptedException
	{
		if ( currentNode == nodeToReturn )
			ppcontrol.setActive(true);
		
		ppcontrol.getSignal();
		editor.setTitle(TITLE_PREFIX + u);
		editor.getMessageArea().println("Regresando a " +u);
		if ( src != null )
		{
			editor.setText(src);
			Rango r = u.obtRango();
			if ( r != null )
				editor.select(r.obtPosFin(), r.obtPosIni());
			else
				editor.selectAll();
		}
		editor.setSelectionColor(exitColor);
		symbolTableWindow.setSymbolTable(symbTab);
		
		onExit = true;
		ppcontrol.getSignal();

		return 0;
	}
	
	//////////////////////////////////////////////////////////////////////
	public int end()
	{
		if ( editor != null )
		{
			editor.getFrame().dispose();
			symbolTableWindow.getFrame().dispose();
		}
		return 0;
	}
	
	//////////////////////////////////////////////////////////////////////
	private void setup(ISymbolTable symbTab)
	{
		if ( editor == null )
		{
			editor = new UEditor(TITLE_PREFIX, false, false, false, false,
				Preferencias.SOURCE_TRACE_RECT
			);
			editor.setEditorListener(new UEditorListener()
			{
				public void changed() {} 
				public void save() {} 
				public void closeWindow() {} 
				public void compile() {}
				public void execute(boolean trace) {} 
				public void reload() {} 
				public void viewDoc() {}
			});
			editor.display();
			symbolTableWindow = new SymbolTableWindow(
				"�mbito en curso",
				symbTab,
				false,   // closeable
				false,   // delVar
				Preferencias.SYMTAB_TRACE_RECT  // preferenceCode
			);
			symbolTableWindow.show();
		}
	}
	
	////////////////////////////////////////////////////////////
	private static String getPrefix(int level) 
	{
		StringBuffer sb = new StringBuffer();
		for ( int i = 0; i < level; ++i )
			sb.append("   ");	
		return sb.toString();
	}                                          

	///////////////////////////////////////////////////////////
	/**
	 * Control para paso a paso.
	 */
	static class PPControl
	{
		int currSignal = -1;
		boolean active = true;
		
		//////////////////////////////////////////////////
		synchronized void setActive(boolean active)
		{
			this.active = active;
			notifyAll();
		}
		
		//////////////////////////////////////////////////
		synchronized void setSignal(int senal)
		throws InterruptedException
		{
			if ( !active )
				return;
			
			while ( active && currSignal >= 0 )
				wait();
			
			currSignal = senal;
			notifyAll();
		}
	
		//////////////////////////////////////////////////
		synchronized int getSignal()
		throws InterruptedException
		{
			if ( !active )
				return 0;
			
			while ( active && currSignal < 0 )
				wait();
			
			int senal = currSignal;
			currSignal = -1;
			notifyAll();
			return senal;
		}
	}
}

