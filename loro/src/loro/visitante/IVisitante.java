package loro.visitante;

import loro.arbol.*;

//////////////////////////////////////////////////
/**
 * La interface que todo visitante debe implementar
 * para visitar un árbol sintáctico de Loro.
 *
 * @author Carlos Rueda
 * @version 2002-03-27
 */
public interface IVisitante
{
	public void visitar(NACadena n)
	throws VisitanteException;
	public void visitar(NAfirmacion n)
	throws VisitanteException;
	public void visitar(NAlgoritmo n)
	throws VisitanteException;
	public void visitar(NAsignacion n)
	throws VisitanteException;
	public void visitar(NCardinalidad n)
	throws VisitanteException;
	public void visitar(NCaso n)
	throws VisitanteException;
	public void visitar(NCiclo n)
	throws VisitanteException;
	public void visitar(NClase n)
	throws VisitanteException;
	public void visitar(NCondicion n)
	throws VisitanteException;
	public void visitar(NConstructor n)
	throws VisitanteException;
	public void visitar(NContinue n)
	throws VisitanteException;
	public void visitar(NConvertirTipo n)
	throws VisitanteException;
	public void visitar(NCorrDer n)
	throws VisitanteException;
	public void visitar(NCorrDerDer n)
	throws VisitanteException;
	public void visitar(NCorrIzq n)
	throws VisitanteException;
	public void visitar(NCrearArreglo n)
	throws VisitanteException;
	public void visitar(NExpresionArreglo n)
	throws VisitanteException;
	public void visitar(NCrearArregloTipoBase n)
	throws VisitanteException;
	public void visitar(NCrearObjeto n)
	throws VisitanteException;
	public void visitar(NCuantificado n)
	throws VisitanteException;
	public void visitar(NDecision n)
	throws VisitanteException;
	public void visitar(NDecisionSiNoSi n)
	throws VisitanteException;
	public void visitar(NDecisionMultiple n)
	throws VisitanteException;
	public void visitar(NDeclaracion n)
	throws VisitanteException;
	public void visitar(NDeclDesc n)
	throws VisitanteException;
	public void visitar(NDescripcion n)
	throws VisitanteException;
	public void visitar(NDiferente n)
	throws VisitanteException;
	public void visitar(NDivReal n)
	throws VisitanteException;
	public void visitar(NEquivalencia n)
	throws VisitanteException;
	public void visitar(NEsInstanciaDe n)
	throws VisitanteException;
	public void visitar(NEspecificacion n)
	throws VisitanteException;
	public void visitar(NFuente n)
	throws VisitanteException;
	public void visitar(NId n)
	throws VisitanteException;
	public void visitar(NIgual n)
	throws VisitanteException;
	public void visitar(NImplicacion n)
	throws VisitanteException;
	public void visitar(NInterface n)
	throws VisitanteException;
	public void visitar(NInvocacion n)
	throws VisitanteException;
	public void visitar(NLiteralBooleano n)
	throws VisitanteException;
	public void visitar(NLiteralCadena n)
	throws VisitanteException;
	public void visitar(NLiteralCaracter n)
	throws VisitanteException;
	public void visitar(NLiteralEntero n)
	throws VisitanteException;
	public void visitar(NLiteralNulo n)
	throws VisitanteException;
	public void visitar(NLiteralReal n)
	throws VisitanteException;
	public void visitar(NMas n)
	throws VisitanteException;
	public void visitar(NMayor n)
	throws VisitanteException;
	public void visitar(NMayorIgual n)
	throws VisitanteException;
	public void visitar(NMenor n)
	throws VisitanteException;
	public void visitar(NMenorIgual n)
	throws VisitanteException;
	public void visitar(NMenos n)
	throws VisitanteException;
	public void visitar(NMientras n)
	throws VisitanteException;
	public void visitar(NMod n)
	throws VisitanteException;
	public void visitar(NNeg n)
	throws VisitanteException;
	public void visitar(NNo n)
	throws VisitanteException;
	public void visitar(NNoBit n)
	throws VisitanteException;
	public void visitar(NNombre n)
	throws VisitanteException;
	public void visitar(NO n)
	throws VisitanteException;
	public void visitar(NOArit n)
	throws VisitanteException;
	public void visitar(NOExc n)
	throws VisitanteException;
	public void visitar(NPaquete n)
	throws VisitanteException;
	public void visitar(NPara n)
	throws VisitanteException;
	public void visitar(NPlus n)
	throws VisitanteException;
	public void visitar(NPor n)
	throws VisitanteException;
	public void visitar(NRepita n)
	throws VisitanteException;
	public void visitar(NRetorne n)
	throws VisitanteException;
	public void visitar(NSubId n)
	throws VisitanteException;
	public void visitar(NSubindexacion n)
	throws VisitanteException;
	public void visitar(NTermine n)
	throws VisitanteException;
	public void visitar(NUtiliza n)
	throws VisitanteException;
	public void visitar(NY n)
	throws VisitanteException;
	public void visitar(NYArit n)
	throws VisitanteException;

	public void visitar(NTipoArreglo n)
	throws VisitanteException;

	public void visitar(NTipoBooleano n)
	throws VisitanteException;

	public void visitar(NTipoCadena n)
	throws VisitanteException;

	public void visitar(NTipoCaracter n)
	throws VisitanteException;

	public void visitar(NTipoClase n)
	throws VisitanteException;

	public void visitar(NTipoEntero n)
	throws VisitanteException;

	public void visitar(NTipoEspecificacion n)
	throws VisitanteException;

	public void visitar(NTipoReal n)
	throws VisitanteException;

	public void visitar(NImplementa n)
	throws VisitanteException;

	public void visitar(NTipoInterface n)
	throws VisitanteException;
}