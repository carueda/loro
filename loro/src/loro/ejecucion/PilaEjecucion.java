package loro.ejecucion;


import loro.arbol.*;



import java.io.PrintWriter;
import java.util.Stack;

import loro.tabsimb.*;

import loro.Rango;

/////////////////////////////////////////////////////////////////
/**
 * La pila de ejecucion de un programa en Loro.
 *
 * @author Carlos Rueda
 * @version 0.1 2000-06-14
 * @version 0.2 2001-09-30 - ahora es ``final class''
 */
public final class PilaEjecucion extends Stack
{
	/**
	 * M�ximo n�mero de niveles desde el tope mostrar.
	 * Subjetivamente inicializado aqui en 21.
	 * (muy util por ejemplo para StackOverflow).
	 */
	int maxNivelesMostrar = 21;


	/////////////////////////////////////////////////////////////
	/**
	 * Elemento de la pila de ejecucion.
	 */
	static class MarcoActivacion
	{
		/** Posicion actual dentro del fuente correspondiente. */
		int iniLin, iniCol;

		/** La unidad de esta activacion. */
		NUnidad uni;

		/** La tabla de simbolos. */
		TablaSimbolos tabSimb;

		/** Crea un marco-elemento de la pila de ejecucion. */
		MarcoActivacion(NUnidad uni, TablaSimbolos tabSimb)
		{
			this.uni = uni;
			this.iniLin = uni.obtRango().obtIniLin();
			this.iniCol = uni.obtRango().obtIniCol();

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

		if ( count > maxNivelesMostrar )
		{
			count = maxNivelesMostrar;
		}

		StringBuffer sb = new StringBuffer();

		for (int i = size - 1; i >= 0 && count >= 0; i--, count-- )
		{
			MarcoActivacion m = (MarcoActivacion) elementAt(i);
			String fuente = m.uni.obtNombreFuente();

			if ( fuente != null )
			{
				// deje solo nombre del archivo fuente (sin ruta):
				fuente = fuente.replace('\\', '/');
				int index = fuente.lastIndexOf('/');
				if ( index >= 0 )
				{
					fuente = fuente.substring(index + 1);
				}
			}
			else
			{
				fuente = "<fuente?>";
			}

			String pos_msg = m.iniLin+ "," +m.iniCol;
			
			sb.append("  en " +m.uni+ " (" +fuente+ ":" +pos_msg+ ")\n");
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
		if ( m != null )
		{
			return m.tabSimb;
		}
		return null;
	}

	/////////////////////////////////////////////////////////////
	/**
	 * Obtiene la unidad del tope.
	 */
	public final NUnidad obtUnidad()
	{
		MarcoActivacion m = (MarcoActivacion) peek();
		if ( m != null )
		{
			return m.uni;
		}
		return null;
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
	public final void actualizarTope(IUbicable u)
	{
		if ( ! isEmpty() )
		{
			MarcoActivacion m = (MarcoActivacion) peek();
			m.iniLin = u.obtRango().obtIniLin();
			m.iniCol = u.obtRango().obtIniCol();
		}
	}
}