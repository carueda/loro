package loro.arbol;

import loro.IFuente;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

////////////////////////////////////////////////
/**
 * Instrucción 'utiliza'.
 */
public class NUtiliza extends Nodo implements IFuente.IUtiliza
{
	String que;
	TNombre nombre;

	/////////////////////////////////////////////////////////
	/**
	 * Crea un NUtiliza.
	 */
	public NUtiliza(Rango rango, String que, TNombre nom)
	{
		super(rango);
		this.que = que;
		this.nombre = nom;
	}

	/////////////////////////////////////////////////////////
	public TNombre obtNombre()
	{
		return nombre;
	}

	/////////////////////////////////////////////////////////
	public String getName()
	{
		return nombre.obtCadena();
	}

	/////////////////////////////////////////////////////////
	public String obtQue()
	{
		return que;
	}
	
	////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

}