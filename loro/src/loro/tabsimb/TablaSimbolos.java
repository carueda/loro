package loro.tabsimb;

import loro.Loro.Str;
import loro.ISymbolTable;
import loro.util.Util;
import loro.util.UtilValor;

import java.util.Vector;
import java.util.Stack;

//////////////////////////////////////////////////////////////////////////
/**
 * Esta clase representa una tabla de símbolos.
 * Cada elemento de esta tabla es una EntradaTabla.
 *
 * @version $Id$
 */
public class TablaSimbolos implements ISymbolTable
{
	/** Guardamos las entradas en un Vector. */
	private Vector tabla;

	//////////////////////////////////////////////////////////////
	/**
	 * Crea una tabla de símbolos.
	 */
	public TablaSimbolos()
	{
		tabla = new Vector();
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * Busca un símbolo en toda esta tabla de símbolos empezando
	 * desde el tope (lo ultimo insertado).
	 * @return La entrada en donde se encuentra el símbolo.
	 * null si no se encuentra.
	 */
	public EntradaTabla buscar(String simbolo)
	{
		return buscar(simbolo, 0);
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * Agrega una entrada en esta tabla de símbolos.
	 * Se chequea que el simbolo no se encuentre ya declarado desde
	 * el tope hasta la marcaAlgoritmo.
	 */
	public void insertar(EntradaTabla et)
	throws TSException
	{
		String id = et.obtId();
		EntradaTabla prev = buscar(id);
		if ( prev != null )
		{
			throw new TSException(
				Str.get("error.1_id_already_defined", id)
			);
		}
		tabla.addElement(et);
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 */
	public void irAMarca(int marca)
	{
		int size = tabla.size();
		if ( marca < size )
			tabla.setSize(marca);
	}

	//////////////////////////////////////////////////////////////
	/**
	 */
	public int marcar()
	{
		int size = tabla.size();
		return size;
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 */
	public int obtNumEntradas()
	{
		return tabla.size();
	}
	
	//////////////////////////////////////////////////////////////
	public String[] getVariableNames()
	{
		String[] vars = new String[tabla.size()];
		for ( int i = 0; i < vars.length; i++ )
		{
			EntradaTabla entr = (EntradaTabla) tabla.get(i);
			vars[i] = entr.obtId();
		}
		return vars;
	}
	//////////////////////////////////////////////////////////////
	/**
	 */
	public String getTypeString(String id)
	{
		EntradaTabla et = buscar(id);
		if ( et == null )
			return null;
		return et.obtTipo().toString();
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene el valor de un identificador. Este identificador se
	 * busca desde el tope de la tabla hasta la marcaAlgoritmo.
	 * Si no se encuentra, se retorna null.
	 */
	public Object obtValor(String id)
	{
		EntradaTabla et = buscar(id);
		if ( et == null )
			return null;
		return et.obtValor();
	}
	
	//////////////////////////////////////////////////////////////
	public String getValueString(String id)
	{
		EntradaTabla et = buscar(id);
		if ( et == null )
			return null;
		
		return et.obtAsignado() 
				  ? UtilValor.valorComillasDeExpresion(et.obtTipo(), et.obtValor())
				  : "?"
		;
	}

	//////////////////////////////////////////////////////////////
	/**
	 */
	public void ponValor(String id, Object val)
	{
		EntradaTabla et = buscar(id);
		if ( et != null )
			et.ponValor(val);
	}
	//////////////////////////////////////////////////////
	/**
	 * Pone el estado de asignacion.
	 */
	public void ponAsignado(String id, boolean asignado)
	{
		EntradaTabla et = buscar(id);
		if ( et != null )
		{
			et.ponAsignado(asignado);
		}
	}
	//////////////////////////////////////////////////////////////
	/**
	 * Deja esta tabla vacía.
	 */
	public void reiniciar()
	{
		tabla.setSize(0);
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * Dice si el identificador esta asignado.
	 */
	public boolean obtAsignado(String id)
	{
		EntradaTabla et = buscar(id);
		if ( et == null )
			return false;
		return et.obtAsignado();
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Elimina una entrada.
	 * Servicio agregado para el Interprete Iteractivo.
	 *
	 * 2001-10-25
	 *
	 * @param id    El identificador cuya entrada debe eliminarse.
	 *
	 * @return true sii la entrada ha sido eliminada.
	 */
	public boolean borrar(String id)
	{
		EntradaTabla et = buscar(id);
		return et != null && et.equals(tabla.remove(tabla.lastIndexOf(et)));
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Busca un símbolo en esta tabla de símbolos desde el tope (lo
	 * ultimo insertado) hasta una marca inclusive.
	 *
	 * @return La entrada en donde se encuentra el símbolo.
	 * null si no se encuentra.
	 */
	private EntradaTabla buscar(String simbolo, int marca)
	{
		for ( int i = tabla.size() -1; i >= marca; i-- )
		{
			EntradaTabla entr = (EntradaTabla) tabla.get(i);
			if ( entr.obtId().equals(simbolo) )
				return entr;
		}
		return null;
	}

	///////////////////////////////////////////////////////////////////////
	/**
	 * Pone a todas las variables el estado de asignacion indicado.
	 */
	public void ponAsignado(boolean asignado)
	{
		for ( int i = 0; i < tabla.size(); i++ )
		{
			EntradaTabla entr = (EntradaTabla) tabla.elementAt(i);
			entr.ponAsignado(asignado);
		}
		
	}


	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene una version cadena del contenido de esta tabla.
	 * La cadena resultante no finaliza con cambio de linea.
	 */
	public String toString()
	{
		int size = tabla.size();
		if ( size == 0 )
			return Str.get("ns.empty");

		// obtenga maximos anchos para las columnas:

		// anchos para las columnas:
		String[] header = new String[] { 
			Str.get("ns.id"), Str.get("ns.type"), Str.get("ns.value") 
		};
		int[] lens = new int[header.length];
		for ( int k = 0; k < lens.length; k++ )
			lens[k] = header[k].length();

		String[] cols = new String[header.length];

		for(int i = 0; i < size; i++) {
			EntradaTabla et = (EntradaTabla) tabla.elementAt(i);
			cols[0] = et.obtId().toString(); 
			cols[1] = et.obtTipo().toString(); 
			cols[2] = et.obtAsignado() 
			          ? UtilValor.valorComillasDeExpresion(et.obtTipo(), et.obtValor())
			          : "?"
			;

			for ( int k = 0; k < cols.length; k++ ) {
				int len = cols[k].length();
				if ( lens[k] < len )
					lens[k] = len;
			}
		}

		StringBuffer sb = new StringBuffer();

		// encabezado:
		for ( int k = 0; k < header.length; k++ ) {
			Util.format(header[k], lens[k], ' ', sb);
			sb.append("  ");
		}
		
		// rayitas
		sb.append("\n");
		for ( int k = 0; k < header.length; k++ ) {
			Util.format("", lens[k], '-', sb);
			sb.append("  ");
		}

		// datos
		for(int i = 0; i < size; i++) {
			EntradaTabla et = (EntradaTabla) tabla.elementAt(i);
			cols[0] = et.obtId().toString(); 
			cols[1] = et.obtTipo().toString(); 
			cols[2] = et.obtAsignado() 
			          ? UtilValor.valorComillasDeExpresion(et.obtTipo(), et.obtValor())
			          : "?"
			;

			sb.append("\n");
			for ( int k = 0; k < cols.length; k++ ) {
				Util.format(cols[k], lens[k], ' ', sb);
				sb.append("  ");
			}
		}
		
		return sb.toString();
	}

}