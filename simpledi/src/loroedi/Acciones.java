package loroedi;


import javax.swing.*;
import java.awt.event.*;


import java.util.Map;
import util.editor.*;

/////////////////////////////////////////////////////////
/**
 * Obtiene los objetos Action para la interaccion con el usuario.
 */
public class Acciones
{
	/** Asociacion nombre->Action. */
	Map acciones;
	/** El manejador general del entorno de desarrollo. */
	EDI edi;

	/////////////////////////////////////////////////////////
	/**
	 * Crea un manejador de acciones para un editor dado.
	 */
	public Acciones(EDI edi)
	{
		this.edi = edi;
		acciones = new java.util.HashMap();

		// Archivo:
		acciones.put("nuevo",          new AccionNuevo());
		acciones.put("abrir",          new AccionAbrir());
		acciones.put("guardar",        new AccionGuardar());
		acciones.put("guardar-como",   new AccionGuardarComo());
		acciones.put("salir",          new AccionSalir());
		
		// Edicion:
		acciones.put("cortar",         new AccionCortar());
		acciones.put("copiar",         new AccionCopiar());
		acciones.put("pegar",          new AccionPegar());
		acciones.put("seleccionar-todo", new AccionSeleccionarTodo());
		acciones.put("buscar",         new AccionBuscar());
		acciones.put("buscar-siguiente",  new AccionBuscarSiguiente());
		acciones.put("ir-a-linea",     new AccionIrALinea());
		acciones.put("incr-font",     new AccionIncrementarFont());
		acciones.put("decr-font",     new AccionDecrementarFont());

		// Accion:
		acciones.put("compilar",       new AccionCompilar());
		
		// Ayuda:
		acciones.put("ayuda",          new AccionAyuda());
		acciones.put("mostrar-interprete-interactivo", 
			                          new AccionInterpreteInteractivo());
		acciones.put("ayuda-doc",      new AccionDocumentacion());
		acciones.put("a-proposito-de", new AccionAPropositoDe());
		acciones.put("verificar-sistema", new AccionVerificarSistema());

	}

	/////////////////////////////////////////////////////////
	/**
	 * Gets an action.
	 * If the name is not a predefined action, it returns
	 * a new NotImplementedAction object.
	 *
	 * @param name The action name
	 * @return The action object.
	 */
	public Action obtAccion(String name)
	{
		Action a = (Action) acciones.get(name);
		if ( a == null )
		{
			a = new AccionNoImplementada(name);
		}
		return a;
	}

	/////////////////////////////////////////////////////////
	/**
	 * Obtiene una accion.
	 */
	public Action obtAccion(String name, Object arg)
	{
		String key = name+ ":" +arg;
		Action a = (Action) acciones.get(key);
		if ( a == null )
		{
			if ( false )
			{
			}
			else
			{
				a = new AccionNoImplementada(key);
			}
		}

		return a;
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionAbrir extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.abrir();
		}

		/////////////////////////////////////////////////////////
		public AccionAbrir()
		{
			super("Abrir...", Util.getIcon("img/Open24.gif"));
			putValue(SHORT_DESCRIPTION, "Abre de disco un archivo");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionAPropositoDe extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.mostrarAPropositoDe();
		}

		/////////////////////////////////////////////////////////
		public AccionAPropositoDe()
		{
			super("A propósito de ...");
			putValue(SHORT_DESCRIPTION, "Muestra identificación y versión del sistema");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionVerificarSistema extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.verificarSistema(true);
		}

		/////////////////////////////////////////////////////////
		public AccionVerificarSistema()
		{
			super("Verificar sistema");
			putValue(SHORT_DESCRIPTION, "Efectúa una verificación del sistema");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionAyuda extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.mostrarAyuda();
		}

		/////////////////////////////////////////////////////////
		public AccionAyuda()
		{
			super("Ayuda Loro", Util.getIcon("img/Help24.gif"));
			putValue(SHORT_DESCRIPTION, "Información general sobre el sistema");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionBuscar extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.obtEditor().buscar();
		}

		/////////////////////////////////////////////////////////
		public AccionBuscar()
		{
			super("Buscar...", Util.getIcon("img/Find24.gif"));
			putValue(SHORT_DESCRIPTION, "Busca una cadena en el texto");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionBuscarSiguiente extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.obtEditor().buscarSiguiente();
		}

		/////////////////////////////////////////////////////////
		public AccionBuscarSiguiente()
		{
			super("Buscar siguiente", Util.getIcon("img/FindAgain24.gif"));
			putValue(SHORT_DESCRIPTION, "Busca de nuevo");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionCompilar extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.compilar();
		}

