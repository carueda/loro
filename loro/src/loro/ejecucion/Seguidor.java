package loro.ejecucion;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.arbol.*;

///////////////////////////////////////////////////////////
/**
 * Un visitante que notifica la entrada/salida de cada nodo
 * visitado.
 *
 * @author Carlos Rueda
 */
public abstract class Seguidor extends LoroEjecutor
{
	////////////////////////////////////////////////////////////
	/**
	 * Método llamado justo antes de visitar el nodo dado.
	 */
	protected abstract void _enter(INodo n)
	throws VisitanteException;
	
	////////////////////////////////////////////////////////////
	/**
	 * Método llamado justo al terminar la visita al nodo dado.
	 */
	protected abstract void _exit(INodo n)
	throws VisitanteException;
	
	
	public void visitar(NACadena n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
	}
	public void visitar(NAfirmacion n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NAlgoritmo n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NAsignacion n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NAtrape n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NCardinalidad n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NCaso n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NCiclo n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NClase n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NCondicion n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NConstructor n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NContinue n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NConvertirTipo n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NCorrDer n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NCorrDerDer n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NCorrIzq n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NCrearArreglo n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NExpresionArreglo n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NCrearArregloTipoBase n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NCrearObjeto n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NCuantificado n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NDecision n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NDecisionSiNoSi n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NDecisionMultiple n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NDeclaracion n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NDeclDesc n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NDescripcion n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NDiferente n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NDivReal n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NEquivalencia n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NEsInstanciaDe n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NEspecificacion n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NEste n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NFuente n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NId n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NIgual n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NImplicacion n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NIntente n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NInterface n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NInvocacion n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NLance n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NLiteralBooleano n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NLiteralCadena n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NLiteralCaracter n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NLiteralEntero n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NLiteralNulo n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NLiteralReal n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NMas n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NMayor n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NMayorIgual n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NMenor n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NMenorIgual n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NMenos n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NMientras n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NMod n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NNeg n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NNo n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NNoBit n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NNombre n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NO n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NOArit n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NOExc n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NPaquete n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}

	public void visitar(NForEach n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
	}
	
	public void visitar(NPara n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NPlus n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NPor n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NRepita n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NRetorne n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NSubId n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NSubindexacion n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NTermine n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NUtiliza n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NY n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
	public void visitar(NYArit n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}

	public void visitar(NTipoArreglo n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}

	public void visitar(NTipoBooleano n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}

	public void visitar(NTipoCadena n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}

	public void visitar(NTipoCaracter n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}

	public void visitar(NTipoClase n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}

	public void visitar(NTipoEntero n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}

	public void visitar(NTipoEspecificacion n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}

	public void visitar(NTipoReal n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}

	public void visitar(NImplementa n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}

	public void visitar(NTipoInterface n)
	throws VisitanteException
	{
		_enter(n);
		super.visitar(n);
		_exit(n);
		}
}

