package loro.tipo;

import loro.ijava.LAlgoritmo;
import loro.java.LAlgoritmoImp;
import loro.arbol.NAlgoritmo;


//////////////////////////////////////////////////////////////////
/**
 * Tipo especificacion.
 */
public class TipoEspecificacion extends TipoUnidad
{
	////////////////////////////////////////////
	/**
	 * Dice si este tipo es algoritmo.
	 */
	public boolean esAlgoritmo()
	{
		return true;
	}

	//////////////////////////////////////////////////////////
	/**
	 * Dice si a este tipo se le puede asignar el valor dado.
	 */
	public boolean esAsignableValor(Object val)
	{
		if ( val == null )
		{
			return true;
		}

		if ( nombre == null )
		{
			// este es generico.
			return true;
		}

		if ( val instanceof NAlgoritmo )
		{
			NAlgoritmo val_alg = (NAlgoritmo) val;

			return val_alg.esParaEspecificacion(nombre);
		}

		if ( val instanceof LAlgoritmo )
		{
			if ( val instanceof LAlgoritmoImp )
			{
				NAlgoritmo val_alg = ((LAlgoritmoImp) val).obtAlgoritmo();

				return val_alg.esParaEspecificacion(nombre);
			}

			return true;	// PENDIENTE: Faltan funcionalidades para LAlgoritmo
		}

		throw new RuntimeException("Uy! TipoEspecificacion.esAsignableValor con " +val.getClass());
	}


	//////////////////////////////////////////////////////////////////////
	/**
	 * Dice si a una variable de este tipo se le puede asignar un valor
	 * del tipo dado.
	 */
	public boolean esAsignable(Tipo t)
	{
		if ( t.esNulo() )
		{
			return true;
		}
		
		if ( !(t instanceof TipoEspecificacion) )
		{
			return false;
		}

		if ( nombre == null )
		{
			return true;
		}

		return igual(t);
	}
	//////////////////////////////////////////////////////////////
	public boolean esInvocable()
	{
		return true;
	}
	//////////////////////////////////////////////////////////////
	/**
	 * Dice si está pendiente de algoritmo.
	 */
	public boolean estaPendiente()
	{
		return nombre == null;
	}
	//////////////////////////////////////////////////////////////
	public boolean igual(Tipo t)
	{
		if (! (t instanceof TipoEspecificacion) )
			return false;

		TipoEspecificacion ta = (TipoEspecificacion) t;

		String[] nom_mi_espec = obtNombreEspecificacion();
		String[] nom_tu_espec = ta.obtNombreEspecificacion();

		if ( nom_mi_espec == null || nom_tu_espec == null )
		{
			return true;
		}

		String mi_espec = loro.util.Util.obtStringRuta(nom_mi_espec);
		String tu_espec = loro.util.Util.obtStringRuta(nom_tu_espec);

		return mi_espec.equals(tu_espec);
	}


	/////////////////////////////////////////////////////////////////////
	public String toString()
	{
		if ( nombre != null )
		{
			return "algoritmo para " +loro.util.Util.obtStringRuta(nombre);
		}
		else
		{
			return "algoritmo";
		}
	}

	//////////////////////////////////////////////////////////////
	public String[] obtNombreEspecificacion()
	{
		return nombre;
	}
	


	//////////////////////////////////////////////////////////////
	TipoEspecificacion(String[] nombre)
	{
		super(nombre);
	}

	/**
	 * La interface a la que pertenece esta operacion.
	 * null si la especificacion es aislada.
	 */
	TipoInterface ti;

	//////////////////////////////////////////////////////////////
	/**
	 * Crea un tipo especificacion para un operacion perteneciente
	 * a un interface.
	 *
	 * @param ti El tipo de la interface a la cual pertenecera esta operacion.
	 * @parm nombre Nombre de la operacion.
	 */
	TipoEspecificacion(TipoInterface ti, String nombre)
	{
		this(new String[]{ nombre });
		this.ti = ti;
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene el tipo interface del que soy miembro.
	 * null en caso que este sea una especificacion aislada.
	 */
	public TipoInterface obtInterface()
	{
		return ti;
	}
}