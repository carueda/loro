package loro.visitante;

import loro.Loro.Str;
import loro.arbol.*;

////////////////////////////////////////////////////////////
/**
 * Obtiene una descripción textual de tipo para cada nodo.
 * 
 * Actualmente esta descripción es general para cada nodo:
 * no se tienen aún en cuenta posibles precisiones para dar
 * más claridad en algunos casos (por ejemplo en un nodo
 * NMas convendría distinguir entre suma aritmética o
 * concatenación de cadenas).
 *
 * Internamente se utiliza un visitante para aprovechar su
 * mecanismo de 'double-dispatch'.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class Descriptor
{
	/** 
	 * Permite el mecanismo 'double-dispatch' para describir
	 * cada tipo de nodo posible.
	 */
	private static VisitanteDescriptor v = new VisitanteDescriptor();
	
	/** Aqui se guarda el resultado de la visita a un nodo. */
	private static String description;
	
	////////////////////////////////////////////////////////////
	/**
	 * Obtiene una descripción concisa del tipo del nodo dado.
	 *
	 * @param n   El nodo a describir.
	 *
	 * @return La descripción concisa del tipo del nodo dado.
	 */
	public static String getDescription(INodo node)
	{
		description = "?PENDING?";
		try
		{
			node.aceptar(v);
		}
		catch(VisitanteException ex)
		{
			description = "!:" +ex.getMessage();
		}
		return description;
	}
	
	////////////////////////////////////////////////////////////
	/**
	 * Este visitante obtiene una descripción textual de tipo 
	 * para cada nodo.
	 */
	static class VisitanteDescriptor implements IVisitante
	{
		public void visitar(NACadena n)
		throws VisitanteException
		{
			description = Str.get("node.to_string");
		}

		public void visitar(NAfirmacion n)
		throws VisitanteException
		{
			description = Str.get("node.assert");
		}
		
		public void visitar(NAlgoritmo n)
		throws VisitanteException
		{
			description = Str.get("node.algorithm");
		}
		
		public void visitar(NAsignacion n)
		throws VisitanteException
		{
			description = Str.get("node.assignment");
		}
		
		public void visitar(NAtrape n)
		throws VisitanteException
		{
			description = Str.get("node.catch");
		}
		
		public void visitar(NCardinalidad n)
		throws VisitanteException
		{
			description = Str.get("node.cardinality");
		}
		
		public void visitar(NCaso n)
		throws VisitanteException
		{
			description = Str.get("node.case");
		}
		
		public void visitar(NCiclo n)
		throws VisitanteException
		{
			description = Str.get("node.loop");
		}
		
		public void visitar(NClase n)
		throws VisitanteException
		{
			description = Str.get("node.class");
		}
		
		public void visitar(NCondicion n)
		throws VisitanteException
		{
			description = Str.get("node.cond");
		}
		
		public void visitar(NConstructor n)
		throws VisitanteException
		{
			description = Str.get("node.constructor");
		}
		
		public void visitar(NContinue n)
		throws VisitanteException
		{
			description = Str.get("node.continue");
		}
		
		public void visitar(NConvertirTipo n)
		throws VisitanteException
		{
			description = Str.get("node.type_conversion");
		}
		
		public void visitar(NCorrDer n)
		throws VisitanteException
		{
			description = Str.get("node.right_shift");
		}
		
		public void visitar(NCorrDerDer n)
		throws VisitanteException
		{
			description = Str.get("node.right_right_shift");
		}
		
		public void visitar(NCorrIzq n)
		throws VisitanteException
		{
			description = Str.get("node.left_shift");
		}
		
		public void visitar(NCrearArreglo n)
		throws VisitanteException
		{
			description = Str.get("node.new_array");
		}

		public void visitar(NExpresionArreglo n)
		throws VisitanteException
		{
			description = Str.get("node.array_expression");
		}

		public void visitar(NCrearArregloTipoBase n)
		throws VisitanteException
		{
			description = Str.get("node.new_array_fragment");
		}

		public void visitar(NCrearObjeto n)
		throws VisitanteException
		{
			description = Str.get("node.new_instance");
		}

		public void visitar(NCuantificado n)
		throws VisitanteException
		{
			description = Str.get("node.quantified");
		}

		public void visitar(NDecision n)
		throws VisitanteException
		{
			description = Str.get("node.decision");
		}

		public void visitar(NDecisionSiNoSi n)
		throws VisitanteException
		{
			description = Str.get("node.else_if");

		}
		public void visitar(NDecisionMultiple n)
		throws VisitanteException
		{
			description = Str.get("node.switch");
		}
		
		public void visitar(NDeclaracion n)
		throws VisitanteException
		{
			description = Str.get("node.declaration");
		}
		
		public void visitar(NDeclDesc n)
		throws VisitanteException
		{
			description = Str.get("node.declaration_description");
		}
		
		public void visitar(NDescripcion n)
		throws VisitanteException
		{
			description = Str.get("node.doc_text");
		}
		
		public void visitar(NDiferente n)
		throws VisitanteException
		{
			description = Str.get("node.not_equal");
		}
		
		public void visitar(NDivReal n)
		throws VisitanteException
		{
			description = Str.get("node.real_div");
		}
		
		public void visitar(NEquivalencia n)
		throws VisitanteException
		{
			description = Str.get("node.equiv");
		}
		
		public void visitar(NEsInstanciaDe n)
		throws VisitanteException
		{
			description = Str.get("node.instance_of");
		}
		
		public void visitar(NEspecificacion n)
		throws VisitanteException
		{
			description = Str.get("node.spec");
		}
		
		public void visitar(NEste n)
		throws VisitanteException
		{
			description = Str.get("node.this");
		}
		
		public void visitar(NForEach n)
		throws VisitanteException
		{
			description = Str.get("node.for_each");
		}
		
		public void visitar(NFuente n)
		throws VisitanteException
		{
			description = Str.get("node.source");
		}
		
		public void visitar(NId n)
		throws VisitanteException
		{
			description = Str.get("node.id");
		}
		
		public void visitar(NIgual n)
		throws VisitanteException
		{
			description = Str.get("node.equal");
		}
		
		public void visitar(NImplicacion n)
		throws VisitanteException
		{
			description = Str.get("node.implies");
		}
		
		public void visitar(NIntente n)
		throws VisitanteException
		{
			description = Str.get("node.try");
		}
		
		public void visitar(NInterface n)
		throws VisitanteException
		{
			description = Str.get("node.interface");
		}
		
		public void visitar(NInvocacion n)
		throws VisitanteException
		{
			description = Str.get("node.call");
		}
		
		public void visitar(NLance n)
		throws VisitanteException
		{
			description = Str.get("node.throw");
		}
		
		public void visitar(NLiteralBooleano n)
		throws VisitanteException
		{
			description = Str.get("node.1_boolean_literal", n.obtImagen());
		}
		
		public void visitar(NLiteralCadena n)
		throws VisitanteException
		{
			description = Str.get("node.1_string_literal", n.obtImagen());
		}
		
		public void visitar(NLiteralCaracter n)
		throws VisitanteException
		{
			description = Str.get("node.1_char_literal", n.obtImagen());
		}
		
		public void visitar(NLiteralEntero n)
		throws VisitanteException
		{
			description = Str.get("node.1_int_literal", n.obtImagen());
		}
		
		public void visitar(NLiteralNulo n)
		throws VisitanteException
		{
			description = Str.get("node.1_null_literal", n.obtImagen());
		}
		
		public void visitar(NLiteralReal n)
		throws VisitanteException
		{
			description = Str.get("node.1_real_literal", n.obtImagen());
		}
		
		public void visitar(NMas n)
		throws VisitanteException
		{
			description = Str.get("node.bin_plus");
		}
		
		public void visitar(NMayor n)
		throws VisitanteException
		{
			description = Str.get("node.gt");
		}
		
		public void visitar(NMayorIgual n)
		throws VisitanteException
		{
			description = Str.get("node.ge");
		}
		
		public void visitar(NMenor n)
		throws VisitanteException
		{
			description = Str.get("node.lt");
		}
		
		public void visitar(NMenorIgual n)
		throws VisitanteException
		{
			description = Str.get("node.le");
		}
		
		public void visitar(NMenos n)
		throws VisitanteException
		{
			description = Str.get("node.bin_minus");
		}
		
		public void visitar(NMientras n)
		throws VisitanteException
		{
			description = Str.get("node.while");
		}
		
		public void visitar(NMod n)
		throws VisitanteException
		{
			description = Str.get("node.mod");
		}
		
		public void visitar(NNeg n)
		throws VisitanteException
		{
			description = Str.get("node.neg");
		}
		
		public void visitar(NNo n)
		throws VisitanteException
		{
			description = Str.get("node.lnot");
		}
		
		public void visitar(NNoBit n)
		throws VisitanteException
		{
			description = Str.get("node.anot");
		}
		
		public void visitar(NNombre n)
		throws VisitanteException
		{
			description = Str.get("node.name");
		}
		
		public void visitar(NO n)
		throws VisitanteException
		{
			description = Str.get("node.or");
		}
		
		public void visitar(NOArit n)
		throws VisitanteException
		{
			description = Str.get("node.aor");
		}
		
		public void visitar(NOExc n)
		throws VisitanteException
		{
			description = Str.get("node.xor");
		}
		
		public void visitar(NPaquete n)
		throws VisitanteException
		{
			description = Str.get("node.package");
		}
		
		public void visitar(NPara n)
		throws VisitanteException
		{
			description = Str.get("node.for");
		}
		
		public void visitar(NPlus n)
		throws VisitanteException
		{
			description = Str.get("node.un_plus");
		}
		
		public void visitar(NPor n)
		throws VisitanteException
		{
			description = Str.get("node.times");
		}
		
		public void visitar(NRepita n)
		throws VisitanteException
		{
			description = Str.get("node.repeat");
		}
		
		public void visitar(NRetorne n)
		throws VisitanteException
		{
			description = Str.get("node.return");
		}
		
		public void visitar(NSubId n)
		throws VisitanteException
		{
			description = Str.get("node.deref");
		}
		
		public void visitar(NSubindexacion n)
		throws VisitanteException
		{
			description = Str.get("node.subindex");
		}
		
		public void visitar(NTermine n)
		throws VisitanteException
		{
			description = Str.get("node.break");
		}
		
		public void visitar(NUtiliza n)
		throws VisitanteException
		{
			description = Str.get("node.uses");
		}
		
		public void visitar(NY n)
		throws VisitanteException
		{
			description = Str.get("node.and");
		}
		
		public void visitar(NYArit n)
		throws VisitanteException
		{
			description = Str.get("node.aand");
		}
		
	
		public void visitar(NTipoArreglo n)
		throws VisitanteException
		{
			description = Str.get("node.array_type");
		}
		
		public void visitar(NTipoBooleano n)
		throws VisitanteException
		{
			description = Str.get("node.boolean_type");
		}
		
		public void visitar(NTipoCadena n)
		throws VisitanteException
		{
			description = Str.get("node.string_type");
		}
	
		public void visitar(NTipoCaracter n)
		throws VisitanteException
		{
			description = Str.get("node.char_type");
		}
	
		public void visitar(NTipoClase n)
		throws VisitanteException
		{
			description = Str.get("node.class_type");
		}
	
		public void visitar(NTipoEntero n)
		throws VisitanteException
		{
			description = Str.get("node.int_type");
		}
	
		public void visitar(NTipoEspecificacion n)
		throws VisitanteException
		{
			description = Str.get("node.spec_type");
		}
	
		public void visitar(NTipoReal n)
		throws VisitanteException
		{
			description = Str.get("node.real_type");
		}
	
		public void visitar(NImplementa n)
		throws VisitanteException
		{
			description = Str.get("node.implements");
		}
	
		public void visitar(NTipoInterface n)
		throws VisitanteException
		{
			description = Str.get("node.interface_type");
		}
	}
}
