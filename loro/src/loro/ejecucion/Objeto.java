package loro.ejecucion;

import loro.ijava.*;
import loro.java.LClaseImp;
import loro.arbol.NClase;

import java.util.Hashtable;
import java.util.Enumeration;

import loro.arbol.NInterface;
import loro.arbol.NAlgoritmo;
import loro.tipo.TipoInterface;

//////////////////////////////////////////////////////////
/**
 * Representa un objeto Loro en tiempo de ejecución.
 */
public class Objeto implements LObjeto
{
	/** La clase de la que es instancia este objeto. */
	NClase clase;

	/** La clase de interface para Java. */
	LClase lclase;

	/** Tabla para guardar valores de los atributos. */
	Hashtable ht;

	/** El objeto Java asociado para el cliente. */
	Object objetoJava;

	//////////////////////////////////////////////////////////
	/**
	 * Crea un Objeto Loro.
	 */
	public Objeto(NClase clase) 
	{
		super();
		this.clase = clase;
		lclase = null;		// se obtiene por demanda
		ht = new Hashtable();
	}
	//////////////////////////////////////////////////////////
	/**
	 * Obtiene la clase de interface para Java de este objeto.
	 */
	public LClase obtClase()
	{
		if ( lclase == null )
			lclase = new LClaseImp(clase);
			
		return lclase;
	}
	//////////////////////////////////////////////////////////
	/**
	 * Obtiene la clase (verdadera) de este objeto.
	 */
	public NClase obtNClase()
	{
		return clase;
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene el objeto java asociado a este objeto Loro.
	 */
	public Object obtObjetoJava()
	throws LException
	{
		return objetoJava;
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene el valor de un atributo.
	 */
	public Object obtValor(String atr)
	throws LException
	{
		return ht.get(atr);
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Pone el objeto java asociado a este objeto Loro.
	 */
	public void ponObjetoJava(Object objetoJava)
	throws LException
	{
		this.objetoJava = objetoJava;
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Pone el valor para un atributo.
	 */
	public void ponValor(String atr, Object valor)
	throws LException
	{
		if ( valor != null )
			ht.put(atr, valor);
		else
			ht.remove(atr);
	}
	//////////////////////////////////////////////////////////
	/**
	 * Version string de este objeto.
	 */
	public String toString() 
	{
		return clase.toString();
	}

	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene el metodo de nombre dado
	 */
	public Object obtMetodo(String nom)
	throws LException
	{
		NAlgoritmo[] mets = clase.obtMetodosDeclarados();
		for (int i = 0; i < mets.length; i++)
		{
			NAlgoritmo met = mets[i];
			if ( met.obtNombreCompletoCadena().equals(nom) )
			{
				return met;
			}
		}
		
		return null;
	}
}