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
			super("A prop�sito de ...");
			putValue(SHORT_DESCRIPTION, "Muestra identificaci�n y versi�n del sistema");
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
			putValue(SHORT_DESCRIPTION, "Efect�a una verificaci�n del sistema");
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
			putValue(SHORT_DESCRIPTION, "Informaci�n general sobre el sistema");
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
			putValue(SHORT_DESCRIPTION, "Compila el texto en edici�n");
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
			super("Documentaci�n para desarrollo",
				Util.getIcon("img/api.gif")
			);
			putValue(SHORT_DESCRIPTION, 
				"Visualiza la documentaci�n para desarrollo de programas");
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
			super("Int�rprete Interactivo",
				Util.getIcon("img/ii.gif")
			);
			putValue(SHORT_DESCRIPTION, 
				"Despliega el Int�rprete Interactivo");
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
			super("Ir a l�nea...");
			putValue(SHORT_DESCRIPTION, "Permite ir a una l�nea en particular");
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
				"Inserta el �ltimo texto copiado o cortado");
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
			putValue(SHORT_DESCRIPTION, "Termina la sesi�n de trabajo");
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
			putValue(SHORT_DESCRIPTION, "Selecciona todo el contenido en edici�n");
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
			super("Decrementar tama�o letra");
			putValue(SHORT_DESCRIPTION, "Decrementa el tama�o de letra en la ventana de edici�n");
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
			super("Incrementar tama�o letra");
			putValue(SHORT_DESCRIPTION, "Incrementa el tama�o de letra en la ventana de edici�n");
		}
	}
}