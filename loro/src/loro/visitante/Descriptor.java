package loro.visitante;

import loro.arbol.*;

////////////////////////////////////////////////////////////
/**
 * Obtiene una descripci�n textual de tipo para cada nodo.
 * 
 * Actualmente esta descripci�n es general para cada nodo:
 * no se tienen a�n en cuenta posibles precisiones para dar
 * m�s claridad en algunos casos (por ejemplo en un nodo
 * NMas convendr�a distinguir entre suma aritm�tica o
 * concatenaci�n de cadenas).
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
	 * Obtiene una descripci�n concisa del tipo del nodo dado.
	 *
	 * @param n   El nodo a describir.
	 *
	 * @return La descripci�n concisa del tipo del nodo dado.
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
	 * Este visitante obtiene una descripci�n textual de tipo 
	 * para cada nodo.
	 */
	static class VisitanteDescriptor implements IVisitante
	{
		public void visitar(NACadena n)
		throws VisitanteException
		{
			description = "Operaci�n de conversi�n a cadena";
		}

		public void visitar(NAfirmacion n)
		throws VisitanteException
		{
			description = "Afirmaci�n";
		}
		
		public void visitar(NAlgoritmo n)
		throws VisitanteException
		{
			description = "Definici�n de un algoritmo";
		}
		
		public void visitar(NAsignacion n)
		throws VisitanteException
		{
			description = "Una asignaci�n";
		}
		
		public void visitar(NAtrape n)
		throws VisitanteException
		{
			description = "Un segmento para atrapar una excepci�n";
		}
		
		public void visitar(NCardinalidad n)
		throws VisitanteException
		{
			description = "Operaci�n que obtiene el tama�o de una arreglo o cadena";
		}
		
		public void visitar(NCaso n)
		throws VisitanteException
		{
			description = "Un segmento para atenci�n de caso en una acci�n 'seg�n'";
		}
		
		public void visitar(NCiclo n)
		throws VisitanteException
		{
			description = "Una iteraci�n 'ciclo'";
		}
		
		public void visitar(NClase n)
		throws VisitanteException
		{
			description = "Definici�n de una clase";
		}
		
		public void visitar(NCondicion n)
		throws VisitanteException
		{
			description = "Operaci�n ternaria condicional";
		}
		
		public void visitar(NConstructor n)
		throws VisitanteException
		{
			description = "Definici�n de un constructor";
		}
		
		public void visitar(NContinue n)
		throws VisitanteException
		{
			description = "Una acci�n 'contin�e'";
		}
		
		public void visitar(NConvertirTipo n)
		throws VisitanteException
		{
			description = "Conversi�n de tipo de una expresi�n";
		}
		
		public void visitar(NCorrDer n)
		throws VisitanteException
		{
			description = "Operaci�n de desplazamiento de bits a la derecha con signo";
		}
		
		public void visitar(NCorrDerDer n)
		throws VisitanteException
		{
			description = "Operaci�n de desplazamiento de bits a la derecha";
			description = "Definici�n de un algoritmo";
		}
		
		public void visitar(NCorrIzq n)
		throws VisitanteException
		{
			description = "Operaci�n de desplazamiento de bits a la izquierda";
		}
		
		public void visitar(NCrearArreglo n)
		throws VisitanteException
		{
			description = "Operaci�n de creaci�n de un arreglo";
		}

		public void visitar(NExpresionArreglo n)
		throws VisitanteException
		{
			description = "Arreglo denotado expl�citamente";
		}

		public void visitar(NCrearArregloTipoBase n)
		throws VisitanteException
		{
			description = "Parte de la indicaci�n para crear un arreglo";
		}

		public void visitar(NCrearObjeto n)
		throws VisitanteException
		{
			description = "Operaci�n de creaci�n de una instancia";
		}

		public void visitar(NCuantificado n)
		throws VisitanteException
		{
			description = "Expresi�n booleana con cuantificador";
		}

		public void visitar(NDecision n)
		throws VisitanteException
		{
			description = "Acci�n de decisi�n";
		}

		public void visitar(NDecisionSiNoSi n)
		throws VisitanteException
		{
			description = "Alternativa en una acci�n de decisi�n";

		}
		public void visitar(NDecisionMultiple n)
		throws VisitanteException
		{
			description = "Acci�n de decisi�n de m�ltiples casos";
		}
		
		public void visitar(NDeclaracion n)
		throws VisitanteException
		{
			description = "Declaraci�n de variable(s)";
		}
		
		public void visitar(NDeclDesc n)
		throws VisitanteException
		{
			description = "Declaraci�n de variable incluyendo descripci�n";
		}
		
		public void visitar(NDescripcion n)
		throws VisitanteException
		{
			description = "Texto para documentaci�n";
		}
		
		public void visitar(NDiferente n)
		throws VisitanteException
		{
			description = "Operaci�n relacional de verificaci�n de no igualdad";
		}
		
		public void visitar(NDivReal n)
		throws VisitanteException
		{
			description = "Operaci�n de divisi�n real";
		}
		
		public void visitar(NEquivalencia n)
		throws VisitanteException
		{
			description = "Operaci�n relacional de verificaci�n de equivalencia l�gica";
		}
		
		public void visitar(NEsInstanciaDe n)
		throws VisitanteException
		{
			description = "Operaci�n de verificaci�n del tipo de un objeto";
		}
		
		public void visitar(NEspecificacion n)
		throws VisitanteException
		{
			description = "Definici�n de una especificaci�n";
		}
		
		public void visitar(NEste n)
		throws VisitanteException
		{
			description = "Expresi�n '�ste' que referencia al objeto sobre el cual opera el contructor o m�todo";
		}
		
		public void visitar(NForEach n)
		throws VisitanteException
		{
			description = "Una iteraci�n 'para cada elemento en ...'";
		}
		
		public void visitar(NFuente n)
		throws VisitanteException
		{
			description = "Fuente completo sometido a compilaci�n";
		}
		
		public void visitar(NId n)
		throws VisitanteException
		{
			description = "Identificador";
		}
		
		public void visitar(NIgual n)
		throws VisitanteException
		{
			description = "Operaci�n relacional de verificaci�n de igualdad";
		}
		
		public void visitar(NImplicacion n)
		throws VisitanteException
		{
			description = "Operaci�n relacional de verificaci�n de implicaci�n l�gica";
		}
		
		public void visitar(NIntente n)
		throws VisitanteException
		{
			description = "Acci�n 'intente'";
		}
		
		public void visitar(NInterface n)
		throws VisitanteException
		{
			description = "Definici�n de una interfaz";
		}
		
		public void visitar(NInvocacion n)
		throws VisitanteException
		{
			description = "Invocaci�n de algoritmo";
		}
		
		public void visitar(NLance n)
		throws VisitanteException
		{
			description = "lanzamiento de una excepci�n";
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
			description = "Operaci�n de suma-concatenaci�n";
		}
		
		public void visitar(NMayor n)
		throws VisitanteException
		{
			description = "Operaci�n relacional de verificaci�n de 'mayor que'";
		}
		
		public void visitar(NMayorIgual n)
		throws VisitanteException
		{
			description = "Operaci�n relacional de verificaci�n de 'mayor que o igual a'";
		}
		
		public void visitar(NMenor n)
		throws VisitanteException
		{
			description = "Operaci�n relacional de verificaci�n de 'menor que'";
		}
		
		public void visitar(NMenorIgual n)
		throws VisitanteException
		{
			description = "Operaci�n relacional de verificaci�n de 'menor que o igual a'";
		}
		
		public void visitar(NMenos n)
		throws VisitanteException
		{
			description = "Operaci�n de resta";
		}
		
		public void visitar(NMientras n)
		throws VisitanteException
		{
			description = "Acci�n de iteraci�n 'mientras'";
		}
		
		public void visitar(NMod n)
		throws VisitanteException
		{
			description = "Operaci�n m�dulo divisi�n entera";
		}
		
		public void visitar(NNeg n)
		throws VisitanteException
		{
			description = "Operaci�n aritm�tica unaria -";
		}
		
		public void visitar(NNo n)
		throws VisitanteException
		{
			description = "Operaci�n de negaci�n l�gica";
		}
		
		public void visitar(NNoBit n)
		throws VisitanteException
		{
			description = "Operaci�n de negaci�n bit a bit";
		}
		
		public void visitar(NNombre n)
		throws VisitanteException
		{
			description = "Nombre cualificado";
		}
		
		public void visitar(NO n)
		throws VisitanteException
		{
			description = "Operaci�n relacional de verificaci�n de 'O inclusiva' l�gica";
		}
		
		public void visitar(NOArit n)
		throws VisitanteException
		{
			description = "Operaci�n 'O inclusiva' bit a bit";
		}
		
		public void visitar(NOExc n)
		throws VisitanteException
		{
			description = "Operaci�n relacional de verificaci�n de 'O exclusiva' l�gica";
		}
		
		public void visitar(NPaquete n)
		throws VisitanteException
		{
			description = "Definici�n de nombre de paquete";
		}
		
		public void visitar(NPara n)
		throws VisitanteException
		{
			description = "Acci�n de iteraci�n 'para'";
		}
		
		public void visitar(NPlus n)
		throws VisitanteException
		{
			description = "Operaci�n aritm�tica unaria +";
		}
		
		public void visitar(NPor n)
		throws VisitanteException
		{
			description = "Operaci�n aritm�tica de multiplicaci�n";
		}
		
		public void visitar(NRepita n)
		throws VisitanteException
		{
			description = "Acci�n de iteraci�n 'repita'";
		}
		
		public void visitar(NRetorne n)
		throws VisitanteException
		{
			description = "Acci�n de retorno";
		}
		
		public void visitar(NSubId n)
		throws VisitanteException
		{
			description = "Operaci�n de acceso a atributo/m�todo de un objeto";
		}
		
		public void visitar(NSubindexacion n)
		throws VisitanteException
		{
			description = "Operaci�n de acceso a campo de arreglo";
		}
		
		public void visitar(NTermine n)
		throws VisitanteException
		{
			description = "Acci�n de terminaci�n de iteraci�n";
		}
		
		public void visitar(NUtiliza n)
		throws VisitanteException
		{
			description = "Indicaci�n 'utiliza' para acceder a unidades";
		}
		
		public void visitar(NY n)
		throws VisitanteException
		{
			description = "Operaci�n relacional de verificaci�n de 'Y' l�gica";
		}
		
		public void visitar(NYArit n)
		throws VisitanteException
		{
			description = "Operaci�n 'Y' bit a bit";
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
			description = "Tipo 'especificaci�n'";
		}
	
		public void visitar(NTipoReal n)
		throws VisitanteException
		{
			description = "Tipo 'real'";
		}
	
		public void visitar(NImplementa n)
		throws VisitanteException
		{
			description = "Indicaci�n de interfaces implementadas";
		}
	
		public void visitar(NTipoInterface n)
		throws VisitanteException
		{
			description = "Tipo 'interfaz'";
		}
	}
}
