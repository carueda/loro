package loro.visitante;

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
			description = "Operación de conversión a cadena";
		}

		public void visitar(NAfirmacion n)
		throws VisitanteException
		{
			description = "Afirmación";
		}
		
		public void visitar(NAlgoritmo n)
		throws VisitanteException
		{
			description = "Definición de un algoritmo";
		}
		
		public void visitar(NAsignacion n)
		throws VisitanteException
		{
			description = "Una asignación";
		}
		
		public void visitar(NAtrape n)
		throws VisitanteException
		{
			description = "Un segmento para atrapar una excepción";
		}
		
		public void visitar(NCardinalidad n)
		throws VisitanteException
		{
			description = "Operación que obtiene el tamaño de una arreglo o cadena";
		}
		
		public void visitar(NCaso n)
		throws VisitanteException
		{
			description = "Un segmento para atención de caso en una acción 'según'";
		}
		
		public void visitar(NCiclo n)
		throws VisitanteException
		{
			description = "Una iteración 'ciclo'";
		}
		
		public void visitar(NClase n)
		throws VisitanteException
		{
			description = "Definición de una clase";
		}
		
		public void visitar(NCondicion n)
		throws VisitanteException
		{
			description = "Operación ternaria condicional";
		}
		
		public void visitar(NConstructor n)
		throws VisitanteException
		{
			description = "Definición de un constructor";
		}
		
		public void visitar(NContinue n)
		throws VisitanteException
		{
			description = "Una acción 'continúe'";
		}
		
		public void visitar(NConvertirTipo n)
		throws VisitanteException
		{
			description = "Conversión de tipo de una expresión";
		}
		
		public void visitar(NCorrDer n)
		throws VisitanteException
		{
			description = "Operación de desplazamiento de bits a la derecha con signo";
		}
		
		public void visitar(NCorrDerDer n)
		throws VisitanteException
		{
			description = "Operación de desplazamiento de bits a la derecha";
			description = "Definición de un algoritmo";
		}
		
		public void visitar(NCorrIzq n)
		throws VisitanteException
		{
			description = "Operación de desplazamiento de bits a la izquierda";
		}
		
		public void visitar(NCrearArreglo n)
		throws VisitanteException
		{
			description = "Operación de creación de un arreglo";
		}

		public void visitar(NExpresionArreglo n)
		throws VisitanteException
		{
			description = "Arreglo denotado explícitamente";
		}

		public void visitar(NCrearArregloTipoBase n)
		throws VisitanteException
		{
			description = "Parte de la indicación para crear un arreglo";
		}

		public void visitar(NCrearObjeto n)
		throws VisitanteException
		{
			description = "Operación de creación de una instancia";
		}

		public void visitar(NCuantificado n)
		throws VisitanteException
		{
			description = "Expresión booleana con cuantificador";
		}

		public void visitar(NDecision n)
		throws VisitanteException
		{
			description = "Acción de decisión";
		}

		public void visitar(NDecisionSiNoSi n)
		throws VisitanteException
		{
			description = "Alternativa en una acción de decisión";

		}
		public void visitar(NDecisionMultiple n)
		throws VisitanteException
		{
			description = "Acción de decisión de múltiples casos";
		}
		
		public void visitar(NDeclaracion n)
		throws VisitanteException
		{
			description = "Declaración de variable(s)";
		}
		
		public void visitar(NDeclDesc n)
		throws VisitanteException
		{
			description = "Declaración de variable incluyendo descripción";
		}
		
		public void visitar(NDescripcion n)
		throws VisitanteException
		{
			description = "Texto para documentación";
		}
		
		public void visitar(NDiferente n)
		throws VisitanteException
		{
			description = "Operación relacional de verificación de no igualdad";
		}
		
		public void visitar(NDivReal n)
		throws VisitanteException
		{
			description = "Operación de división real";
		}
		
		public void visitar(NEquivalencia n)
		throws VisitanteException
		{
			description = "Operación relacional de verificación de equivalencia lógica";
		}
		
		public void visitar(NEsInstanciaDe n)
		throws VisitanteException
		{
			description = "Operación de verificación del tipo de un objeto";
		}
		
		public void visitar(NEspecificacion n)
		throws VisitanteException
		{
			description = "Definición de una especificación";
		}
		
		public void visitar(NEste n)
		throws VisitanteException
		{
			description = "Expresión 'éste' que referencia al objeto sobre el cual opera el contructor o método";
		}
		
		public void visitar(NForEach n)
		throws VisitanteException
		{
			description = "Una iteración 'para cada elemento en ...'";
		}
		
		public void visitar(NFuente n)
		throws VisitanteException
		{
			description = "Fuente completo sometido a compilación";
		}
		
		public void visitar(NId n)
		throws VisitanteException
		{
			description = "Identificador";
		}
		
		public void visitar(NIgual n)
		throws VisitanteException
		{
			description = "Operación relacional de verificación de igualdad";
		}
		
		public void visitar(NImplicacion n)
		throws VisitanteException
		{
			description = "Operación relacional de verificación de implicación lógica";
		}
		
		public void visitar(NIntente n)
		throws VisitanteException
		{
			description = "Acción 'intente'";
		}
		
		public void visitar(NInterface n)
		throws VisitanteException
		{
			description = "Definición de una interfaz";
		}
		
		public void visitar(NInvocacion n)
		throws VisitanteException
		{
			description = "Invocación de algoritmo";
		}
		
		public void visitar(NLance n)
		throws VisitanteException
		{
			description = "lanzamiento de una excepción";
		}
		
		public void visitar(NLiteralBooleano n)
		throws VisitanteException
		{
			description = "Literal booleana '" +n.obtImagen()+ "'";
		}
		
		public void visitar(NLiteralCadena n)
		throws VisitanteException
		{
			description = "Literal cadena '" +n.obtImagen()+ "'";
		}
		
		public void visitar(NLiteralCaracter n)
		throws VisitanteException
		{
			description = "Literal caracter '" +n.obtImagen()+ "'";
		}
		
		public void visitar(NLiteralEntero n)
		throws VisitanteException
		{
			description = "Literal entera '" +n.obtImagen()+ "'";
		}
		
		public void visitar(NLiteralNulo n)
		throws VisitanteException
		{
			description = "Literal para referencia nula '" +n.obtImagen()+ "'";
		}
		
		public void visitar(NLiteralReal n)
		throws VisitanteException
		{
			description = "Literal real '" +n.obtImagen()+ "'";
		}
		
		public void visitar(NMas n)
		throws VisitanteException
		{
			description = "Operación de suma-concatenación";
		}
		
		public void visitar(NMayor n)
		throws VisitanteException
		{
			description = "Operación relacional de verificación de 'mayor que'";
		}
		
		public void visitar(NMayorIgual n)
		throws VisitanteException
		{
			description = "Operación relacional de verificación de 'mayor que o igual a'";
		}
		
		public void visitar(NMenor n)
		throws VisitanteException
		{
			description = "Operación relacional de verificación de 'menor que'";
		}
		
		public void visitar(NMenorIgual n)
		throws VisitanteException
		{
			description = "Operación relacional de verificación de 'menor que o igual a'";
		}
		
		public void visitar(NMenos n)
		throws VisitanteException
		{
			description = "Operación de resta";
		}
		
		public void visitar(NMientras n)
		throws VisitanteException
		{
			description = "Acción de iteración 'mientras'";
		}
		
		public void visitar(NMod n)
		throws VisitanteException
		{
			description = "Operación módulo división entera";
		}
		
		public void visitar(NNeg n)
		throws VisitanteException
		{
			description = "Operación aritmética unaria -";
		}
		
		public void visitar(NNo n)
		throws VisitanteException
		{
			description = "Operación de negación lógica";
		}
		
		public void visitar(NNoBit n)
		throws VisitanteException
		{
			description = "Operación de negación bit a bit";
		}
		
		public void visitar(NNombre n)
		throws VisitanteException
		{
			description = "Nombre cualificado";
		}
		
		public void visitar(NO n)
		throws VisitanteException
		{
			description = "Operación relacional de verificación de 'O inclusiva' lógica";
		}
		
		public void visitar(NOArit n)
		throws VisitanteException
		{
			description = "Operación 'O inclusiva' bit a bit";
		}
		
		public void visitar(NOExc n)
		throws VisitanteException
		{
			description = "Operación relacional de verificación de 'O exclusiva' lógica";
		}
		
		public void visitar(NPaquete n)
		throws VisitanteException
		{
			description = "Definición de nombre de paquete";
		}
		
		public void visitar(NPara n)
		throws VisitanteException
		{
			description = "Acción de iteración 'para'";
		}
		
		public void visitar(NPlus n)
		throws VisitanteException
		{
			description = "Operación aritmética unaria +";
		}
		
		public void visitar(NPor n)
		throws VisitanteException
		{
			description = "Operación aritmética de multiplicación";
		}
		
		public void visitar(NRepita n)
		throws VisitanteException
		{
			description = "Acción de iteración 'repita'";
		}
		
		public void visitar(NRetorne n)
		throws VisitanteException
		{
			description = "Acción de retorno";
		}
		
		public void visitar(NSubId n)
		throws VisitanteException
		{
			description = "Operación de acceso a atributo/método de un objeto";
		}
		
		public void visitar(NSubindexacion n)
		throws VisitanteException
		{
			description = "Operación de acceso a campo de arreglo";
		}
		
		public void visitar(NTermine n)
		throws VisitanteException
		{
			description = "Acción de terminación de iteración";
		}
		
		public void visitar(NUtiliza n)
		throws VisitanteException
		{
			description = "Indicación 'utiliza' para acceder a unidades";
		}
		
		public void visitar(NY n)
		throws VisitanteException
		{
			description = "Operación relacional de verificación de 'Y' lógica";
		}
		
		public void visitar(NYArit n)
		throws VisitanteException
		{
			description = "Operación 'Y' bit a bit";
		}
		
	
		public void visitar(NTipoArreglo n)
		throws VisitanteException
		{
			description = "Tipo arreglo";
		}
		
		public void visitar(NTipoBooleano n)
		throws VisitanteException
		{
			description = "Tipo 'booleano'";
		}
		
		public void visitar(NTipoCadena n)
		throws VisitanteException
		{
			description = "Tipo 'cadena'";
		}
	
		public void visitar(NTipoCaracter n)
		throws VisitanteException
		{
			description = "Tipo 'caracter'";
		}
	
		public void visitar(NTipoClase n)
		throws VisitanteException
		{
			description = "Tipo 'clase'";
		}
	
		public void visitar(NTipoEntero n)
		throws VisitanteException
		{
			description = "Tipo 'entero'";
		}
	
		public void visitar(NTipoEspecificacion n)
		throws VisitanteException
		{
			description = "Tipo 'especificación'";
		}
	
		public void visitar(NTipoReal n)
		throws VisitanteException
		{
			description = "Tipo 'real'";
		}
	
		public void visitar(NImplementa n)
		throws VisitanteException
		{
			description = "Indicación de interfaces implementadas";
		}
	
		public void visitar(NTipoInterface n)
		throws VisitanteException
		{
			description = "Tipo 'interfaz'";
		}
	}
}
