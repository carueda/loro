package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 * Expresión para crear un objeto.
 */
public class NCrearObjeto extends NExpresion
{
	/** El nombre de la clase. */
	TNombre nom;

	/** Los argumentos para la creación. */
	NExpresion[] args;

	/** La clase correspondiente. */
	NClase clase;

	/** El constructor correspondiente. */
	NConstructor constructor;


	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
	public NExpresion[] obtArgumentos()
	{
		return args;
	}
	public NClase obtClase()
	{
		return clase;
	}
	public NConstructor obtConstructor()
	{
		return constructor;
	}


	public void ponClase(NClase c)
	{
		clase = c;
	}
	public void ponConstructor(NConstructor c)
	{
		constructor = c;
	}

	public NCrearObjeto(Rango rango, TNombre tnom, NExpresion[] args)
	{
		super(rango);
		this.nom = tnom;
		this.args = args;

		// se obtienen en chequeo
		clase = null;
		constructor = null;
	}

	public TNombre obtNombreClase()
	{
		return nom;
	}
}