		/////////////////////////////////////////////////////////
		public AccionCompilar()
		{
			super("Compilar", Util.getIcon("img/compile.gif"));
			putValue(SHORT_DESCRIPTION, "Compila el texto en edición");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionCopiar extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.obtEditor().copy();
		}

		/////////////////////////////////////////////////////////
		public AccionCopiar()
		{
			super("Copiar", Util.getIcon("img/Copy24.gif"));
			putValue(SHORT_DESCRIPTION, "Copia el texto seleccionado");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionCortar extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.obtEditor().cut();
		}

		/////////////////////////////////////////////////////////
		public AccionCortar()
		{
			super("Cortar", Util.getIcon("img/Cut24.gif"));
			putValue(SHORT_DESCRIPTION, "Corta el texto seleccionado");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionDocumentacion extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.mostrarDocumentacion();
		}

		/////////////////////////////////////////////////////////
		public AccionDocumentacion ()
		{
			super("Documentación para desarrollo",
				Util.getIcon("img/api.gif")
			);
			putValue(SHORT_DESCRIPTION, 
				"Visualiza la documentación para desarrollo de programas");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionGuardar extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.guardar();
		}

		/////////////////////////////////////////////////////////
		public AccionGuardar()
		{
			super("Guardar", Util.getIcon("img/Save24.gif"));
			putValue(SHORT_DESCRIPTION, "Guarda en disco el archivo actual");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionGuardarComo extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.obtEditor().guardarComo();
		}

		/////////////////////////////////////////////////////////
		public AccionGuardarComo()
		{
			super("Guardar como...", Util.getIcon("img/SaveAs24.gif"));
			putValue(SHORT_DESCRIPTION, "Guarda el archivo actual con otro nombre");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionInterpreteInteractivo extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.mostrarInterpreteInteractivo();
		}

		/////////////////////////////////////////////////////////
		public AccionInterpreteInteractivo()
		{
			super("Intérprete Interactivo",
				Util.getIcon("img/ii.gif")
			);
			putValue(SHORT_DESCRIPTION, 
				"Despliega el Intérprete Interactivo");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionIrALinea extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.obtEditor().irALinea();
		}

		/////////////////////////////////////////////////////////
		public AccionIrALinea()
		{
			super("Ir a línea...");
			putValue(SHORT_DESCRIPTION, "Permite ir a una línea en particular");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionNoImplementada extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			System.out.println(" no implementado");
		}

		/////////////////////////////////////////////////////////
		public AccionNoImplementada(String name)
		{
			super(name);
			putValue(SHORT_DESCRIPTION, "Sin implementar aun");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionNuevo extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.obtEditor().nuevo();
		}

		/////////////////////////////////////////////////////////
		public AccionNuevo()
		{
			super("Nuevo", Util.getIcon("img/New24.gif"));
			putValue(SHORT_DESCRIPTION, "Abre un nuevo contenido en blanco");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionPegar extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.obtEditor().paste();
		}

		/////////////////////////////////////////////////////////
		public AccionPegar()
		{
			super("Pegar", Util.getIcon("img/Paste24.gif"));
			putValue(SHORT_DESCRIPTION, 
				"Inserta el último texto copiado o cortado");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionSalir extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.salir();
		}

		/////////////////////////////////////////////////////////
		public AccionSalir()
		{
			super("Salir");
			putValue(SHORT_DESCRIPTION, "Termina la sesión de trabajo");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionSeleccionarTodo extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.obtEditor().selectAll();
		}

		/////////////////////////////////////////////////////////
		public AccionSeleccionarTodo()
		{
			super("Seleccionar todo");
			putValue(SHORT_DESCRIPTION, "Selecciona todo el contenido en edición");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionDecrementarFont extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.changeFontSize(-1);
		}

		/////////////////////////////////////////////////////////
		public AccionDecrementarFont()
		{
			super("Decrementar tamaño letra");
			putValue(SHORT_DESCRIPTION, "Decrementa el tamaño de letra en la ventana de edición");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class AccionIncrementarFont extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			edi.changeFontSize(+1);
		}

		/////////////////////////////////////////////////////////
		public AccionIncrementarFont()
		{
			super("Incrementar tamaño letra");
			putValue(SHORT_DESCRIPTION, "Incrementa el tamaño de letra en la ventana de edición");
		}
	}
}