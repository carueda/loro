package loro.ejecucion;

import loro.Loro.Str;
import loro.util.ManejadorUnidades;
import loro.util.Util;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.arbol.*;
import loro.ijava.*;
import loro.java.*;
import loro.tabsimb.*;
import loro.tipo.*;
import loro.*;
import loro.compilacion.ChequeadorException;
import loro.compilacion.ClaseNoEncontradaException;
import loro.util.UtilValor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Array;

import java.io.Reader;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.*;


/**
 * Visitante b�sico para ejecuci�n. 
 * Aqu� se implementan propiamente las operaciones visitar(*) de la
 * interface IVisitante.
 *
 * Extiende LoroEjecutorBase en donde se cuenta con un conjunto de servicios
 * auxiliares para la tarea.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class LoroEjecutor extends LoroEjecutorBase
{
	/**
	 * Crea un ejecutor.
	 * La tabla de simbolos de base se pone en null.
	 */
	public LoroEjecutor()
	{
		super();
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Crea un ejecutor b�sico.
	 *
	 * @param tabSimbBase Tabla de s�mbolos de base.
	 * @param unidadBase Unidad de base.
	 */
	public LoroEjecutor(TablaSimbolos tabSimbBase, NUnidad unidadBase)
	{
		super(tabSimbBase, unidadBase);
	}


	/**
	 * Ejecuta un algoritmo implementado en Java.
	 *
	 * @throws EjecucionException 
	 *                   Por cualquier error de ejecucion
	 *                   excepto relacionados con terminacion sea
	 *                   interna o externa.
	 *
	 * @throws TerminacionException 
	 *                   Por terminacion sea interna o externa.
	 */
	protected Object ejecutarAlgoritmoJava(NAlgoritmo alg, Object[] args)
	throws EjecucionVisitanteException
	{
		String id = alg.obtNombreSimpleCadena();
		if ( !alg.implementadoEnLenguaje("java") )
		{
				throw _crearEjecucionException(alg,
					"Algorithm '" +id+ "' not implemented in Java"
				);
		}

		String clase_java = alg.obtInfoImplementacion();
		if ( clase_java == null )
		{
			throw _crearEjecucionException(alg,
				"Missing class name to execute Java method"
			);
		}
		
		Class clazz = null;
		Object res = null;
		try
		{
			clazz = loroClassLoader.getClass(clase_java);
			Method[] mets = clazz.getDeclaredMethods();
			Method m = null;
			for (int i = 0; i < mets.length; i++)
			{
				if ( mets[i].getName().equals(id) )
				{
					m = mets[i];
					break;
				}
			}
			if ( m == null )
			{
				throw _crearEjecucionException(alg,
					"Java class '" +clase_java+
					"' does not contain expected method: " +id
				);
			}

			Object[] new_args = new Object[args.length + 1];
			new_args[0] = this;

			// Convierta argumentos:
			for (int i = 0; i < args.length; i++)
				new_args[i + 1] = _convertirArgumentoParaJava(args[i]);

			// Invoque el metodo Java:
			res = m.invoke(null, new_args);

			// Convierta retorno:
			res = _convertirRetornoDeJava(res);
		}
		catch(ClassNotFoundException ex)
		{
			throw _crearEjecucionException(alg,
				"Java class not found: " +clase_java
			);
		}
		catch(IllegalAccessException ex)
		{
			throw _crearEjecucionException(alg,
				"An error ocurred while trying to execute Java method '"
				+clase_java+ ". " +id+ "'"
			);
		}
		catch(InvocationTargetException ex)
		{
			Throwable thr = ex.getTargetException();

			if ( thr instanceof TerminacionInternaException )
			{
				// OK, indica terminar programa Loro desde una
				// invocaci�n de programa:
				throw _crearEjecucionException(alg,
					thr.getMessage()
				);
				// TerminacionInternaException ti = (TerminacionInternaException) thr;
				// throw new TerminacionException(alg, pilaEjec, ti.obtCodigo());
			}
			else if ( thr instanceof TerminacionExternaException )
			{
				// OK, indica terminar programa Loro por un evento externo:
				throw (TerminacionExternaException) thr;
			}
			else if ( thr instanceof java.io.InterruptedIOException )
			{
				// OK, indica terminar programa Loro por un evento externo:
				// Ver metodos Java Sistema.leer..()
				throw new TerminacionException(alg, pilaEjec);
			}
			else if ( thr instanceof LException )
			{
				throw _crearEjecucionException(alg,
					thr.getMessage()
				//	+ "\nExcepcion en metodo Java '" +clase_java+ "." +id
				);
			}
			else
			{
				ex.printStackTrace();
				return _crearEjecucionException(alg,
						"Java method '"
						+clase_java+ "." +id+ "' threw exception:\n"
						+thr.getClass()+ ": " +thr.getMessage()
				);
			}
		}

		return res;
	}

	/**
	 * Executes a BeanShel-implemented algorithm.
	 *
	 * @throws EjecucionException 
	 *                   Por cualquier error de ejecucion
	 *                   excepto relacionados con terminacion sea
	 *                   interna o externa.
	 *
	 * @throws TerminacionException 
	 *                   Por terminacion sea interna o externa.
	 */
	protected Object ejecutarAlgoritmoBsh(NAlgoritmo alg, Object[] args)
	throws EjecucionVisitanteException {
		String id = alg.obtNombreSimpleCadena();
		if ( !alg.implementadoEnLenguaje("bsh") ) {
			throw _crearEjecucionException(alg,
				"Algorithm '" +id+ "' not implemented in BeanShell"
			);
		}

		NDeclaracion[] pent = alg.obtParametrosEntrada();
		NDeclaracion[] psal = alg.obtParametrosSalida();
		
		String src = alg.obtInfoImplementacion();
		if ( src  == null ) {
			throw _crearEjecucionException(alg,
				"Missing script source"
			);
		}
		bsh.Interpreter bsh = new bsh.Interpreter();
		bsh.getNameSpace().importPackage("loro.ijava");
		Object res = null;
		try {
			bsh.set("$amb", this);
			bsh.set("$este", este);
			bsh.set("$this", este);       // should be only this one  PENDING
			bsh.set("$thrw", null);       // any thrown object
			
			for (int i = 0; i < args.length; i++) {
				String in_id = pent[i].obtId().obtId();
				Object obj = args[i];
				if ( obj instanceof Integer )
					bsh.set(in_id, ((Integer) obj).intValue());
				else if ( obj instanceof Boolean )
					bsh.set(in_id, ((Boolean) obj).booleanValue());
				else if ( obj instanceof Double )
					bsh.set(in_id, ((Double) obj).doubleValue());
				else if ( obj instanceof Character )
					// BeanShell 1.2b6 no tiene set(id, char) (!)
					bsh.set(in_id, new bsh.Primitive(((Character) obj).charValue()));
				else
					bsh.set(in_id, _convertirArgumentoParaJava(obj));
			}
			
			// put some code to catch any throwable:
			String bsh_src =  "try { " +src+ " } catch(Throwable $thrwarg) { $thrw = $thrwarg; }";
			res = bsh.eval(bsh_src);
			
			Throwable thrw = (Throwable) bsh.get("$thrw");
			if ( thrw != null )
				throw thrw;
			
			if ( res == null && psal.length > 0 ) {
				String out_id = psal[0].obtId().obtId();
				res = bsh.get(out_id);
			}
			
			// Convierta retorno:
			res = _convertirRetornoDeJava(res);
		}
		catch ( bsh.EvalError ex ) {
			throw _crearEjecucionException(alg,
				"Error while ejecuting script:\n"+
				"line: " +ex.getErrorLineNumber()+ "\n"+
				"error text: " +ex.getErrorText()+ "\n"+
				"message: " +ex.getMessage()+ "\n"
			);
		}
		catch(java.io.InterruptedIOException thr) {
			// OK, indica terminar programa Loro por un evento externo:
			// Ver metodos Java Sistema.leer..()
			throw new TerminacionException(alg, pilaEjec);
		}
		catch(TerminacionExternaException thr) {
			// OK, indica terminar programa Loro por un evento externo:
			throw thr;
		}
		catch(InterruptedException thr) {
			throw new TerminacionException(alg, pilaEjec);
		}
		catch(LException thr) {
			throw _crearEjecucionException(alg,
				thr.getMessage()
			);
		}
		catch(TerminacionInternaException thr) {
			throw _crearEjecucionException(alg,
				thr.getMessage()
			);
		}
		catch(Throwable thr) {
			thr.printStackTrace();
			throw _crearEjecucionException(alg,
				thr.getMessage()
			);
		}
		
		return res;
	}

	/**
	 * Simulaci�n "usr" para ejecutar algoritmo.
	 *
	 * @throws EjecucionException 
	 *                   Por cualquier error de ejecucion
	 *                   excepto relacionados con terminacion sea
	 *                   interna o externa.
	 *
	 * @throws TerminacionException 
	 *                   Por terminacion sea interna o externa.
	 */
	protected void ejecutarAlgoritmoUsr(NAlgoritmo alg, Object[] args, TablaSimbolos tabSimb)
	throws EjecucionVisitanteException
	{
		String info = alg.obtInfoImplementacion();
		if ( info != null )
		{
			throw _crearEjecucionException(alg,
				"No info required for 'usr' implemented algorithm"
			);
		}
		UtilEjecucion._executeUsrAlgorithm(
			alg, args, tabSimb, mu, mes, pilaEjec, loroClassLoader
		);
	}
	
	/**
	 * Conversion de argumento para invocacion de metodo en Java.
	 */
	protected Object _convertirArgumentoParaJava(Object arg)
	{
		if ( arg instanceof NAlgoritmo )
		{
			// [2000-08-09]
			// Ineficiente desde luego (las optimizaciones se
			// haran despues).
			arg = new LAlgoritmoImp(this, (NAlgoritmo) arg);
		}
		else if ( arg instanceof Object[] )
		{
			Object[] a = (Object[]) arg;

			for (int i = 0; i < a.length; i++)
			{
				a[i] = _convertirArgumentoParaJava(a[i]);
			}
			return a;
		}

		// si (arg instanceof Objeto), Ok: siga tal cual.

		return arg;
	}

	public LObjeto crearInstancia(LClase c)
	throws LException
	{
		NClase clase = ((LClaseImp) c).obtClase();
		try
		{
			Objeto obj = _crearObjetoInicializado(clase, null);
			return obj;
		}
		catch(VisitanteException ex)
		{
			throw new LException(ex.getMessage());
		}
	}

	public LObjeto obtEste()
	throws LException
	{
		return este;
	}

	/**
	 * Auxiliar para ejecutar un algoritmo con argumentos dados.
	 */
	private Object _ejecutarAlgoritmo(
		boolean showArgs,
		NAlgoritmo alg, Object[] args,
		boolean showResult
	)
	throws EjecucionException
	{
		if ( algoritmoPrincipal == null )
		{
			algoritmoPrincipal = alg;
		}

		if ( showArgs )
		{
			UtilEjecucion._showAlgorithmArguments(alg, args, mu, mes);
		}
		
		try
		{
			ponArgumentosParaAlgoritmo(args);
			alg.aceptar(this);
			Object res = obtRetorno();

			if ( algoritmoPrincipal == alg )
			{
				// despachar terminaci�n:
				despacharTerminacion();
				algoritmoPrincipal = null;
			}
			
			if ( showResult )
			{
				if ( res != null )
				{
					UtilEjecucion._showAlgorithmOutput(alg, res, mu, mes);
				}
				mes.escribir(
					UtilEjecucion.PREFIX+ 
					Str.get("msg.execution_completed")+
					UtilEjecucion.PREFIX+ "\n"
				);
			}

			return res;
		}
		catch(TerminacionException ex)
		{
			if ( algoritmoPrincipal == alg )
			{
				// despachar terminaci�n:
				despacharTerminacion();
				algoritmoPrincipal = null;
			}

			if ( ex.esInterna() )
			{
				throw new EjecucionException(
					ex.obtPilaEjecucion(), 
					ex.obtRango(), ex.obtCodigoTerminacionInterna()
				);
			}
			else
			{
				throw new EjecucionException(
					ex.obtPilaEjecucion(), 
					ex.obtRango()
				);
			}
		}
		catch(EjecucionVisitanteException ex)
		{
			throw new EjecucionException(ex.obtPilaEjecucion(), ex.obtRango(), ex.getMessage());
		}
		catch(ControlLanceException ex)
		{
			throw new EjecucionException(null, null, 
				ex.getMessage()+ "\n" +ex.obtEstadoPila()
			);
		}
		catch(VisitanteException ex)
		{
			throw new RuntimeException("Internal error: " +
				"VisitanteException is not EjecucionException"
			);
		}
	}

	/**
	 * Ejecuta un algoritmo con argumentos dados.
	 */
	public Object ejecutarAlgoritmo(
		NAlgoritmo alg, Object[] args
	)
	throws EjecucionException
	{
		return _ejecutarAlgoritmo(true, alg, args, true);
	}

	/**
	 * Para cuando se llame desde l�nea de comandos.
	 * Este hace preparativos y llama a ejecutarAlgoritmo(alg, Object[]).
	 */
	public void ejecutarAlgoritmoArgumentosCadena(
		NAlgoritmo alg, String[] cadArgs
	)
	throws EjecucionException
	{
		TId id = alg.obtId();
		NDeclaracion[] pent = alg.obtParametrosEntrada();
		Object[] args = new Object[pent.length];

		boolean showArgs = cadArgs.length == pent.length;
		
		try
		{
			if ( cadArgs.length != pent.length )
			{
				if ( cadArgs.length == 0 )
				{
					try
					{
						// Pida al usuario los pent.length argumentos:
						cadArgs = UtilEjecucion._pedirArgumentosParaAlgoritmo(
							alg, mu, mes, pilaEjec
						);
					}
					catch(LException ex)
					{
						throw _crearEjecucionException(alg, ex.getMessage());
					}
				}

				if ( cadArgs.length != pent.length )
				{
					throw _crearEjecucionException(id,
						Str.get("rt.2_expected_args", id, ""+pent.length)
					);
				}
			}

			for ( int i = 0; i < pent.length; i++ )
			{
				String cad = cadArgs[i];
				Tipo tipo = pent[i].obtTipo();
				Object val;
				if ( tipo.esEntero() )
				{
					val = new Integer(cad);
				}
				else if ( tipo.esReal() )
				{
					val = new Double(cad);
				}
				else if ( tipo.esCaracter() )
				{
					char c = 0;
					if ( cad.length() > 0 )
						c = cad.charAt(0);
					val = new Character(c);
				}
				else if ( tipo.esBooleano() )
				{
					val = new Boolean(cad.equals(Str.get("true")));
				}
				else if ( pent[i].obtTipo().esCadena() )
				{
					val = cad;
				}
				else if ( tipo.esEspecificacion() )
				{
					NAlgoritmo a = mu.obtAlgoritmo(cad);
					if ( a == null )
					{
						throw _crearEjecucionException(id,
							Str.get("error.1_algorithm_not_found", cad)
						);
					}
					val = a;
				}
				else
				{
					throw _crearEjecucionException(id,
						"Type '" +tipo+ "' cannot be read from standard input"
					);
				}

				args[i] = val;
			}
		}
		catch(EjecucionVisitanteException ex)
		{
			throw new EjecucionException(
				ex.obtPilaEjecucion(), 
				ex.obtRango(), ex.getMessage()
			);
		}
		catch ( NumberFormatException nfex )
		{
			EjecucionVisitanteException ex = _crearEjecucionException(null, 
				Str.get("rt.error.1_invalid_input", nfex.getMessage())
			);
			throw new EjecucionException(
				ex.obtPilaEjecucion(), 
				ex.obtRango(), ex.getMessage()
			);
		}

		_ejecutarAlgoritmo(showArgs, alg, args, true);
	}
	


	public LAlgoritmo obtAlgoritmo(String nombre) {
		NAlgoritmo alg = mu.obtAlgoritmo(nombre);
		if ( alg != null )
			return new LAlgoritmoImp(this, alg);
		else
			return null;
	}


	/**
	 * Ejecuta el nodo.
	 */
	public void visitar(NACadena n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		retorno = UtilValor.comoCadena(_ejecutarExpresion(e));
	}
	
	/**
	 * Ejecuta un nodo afirmacion.
	 */
	public void visitar(NAfirmacion n)
	throws VisitanteException
	{
		NExpresion expresion = n.obtExpresion();
		if ( expresion == null
		||   expresion instanceof NLiteralCadena )
		{
			// OK. y se asume cierto simplemente.
			return;
		}

		enAfirmacion = true;
		afirmacionCiertaPorCuantificacion = false;
		Boolean b = (Boolean) _ejecutarExpresion(expresion);
		enAfirmacion = false;
		boolean acpc = afirmacionCiertaPorCuantificacion;
		afirmacionCiertaPorCuantificacion = false;

		if ( !acpc && !b.booleanValue() )
		{
			String a =
				contextoAfirmacion == A_PRECONDICION ?
					Str.get("rt.error.precondition_failed")
				: contextoAfirmacion == A_POSCONDICION ?
					Str.get("rt.error.postcondition_failed")
				:	Str.get("rt.error.assertion_failed")
			;
			throw _crearEjecucionException(expresion, a+ ": "+n.obtCadena());
		}
	}
	
	/**
	 * Visita un algoritmo para su ejecucion.
	 *
	 * Se asume que en argsParaAlgoritmo se encuentran los
	 * valores ya calculados de los argumentos.
	 *
	 * Siempre se procesan las declaraciones e/s, independientemente
	 * de en que lenguaje este implementado el algoritmo: esto para
	 * darle base al chequeo de pre/pos-condiciones principalmente.
	 *
	 * Si el algoritmo esta implementado en Java, se invoca
	 * ejecutarAlgoritmoJava() y lo que este produzca se deja como
	 * resultado (retorno).
	 *
	 * En otro caso (algoritmo normal), se visitan las acciones y
	 * hace manejo del valor de retorno del algoritmo (en caso que
	 * lo tenga).
	 */
	public void visitar(NAlgoritmo alg)
	throws VisitanteException
	{
		// por si se trata de m�todo.
		NClase clase = null;
		
		try
		{
			if ( argsParaAlgoritmo == null )
			{
				throw new RuntimeException("Internal error: argsParaAlgoritmo == null");
			}

			// Tome nota de los argumentos:
			Object[] val_args = argsParaAlgoritmo;

			_pushAlgoritmo(alg);
			String sespec = Util.obtStringRuta(alg.obtNombreEspecificacion());
			NEspecificacion espec = null;
			if ( objInvocado == null )
			{
				espec = mu.obtEspecificacion(sespec);
			}
			else
			{
				//
				// PENDING: Implementaci�n bajo ajustes!
				//
				
				String nombreMetodo = alg.obtNombreSimpleCadena();
				
				if ( !(objInvocado instanceof Objeto) )
					throw new RuntimeException("PENDING: !(objInvocado instanceof Objeto)");
				
				clase = ((Objeto) objInvocado).obtNClase();
				
				NClase klase = clase;
				// ciclo para mirar superclase si es necesario:
				try
				{
					search:
					while ( klase != null )
					{
						TNombre[] interfs = klase.obtInterfacesDeclaradas();
						for ( int i = 0; i < interfs.length; i++ )
						{
							NInterface ni = mu.obtInterface(interfs[i].obtCadena());
							if ( ni == null )
							{
								// intente nombre compuesto:
								String nombreCompuesto = unidadActual.obtNombreCompuesto("i" +interfs[i].obtCadena());
								if ( nombreCompuesto != null )
									ni = mu.obtInterface(nombreCompuesto);
							}
						
							if ( ni == null )
							{
								throw _crearEjecucionException(alg,
									Str.get("error.1_interface_not_found", interfs[i].obtCadena())
								);
							}

							NEspecificacion[] opers = ni.obtOperacionesDeclaradas();
							for ( int j = 0; j < opers.length; j++ )
							{
								NEspecificacion oper = opers[j];
								String id = oper.obtNombreSimpleCadena();
								if ( nombreMetodo.equals(id) )
								{
									espec = oper;
									break search;
								}
							}
						}
						klase = mu.obtSuperClase(klase);
					}
				}
				catch(ClaseNoEncontradaException ex)
				{
					throw _crearEjecucionException(alg,
						Str.get("error.1_class_not_found", ex.obtNombre())
					);
				}
			}
			
			if ( espec == null )
			{
				throw _crearEjecucionException(alg,
					Str.get("error.1_spec_not_found", "")
				);
			}

			TId id = alg.obtId();

			NDeclaracion[] pent = alg.obtParametrosEntrada();
			NDeclaracion[] psal = alg.obtParametrosSalida();

			try
			{
				///////////////////////////////////////////////////////////////////////
				// Enlace parametros reales de entrada
				for ( int i = 0; i < pent.length; i++ )
				{
					NDeclaracion d = pent[i];
					TId d_id = d.obtId();
					Tipo tipo = d.obtTipo();
					Object valor = val_args[i];

					if ( !tipo.esAsignableValor(valor) )
					{
						throw _crearEjecucionException(alg,
							Str.get("rt.error.2_cannot_convert_value", UtilValor.comoCadena(valor), tipo)
						);
					}
					EntradaTabla et = new EntradaTabla(d_id.obtId(), tipo);
					_insertarEnTablaSimbolos(et, d_id);
					tabSimb.ponValor(d_id.obtId(), valor);
				}

				///////////////////////////////////////////////////////////////////////
				// Revisar precondicion dada en la especificacion:
				_visitarCondicionEnEspecificacion(espec, A_PRECONDICION);

				///////////////////////////////////////////////////////////////////////
				// Declare parametros salida
				for ( int i = 0; i < psal.length; i++ )
				{
					NDeclaracion d = psal[i];
					EntradaTabla et = new EntradaTabla(d.obtId().obtId(), d.obtTipo());
					_insertarEnTablaSimbolos(et, d.obtId());
				}
			}
			catch (loro.compilacion.ChequeadorException ex)
			{
				throw new RuntimeException("ChequeadorException: " +ex);
			}


			Object res[] = null;

			if ( alg.implementadoEnLoro() )
			{
				boolean hubo_retorne = false;

				///////////////////////////////////////////////////////////////////////
				// Ejecute las acciones:
				Nodo[] acciones = alg.obtAcciones();
				try
				{
					for ( int i = 0; i < acciones.length; i++ )
					{
						pilaEjec.actualizarTope(acciones[i]);
						acciones[i].aceptar(this);
					}
				}
				catch(ControlRetorneException ret)
				{
					hubo_retorne = true;
					res = ret.obtResultado();
					NExpresion[] exprs = ret.obtExpresiones();

					// enlace los valores a los parametros de salida:
					// Esto es necesario para procesar poscondicion mas adelante.
					for ( int i = 0; i < res.length; i++ )
					{
						NDeclaracion d = psal[i];
						res[i] = _convertirValor(exprs[i], d.obtTipo(), exprs[i].obtTipo(), res[i]);
						tabSimb.ponValor(d.obtId().obtId(), res[i]);
					}
				}
				catch(ControlLanceException ex)
				{
					_pop();
					throw ex;
				}

				if ( !hubo_retorne )
				{
					// Tome valores enlazados a los par�metros de salida:
					res = new Object[psal.length];

					for ( int i = 0; i < res.length; i++ )
					{
						NDeclaracion d = psal[i];
						
						if ( !tabSimb.obtAsignado(d.obtId().obtId()) )
						{
							throw _crearEjecucionException(alg,
								Str.get("rt.error.1_output_var_not_assigned", d.obtId())
							);
						}
						
						res[i] = tabSimb.obtValor(d.obtId().obtId());
					}
				}
			}
			else if ( alg.implementadoEnLenguaje("java") )
			{
				Object obj = ejecutarAlgoritmoJava(alg, val_args);
				res = new Object[psal.length];
				if ( psal.length > 0 )
				{
					if ( obj == null 
					&&   ! Tipos.esReferencia(psal[0].obtTipo()) )
					{
						throw _crearEjecucionException(alg,
							Str.get("rt.error.1_output_var_not_assigned", psal[0].obtId())
						);
					}
					res[0] = obj;
				}

				// enlace los valores a los parametros de salida:
				// Esto es necesario para procesar poscondicion mas adelante.
				for ( int i = 0; i < res.length; i++ )
				{
					NDeclaracion d = psal[i];
					tabSimb.ponValor(d.obtId().obtId(), res[i]);
				}
			}
			else if ( alg.implementadoEnLenguaje("bsh") )
			{
				Object obj = ejecutarAlgoritmoBsh(alg, val_args);
				res = new Object[psal.length];
				if ( psal.length > 0 )
				{
					if ( obj == null 
					&&   ! Tipos.esReferencia(psal[0].obtTipo()) )
					{
						throw _crearEjecucionException(alg,
							Str.get("rt.error.1_output_var_not_assigned", psal[0].obtId())
						);
					}
					res[0] = obj;
				}

				// enlace los valores a los parametros de salida:
				// Esto es necesario para procesar poscondicion mas adelante.
				for ( int i = 0; i < res.length; i++ )
				{
					NDeclaracion d = psal[i];
					tabSimb.ponValor(d.obtId().obtId(), res[i]);
				}
			}
			else if ( alg.implementadoEnLenguaje("usr") )
			{
				ejecutarAlgoritmoUsr(alg, val_args, tabSimb);
				res = new Object[psal.length];
				if ( psal.length > 0 )
				{
					NDeclaracion d = psal[0];
					if ( !tabSimb.obtAsignado(d.obtId().obtId()) )
					{
						throw _crearEjecucionException(alg,
							Str.get("rt.error.1_output_var_not_assigned", d.obtId())
						);
					}
					res[0] = tabSimb.obtValor(d.obtId().obtId());
				}
			}
			else
			{
				String leng = alg.obtLenguajeImplementacion();
				throw _crearEjecucionException(alg,
					"C�digo de implementaci�n '" +leng+ "' no reconocido"
				);
			}

			//{ res.length <= 1 } Ver Chequeador.visitar(NEspecificacion).

			Object r;
			if ( res.length == 1)
			{
				r = res[0];
			}
			else
			{
				r = null;
			}

			///////////////////////////////////////////////////////////////////////
			// Revisar poscondicion dada en la especificacion:
			_visitarCondicionEnEspecificacion(espec, A_POSCONDICION);

			_pop();

			retorno = r;
		}
		catch (StackOverflowError e)
		{
			// normalmente, los "Error" de Java no son para ser manejados;
			// sin embargo, esta es la forma en que actualmente detectamos el
			// desbordamiento de pila.
			throw _crearEjecucionException(alg, 
				Str.get("rt.error.stack_overflow")
			);
		}
	}
	
	/**
	 * Ejecuta una asignaci�n.
	 */
	public void visitar(NAsignacion n)
	throws VisitanteException
	{
		NExpresion var = n.obtExpresionIzq();
		Tipo var_tipo = var.obtTipo();
		NExpresion expr = n.obtExpresionDer();
		Tipo expr_tipo = expr.obtTipo();

		Object val = _ejecutarExpresion(expr);
		val = _convertirValor(n, var_tipo, expr_tipo, val);

		if ( var instanceof NId )
		{
			tabSimb.ponValor(((NId)var).obtId().obtId(), val);
		}
		else if ( var instanceof NSubId )
		{
			// Visitamos la expresi�n de var; tomamos su valor que 
			// debe ser un Objeto; al id de este objeto le 
			// asignamos el val.
			NSubId subId = (NSubId) var;
			// tome la expresi�n (lado izquierdo del ultimo punto):
			NExpresion subId_e = subId.obtExpresion();
			// ejecutela:
			subId_e.aceptar(this);
			// tome el objeto correspondiente:
			Objeto obj = (Objeto) retorno;
			// asignele al id del objeto el valor:
			_ponValorAObjeto(obj, subId.obtId().obtId(), val, subId_e);
		}
		else // var instanceof NSubindexacion.
		{
			NSubindexacion si = (NSubindexacion) var;

			si.obtExpresionIzq().aceptar(this);
			Object obj = retorno;
			
			si.obtExpresionDer().aceptar(this);
			int i = ((Integer) retorno).intValue();
			
			_ponValorAArreglo(obj, i, val, si);
		}

		retorno = val;
	}
	
	/**
	 * Ejecuta el nodo.
	 */
	public void visitar(NCardinalidad n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		Object obj = _ejecutarExpresion(e);

		if ( obj == null )
		{
			throw _crearEjecucionException(n,
				Str.get("rt.error.null_pointer")
			);

		}
		int res;
		Tipo e_tipo = e.obtTipo();

		if ( e_tipo.esCadena() )
		{
			res = ((String) obj).length();
		}
		else if ( e_tipo.esArreglo() )
		{
			if ( obj instanceof ArregloBaseNoCero )
			{
				ArregloBaseNoCero abnc = (ArregloBaseNoCero) obj;
				res = abnc.array.length;
			}
			else
			{
				Object[] ev = (Object[]) obj;
				res = ev.length;
			}
		}
		else
		{
			throw new RuntimeException("Internal error: " +e_tipo);
		}

		retorno = new Integer(res);
	}
	
	/**
	 * Ejecuta el nodo.
	 */
	public void visitar(NCaso n)
	throws VisitanteException
	{
		Nodo[] acciones = n.obtAcciones();
		for ( int i = 0; i < acciones.length; i++ )
		{
			acciones[i].aceptar(this);
		}
	}
	
	/**
	 */
	public void visitar(NCiclo n)
	throws VisitanteException
	{
		Nodo[] acciones = n.obtAcciones();
		TId etq_ciclo = n.obtEtiqueta();
		while ( true )
		{
			try
			{
				_visitarAccionesIteracion(n, acciones);
			}
			catch ( ControlInteracionException ex )
			{
				TId etq_ex = ex.obtEtiqueta();

				// Esta exception es para este ciclo si su
				// etiqueta es nula, o si las dos etiquetas
				// son iguales:
				if ( etq_ex == null
				||  (etq_ciclo != null && etq_ex.obtId().equals(etq_ciclo.obtId())) )
				{
					// es para este ciclo.
					if ( ex.esTermine() )
					{
						break;
					}
					else
					{
						continue;
					}
				}
				else
				{
					// NO es para este ciclo; relance la exception:
					throw ex;
				}
			}
		}
	}
	
	/**
	 * Por cumplir con la implementaci�n.  NO se usa.
	 */
	public void visitar(NClase n)
	throws VisitanteException
	{
		throw new RuntimeException("Internal eror: Ejecutor.visitar(NClase) called");
	}
	
	/**
	 * Por cumplir con la implementaci�n.  NO se usa.
	 */
	public void visitar(NInterface n)
	throws VisitanteException
	{
		throw new RuntimeException("Internal eror: Ejecutor.visitar(NInterface) called");
	}
	
	public void visitar(NCondicion n)
	throws VisitanteException
	{
		NExpresion e = n.obtCondicion();
		if ( ((Boolean)_ejecutarExpresion(e)).booleanValue() )
		{
			retorno = _ejecutarExpresion(n.obtPrimeraAlternativa());
		}
		else
		{
			retorno = _ejecutarExpresion(n.obtSegundaAlternativa());
		}
	}
	
	/**
	 * Visita un constructor para su ejecucion.
	 *
	 * Se asume que en argsParaAlgoritmo se encuentran los
	 * valores ya calculados de los argumentos del constructor.
	 *
	 * Se enlazan los parametros formales con los argsParaAlgoritmo,
	 * se visitan las acciones y hace manejo del valor de retorno
	 * del algoritmo (en caso que lo tenga).
	 *
	 * 2000-07-14
	 */
	public void visitar(NConstructor n)
	throws VisitanteException
	{
		if ( argsParaAlgoritmo == null )
			throw new RuntimeException("Internal error: argsParaAlgoritmo == null");

		// Tome nota de los argumentos:
		Object[] val_args = argsParaAlgoritmo;

		NClase clase = n.obtClase();
		_pushClase(clase);

		List decl_atrs = null;
		try
		{
			decl_atrs = _obtDeclaracionesAtributos(clase);
		}
		catch(ClaseNoEncontradaException ex)
		{
			throw _crearEjecucionException(n,
				Str.get("error.1_class_not_found", ex.obtNombre())
			);
		}

		// Cree objeto a retornar y actualice "�ste":
		Objeto obj = new Objeto(clase);
		LObjeto save_este = este;
		este = obj;

		try
		{
			int marca = tabSimb.marcar();
			for ( Iterator it = decl_atrs.iterator(); it.hasNext(); )
			{
				NDeclDesc d = (NDeclDesc) it.next();
				
				Tipo d_tipo = d.obtTipo();
				Object val;
				if ( d.tieneInicializacion() )
				{
					NExpresion d_e = d.obtExpresion();
					d_e.aceptar(this);
					val = retorno;
				}
				else
				{
					val = d_tipo.obtValorDefecto();
				}
				TId d_id = d.obtId();
				String atr = d_id.obtId();
				
				_ponValorAObjeto(obj, atr, val, n);


				// Se hacen las declaraciones de los atributos para dar soporte
   				// a las posibles expresiones de inicializaci�n.
				EntradaTabla et = new EntradaTabla(atr, d_tipo);
				et.ponValor(val);
				_insertarEnTablaSimbolos(et, d_id);
			}
			
			// Se deshacen las declaraciones de atributos: 
			tabSimb.irAMarca(marca);
			

			// Enlace par�metros reales para el constructor:
			NDeclaracion[] n_pent = n.obtParametrosEntrada();
			for ( int i = 0; i < n_pent.length; i++ )
			{
				NDeclaracion d = n_pent[i];
				TId d_id = d.obtId();
				Tipo d_tipo = d.obtTipo();
				EntradaTabla et = new EntradaTabla(d_id.obtId(), d_tipo);
				_insertarEnTablaSimbolos(et, d_id);
				tabSimb.ponValor(d_id.obtId(), val_args[i]);
			}
			// Ejecute las acciones del constructor:
			Nodo[] c_acciones = n.obtAcciones();
			for ( int i = 0; i < c_acciones.length; i++ )
			{
				pilaEjec.actualizarTope(c_acciones[i]);
				c_acciones[i].aceptar(this);
			}
		}
		catch (loro.compilacion.ChequeadorException ex)
		{
			throw new RuntimeException("Internal error: " +ex);
		}
		finally
		{
			este = save_este;
		}

		_pop();

		retorno = obj;
	}
	
	public void visitar(NContinue n)
	throws VisitanteException
	{
		TId etq = n.obtEtiqueta();
		NExpresion expr = n.obtExpresion();

		boolean doit = true;

		if ( expr != null )
		{
			Boolean b = (Boolean) _ejecutarExpresion(expr);
			doit = b.booleanValue();
		}

		if ( doit )
		{
			throw new ControlInteracionException(false, etq);
		}
	}
	
	public void visitar(NConvertirTipo n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		Object val = _ejecutarExpresion(e);

		retorno = _convertirValor(n, n.obtTipo(), e.obtTipo(), val);
	}
	
	public void visitar(NCorrDer n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		int ev = ((Integer)_ejecutarExpresion(e)).intValue();
		int fv = ((Integer)_ejecutarExpresion(f)).intValue();
		retorno = new Integer(ev >> fv);
	}
	
	public void visitar(NCorrDerDer n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		int ev = ((Integer)_ejecutarExpresion(e)).intValue();
		int fv = ((Integer)_ejecutarExpresion(f)).intValue();
		retorno = new Integer(ev >>> fv);
	}
	
	public void visitar(NCorrIzq n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		int ev = ((Integer)_ejecutarExpresion(e)).intValue();
		int fv = ((Integer)_ejecutarExpresion(f)).intValue();
		retorno = new Integer(ev << fv);
	}
	
	/**
	 * Visita una expresi�n arreglo.
	 * Se crea un arreglo con los valores de las expresiones correspondientes.
	 */
	public void visitar(NExpresionArreglo n)
	throws VisitanteException
	{
		NExpresion[] exprs = n.obtExpresiones();
		NExpresion e = exprs[0];
		Tipo e_tipo = e.obtTipo();
		Object[] vals = new Object[exprs.length];
		
		for ( int i = 0; i < exprs.length; i++ )
		{
			NExpresion f = exprs[i];
			vals[i] = _ejecutarExpresion(f);
			Tipo f_tipo = f.obtTipo();
			vals[i] = _convertirValor(n, e_tipo, f_tipo, vals[i]);
		}

		retorno = vals;
	}
	
	/**
	 * Ejecuta una creacion de arreglo.
	 */
	public void visitar(NCrearArreglo n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		NExpresion g = n.obtExpresionLimiteSuperior();

		int size;  // tamano para el arreglo
		
		int base = ((Integer) _ejecutarExpresion(e)).intValue();
		
		if ( g != null )
		{
			// es modo rango:  [e .. g] ...
			int sup = ((Integer) _ejecutarExpresion(g)).intValue();
			size = sup - base + 1;
		}
		else
		{
			// es modo simple:  [e] ...
			size = base;
			base = 0;
		}
		
		if ( size < 0 )
		{
			throw _crearEjecucionException(n,
				Str.get("rt.error.negative_size", ""+size)
			);
		}
		
		Object[] array = new Object[size];
		for ( int i = 0; i < size; i++ )
		{
			array[i] = _ejecutarExpresion(f);
		}
		
		if ( base == 0 )
		{
	        retorno = array;
		}
		else
		{
			retorno = new ArregloBaseNoCero(base, array);
		}
	}
	
	public void visitar(NCrearArregloTipoBase n)
	throws VisitanteException
	{
		Tipo tipo = n.obtTipo();
		retorno = tipo.obtValorDefecto();
	}
	
	public void visitar(NCrearObjeto n)
	throws VisitanteException
	{
		NExpresion[] args = n.obtArgumentos();
		Object[] a = new Object[args.length];

		for ( int i = 0; i < args.length; i++ )
		{
			args[i].aceptar(this);
			a[i] = retorno;
		}

		// ponga los argumentos para el algoritmo del constructor:
		argsParaAlgoritmo = a;

		// pida al constructor que me acepte:
		NConstructor constructor = n.obtConstructor();
		constructor.aceptar(this);
	}
	
	/**
	 * Ejecuta una expresi�n cuantificada.
	 *
	 * Se retorna simplemente cierto y se le avisa a
	 * la afirmacion que se satisface directamente.
	 */
	public void visitar(NCuantificado n)
	throws VisitanteException
	{
		retorno = Boolean.TRUE;
		afirmacionCiertaPorCuantificacion = true;
	}
	
	/**
	 * Ejecuta una decision.
	 */
	public void visitar(NDecision n)
	throws VisitanteException
	{
		// evalue expresi�n principal:
		NExpresion expr = n.obtCondicion();
		Boolean b = (Boolean) _ejecutarExpresion(expr);

		// si es cierta, ejecute acciones correspondientes:
		if ( b.booleanValue() )
		{
			Nodo[] as = n.obtAccionesCierto();
			for ( int i = 0; i < as.length; i++ )
			{
				as[i].aceptar(this);
			}
			return;  // listo
		}

		// vaya entonces por las posibles opciones "si_no_si": 
		NDecisionSiNoSi[] sinosis = n.obtSiNoSis();
		if ( sinosis.length > 0 )
		{
			for ( int k = 0; k < sinosis.length; k++ )
			{
				// evalue expresi�n:
				NExpresion exprk = sinosis[k].obtCondicion();
				Boolean bk = (Boolean) _ejecutarExpresion(exprk);
				
				// si es cierta, ejecute acciones correspondientes:
				if ( bk.booleanValue() )
				{
					Nodo[] ask = sinosis[k].obtAcciones();
					for ( int i = 0; i < ask.length; i++ )
					{
						ask[i].aceptar(this);
					}
					return;  // listo
				}
			}
		}
		
		// intente ahora por el "si_no" (si lo hay):
		Nodo[] an = n.obtAccionesFalso();
		if ( an != null )
		{
			for ( int i = 0; i < an.length; i++ )
			{
				an[i].aceptar(this);
			}
		}
	}
	
	/**
	 * No debe llamarse. La ejecucion de un fragmento "si_no_si" se hace
	 * desde la misma decision.
	 */
	public void visitar(NDecisionSiNoSi n)
	throws VisitanteException
	{
		throw new RuntimeException("Internal error: visitar(NDecisionSiNoSi) called");
	}
	
	
	public void visitar(NDecisionMultiple n)
	throws VisitanteException
	{
		NExpresion expr = n.obtExpresion();
		Object ve = _ejecutarExpresion(expr);
		boolean hubo_caso = false;			// hubo alg�n caso?
		boolean seguir_ejecutando = false;	// para cuando no hay fin caso
		NCaso[] casos = n.obtCasos();
		for ( int i = 0; i < casos.length; i++ )
		{
			NCaso c = casos[i];
			if ( seguir_ejecutando )
			{
				c.aceptar(this);
			}
			else
			{
				NExpresion c_expr = c.obtExpresion();
				c_expr.aceptar(this);
				Object vc = retorno;
				if ( vc.equals(ve) )
				{
					c.aceptar(this);
					hubo_caso = true;
					if ( ! c.tieneFinCaso() )
						seguir_ejecutando = true;
					else
						break;
				}
			}
		}

		NCaso si_no = n.obtCasoSiNo();
		if ( si_no != null && ( !hubo_caso || seguir_ejecutando) )
			si_no.aceptar(this);
	}
	/**
	 * Ejecuta una declaracion.
	 */
	public void visitar(NDeclaracion n)
	throws VisitanteException
	{
		Tipo tipo = n.obtTipo();
		Object val = null;	// de expresi�n, si aparece.

		NExpresion e = n.obtExpresion();
		if ( e != null )
		{
			e.aceptar(this);
			val = _convertirValor(n, tipo, e.obtTipo(), retorno);
		}

		TId[] ids = n.obtIds();
		TId id = null;

		// primero haga las declaraciones como tal:
		try
		{
			if ( ids != null )
			{
				for ( int k = 0; k < ids.length; k++ )
				{
					EntradaTabla et = new EntradaTabla(ids[k].obtId(), tipo);
					_insertarEnTablaSimbolos(et, ids[k]);
				}
			}
			else
			{
				id = n.obtId();
				EntradaTabla et = new EntradaTabla(id.obtId(), tipo);
				_insertarEnTablaSimbolos(et, id);
			}
		}
		catch (loro.compilacion.ChequeadorException ex)
		{
			// Debe corresponder a que estoy dentro de un ciclo
			// y esta es la vez >= 2 que se pasa por aqu�.
			// Simplemente se IGNORA.
			// Pero esta soluci�n es muy ineficiente
			// porque la inserci�n en la tabla cuesta bastante.
			// PENDIENTE
		}

		///////////////////////
		// finalmente, si hubo expresi�n, haga la asignacion:
		if ( ids != null )
		{
			if ( e != null )
			{
				for ( int k = 0; k < ids.length; k++ )
				{
					tabSimb.ponValor(ids[k].obtId(), val);
				}
			}
		}
		else if ( e != null )
		{
			tabSimb.ponValor(id.obtId(), val);
		}
	}
	/**
	 * No hace nada.
	 */
	public void visitar(NDeclDesc n)
	throws VisitanteException
	{
	}
	/**
	 * No hace nada.
	 */
	public void visitar(NDescripcion n)
	throws VisitanteException
	{
	}
	/**
	 */
	public void visitar(NDiferente n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		Object e_val = _ejecutarExpresion(e);
		Object f_val = _ejecutarExpresion(f);
		retorno = new Boolean(! Util.valoresIguales(e_val, f_val));
	}
	public void visitar(NDivReal n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		Number ev = (Number)_ejecutarExpresion(e);
		Number fv = (Number)_ejecutarExpresion(f);

		try
		{
			if ( n.obtTipo().esEntero() )
				retorno = new Integer(ev.intValue() / fv.intValue());
			else
				retorno = new Double(ev.doubleValue() / fv.doubleValue());
		}
		catch (ArithmeticException ex)
		{
			throw _crearEjecucionException(n, Str.get("rt.error.arithmetic"));
		}
	}
	public void visitar(NEquivalencia n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		retorno = new Boolean(
				   ((Boolean)_ejecutarExpresion(e)).booleanValue()
				== ((Boolean)_ejecutarExpresion(f)).booleanValue()
		);
	}
	
	public void visitar(NEsInstanciaDe n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		Object object = _ejecutarExpresion(e);
		Tipo e_tipo = e.obtTipo();
		Tipo trev = n.obtNTipoRevisado().obtTipo();

		try
		{
			retorno = new Boolean(Tipos.isInstanceOf(object, e_tipo, trev));
		}
		catch(ClaseNoEncontradaException exc)
		{
			throw _crearEjecucionException(n,
				Str.get("error.1_class_not_found", exc.obtNombre())
			);
		}
	}
	
	/**
	 * NO SE USA. Vea _visitarCondicion(NEspecificacion, int).
	 */
	public void visitar(NEspecificacion n)
	throws VisitanteException
	{
		throw new RuntimeException(
			"Internal error: Ejecutor.visitar(NEspecificacion) called"
		);
	}

	/**
	 * Ejecuta una expresi�n "�ste".
	 */
	public void visitar(NEste n)
	throws VisitanteException
	{
		retorno = este;
	}

	/**
	 * Por cumplir con la implementaci�n.  NO se usa.
	 */
	public void visitar(NFuente n)
	throws VisitanteException
	{
		throw new RuntimeException(
			"Internal error: Ejecutor.visitar(NFuente) called"
		);
	}

	/**
	 * Ejecuta un nodo NId.  Version vieja buggy
	 * Ver mas abajo por nueva version.
	 */
	public void visitar__OLD(NId n)
	throws VisitanteException
	{
		if ( enInvocacion )
		{
			if ( _resolverIdEnAmbiente(n) )
			{
				if ( retorno instanceof NEspecificacion )
				{
					// Solucion a bug detectado 2002-04-05:
					// En compilacion se manejo por tabla de simbolos, no
					// por ambiente, y no debio tratarse de especificacion.
					// Asi que intente por tabla de simbolos:
					if (  _resolverIdEnTablaSimbolos(n) )
					{
						// OK
					}
					else
					{
						// Solo por especificion: problema!
						throw _crearEjecucionException(n,
							"Unexpected: '" +n.obtId()+ "' is a specification!"
						);
					}
				}
			}
			else if (  _resolverIdEnTablaSimbolos(n) )
			{
				// OK
			}
			else
			{
				// problema:
				throw _crearEjecucionException(n,
					Str.get("error.1_algorithm_not_found", n.obtId())
				);
			}
		}
		else
		{
			if ( _resolverIdEnTablaSimbolos(n)
			||   _resolverIdEnAmbiente(n) )
			{
				// OK
			}
			else
			{
				// problema:
				throw _crearEjecucionException(n,
					Str.get("error.1_id_undefined", n.obtId())
				);
			}
		}
	}

	/**
	 * Ejecuta un nodo NId.  2004-06-30: Version nueva
	 */
	public void visitar(NId n) throws VisitanteException {
		if ( _resolverIdEnTablaSimbolos(n)
		||   _resolverIdEnAmbiente(n) ) {
			// OK
		}
		else {
			// problema: genere exception:
			if ( enInvocacion )
				throw _crearEjecucionException(n,
					Str.get("error.1_algorithm_not_found", n.obtId())
				);
			else
				throw _crearEjecucionException(n,
					Str.get("error.1_id_undefined", n.obtId())
				);
		}
	}

	public void visitar(NIgual n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();

		Object e_val = null;
		Object f_val = null;

		if ( !enAfirmacion )
		{
			e_val = _ejecutarExpresion(e);
			f_val = _ejecutarExpresion(f);
			retorno = new Boolean(Util.valoresIguales(e_val, f_val));
			return;
		}

		//{ enAfirmacion }

		// Manejo para posibles variables sem�nticas:

		TId id_e = Util.obtVarSemantica(e);
		TId id_f = Util.obtVarSemantica(f);

		if ( id_e == null && id_f == null )
		{
			//{ No hay variables sem�nticas }
			// Haga lo normal:
			e_val = _ejecutarExpresion(e);
			f_val = _ejecutarExpresion(f);
			retorno = new Boolean(Util.valoresIguales(e_val, f_val));
			return;	// listo
		}


		//{ Hay variables sem�nticas }

		EjecucionVisitanteException ex1 = null, ex2 = null;

		int mal = 2;	// asuma mal las dos expresiones
		try
		{
			e_val = _ejecutarExpresion(e);
			mal--;
		}
		catch(EjecucionVisitanteException sex)
		{
			ex1 = sex;
		}
		try
		{
			f_val = _ejecutarExpresion(f);
			mal--;
		}
		catch(EjecucionVisitanteException sex)
		{
			ex2 = sex;
		}

		if ( mal == 2 )
		{
			// ambas expresiones malas.
			// Lance la primera exception:
			throw ex1;
		}


		if ( mal == 0 )
		{
			// ambas expresiones buenas.
			// Haga lo normal:
			e_val = _ejecutarExpresion(e);
			f_val = _ejecutarExpresion(f);
			retorno = new Boolean(Util.valoresIguales(e_val, f_val));
			return;	// listo
		}

		//{ mal == 1 }
		// Es decir, { ex1 != null XOR ex2 != null }
		// Trate de declarar y asignar una variable semantica

		if ( ex1 != null )
		{
			//{ e mal && f bien }
			// e debe ser variable sem�ntica:
			if ( id_e == null )
				throw ex1;		// no lo es

			//{ id_e != null }

			// Instancie (o sea, declare) id_e:
			EntradaTabla et = new EntradaTabla(id_e.obtId(), f.obtTipo());
			// y pongale el valor de f:
			et.ponValor(f_val);
			_insertarEnTablaSimbolos(et, id_e);
		}
		else //{ ex2 != null }
		{
			//{ e bien && f mal }
			// f debe ser variable sem�ntica:
			if ( id_f == null )
				throw ex2;		// no lo es

			//{ id_f != null }

			// Instancie (o sea, declare) id_f:
			EntradaTabla et = new EntradaTabla(id_f.obtId(), e.obtTipo());
			// y pongale el valor de e:
			et.ponValor(e_val);
			_insertarEnTablaSimbolos(et, id_f);
		}

		// Igualdad satisfecha por instanciacion:
		retorno = Boolean.TRUE;
	}
	public void visitar(NImplicacion n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		retorno = new Boolean(
				  !((Boolean)_ejecutarExpresion(e)).booleanValue()
				|| ((Boolean)_ejecutarExpresion(f)).booleanValue()
		);
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Ejecuta una invocaci�n.
	 */
	public void visitar(NInvocacion n)
	throws VisitanteException
	{
		NExpresion expr = n.obtExpresion();
		pilaEjec.actualizarTope(n);

		NExpresion[] args = n.obtArgumentos();
		Object[] a = new Object[args.length];

		// haga primero una ejecuci�n directa de los argumentos,
		// m�s abajo se revisa posible conversi�n de estos valores:
		for ( int i = 0; i < args.length; i++ ) {
			a[i] = _ejecutarExpresion(args[i]);
		}

		LObjeto save_este = este;
		boolean save_enInvocacion = enInvocacion;
		LObjeto save_objInvocado = objInvocado;

		try {
			// ejecute la expresi�n que se va a invocar:
			objInvocado = null;
			enInvocacion = expr instanceof NId || expr instanceof NSubId;
			Object invoc = _ejecutarExpresion(expr);
			
			enInvocacion = save_enInvocacion;

			if ( invoc instanceof NAlgoritmo ) {
				NAlgoritmo alg = (NAlgoritmo) invoc;
	
				// confronte con los tipos esperados para posible conversion
				// de valores:
				NDeclaracion[] pent = alg.obtParametrosEntrada();

				if ( pent.length != args.length ) {
					throw new RuntimeException(
						"pent.length=" +pent.length+ " != " +args.length+ "=args.length\n"+
						"expr = " +expr+ "\n"+
						"alg = " +alg+ "  " +alg.getTypeString()+ "\n"
					);
				}
				
				for ( int i = 0; i < args.length; i++ ) {
					a[i] = _convertirValor(
								args[i],            // nodo
								pent[i].obtTipo(),  // tipo esperado
								args[i].obtTipo(),  // tipo valor
								a[i]                // valor
					);
				}
	
				
				este = objInvocado;
				argsParaAlgoritmo = a;
	
				// todo listo para ejecutar el algoritmo:
				try {
					alg.aceptar(this);
				}
				finally {
					este = save_este;
				}
			}
			else if ( invoc instanceof LAlgoritmo ) {
				// PENDIENTE (2001-10-11)
				// Hacer mismo manejo que con NAlgoritmo sobre posible
				// conversion de valores.
	
				LAlgoritmo lalg = (LAlgoritmo) invoc;
				try {
					retorno = lalg.ejecutar(a);
				}
				catch(LException ex) {
					throw _crearEjecucionException(expr,
						"Error executing LAlgoritmo: " +ex.getMessage()
					);
				}
			}
			else if ( invoc instanceof LMethod ) {
				// PENDIENTE (2001-10-11)
				// Hacer mismo manejo que con NAlgoritmo sobre posible
				// conversion de valores.
	
				LMethod lalg = (LMethod) invoc;
				try {
					retorno = lalg.invoke(este, a);
				}
				catch(LException ex) {
					throw _crearEjecucionException(expr,
						"Error invoking LMethod: " +ex.getMessage()
					);
				}
			}
			else if ( invoc == null ) {
				throw _crearEjecucionException(n,
					Str.get("rt.error.null_algorithm_called")
				);
			}
			else {
				throw new RuntimeException(
					"Internal error: invoc is " +invoc.getClass().toString()
						+ "\ninvoc.toString()= " +invoc
				);
			}
		}
		finally {
			enInvocacion = save_enInvocacion;
			objInvocado = save_objInvocado;
		}
	}
	
	public void visitar(NLiteralBooleano n)
	throws VisitanteException
	{
		retorno = n.obtValor();
	}
	public void visitar(NLiteralCadena n)
	throws VisitanteException
	{
		retorno = n.obtValor();
	}
	public void visitar(NLiteralCaracter n)
	throws VisitanteException
	{
		retorno = n.obtValor();
	}
	public void visitar(NLiteralEntero n)
	throws VisitanteException
	{
		retorno = n.obtValor();
	}
	public void visitar(NLiteralNulo n)
	throws VisitanteException
	{
		retorno = n.obtValor();
	}
	public void visitar(NLiteralReal n)
	throws VisitanteException
	{
		retorno = n.obtValor();
	}
	
	public void visitar(NMas n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();

		Tipo tipo = n.obtTipo();
		if ( tipo.esCadena() )
		{
			Object re = _ejecutarExpresion(e);
			Object rf = _ejecutarExpresion(f);
			retorno = UtilValor.comoCadena(re) + UtilValor.comoCadena(rf);
		}
		else
		{
			Number ev = (Number) _ejecutarExpresion(e);
			Number fv = (Number) _ejecutarExpresion(f);
			if ( tipo.esEntero() )
				retorno = new Integer(ev.intValue() + fv.intValue());
			else
				retorno = new Double(ev.doubleValue() + fv.doubleValue());
		}
	}
	public void visitar(NMayor n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		Tipo e_tipo = e.obtTipo();
		NExpresion f = n.obtExpresionDer();
		Tipo f_tipo = f.obtTipo();

		e.aceptar(this);
		Object ev = retorno;
		f.aceptar(this);
		Object fv = retorno;
		boolean res;

		if ( e_tipo.esNumerico() )
		{	if ( e_tipo.esReal() || f_tipo.esReal() )
				res = ((Number)ev).doubleValue() > ((Number)fv).doubleValue();
			else
				res = ((Number)ev).intValue() > ((Number)fv).intValue();
		}
		else if ( e_tipo.esCaracter() )
			res = ((Character)ev).charValue() > ((Character)fv).charValue();
		else if ( e_tipo.esCadena() )
			res = ((String)ev).compareTo((String)fv) > 0;
		else
			throw new RuntimeException("Internal error: " +e_tipo);

		retorno = new Boolean(res);
	}
	public void visitar(NMayorIgual n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		Tipo e_tipo = e.obtTipo();
		NExpresion f = n.obtExpresionDer();
		Tipo f_tipo = f.obtTipo();

		e.aceptar(this);
		Object ev = retorno;
		f.aceptar(this);
		Object fv = retorno;
		boolean res;

		if ( e_tipo.esNumerico() )
		{	if ( e_tipo.esReal() || f_tipo.esReal() )
				res = ((Number)ev).doubleValue() >= ((Number)fv).doubleValue();
			else
				res = ((Number)ev).intValue() >= ((Number)fv).intValue();
		}
		else if ( e_tipo.esCaracter() )
			res = ((Character)ev).charValue() >= ((Character)fv).charValue();
		else if ( e_tipo.esCadena() )
			res = ((String)ev).compareTo((String)fv) >= 0;
		else
			throw new RuntimeException("Internal error: " +e_tipo);

		retorno = new Boolean(res);
	}
	public void visitar(NMenor n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		Tipo e_tipo = e.obtTipo();
		NExpresion f = n.obtExpresionDer();
		Tipo f_tipo = f.obtTipo();

		e.aceptar(this);
		Object ev = retorno;
		f.aceptar(this);
		Object fv = retorno;
		boolean res;

		if ( e_tipo.esNumerico() )
		{	if ( e_tipo.esReal() || f_tipo.esReal() )
				res = ((Number)ev).doubleValue() < ((Number)fv).doubleValue();
			else
				res = ((Number)ev).intValue() < ((Number)fv).intValue();
		}
		else if ( e_tipo.esCaracter() )
			res = ((Character)ev).charValue() < ((Character)fv).charValue();
		else if ( e_tipo.esCadena() )
			res = ((String)ev).compareTo((String)fv) < 0;
		else
			throw new RuntimeException("Internal error: " +e_tipo);

		retorno = new Boolean(res);
	}
	public void visitar(NMenorIgual n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		Tipo e_tipo = e.obtTipo();
		NExpresion f = n.obtExpresionDer();
		Tipo f_tipo = f.obtTipo();

		e.aceptar(this);
		Object ev = retorno;
		f.aceptar(this);
		Object fv = retorno;
		boolean res;

		if ( e_tipo.esNumerico() )
		{	if ( e_tipo.esReal() || f_tipo.esReal() )
				res = ((Number)ev).doubleValue() <= ((Number)fv).doubleValue();
			else
				res = ((Number)ev).intValue() <= ((Number)fv).intValue();
		}
		else if ( e_tipo.esCaracter() )
			res = ((Character)ev).charValue() <= ((Character)fv).charValue();
		else if ( e_tipo.esCadena() )
			res = ((String)ev).compareTo((String)fv) <= 0;
		else
			throw new RuntimeException("Internal error: " +e_tipo);

		retorno = new Boolean(res);
	}
	public void visitar(NMenos n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		Number ev = (Number)_ejecutarExpresion(e);
		Number fv = (Number)_ejecutarExpresion(f);

		if ( n.obtTipo().esEntero() )
			retorno = new Integer(ev.intValue() - fv.intValue());
		else
			retorno = new Double(ev.doubleValue() - fv.doubleValue());
	}
	public void visitar(NMientras n)
	throws VisitanteException
	{
		TId etq_ciclo = n.obtEtiqueta();
		NExpresion expr = n.obtCondicion();
		Nodo[] acciones = n.obtAcciones();
		while ( ((Boolean)_ejecutarExpresion(expr)).booleanValue() )
		{
			try
			{
				_visitarAccionesIteracion(n, acciones);
			}
			catch ( ControlInteracionException ex )
			{
				TId etq_ex = ex.obtEtiqueta();

				// Esta exception es para este ciclo si su
				// etiqueta es nula, o si las dos etiquetas
				// son iguales:
				if ( etq_ex == null
				||  (etq_ciclo != null && etq_ex.obtId().equals(etq_ciclo.obtId())) )
				{
					// es para este ciclo.
					if ( ex.esTermine() )
					{
						break;
					}
					else
					{
						continue;
					}
				}
				else
				{
					// NO es para este ciclo; relance la exception:
					throw ex;
				}
			}
		}
	}
	public void visitar(NMod n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		Number ev = (Number)_ejecutarExpresion(e);
		Number fv = (Number)_ejecutarExpresion(f);

		retorno = new Integer(ev.intValue() % fv.intValue());
	}
	public void visitar(NNeg n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();

		Tipo e_tipo = e.obtTipo();
		Number ev = (Number) _ejecutarExpresion(e);
		if ( e_tipo.esEntero() )
			retorno = new Integer( - ev.intValue() );
		else
			retorno = new Double( - ev.doubleValue() );
	}
	public void visitar(NNo n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		Boolean ev = (Boolean) _ejecutarExpresion(e);
		retorno = new Boolean( ! ev.booleanValue() );
	}
	public void visitar(NNoBit n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		Integer ev = (Integer) _ejecutarExpresion(e);
		retorno = new Integer( ~ ev.intValue() );
	}
	//////////////////////////////////////////////////////////////////////
	/**
	 * Se ejecuta un nombre.
	 * Siempre se asume que debe ser un algoritmo.
	 * PENDIENTE una revision de esto.
	 */
	public void visitar(NNombre n)
	throws VisitanteException
	{
		// Se busca por paquete:
		TNombre nom = n.obtNombre();
		NAlgoritmo alg = _obtAlgoritmoParaNombre(nom);
		if ( alg == null )
		{
			throw _crearEjecucionException(n,
				Str.get("rt.error.1_name_not_found", nom)
			);
		}
		retorno = alg;
	}
	/** Visits a NQualifiedName */
	public void visitar(NQualifiedName n) throws VisitanteException {
		// Se busca por paquete:
		TNombre nom = n.obtNombre();
		NAlgoritmo alg = _obtAlgoritmoParaNombre(nom);
		if ( alg == null ) {
			throw _crearEjecucionException(n,
				Str.get("rt.error.1_name_not_found", nom)
			);
		}
		retorno = alg;
	}



	public void visitar(NO n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		retorno = new Boolean(
				   ((Boolean)_ejecutarExpresion(e)).booleanValue()
				|| ((Boolean)_ejecutarExpresion(f)).booleanValue()
		);
	}
	public void visitar(NOArit n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		if ( n.obtTipo().esEntero() )
		{
			retorno = new Integer
			(	  ((Integer)_ejecutarExpresion(e)).intValue()
				| ((Integer)_ejecutarExpresion(f)).intValue()
			);
		}
		else
		{	//{ tipo esBooleano }
			retorno = new Boolean
			(	   ((Boolean)_ejecutarExpresion(e)).booleanValue()
				|| ((Boolean)_ejecutarExpresion(f)).booleanValue()
			);
		}
	}
	public void visitar(NOExc n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		if ( n.obtTipo().esEntero() )
		{
			retorno = new Integer
			(	  ((Integer)_ejecutarExpresion(e)).intValue()
				^ ((Integer)_ejecutarExpresion(f)).intValue()
			);
		}
		else
		{	//{ tipo esBooleano }
			retorno = new Boolean
			(	  ((Boolean)_ejecutarExpresion(e)).booleanValue()
				^ ((Boolean)_ejecutarExpresion(f)).booleanValue()
			);
		}
	}
	public void visitar(NPaquete n)
	throws VisitanteException
	{
		throw new RuntimeException("Internal error: visitar(NPaquete) called");
	}

	public void visitar(NForEach n)
	throws VisitanteException
	{
		TId etq_ciclo = n.obtEtiqueta();
		NExpresion in = n.obtExpresionEn();

		String ident;
		int marca = 0;  // cualquier inicializacion

		NDeclaracion dec = n.obtDeclaracion();
		if ( dec != null )		// Declaraci�n interna
		{
			// Marcar la tabla;
			marca = tabSimb.marcar();
			dec.aceptar(this);
			ident = dec.obtId().obtId();
		}
		else
		{
			ident = n.obtId().obtId();
		}

		Nodo[] acciones = n.obtAcciones();

		
		Object obj = _ejecutarExpresion(in);
		String string = null;
		Object[] array = null;
		int len;
		if ( obj instanceof String )
		{
			string = (String) obj;
			len = string.length();
		}
		else 
		{
			if ( obj instanceof ArregloBaseNoCero )
				array = ((ArregloBaseNoCero) obj).array;
			else 
				array = (Object[]) obj;
			len = array.length;
		}
		
		try
		{
			for ( int i = 0; i < len; i++ )
			{
				Object val;
				if ( array != null )
					val = array[i];
				else
					val = new Character(string.charAt(i));
				
				// ponga valor a ident:
				tabSimb.ponValor(ident, val);
	
				// ejecute las acciones:
				try
				{
					_visitarAccionesIteracion(n, acciones);
				}
				catch ( ControlInteracionException ex )
				{
					TId etq_ex = ex.obtEtiqueta();

					// Esta exception es para este ciclo si su
					// etiqueta es nula, o si las dos etiquetas
					// son iguales:
					if ( etq_ex == null
					||  (etq_ciclo != null && etq_ex.obtId().equals(etq_ciclo.obtId())) )
					{
						// es para este ciclo.
						if ( ex.esTermine() )
						{
							break;
						}
						else
						{
							// Nada que hacer.
							// Simplemente vaya y actualice variable de control.
						}
					}
					else
					{
						// NO es para este ciclo; relance la exception:
						throw ex;
					}
				}
			}
		}
		finally
		{
			if ( dec != null )		// Declaraci�n interna
			{
				tabSimb.irAMarca(marca);
			}
		}
	}
	
	public void visitar(NPara n)
	throws VisitanteException
	{
		TId etq_ciclo = n.obtEtiqueta();
		NExpresion desde = n.obtExpresionDesde();
		int dv = ((Integer)_ejecutarExpresion(desde)).intValue();
		int pv = 1;

		NExpresion paso = n.obtExpresionPaso();
		if ( paso != null )
			pv = ((Integer)_ejecutarExpresion(paso)).intValue();

		NExpresion hasta = n.obtExpresionHasta();
		int hv = ((Integer)_ejecutarExpresion(hasta)).intValue();

		String ident;
		int marca = 0;  // cualquier inicializacion

		NDeclaracion dec = n.obtDeclaracion();
		if ( dec != null )		// Declaraci�n interna
		{
			// Marcar la tabla;
			marca = tabSimb.marcar();
			dec.aceptar(this);
			ident = dec.obtId().obtId();
		}
		else
		{
			ident = n.obtId().obtId();
		}

		Nodo[] acciones = n.obtAcciones();
		boolean bajando = n.esBajando();

		// ponga el valor inicial para ident:
		tabSimb.ponValor(ident, new Integer(dv));

		try
		{
			while ( true )
			{
				// consulte valor actual de la variable de control:
				int i = ((Integer) tabSimb.obtValor(ident)).intValue();
	
				// verifique condicion de continuacion en el ciclo:
				if (  bajando && i >= hv   ||   !bajando && i <= hv )
				{
					// ejecute las acciones:
					try
					{
						_visitarAccionesIteracion(n, acciones);
					}
					catch ( ControlInteracionException ex )
					{
						TId etq_ex = ex.obtEtiqueta();
	
						// Esta exception es para este ciclo si su
						// etiqueta es nula, o si las dos etiquetas
						// son iguales:
						if ( etq_ex == null
						||  (etq_ciclo != null && etq_ex.obtId().equals(etq_ciclo.obtId())) )
						{
							// es para este ciclo.
							if ( ex.esTermine() )
							{
								break;
							}
							else
							{
								// Nada que hacer.
								// Simplemente vaya y actualice variable de control.
							}
						}
						else
						{
							// NO es para este ciclo; relance la exception:
							throw ex;
						}
					}
	
					// haga la actualizacion de la variable de control:
					i = ((Integer) tabSimb.obtValor(ident)).intValue();
					i = bajando ? i - pv : i + pv;
	
					// pongalo en la tabla de simbolos:
					tabSimb.ponValor(ident, new Integer(i));
				}
				else
				{
					break;
				}
			}
		}
		finally
		{
			if ( dec != null )		// Declaraci�n interna
			{
				tabSimb.irAMarca(marca);
			}
		}
	}
	public void visitar(NPlus n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		retorno = _ejecutarExpresion(e);
	}
	public void visitar(NPor n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		Number ev = (Number)_ejecutarExpresion(e);
		Number fv = (Number)_ejecutarExpresion(f);

		if ( n.obtTipo().esEntero() )
			retorno = new Integer(ev.intValue() * fv.intValue());
		else
			retorno = new Double(ev.doubleValue() * fv.doubleValue());
	}
	public void visitar(NRepita n)
	throws VisitanteException
	{
		TId etq_ciclo = n.obtEtiqueta();
		Nodo[] acciones = n.obtAcciones();
		NExpresion expr = n.obtCondicion();
		do
		{
			try
			{
				_visitarAccionesIteracion(n, acciones);
			}
			catch ( ControlInteracionException ex )
			{
				TId etq_ex = ex.obtEtiqueta();

				// Esta exception es para este ciclo si su
				// etiqueta es nula, o si las dos etiquetas
				// son iguales:
				if ( etq_ex == null
				||  (etq_ciclo != null && etq_ex.obtId().equals(etq_ciclo.obtId())) )
				{
					// es para este ciclo.
					if ( ex.esTermine() )
						break;
					else
						continue;
				}
				else
				{
					// NO es para este ciclo; relance la exception:
					throw ex;
				}
			}
		}
		while ( ! ((Boolean)_ejecutarExpresion(expr)).booleanValue() );
	}
	public void visitar(NRetorne n)
	throws VisitanteException
	{
		NExpresion[] expresiones = n.obtExpresiones();
		Object res[] = new Object[expresiones.length];
		for ( int i = 0; i < res.length; i++ )
		{
			res[i] = _ejecutarExpresion(expresiones[i]);
		}

		throw new ControlRetorneException(res, expresiones);
	}
	
	public void visitar(NLance n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		Object res = _ejecutarExpresion(e);
		throw new ControlLanceException(res, e, pilaEjec);
	}
	
	public void visitar(NIntente n)
	throws VisitanteException
	{
		int marca = tabSimb.marcar();
		
		try
		{
			Nodo[] acciones = n.obtAcciones();
			for ( int i = 0; i < acciones.length; i++ )
				acciones[i].aceptar(this);
		}
		catch(ControlLanceException ex)
		{
			NAtrape[] cc = n.obtAtrapes();
			Object res = ex.obtResultado();
			NExpresion e = ex.obtExpresion();
			Tipo te = e.obtTipo();
			
			// el que se ejecutar�:
			NAtrape c = null;
			
			try
			{
				for ( int i = 0; i < cc.length; i++ )
				{
					NDeclaracion d = cc[i].obtDeclaracion();
					Tipo td = d.obtTipo();
					if ( td.esAsignable(te) )
					{
						c = cc[i];
						break;
					}
				}
			}
			catch (ClaseNoEncontradaException exc)
			{
				throw _crearEjecucionException(n,
					Str.get("error.1_class_not_found", exc.obtNombre())
				);
			}
			
			if ( c != null )   // si hubo un atrape adecuado
			{
				int marcax = tabSimb.marcar();
				try
				{
					NDeclaracion d = c.obtDeclaracion();
					d.aceptar(this);
					tabSimb.ponValor(d.obtId().obtId(), res);		
					Nodo[] cacciones = c.obtAcciones();
					for ( int i = 0; i < cacciones.length; i++ )
						cacciones[i].aceptar(this);
				}
				finally
				{
					tabSimb.irAMarca(marcax);
				}
			}
			else
				throw ex;      // rethrow
		}
		finally
		{
			tabSimb.irAMarca(marca);
			NAtrape f = n.obtSiempre();
			if ( f != null )
			{
				int marcax = tabSimb.marcar();
				try
				{
					Nodo[] cacciones = f.obtAcciones();
					for ( int i = 0; i < cacciones.length; i++ )
						cacciones[i].aceptar(this);
				}
				finally
				{
					tabSimb.irAMarca(marcax);
				}
			}
		}
	}
	
	/**
	 * No hace nada. Ver visitar(NIntente)
	 */
	public void visitar(NAtrape n)
	throws VisitanteException
	{
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Ejecuta un NSubId.
	 */
	public void visitar(NSubId n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		Object o = _ejecutarExpresion(e);
		if ( o == null ) {
			throw _crearEjecucionException(e,
				Str.get("rt.error.null_pointer")
			);
		}

		TId id = n.obtId();
		
		if ( o instanceof LObjeto )
		{
			LObjeto obj = (LObjeto) o;
			
			retorno = _obtValorDeObjeto(obj, id.obtId(), e);
			if ( retorno == null && enInvocacion )
			{
				retorno = _obtMetodoDeObjeto(obj, id.obtId(), e);
				if ( retorno != null )
				{
					objInvocado = obj;
					este = obj;
				}
			}
		}
		else if ( o instanceof String
		||        o instanceof ArregloBaseNoCero
		||        o instanceof Object[] )
		{
			int size;
			int base;

			if ( o instanceof String )
			{
				String s = (String) o;
				size = s.length();
				base = 0;
			} 
			else if ( o instanceof ArregloBaseNoCero )
			{
				ArregloBaseNoCero abnc = (ArregloBaseNoCero) o;
				size = abnc.array.length;
				base = abnc.base;
			}
			else // if ( o instanceof Object[] )
			{
				size = ((Object[]) o).length;
				base = 0;
			}
			
			String oper = id.obtId();
			if ( oper.equals(Str.get("length")) )
				retorno = new Integer(size);
			else if ( oper.equals(Str.get("inf")) )
				retorno = new Integer(base);
			else if ( oper.equals(Str.get("sup")) )
				retorno = new Integer(base + size - 1);
			else
			{
				// pero deber�a controlarse desde compilaci�n
				throw _crearEjecucionException(e,
					Str.get("error.1_invalid_operation", oper)
				);
			}
		}
		else
		{
			throw _crearEjecucionException(e,
				Str.get("rt.error.1_not_an_object", id.obtId())
			);
		}
	}
	
	/**
	 * Ejecuta una subindizaci�n.
	 */
	public void visitar(NSubindexacion n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();

		Object obj = _ejecutarExpresion(e);
		int i = ((Integer) _ejecutarExpresion(f)).intValue();

		retorno = _obtValorDeArreglo(obj, i, e);
	}
	
	public void visitar(NTermine n)
	throws VisitanteException
	{
		TId etq = n.obtEtiqueta();
		NExpresion expr = n.obtExpresion();

		boolean doit = true;

		if ( expr != null )
		{
			Boolean b = (Boolean) _ejecutarExpresion(expr);
			doit = b.booleanValue();
		}

		if ( doit )
		{
			throw new ControlInteracionException(true, etq);
		}
	}

	/**
	 * Por cumplir con la implementaci�n.
	 * Genera un RuntimeException.
	 */
	public void visitar(NUtiliza n)
	throws VisitanteException
	{
		throw new RuntimeException("Internal error: Ejecutor.visitar(NUtiliza) called");
	}
	public void visitar(NY n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		retorno = new Boolean(
				   ((Boolean)_ejecutarExpresion(e)).booleanValue()
				&& ((Boolean)_ejecutarExpresion(f)).booleanValue()
		);
	}
	public void visitar(NYArit n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		if ( n.obtTipo().esEntero() )
		{
			retorno = new Integer
			(	  ((Integer)_ejecutarExpresion(e)).intValue()
				& ((Integer)_ejecutarExpresion(f)).intValue()
			);
		}
		else
		{	//{ tipo esBooleano }
			retorno = new Boolean
			(	   ((Boolean)_ejecutarExpresion(e)).booleanValue()
				&& ((Boolean)_ejecutarExpresion(f)).booleanValue()
			);
		}
	}

	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 */
	public void visitar(NTipoArreglo n)
	throws VisitanteException
	{
		NTipo nte = n.obtNTipoElemento();
		nte.aceptar(this);
		Tipo te = nte.obtTipo();
		
		n.ponTipo(Tipo.arreglo(te));
	}

	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 */
	public void visitar(NTipoBooleano n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.booleano);
	}

	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 */
	public void visitar(NTipoCadena n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.cadena);
	}

	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 */
	public void visitar(NTipoCaracter n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.caracter);
	}

	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 */
	public void visitar(NTipoClase n)
	throws VisitanteException
	{
		TNombre nombre = n.obtTNombre();

		NClase clase = _obtClaseParaNombre(nombre);
		if ( clase == null )
		{
			throw _crearEjecucionException(n,
				Str.get("error.1_class_not_found", nombre)
			);
		}
		Tipo tipo = Tipo.clase(clase.obtNombreCompleto());
		n.ponTipo(tipo);
	}

	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 */
	public void visitar(NTipoEntero n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.entero);
	}

	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 * Un algoritmo se indica para una cierta especificacion,
	 * o es generico.
	 */
	public void visitar(NTipoEspecificacion n)
	throws VisitanteException
	{
		TNombre nombre = n.obtTNombre();

		// Si nombre == null, entonces se trata de tipo generico:

		NEspecificacion espec = null;
		if ( nombre != null )
		{
			espec = _obtEspecificacionParaNombre(nombre);
			if ( espec == null )
			{
				String s = nombre.obtCadena();
				throw _crearEjecucionException(n,
					Str.get("error.1_spec_not_found", s)
				);
			}
		}

		Tipo tipo = Tipo.especificacion(espec.obtNombreCompleto());
		n.ponTipo(tipo);
	}

	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 */
	public void visitar(NTipoReal n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.real);
	}


	////////////////////////////////////////////////////////////////////
	/**
	 * Ejecuta una expresion "implementa".
	 * Actualmente (2002-09-23) tiene en cuenta:
	 * <pre>
	 *    algun_algoritmo 'implementa'  alguna_especificacion
	 * </pre>
	 * N�tese que cualquier algoritmo implementa el algoritmo
	 * gen�rico.
	 */
	public void visitar(NImplementa n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		Tipo tipo = n.obtNTipoRevisado().obtTipo();
		
		if ( tipo instanceof TipoEspecificacion )
		{
			TipoEspecificacion ti = (TipoEspecificacion) tipo;
			NAlgoritmo alg = (NAlgoritmo) _ejecutarExpresion(e);
			if ( alg == null )
			{
				throw _crearEjecucionException(e,
					Str.get("rt.error.null_pointer")
				);
			}
			
			String[] nom_spec = ti.obtNombreEspecificacion();
			retorno = new Boolean(
				   nom_spec == null  
				|| alg.esParaEspecificacion(nom_spec)
			);
		}
		else if ( tipo instanceof TipoInterface )
		{
			TipoInterface ti = (TipoInterface) tipo;
			Objeto obj = (Objeto) _ejecutarExpresion(e);
			
			try
			{
				retorno = new Boolean(Tipos.implementa(obj, ti));
			}
			catch (ClaseNoEncontradaException ex)
			{
				throw _crearEjecucionException(n,
					Str.get("error.1_class_not_found", ex.obtNombre())
				);
			}
		}
		else
		{
			Util._assert(false, "Internal error: Unexpected type: " +tipo.getClass());
		}

	}

	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 */
	public void visitar(NTipoInterface n)
	throws VisitanteException
	{
		TNombre nombre = n.obtTNombre();

		NInterface interf = _obtInterfaceParaNombre(nombre);
		if ( interf == null )
		{
			throw _crearEjecucionException(n,
				Str.get("error.1_interface_not_found", nombre)
			);
		}
		Tipo tipo = Tipo.interface_(interf.obtNombreCompleto());
		n.ponTipo(tipo);
	}
}
