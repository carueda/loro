package loroedi;

import loro.IObservadorPP;
import loro.ISymbolTable;
import loro.Rango;
import loro.arbol.IUbicable;

import loroedi.gui.editor.UEditor;
import loroedi.gui.editor.UEditorListener;
import loroedi.gui.project.SymbolTableWindow;


/////////////////////////////////////////////////////////////////////
/**
 * Atiende eventos de ejecución paso-a-paso.
 *
 * @author Carlos Rueda
 */
public class ObservadorPP implements IObservadorPP
{
	static final String PREFIX = "[Paso-a-Paso] ";
	UEditor editor;
	SymbolTableWindow symbolTableWindow;
	
	//////////////////////////////////////////////////////////////////////
	public int enter(IUbicable u, ISymbolTable symbTab, String src)
	{
		setup(symbTab);
		editor.setTitle(PREFIX + u.getClass());
		editor.getMessageArea().println("[->] " +u);
		if ( src != null )
		{
			editor.setText(src);
			Rango r = u.obtRango();
			if ( r != null )
				editor.select(r.obtPosIni(), r.obtPosFin());
			else
				editor.selectAll();
		}
		symbolTableWindow.setSymbolTable(symbTab);
		
		return 0;
	}
	
	//////////////////////////////////////////////////////////////////////
	public int exit(IUbicable u, ISymbolTable symbTab, String src)
	{
		editor.setTitle(PREFIX + u.getClass());
		editor.getMessageArea().println("[<-] " +u);
		if ( src != null )
		{
			editor.setText(src);
			Rango r = u.obtRango();
			if ( r != null )
				editor.select(r.obtPosIni(), r.obtPosFin());
			else
				editor.selectAll();
		}
		symbolTableWindow.setSymbolTable(symbTab);
		
		return 0;
	}
	
	//////////////////////////////////////////////////////////////////////
	public int end()
	{
		editor.getFrame().dispose();
		symbolTableWindow.getFrame().dispose();
		return 0;
	}
	
	//////////////////////////////////////////////////////////////////////
	private void setup(ISymbolTable symbTab)
	{
		if ( editor == null )
		{
			editor = new UEditor(PREFIX, false, false, false, false);
			editor.setEditorListener(new UEditorListener()
			{
				public void changed() {} 
				public void save() {} 
				public void closeWindow() {} 
				public void compile() {}
				public void execute() {} 
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

