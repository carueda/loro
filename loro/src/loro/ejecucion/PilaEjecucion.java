package loro.ejecucion;

import loro.Loro.Str;
import loro.tabsimb.*;
import loro.Rango;
import loro.arbol.*;

import java.io.PrintWriter;
import java.util.Stack;


/////////////////////////////////////////////////////////////////
/**
 * La pila de ejecucion de un programa en Loro.
 *
 * @author Carlos Rueda
 */
public final class PilaEjecucion extends Stack
{
	/**
	 * Máximo número de niveles desde el tope a mostrar.
	 * Arbitrariamente inicializado aqui en 21.
	 * (muy util por ejemplo para StackOverflow).
	 */
	int maxNivelesMostrar = 21;


	/////////////////////////////////////////////////////////////
	/**
	 * Elemento de la pila de ejecucion.
	 */
	static class MarcoActivacion
	{
		/** El nodo en curso. */
		INodo curr_node;
		
		/** La unidad de esta activacion. */
		NUnidad uni;

		/** La tabla de simbolos. */
		TablaSimbolos tabSimb;

		/** Crea un marco-elemento de la pila de ejecucion. */
		MarcoActivacion(NUnidad uni, TablaSimbolos tabSimb)
		{
			this.uni = uni;
			this.curr_node = uni;
			this.tabSimb = tabSimb;
		}
	}
	
	/////////////////////////////////////////////////////////////
	/**
	 * Crea un pila de ejecucion.
	 */
	public PilaEjecucion()
	{
		super();
	}

	/////////////////////////////////////////////////////////////
	/**
	 * Muestra el estado de esta pila por salida estandar.
	 */
	public final void mostrar()
	{
		mostrar(new PrintWriter(System.out, true));
	}

	/////////////////////////////////////////////////////////////
	/**
	 * Muestra el estado de esta pila por una salida dada.
	 */
	public final void mostrar(PrintWriter pw)
	{
		int size = size();
		int count = size;

		if ( count > maxNivelesMostrar ) {
			count = maxNivelesMostrar;
		}

		String at = Str.get("rt.stack_at");
		
		StringBuffer sb = new StringBuffer();

		for (int i = size - 1; i >= 0 && count >= 0; i--, count-- )
		{
			MarcoActivacion m = (MarcoActivacion) elementAt(i);
			String fuente = m.uni.obtNombreFuente();

			if ( fuente != null ) {
				// deje solo nombre del archivo fuente (sin ruta):
				fuente = fuente.replace('\\', '/');
				int index = fuente.lastIndexOf('/');
				if ( index >= 0 )
				{
					fuente = fuente.substring(index + 1);
				}
			}
			else {
				fuente = "<?>";
			}

			int iniLin = m.curr_node.obtRango().obtIniLin();
			int iniCol = m.curr_node.obtRango().obtIniCol();
			String pos_msg = iniLin+ "," +iniCol;
			sb.append("  " +at+ " " +m.uni+ " (" +fuente+ ":" +pos_msg+ ")\n");
		}

		pw.print(sb.toString());
		pw.flush();
	}

	/////////////////////////////////////////////////////////////
	/**
	 * Obtiene la tabla de simbolos del tope.
	 */
	public final TablaSimbolos obtTablaSimbolos()
	{
		MarcoActivacion m = (MarcoActivacion) peek();
		return m != null ? m.tabSimb : null;
	}

	/////////////////////////////////////////////////////////////
	/**
	 * Obtiene la unidad del tope.
	 */
	public final NUnidad obtUnidad()
	{
		MarcoActivacion m = (MarcoActivacion) peek();
		return m != null ? m.uni : null;
	}

	/////////////////////////////////////////////////////////////
	/**
	 * Mete un algoritmo a la pila de ejecucion.
	 * Se crea una nueva tabla de simbolos.
	 */
	public final void pushAlgoritmo(NAlgoritmo uni)
	{
		push(new MarcoActivacion(uni, new TablaSimbolos()));
	}

	/////////////////////////////////////////////////////////////
	/**
	 * Mete una clase a la pila de ejecucion.
	 * Se crea una nueva tabla de simbolos.
	 */
	public final void pushClase(NClase uni)
	{
		push(new MarcoActivacion(uni, new TablaSimbolos()));
	}
	
	/////////////////////////////////////////////////////////////
	/**
	 * Mete una especificacion a la pila de ejecucion. En este caso
	 * no se crea una nueva tabla de simbolos, sino que se toma
	 * la tabla de simbolos actual (que debe ser de un algoritmo).
	 */
	public final void pushEspecificacion(NEspecificacion uni)
	{
		TablaSimbolos tabSimb = obtTablaSimbolos();
		if ( tabSimb == null )
		{
			// bueno, aqui la creamos, pero esta situacion NO deberia darse!
			tabSimb = new TablaSimbolos();
		}
		push(new MarcoActivacion(uni, tabSimb));
	}
	
	/////////////////////////////////////////////////////////////
	/**
	 * Pone en cero el tamano de esta pila.
	 */
	public final void reset()
	{
		setSize(0);
	}

	/////////////////////////////////////////////////////////////
	/**
	 * Actualiza info sobre posicion en el tope de esta pila.
	 * Si esta pila esta vacia, no se hace nada.
	 */
	public final void actualizarTope(INodo u)
	{
		if ( ! isEmpty() )
		{
			MarcoActivacion m = (MarcoActivacion) peek();
			m.curr_node = u;
		}
	}

	/////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nodo en el tope de esta pila.
	 * Si esta pila esta vacia, se retorna null.
	 */
	public final INodo peekNode()
	{
		if ( ! isEmpty() )
		{
			MarcoActivacion m = (MarcoActivacion) peek();
			return m.curr_node;
		}
		return null;
	}

}