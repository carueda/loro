package loro;

import java.io.IOException;


/////////////////////////////////////////////////////////////////////
/**
 * Interprete para acciones.
 *
 * Esta interface no es para ser implementada por el cliente.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public interface IInterprete
{
	//////////////////////////////////////////////////////////////
	/**
	 * Compila un texto.
	 *
	 * @param texto El codigo fuente a compilar.
	 *
	 * @throws CompilacionException Si se presenta algun error en 
	 *               la compilacion.
	 */
	public void compilar(String text)
	throws CompilacionException;

	//////////////////////////////////////////////////////////////
	/**
	 * Interpreta un texto.
	 *
	 * @param texto El codigo fuente a ejecutar.
	 *
	 * @return Una cadena descriptiva del resultado de la ejecucion.
	 */
	public String ejecutar(String text)
	throws AnalisisException;

	//////////////////////////////////////////////////////////////
	/**
	 * Procesa un texto. Esto significa interpretar el texto (con
	 * ejecucion) o bien solo compilarlo si el modo de interpretacion
	 * actual indica incluir la ejecucion.
	 *
	 * @param texto El codigo fuente a procesar.
	 *
	 * @return Una cadena descriptiva del resultado de la ejecucion.
	 *         null si el modo es solo compilacion.
	 */
	public String procesar(String text)
	throws AnalisisException;

	//////////////////////////////////////////////////////////////
	/**
	 * Dice si se incluye ejecucion en la interpretacion.
	 *
	 * @return true ssi se incluye ejecucion.
	 */
	public boolean getExecute();

	//////////////////////////////////////////////////////////////
	/**
	 * Pone el modo de interpretacion actual.
	 *
	 * @param execute true para incluir ejecucion.
	 */
	public void setExecute(boolean execute);

	//////////////////////////////////////////////////////////////
	/**
	 * Elimina una entrada de la tabla de simbolos.
	 *
	 * @param id    El identificador cuya entrada debe eliminarse.
	 *
	 * @return true sii la entrada ha sido eliminada.
	 */
	public boolean quitarID(String id);
	/////////////////////////////////////////////////////////////////
	/**
	 * Reinicia la tabla de simbolos a vacia.
	 */
	public void reiniciar();
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Hace terminar la ejecucion en curso.
	 */
	public void terminarExternamente();

	/////////////////////////////////////////////////////////////////
	/**
	 * Dice si hay disponibilidad de ejecucion paso-a-paso.
	 */
	public boolean isTraceable();

	///////////////////////////////////////////////////////////////////////
	/**
	 * Pone a todas las variables el estado de asignacion indicado.
	 */
	public void ponAsignado(boolean asignado);
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Pone el nivel de profundidad para efectos de mostrar la composicion
	 * de un objeto.
	 */
	public void ponNivelVerObjeto(int nivelVerObjeto);

	///////////////////////////////////////////////////////////////////////
	/**
	 * Pone la maxima longitud para efectos de mostrar la composicion
	 * de un arreglo
	 */
	public void ponLongitudVerArreglo(int longitudVerArreglo);
	
	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene la tabla de s�mbolos con que se creo este int�rprete.
	 *
	 * @return la tabla de s�mbolos con que se creo este int�rprete.
	 */
	public ISymbolTable getSymbolTable();

	///////////////////////////////////////////////////////////////////////
	/**
	 * Pone listener para atender meta-comandos.
	 */
	public void setMetaListener(IMetaListener ml);
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Interfaz para objetos interesados en ejecutar meta-comandos.
	 */
	public static interface IMetaListener
	{
		///////////////////////////////////////////////////////////////////////
		/**
		 * Obtiene info acerca de los meta-comandos atendidos.
		 */
		public String getInfo();
		
		///////////////////////////////////////////////////////////////////////
		/**
		 * Ejecuta un meta-comando.
		 * @return cadena de respuesta para el meta-comando.
		 *         null si el meta-comando no es entendido.
		 */
		public String execute(String meta);
	}
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el objeto Runnable asociado a este int�rprete.
	 */
	public IInteractiveInterpreter getInteractiveInterpreter();
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Interfaz para el objeto retornado por getInteractiveInterpreter.
	 * (No es para ser implementada por el cliente.)
	 */
	public static interface IInteractiveInterpreter extends Runnable
	{
		///////////////////////////////////////////////////////////////////////
		/**
		 * Hace terminar el m�todo run(), esto significa completar el comando
		 * en curso pero no continuar pidiendo m�s comandos.
		 */
		public void end();

		///////////////////////////////////////////////////////////////////////
		/**
		 * Pone el objeto para atender interacci�n.
		 */
		public void setManager(IManager mgr);
		
		///////////////////////////////////////////////////////////////////////
		/**
		 * Interfaz para objetos interesados en atender interacci�n.
		 */
		public static interface IManager
		{
			///////////////////////////////////////////////////////////////////////
			/**
			 * Llamado para pedir una orden de entrada.
			 */
			public String prompt()
			throws IOException;

			///////////////////////////////////////////////////////////////////////
			/**
			 * Llamado para imprimir un resultado.
			 */
			public void expression(String expr);

			///////////////////////////////////////////////////////////////////////
			/**
			 * Llamado para imprimir un mensaje de error.
			 */
			public void exception(String msg);
		}
	}
}