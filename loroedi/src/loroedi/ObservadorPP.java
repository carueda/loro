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
	public int ver(IUbicable u, ISymbolTable symbTab, String src)
	{
		if ( editor == null )
		{
			editor = new UEditor(PREFIX, false, false, false);
			editor.setEditorListener(new UEditorListener()
			{
				public void changed() {} 
				public void save() {} 
				public void closeWindow() {} 
				{
					editor.getFrame().setVisible(false);
					editor.getFrame().dispose();
				}
				public void compile() {}
				public void execute() {} 
				public void reload() {} 
				public void viewDoc() {}
			});
			editor.display();
			symbolTableWindow = new SymbolTableWindow(symbTab);
			symbolTableWindow.show();
		}
			
		editor.setTitle(PREFIX + u.getClass());
		System.out.println("u = " +u);
		if ( src != null )
		{
			editor.setText(src);
			Rango r = u.obtRango();
			editor.select(r.obtPosIni(), r.obtPosFin());
		}
		symbolTableWindow.setSymbolTable(symbTab);
		
		return 0;
	}
}

