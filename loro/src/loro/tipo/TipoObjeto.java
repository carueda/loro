package loro.tipo;

import loro.Loro;
import loro.util.ManejadorUnidades;
import loro.compilacion.ClaseNoEncontradaException;

//////////////////////////////////////////////////////////////////
/**
 * Superclase de todos los tipos que representan objetos en el
 * lenguaje Loro.
 */
abstract class TipoObjeto extends Tipo
{
	//////////////////////////////////////////////////////////////////////
	public boolean esObjeto()
	{
		return true;
	}

	//////////////////////////////////////////////////////////
	/**
	 * Si el tipo objetivo es Clase y corresponde a la raiz, retorna true;
	 * si no, se retorna super.esConvertibleA(t).
	 */
	public boolean esConvertibleA(Tipo t)
	{
		if ( t.esClase() )
		{
			TipoClase tc = (TipoClase) t;
			if ( Loro.getLanguageInfo().getRootClassName().equals(tc.obtNombreCompletoString()) )
				return true;
		}
		return super.esConvertibleA(t);
	}
	
}
