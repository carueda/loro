package loroedi;

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
 * Atiende eventos de ejecución paso-a-paso.
 *
 * @author Carlos Rueda
 */
public class ObservadorPP implements IObservadorPP
{
	static final String PREFIX = "[Paso-a-Paso] ";
	static final Color enterColor = new Color(0xccffcc);
	static final Color exitColor  = new Color(0xffcccc);
	private UEditor editor;
	private SymbolTableWindow symbolTableWindow;
	
	//////////////////////////////////////////////////////////////////////
	public int enter(INodo n, ISymbolTable symbTab, String src)
	{
		setup(symbTab);
		editor.setTitle(PREFIX + n.getClass());
		editor.getMessageArea().println("[->] " +n);
		if ( src != null )
		{
			editor.setText(src);
			Rango r = n.obtRango();
			if ( r != null )
				editor.select(r.obtPosIni(), r.obtPosFin());
			else
				editor.selectAll();
		}
		editor.setSelectionColor(enterColor);
		symbolTableWindow.setSymbolTable(symbTab);
		
		return 0;
	}
	
	//////////////////////////////////////////////////////////////////////
	public int exit(INodo n, ISymbolTable symbTab, String src)
	{
		editor.setTitle(PREFIX + n.getClass());
		editor.getMessageArea().println("[<-] " +n);
		if ( src != null )
		{
			editor.setText(src);
			Rango r = n.obtRango();
			if ( r != null )
				editor.select(r.obtPosIni(), r.obtPosFin());
			else
				editor.selectAll();
		}
		editor.setSelectionColor(exitColor);
		symbolTableWindow.setSymbolTable(symbTab);
		
		return 0;
	}
	
	//////////////////////////////////////////////////////////////////////
	public int push(IUnidad u, ISymbolTable symbTab, String src)
	{
		setup(symbTab);
		editor.setTitle(PREFIX + u);
		editor.getMessageArea().println("Entrando a " +u);
		if ( src != null )
		{
			editor.setText(src);
			Rango r = u.obtRango();
			if ( r != null )
				editor.select(r.obtPosIni(), r.obtPosFin());
			else
				editor.selectAll();
		}
		editor.setSelectionColor(enterColor);
		symbolTableWindow.setSymbolTable(symbTab);
		
		return 0;
	}

	//////////////////////////////////////////////////////////////////////
	public int pop(IUnidad u, ISymbolTable symbTab, String src)
	{
		editor.setTitle(PREFIX + u);
		editor.getMessageArea().println("Regresando a " +u);
		if ( src != null )
		{
			editor.setText(src);
			Rango r = u.obtRango();
			if ( r != null )
				editor.select(r.obtPosIni(), r.obtPosFin());
			else
				editor.selectAll();
		}
		editor.setSelectionColor(exitColor);
		symbolTableWindow.setSymbolTable(symbTab);
		
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
			editor = new UEditor(PREFIX, false, false, false, false,
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
				"ámbito en curso",
				symbTab,
				false,   // closeable
				false,   // delVar
				Preferencias.SYMTAB_TRACE_RECT  // preferenceCode
			);
			symbolTableWindow.show();
		}
	}
	
}

