package loro.compilacion;

import loro.Loro.Str;
import loro.visitante.VisitanteException;
import loro.arbol.*;
import loro.util.Util;
import loro.tabsimb.*;
import loro.tipo.*;
import loro.IUbicable;

/**
 * Visitante encargado de hacer la compilación.
 * Aquí se implementan propiamente las operaciones visitar(*) de la
 * interface IVisitante.
 *
 * Extiende ChequeadorBase en donde se cuenta con un conjunto de servicios 
 * auxiliares para la tarea.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class Chequeador extends ChequeadorBase
{
	///////////////////////////////////////////////////////////////
	/**
	 * Crea un chequeador. Se reduce a invocar super().
	 */
	public Chequeador()
	{
		super();
	}

	///////////////////////////////////////////////////////////////
	/**
	 * Crea un chequeador. Se reduce a invocar super(tabSimbBase, unidadActual).
	 *
	 * @param tabSimbBase	Tabla de simbolos a tomar como de base.
	 */
	public Chequeador(TablaSimbolos tabSimbBase, NUnidad unidadActual)
	{
		super(tabSimbBase, unidadActual);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea el nodo.
	 */
	public void visitar(NACadena n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		e.aceptar(this);
		Tipo e_tipo = e.obtTipo();

		if ( e_tipo.esUnit() )
		{
			_operadorUnNoDefinido(
				e,
				n.obtOperador(), e_tipo
			);
		}

		n.ponTipo(Tipo.cadena);
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo afirmacion.
	 */
	public void visitar(NAfirmacion n)
	throws VisitanteException
	{
		NExpresion expresion = n.obtExpresion();
		if ( expresion == null )
		{
			// se indicó una cadena de documentación. OK.
			return;
		}
		
		enAfirmacion = true;
		try
		{
			expresion.aceptar(this);
		}
		finally
		{
			enAfirmacion = false;
		}

		Tipo tipo = expresion.obtTipo();
		if ( !tipo.esBooleano() )
		{
			if ( expresion instanceof NLiteralCadena )
			{
				// OK. Se acepta como una afirmacion.
				// 2000-07-18 - en prueba
				return;
			}

			// problema:
			throw new ChequeadorException(
				expresion,
				Str.get("error.not_boolean_assert")
			);
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea la definición de un algoritmo.
	 * Pendiente complementar manejo como método.
	 */
	public void visitar(NAlgoritmo n)
	throws VisitanteException
	{
		int marca_tabla = -1;
		
		NClase miclase = n.obtClase();

		try {
			if ( miclase == null ) {
				String nombre_simple = n.obtNombreSimpleCadena();
				if ( _yaHayAlgoritmo(nombre_simple) ) {
					throw new ChequeadorException(n,
						Str.get("error.1_algorithm_redefined", nombre_simple)
					);
				}
			}
			else {
				// pendiente: verificar en el namespace de mi clase hasta mi ubicación. 
			}

			
			TId id = n.obtId();
			
			if ( Util.esVarSemantica(id) ) {
				throw new ChequeadorException(id,
					Str.get("error.1_invalid_algorithm_name", id)
				);
			}

			n.iniciarAsociacionesSimpleCompuesto();

			// Tomar nota del paquete:
			n.ponPaquete(paqueteActual);
			
			// en caso que la compilación falle:
			_borrarCompilado(n);

			TNombre espec = n.obtTNombreEspecificacion();
			NEspecificacion nespec = null;

			if ( miclase == null )  // es un algoritmo aislado
			{
				// Ver que la especificación exista:
				nespec = _obtEspecificacionParaNombre(espec);
				if ( nespec == null )
				{
					throw new IdIndefinidoException(
						espec,
						Str.get("error.1_spec_not_found", espec.obtCadena())
					);
				}
			} 
			else   // es un metodo
			{
				TId[] ids = espec.obtIds();
				if ( ids.length > 1 ) {
					throw new ChequeadorException(
						espec,
						Str.get("error.unexpected_compound_name")
					);
				}
				String sid = ids[0].obtId();  // el nombre simple de la operacion
				
				// Ver que la operación exista en alguna de las interfaces de miclase:
				try {
					nespec = Tipos.obtOperacion(miclase, sid);
					if ( nespec == null ) {
						throw new IdIndefinidoException(
							espec,
							Str.get("error.1_no_interface_with_operation", sid)
						);
					}
				}
				catch(ClaseNoEncontradaException ex) {
					throw new ChequeadorException(
						espec,
						Str.get("error.2_not_found_while_searching_operation",
							ex.obtNombre(), sid
						)
					);
				}
			}

			// Marcar la tabla;
			marca_tabla = tabSimb.marcar();

			// obtenga la interface de la especificacion
			// Este puede ser null.
			NInterface interf = nespec.obtInterface();
			
			// Revisar concordancia de entradas y salidas:

			// entradas:
			NDeclaracion[] pent = n.obtParametrosEntrada();
			NDeclaracion[] nespec_pent = nespec.obtParametrosEntrada();
			_chequearConcordanciaParametros(
				espec,          // Ubicable 
				pent,           // pent
				nespec_pent,    // qent
				interf,
				true            // entradas
			);

			// salidas:
			NDeclaracion[] psal = n.obtParametrosSalida();
			NDeclaracion[] nespec_psal = nespec.obtParametrosSalida();
			_chequearConcordanciaParametros(
				espec,          // Ubicable 
				psal,           // pent
				nespec_psal,    // qent
				interf,
				false           // salidas
			);
			
			Nodo[] acciones = n.obtAcciones();

			if ( n.implementadoEnLoro() )
			{
				// Anotar este algoritmo como el actual para chequeo de acciones
				// (en particular, NRetorne requiere esta información):
				n.ponNombreEspecificacion(nespec.obtNombreCompleto());
				unidadActual = n;

				// Chequear acciones:
				for ( int i = 0; i < acciones.length; i++ )
				{
					if ( acciones[i] instanceof NDeclaracion )
					{
						NDeclaracion d = (NDeclaracion) acciones[i];
						if ( d.esConstante() && !d.tieneInicializacion() )
						{
							throw new ChequeadorException(
								d,
								Str.get("error.missing_init")
							);
						}
					}
					visitarAccion(acciones[i]);
				}

				// Si no hay retorne, verificar que los parámetros de salida
				// hayan tenido alguna asignación.
				for ( int i = 0; i < psal.length; i++ )
				{
					if ( ! tabSimb.obtAsignado(psal[i].obtId().obtId()) )
					{
						throw new ChequeadorException(
							psal[i].obtId(),
							Str.get("error.1_output_not_assigned", psal[i].obtId())
						);
					}
				}
			}

			// Actualice algoritmo con el nombre completo de la especificacion:
			n.ponNombreEspecificacion(nespec.obtNombreCompleto());
		}
		catch (IdIndefinidoException ex)
		{
			if ( permitirIdIndefinido )
			{
				numIdIndefinidos++;
			}
			else
			{
				throw ex;
			}
		}
		catch (VisitanteException ex)
		{
			throw ex;
		}
		finally
		{
			if ( marca_tabla >= 0 )
				tabSimb.irAMarca(marca_tabla);

		}

		if ( miclase == null )
		{
			// es un algoritmo aislado:
			n.ponNombreFuente(nombreFuente);
			_agregarAlgoritmo(n);
			_guardarCompilado(n);
		}
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NAsignacion n)
	throws VisitanteException
	{
		NExpresion var = n.obtExpresionIzq();
		NExpresion expr = n.obtExpresionDer();

		// primero chequear la expresion derecha:
		expr.aceptar(this);

		if ( !(var instanceof NId)
		&&   !(var instanceof NNombre)
		&&   !(var instanceof NSubId)
		&&   !(var instanceof NSubindexacion) ) {
			throw new ChequeadorException(var, 
				Str.get("error.invalid_lhs")
			);
		}

		if ( var instanceof NId ) {
			TId id = ((NId)var).obtId();
			EntradaTabla et = tabSimb.buscar(id.obtId());
			if ( et == null ) {
				throw new ChequeadorException(var,
					Str.get("error.1_id_undefined", id)
				);
			}

			if ( et.esConstante() ) {
				throw new ChequeadorException(var,
					Str.get("error.1_id_constant_lhs", id)
				);
			}

			if ( ! tabSimb.obtAsignado(id.obtId()) ) {
				// Indique que var ha sido asignado:
				tabSimb.ponAsignado(id.obtId(), true);
			}
			var.ponTipo(et.obtTipo());
		}
		else {
			// var instanceof NSubindexacion | NSubId
			var.aceptar(this);

			if ( _esConstanteExpresion(var) ) {
				throw new ChequeadorException(var,
					Str.get("error.1_constant_lhs")
				);
			}
		}

		// revise asignabilidad:
		Tipo expr_tipo = expr.obtTipo();
		Tipo var_tipo = var.obtTipo();
		_chequearAsignabilidad(expr, var_tipo, expr_tipo, expr);

		n.ponTipo(var_tipo);

	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NCardinalidad n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		e.aceptar(this);
		Tipo e_tipo = e.obtTipo();

		if ( !e_tipo.esCadena() && !e_tipo.esArreglo())
		{
			_operadorUnNoDefinido(
				e,
				n.obtOperador(), e_tipo
			);
		}

		n.ponTipo(Tipo.entero);
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Chequea este caso.
	 * El bloque de acciones es un ámbito de variables independientemente 
	 * de si hay 'fin caso' o no.
	 * Esto difiere de Java: lo siguiente es válido en Java:
	 * <pre>
	 * 			switch ( 5 )
	 *			{
	 * 				case 1:
	 * 					int nn = 10;
	 * 					System.out.println("uno " +nn);
	 * 				case 2:
	 * 					nn = 20;
	 * 					System.out.println("dos " +nn);
	 * 			}
	 * </pre>
	 * pero no en Loro:
	 * <pre>
	 * 			segun 5 haga
	 * 				caso 1:
	 * 					nn: entero := 10;
	 * 					escriba "uno " +nn+ "\n";
	 *	 			caso 2:
	 * 					nn := 20;
	 * 					escriba "dos " +nn+ "\n";
	 * 			fin caso;
	 * </pre>
	 * Esto obliga una mejor disciplina de declaración de variables.
	 * (permitir declaraciones antes del primer caso?)
	 */
	public void visitar(NCaso n)
	throws VisitanteException
	{
		NExpresion expr = n.obtExpresion();
		if ( expr != null ) {
			// it's not an "else".
			expr.aceptar(this);
			if ( tipoSegun.esEntero() && !(expr instanceof NLiteralEntero)
			||   tipoSegun.esCaracter() && !(expr instanceof NLiteralCaracter) )
			{
				throw new ChequeadorException(
					expr,
					Str.get("error.1_not_constant_case", tipoSegun)
				);
			}
		}

		int marca = tabSimb.marcar();
		try {
			Nodo[] acciones = n.obtAcciones();
			visitarAcciones(acciones);
		}
		finally {
			tabSimb.irAMarca(marca);
		}
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NCiclo n)
	throws VisitanteException
	{
		TId etq = n.obtEtiqueta();
		String label = _chequearDefinicionEtiqueta(etq);
		labels.push(label);
		int marca = tabSimb.marcar();
		try
		{
			visitarAcciones(n.obtAcciones());
		}
		finally
		{
			tabSimb.irAMarca(marca);
			labels.pop();
		}
	}

	///////////////////////////////////////////////////////////////////////
	/**
	 * Chequeo de la definicion de una clase.
	 */
	public void visitar(NClase n)
	throws VisitanteException
	{
		TId id = n.obtId();
		
		if ( n.defineObjeto() ) {
			throw new ChequeadorException(id,
				Str.get("error.unimpl_object_def")
			);
		}
		
		int marca_tabla = -1;

		try {
			claseActual = n;
			unidadActual = n;

			if ( _yaHayClase(id.obtId()) ) {
				throw new ChequeadorException(id,
					Str.get("error.1_class_redefined", id)
				);
			}

			if ( Util.esVarSemantica(id) )
			{
				throw new ChequeadorException(id,
					Str.get("error.1_invalid_class_name", id)
				);
			}

			n.iniciarAsociacionesSimpleCompuesto();

			// Tomar nota del paquete:
			n.ponPaquete(paqueteActual);
			
			// en caso que la compilación falle:
			_borrarCompilado(n);

			TNombre extiende = n.obtNombreExtiende();
			
			// Marcar la tabla;
			marca_tabla = tabSimb.marcar();

			//////////////////////////////////////////////////////////////////
			// Manejo de super clase:
			// enlace de atributos
			if ( extiende != null ) {
				NClase superclase = _obtClaseParaNombre(extiende);

				if ( superclase == null ) {
					throw new ChequeadorException(
						extiende,
						Str.get("error.1_class_not_found", extiende.obtCadena())
					);
				}

				// revise no ciclicidad:  clase X extiende X ...
				// teniendo en cuenta por supuesto los nombres completos
				// correspondientes:
				String super_name = superclase.obtNombreCompletoCadena();
				String this_name = n.obtNombreCompletoCadena();
				if ( this_name.equals(super_name) ) {
					throw new ChequeadorException(
						extiende,
						Str.get("error.class_cyclic_inheritance")
					);
				}
				
				// si el extiende es un nombre simple correspondiente a un nombre
				// compuesto, entonces hacer la asociación correspondiente:
				if ( extiende.obtIds().length == 1
				&&   superclase.obtNombreCompleto().length > 1 ) {
					n.ponNombreCompuesto("c" +extiende.obtCadena(), super_name);
				}

				// enlace todos los atributos de la superclase:
				NDeclDesc[] super_atrs;
				try {
					super_atrs = mu.obtAtributos(superclase);
				}
				catch ( ClaseNoEncontradaException ex ) {
					String nombre_super = ex.obtNombre();
					throw new ChequeadorException(
						extiende,
						Str.get("error.1_superclass_not_found", nombre_super)
					);
				}

				for ( int i = 0; i < super_atrs.length; i++ )
				{
					NDeclDesc d = super_atrs[i];
					EntradaTabla et = new EntradaTabla(d.obtId().obtId(), d.obtTipo());
					_insertarEnTablaSimbolos(et, d.obtId());
					et.ponAsignado(true);
					et.ponConstante(d.esConstante());
				}
			}
			
			//////////////////////////////////////////////////////////////////
			// Manejo de interfaces:
			TNombre[] interfaces = n.obtInterfacesDeclaradas();
			for ( int i = 0; i < interfaces.length; i++ )
			{
				NInterface interface_ = _obtInterfaceParaNombre(interfaces[i]);

				if ( interface_ == null )
				{
					throw new ChequeadorException(
						interfaces[i],
						Str.get("error.1_interface_not_found", interfaces[i].obtCadena())
					);
				}
				
				// si la interface es un nombre simple correspondiente a un nombre
				// compuesto, entonces hacer la asociación correspondiente:
				if ( interfaces[i].obtIds().length == 1
				&&   interface_.obtNombreCompleto().length > 1 )
				{
					n.ponNombreCompuesto("i" +interfaces[i].obtCadena(), 
						interface_.obtNombreCompletoCadena()
					);
				}
			}

			NDeclDesc[] pent = n.obtParametrosEntrada();
			NConstructor[] pcons = n.obtConstructores();

			//
			// Es posible que algún atributo tenga como tipo esta misma clase.
			// Agrego esta clase de antemano para esta posibilidad.
			//
			_agregarClase(n);

			// Enlazar atributos:
			for ( int i = 0; i < pent.length; i++ ) {
				if ( pent[i].esConstante()
				&&  !pent[i].tieneInicializacion() ) {
					throw new ChequeadorException(
						pent[i],
						Str.get("error.missing_init")
					);
				}

				pent[i].aceptar(this);

				if ( !pent[i].esConstante() ) {
					// Ponga que ha habido asignacion.
					// Aquí, para efectos de poscondición.

					tabSimb.ponAsignado(pent[i].obtId().obtId(), true);
				}
			}
			
			// volver a la marca inicial para deshacer declaraciones de
			// atributos, tanto de superclases como de este misma clase:
			tabSimb.irAMarca(marca_tabla);

			// Chequear constructores:
			for ( int i = 0; i < pcons.length; i++ )
			{
				if ( i > 0
				&& n.obtConstructor(pcons[i].obtParametrosEntrada(), i -1) != null ) {
					throw new ChequeadorException(
						pcons[i],
						Str.get("error.redefined_constructor")
					);
				}
				pcons[i].aceptar(this);
			}

			// Chequear métodos:
			NAlgoritmo[] metodos = n.obtMetodosDeclarados();
			for ( int i = 0; i < metodos.length; i++ ) {
				/* PENDIENTE AJUSTAR LO SIGUIENTE (DE CONSTRUCTORES) PARA METODOS:
				if ( i > 0
				&& n.obtConstructor(pcons[i].obtParametrosEntrada(), i -1) != null )
				{
					throw new ChequeadorException(
						pcons[i],
						"Ya tiene un constructor con este tipo de entradas."
					);
				}
				*/
				
				metodos[i].aceptar(this);
			}
		}
		catch (IdIndefinidoException ex)
		{
			if ( permitirIdIndefinido )
			{
				numIdIndefinidos++;
			}
			else
			{
				throw ex;
			}
		}
		finally
		{
			if ( marca_tabla >= 0 )
				tabSimb.irAMarca(marca_tabla);

		}

		n.ponNombreFuente(nombreFuente);
		_agregarClase(n);
		_guardarCompilado(n);
	}

	///////////////////////////////////////////////////////////////////////
	/**
	 * Chequeo de la definicion de una interface.
	 * NOTA: Implementación INCOMPLETA.
	 */
	public void visitar(NInterface n)
	throws VisitanteException
	{
		TId id = n.obtId();

		try {
			interfaceActual = n;
			unidadActual = n;
			
			if ( _yaHayInterface(id.obtId()) ) {
				throw new ChequeadorException(id,
					Str.get("error.1_interface_redefined", id)
				);
			}

			if ( Util.esVarSemantica(id) ) {
				throw new ChequeadorException(id,
					Str.get("error.1_invalid_interface_name", id)
				);
			}

			n.iniciarAsociacionesSimpleCompuesto();

			// Tomar nota del paquete:
			n.ponPaquete(paqueteActual);

			// en caso que la compilación falle:
			_borrarCompilado(n);

			//////////////////////////////////////////////////////////////////
			// Manejo de interfaces:
			TNombre[] interfaces = n.obtInterfaces();
			for ( int i = 0; i < interfaces.length; i++ ) {
				NInterface superinterface = _obtInterfaceParaNombre(interfaces[i]);

				if ( superinterface == null ) {
					throw new ChequeadorException(
						interfaces[i],
						Str.get("error.1_interface_not_found", interfaces[i].obtCadena())
					);
				}

				// revise no ciclicidad:  interface X extiende X ...
				// teniendo en cuenta por supuesto los nombres completos
				// correspondientes:
				String super_name = superinterface.obtNombreCompletoCadena();
				String this_name = n.obtNombreCompletoCadena();
				if ( this_name.equals(super_name) )
				{
					throw new ChequeadorException(
						interfaces[i],
						Str.get("error.interface_cyclic_inheritance")
					);
				}

			}

			NEspecificacion[] opers = n.obtOperacionesDeclaradas();

			//
			// Para permitir recursion sobre este tipo
			//
			_agregarInterface(n);

			// Chequear operaciones:
			for ( int i = 0; i < opers.length; i++ ) {
				opers[i].aceptar(this);
			}
		}
		catch (IdIndefinidoException ex) {
			if ( permitirIdIndefinido ) {
				numIdIndefinidos++;
			}
			else {
				throw ex;
			}
		}

		n.ponNombreFuente(nombreFuente);
		_agregarInterface(n);
		_guardarCompilado(n);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NCondicion n)
	throws VisitanteException
	{
		NExpresion e = n.obtCondicion();
		e.aceptar(this);
		Tipo e_tipo = e.obtTipo();
		if ( !e_tipo.esBooleano() ) {
			throw new ChequeadorException(
				e,
				Str.get("error.not_boolean_expression")
			);
		}

		NExpresion f = n.obtPrimeraAlternativa();
		f.aceptar(this);
		Tipo f_tipo = f.obtTipo();
		NExpresion g = n.obtSegundaAlternativa();
		g.aceptar(this);
		Tipo g_tipo = g.obtTipo();

		if ( !f_tipo.igual(g_tipo) ) {
			throw new ChequeadorException(
				f,
				Str.get("error.incompatible_types")
			);
		}

		n.ponTipo(f_tipo);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NConstructor n)
	throws VisitanteException
	{
		if ( n.esPorDefecto() ) {
			// no problem
			return;
		}

		NDeclaracion[] pent = n.obtParametrosEntrada();
		NDescripcion[] dent = n.obtDescripcionesEntrada();
		Nodo[] acciones = n.obtAcciones();


		// Marcar la tabla;
		int marca = tabSimb.marcar();

		try {
			// verifique cantidades de descripciones:
			if ( dent.length != pent.length ) {
				IUbicable u = dent.length > 0 ? new Ubicacion(dent) : new Ubicacion(pent);
				throw new ChequeadorException(
					u,
					Str.get("error.different_number_input_descriptions")
				);
			}
	
			// Enlazar atributos
			for ( int i = 0; i < pent.length; i++ ) {
				if ( pent[i].tieneInicializacion() ) {
					throw new ChequeadorException(
						pent[i].obtId(),
						Str.get("error.1_invalid_param_init", pent[i].obtId())
					);
				}
	
				pent[i].aceptar(this);
				// Ponga que ha habido asignacion. Vea NId.aceptar(this):
				tabSimb.ponAsignado(pent[i].obtId().obtId(), true);
	
				// Vea que la descripción correspondiente sea para el nombre:
				if ( !dent[i].obtId().obtId().equals(pent[i].obtId().obtId()) ) {
					throw new ChequeadorException(
						dent[i].obtId(),
						Str.get("error.1_expected_input_description", pent[i].obtId())
					);
				}
			}
	
			// Chequear precondicion:
			NAfirmacion pre = n.obtPrecondicion();
			if ( pre == null ) {
				if ( pent.length > 0 ) {
					throw new ChequeadorException(
						new Ubicacion(pent),
						Str.get("error.expected_input_precond")
					);
				}
			}
			else {
				pre.aceptar(this);
			}
	
			// Chequear poscondicion:
			NAfirmacion pos = n.obtPoscondicion();
			if ( pos != null ) {
				pos.aceptar(this);
			}
	
	
			// Chequear acciones:
			for ( int i = 0; i < acciones.length; i++ ) {
				if ( acciones[i] instanceof NDeclaracion ) {
					NDeclaracion d = (NDeclaracion) acciones[i];
					if ( d.esConstante() && !d.tieneInicializacion() ) {
						throw new ChequeadorException(
							d,
							Str.get("error.missing_init")
						);
					}
				}
				visitarAccion(acciones[i]);
			}
		}
		finally {
			tabSimb.irAMarca(marca);
		}
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NContinue n)
	throws VisitanteException
	{
		TId etq = n.obtEtiqueta();
		_chequearEtiqueta(false, etq, n);

		NExpresion e = n.obtExpresion();
		if ( e != null )
		{
			e.aceptar(this);
			Tipo e_tipo = e.obtTipo();
			if ( !e_tipo.esBooleano() )
			{
				throw new ChequeadorException(
					e,
					Str.get("error.not_boolean_expression")
				);
			}
		}
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NConvertirTipo n)
	throws VisitanteException
	{
		NTipo ntipo = n.obtNTipo();
		ntipo.aceptar(this);
		Tipo tipo = ntipo.obtTipo();
		n.ponTipo(tipo);

		NExpresion e = n.obtExpresion();
		e.aceptar(this);
		Tipo e_tipo = e.obtTipo();

		if ( !e_tipo.esConvertibleA(tipo) ) {
			throw new ChequeadorException(e,
				Str.get("error.2_incompatible_types", e_tipo, tipo)
			);
		}
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NCorrDer n)
	throws VisitanteException
	{
		_visitarBinEntero(n);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NCorrDerDer n)
	throws VisitanteException
	{
		_visitarBinEntero(n);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NCorrIzq n)
	throws VisitanteException
	{
		_visitarBinEntero(n);
	}
	

	//////////////////////////////////////////////////////////////////////
	/**
	 * Visita una expresión arreglo.
	 * Se revisa que todas las subexpresiones sean compatibles con la
	 * primera subexpresión. Tampoco que sea <code>[nulo, ...]</code>.
	 */
	public void visitar(NExpresionArreglo n)
	throws VisitanteException
	{
		NExpresion[] exprs = n.obtExpresiones();
		NExpresion e = exprs[0];
		e.aceptar(this);
		Tipo e_tipo = e.obtTipo();
		if ( e_tipo.esNulo() ) {
			// tenemos aqui algo como [nulo, ...]
			throw new ChequeadorException(
				e,
				Str.get("error.invalid_first_expression_array")
			);
		}
		
		for ( int i = 1; i < exprs.length; i++ ) {
			NExpresion f = exprs[i];
			f.aceptar(this);
			Tipo f_tipo = f.obtTipo();
			_chequearAsignabilidad(f, e_tipo, f_tipo, f);
		}

		n.ponTipo(Tipo.arreglo(e_tipo));
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Chequea  una creacion de arreglo.
	 */
	public void visitar(NCrearArreglo n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();
		NExpresion g = n.obtExpresionLimiteSuperior();

		e.aceptar(this);
		Tipo e_tipo = e.obtTipo();
		if ( !e_tipo.esEntero() ) {
			throw new ChequeadorException(
				e,
				Str.get("error.integer_expression_expected")
			);
		}

		if ( g != null ) {
			g.aceptar(this);
			Tipo g_tipo = g.obtTipo();
			if ( !g_tipo.esEntero() ) {
				throw new ChequeadorException(
					g,
					Str.get("error.integer_expression_expected")
				);
			}
		}

		f.aceptar(this);
		Tipo f_tipo = f.obtTipo();

		n.ponTipo(Tipo.arreglo(f_tipo));
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NCrearArregloTipoBase n)
	throws VisitanteException
	{
		NTipo ntipo = n.obtNTipo();
		ntipo.aceptar(this);
		Tipo tipo = ntipo.obtTipo();
		n.ponTipo(tipo);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NCrearObjeto n)
	throws VisitanteException
	{
		NExpresion[] args = n.obtArgumentos();

		// chequee argumentos:
		for ( int i = 0; i < args.length; i++ ) {
			args[i].aceptar(this);
		}

		TNombre nom = n.obtNombreClase();

		NClase clase = _obtClaseParaNombre(nom);

		if ( clase == null ) {
			TId[] ids = nom.obtIds();
			throw new ChequeadorException(
				n,
				Str.get("error.1_class_not_found", ids[ids.length -1])
			);
		}

		// chequear si existe constructor compatible
		// con los argumentos:

		NConstructor constructor = null;
		try {
			constructor = clase.obtConstructor(args);
		}
		catch(ClaseNoEncontradaException ex) {
			throw new ChequeadorException(
				n,
				Str.get("error.1_class_not_found", ex.obtNombre())
			);
		}

		if ( constructor == null ) {
			throw new ChequeadorException(
				n,
				Str.get("error.1_constructor_not_found", clase)
			);
		}

		n.ponClase(clase);
		n.ponConstructor(constructor);
		n.ponTipo(Tipo.clase(clase.obtNombreCompleto()));
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NCuantificado n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		NExpresion con = n.obtExpresionCon();
		NDeclaracion[] d = n.obtDeclaraciones();

		// Marcar la tabla;
		int marca = tabSimb.marcar();

		try {
			// variables cuantificadas:
			for ( int i = 0; i < d.length; i++ ) {
				d[i].aceptar(this);
				// Ponga que ha habido asignacion.. Vea NId.chequear:
				TId id = d[i].obtId();
				tabSimb.ponAsignado(id.obtId(),true);
			}
	
			if ( con != null ) {
				con.aceptar(this);
				if ( !con.obtTipo().esBooleano() ) {
					throw new ChequeadorException(
						con,
						Str.get("error.not_boolean_expression")
					);
				}
			}
	
			e.aceptar(this);
			Tipo e_tipo = e.obtTipo();
	
			if ( !e_tipo.esBooleano() ) {
				throw new ChequeadorException(
					e,
					Str.get("error.not_boolean_expression")
				);
			}
		}
		finally {
			tabSimb.irAMarca(marca);
		}

		n.ponTipo(Tipo.booleano);
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Chequea una decision.
	 */
	public void visitar(NDecision n)
	throws VisitanteException
	{
		NExpresion expr = n.obtCondicion();
		expr.aceptar(this);
		Tipo expr_tipo = expr.obtTipo();
		if ( !expr_tipo.esBooleano() ) {
			throw new ChequeadorException(
				expr,
				Str.get("error.not_boolean_expression")
			);
		}

		int marca = tabSimb.marcar();
		try {
			Nodo[] as = n.obtAccionesCierto();
			visitarAcciones(as);
		}
		finally {
			tabSimb.irAMarca(marca);
		}

		NDecisionSiNoSi[] sinosis = n.obtSiNoSis();
		for ( int i = 0; i < sinosis.length; i++ ) {
			sinosis[i].aceptar(this);
		}

		Nodo[] an = n.obtAccionesFalso();
		if ( an != null ) {
			marca = tabSimb.marcar();
			try {
				visitarAcciones(an);
			}
			finally {
				tabSimb.irAMarca(marca);
			}
		}
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un fragmento "elseif" de una decision.
	 */
	public void visitar(NDecisionSiNoSi n)
	throws VisitanteException
	{
		NExpresion expr = n.obtCondicion();
		expr.aceptar(this);
		Tipo expr_tipo = expr.obtTipo();
		if ( !expr_tipo.esBooleano() ) {
			throw new ChequeadorException(
				expr,
				Str.get("error.not_boolean_expression")
			);
		}

		int marca = tabSimb.marcar();
		try {
			Nodo[] as = n.obtAcciones();
			visitarAcciones(as);
		}
		finally {
			tabSimb.irAMarca(marca);
		}
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NDecisionMultiple n)
	throws VisitanteException
	{
		NExpresion expr = n.obtExpresion();
		expr.aceptar(this);
		Tipo expr_tipo = expr.obtTipo();
		if ( !expr_tipo.esEntero() && !expr_tipo.esCaracter() ) {
			throw new ChequeadorException(
				expr,
				Str.get("error.1_invalid_expression_type_switch", expr_tipo)
			);
		}
		NCaso[] casos = n.obtCasos();
		for ( int i = 0; i < casos.length; i++ ) {
			NCaso c = casos[i];
			tipoSegun = expr_tipo;
			c.aceptar(this);
			for ( int j = i -1; j >= 0; j-- ) {
				NCaso cc = casos[j];
				NLiteral cc_expr = (NLiteral) cc.obtExpresion();
				NLiteral c_expr  = (NLiteral) c.obtExpresion();
				if ( cc_expr.igual(c_expr) ) {
					throw new ChequeadorException(
						c_expr,
						Str.get("error.1_repeated_case", c_expr)
					);
				}
			}
		}
		NCaso si_no = n.obtCasoSiNo();
		if ( si_no != null ) {
			si_no.aceptar(this);
		}
	}
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea una declaracion.
	 */
	public void visitar(NDeclaracion n)
	throws VisitanteException
	{
		NTipo ntipo = n.obtNTipo();
		ntipo.aceptar(this);
		Tipo tipo = ntipo.obtTipo();

		NExpresion e = n.obtExpresion();

		// primero se revisa la expresion si la hay:
		if ( e != null ) {
			e.aceptar(this);
		}

		TId id = n.obtId();
		TId[] ids = n.obtIds();

		if ( ids != null ) {
			for ( int k = 0; k < ids.length; k++ ) {
				if ( Util.esVarSemantica(ids[k].obtId()) ) {
					throw new IdIndefinidoException(ids[k],
						Str.get("error.1_invalid_declaration", ids[k])
					);
				}

				EntradaTabla et = new EntradaTabla(ids[k].obtId(), tipo);
				_insertarEnTablaSimbolos(et, ids[k]);
				et.ponAsignado(e != null);
				et.ponConstante(n.esConstante());
			}
		}
		else {
			if ( Util.esVarSemantica(id.obtId()) ) {
				throw new IdIndefinidoException(id,
					Str.get("error.1_invalid_declaration", id)
				);
			}
			EntradaTabla et = new EntradaTabla(id.obtId(), tipo);
			_insertarEnTablaSimbolos(et, id);
			et.ponAsignado(e != null);
			et.ponConstante(n.esConstante());
		}

		if ( e != null ) {
			// revise asignabilidad:
			_chequearAsignabilidad(e, tipo, e.obtTipo(), e);
		}
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NDeclDesc n)
	throws VisitanteException
	{
		NTipo ntipo = n.obtNTipo();
		ntipo.aceptar(this);
		Tipo tipo = ntipo.obtTipo();

		NExpresion e = n.obtExpresion();

		// primero se revisa la expresion si la hay:
		if ( e != null ) {
			e.aceptar(this);
		}

		TId id = n.obtId();
		if ( Util.esVarSemantica(id) ) {
			throw new IdIndefinidoException(id,
				Str.get("error.1_invalid_declaration", id)
			);
		}
		EntradaTabla et = new EntradaTabla(id.obtId(), tipo);
		_insertarEnTablaSimbolos(et, id);
		et.ponAsignado(e != null);
		et.ponConstante(n.esConstante());

		if ( e != null ) {
			// Se revisa compatibilidad:
			_chequearAsignabilidad(e, tipo, e.obtTipo(), e);
		}
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo. No hace nada.
	 */
	public void visitar(NDescripcion n)
	throws VisitanteException
	{
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NDiferente n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.booleano);

		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();

		e.aceptar(this);
		f.aceptar(this);
		Tipo e_tipo = e.obtTipo();
		Tipo f_tipo = f.obtTipo();

		_chequearIgualdad(
			n,
			n.obtOperador(), e_tipo, f_tipo
		);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NDivReal n)
	throws VisitanteException
	{
		_visitarBinNumerico(n);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NEquivalencia n)
	throws VisitanteException
	{
		_visitarBinBooleano(n);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NEsInstanciaDe n)
	throws VisitanteException
	{
		// chequee la expresion:
		NExpresion e = n.obtExpresion();
		e.aceptar(this);
		Tipo e_tipo = e.obtTipo();
		
		// chequee el tipo revisado:
		NTipo ntipoRevisado = n.obtNTipoRevisado();
		ntipoRevisado.aceptar(this);
		Tipo tipoRevisado = ntipoRevisado.obtTipo();

		if ( e_tipo.esNulo() ) {    // no puede ser "null"
			throw new ChequeadorException(
				e,
				Str.get("error.null_instance")
			);
		}
		
		if ( !e_tipo.esObjeto() ) {
			throw new ChequeadorException(
				e,
				Str.get("error.not_object_instanceof")
			);
		}

		// tipo revisado debe ser objeto:
		if ( !tipoRevisado.esObjeto() ) {
			throw new ChequeadorException(
				n.obtNTipoRevisado(),
				Str.get("error.1_not_a_class", tipoRevisado)
			);
		}

		boolean ok = true;
		
		// El siguiente fragmento deberá modificarse para hacer un chequeo
		// más completo. PENDIENTE
		if ( e_tipo instanceof TipoClase && tipoRevisado instanceof TipoClase ) {
			ok = _aKindOf(n, (TipoClase)tipoRevisado, (TipoClase)e_tipo)
			  || _aKindOf(n, (TipoClase)e_tipo, (TipoClase)tipoRevisado)
			;
		}
		else {
			// OK.
			// 2003-05-13
			// Por ahora se permitirá cualquier chequeo de es_instancia_de que
			// sea válido hasta este punto.
			// pero están pendientes otros casos posibles que deberían chequearse.
		}
		
		if ( !ok ) {
			throw new ChequeadorException(
				e,
				Str.get("error.2_incompatible_types", e_tipo, tipoRevisado)
			);
		}

		n.ponTipo(Tipo.booleano);
	}
	
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Chequea la declaracion de una especificacion.
	 *
	 * Primero se procesan entradas y salidas antes del "cuerpo" de
	 * pre- y pos-condiciones. Esto con la finalidad de priorizar completar
	 * las asociaciones de tipo para entradas/salidas en caso de posterior
	 * error en las condiciones, lo cual validara provisionalmente esta
	 * especificacion es caso de ser referenciada en otra unidad.
	 *
	 * Para evitar que la PRE acceda a la variable de salida, esta se deja con
	 * estado de asignacion en falso. Asi se generara error de "no asignacion".
	 * Pero se pone este estado de asignacion a verdadero justo antes de chequear 
	 * la POS. 
	 */
	public void visitar(NEspecificacion n)
	throws VisitanteException
	{
		int marca_tabla = -1;
		TId id = n.obtId();
		NInterface miinterf = n.obtInterface();

		try {
			unidadActual = n;

			if ( miinterf == null ) {
				if ( _yaHayEspecificacion(id.obtId()) ) {
					throw new ChequeadorException(id,
						Str.get("error.1_spec_redefined", id)
					);
				}
			}
			else {
				// pendiente: verificar en el namespace de mi interface hasta mi ubicación. 
			}

			n.iniciarAsociacionesSimpleCompuesto();

			// Tomar nota del paquete:
			n.ponPaquete(paqueteActual);

			// en caso que la compilación falle:
			_borrarCompilado(n);

			NDeclaracion[] psal = n.obtParametrosSalida();
			NDeclaracion[] pent = n.obtParametrosEntrada();
			NDescripcion[] dent = n.obtDescripcionesEntrada();
			NDescripcion[] dsal = n.obtDescripcionesSalida();

			if ( Util.esVarSemantica(id.obtId()) ) {
				throw new ChequeadorException(id,
					Str.get("error.1_invalid_spec_name", id)
				);
			}

			if ( psal.length > 1 ) {
				// La sintaxis permite más de un dato de salida; PERO esto no se
				// soporta todavía.
				throw new ChequeadorException(
					id,
					Str.get("error.max_one_output_allowed")
				);
			}

			// Marcar la tabla;
			marca_tabla = tabSimb.marcar();

			// verifique cantidades de descripciones:
			if ( dent.length != pent.length && requiredDescription ) {
				IUbicable u = dent.length > 0 ? new Ubicacion(dent) : new Ubicacion(pent);
				throw new ChequeadorException(
					u,
					Str.get("error.different_number_input_descriptions")
				);
			}
			if ( dsal.length != psal.length && requiredDescription ) {
				IUbicable u = dsal.length > 0 ? new Ubicacion(dsal) : new Ubicacion(psal);
				throw new ChequeadorException(
					u,
					Str.get("error.different_number_output_descriptions")
				);
			}


			// Enlazar parámetros de entrada:
			for ( int i = 0; i < pent.length; i++ ) {
				if ( pent[i].tieneInicializacion() ) {
					throw new ChequeadorException(
						pent[i].obtId(),
						Str.get("error.1_invalid_param_init", pent[i].obtId())
					);
				}

				pent[i].aceptar(this);

				// Ponga que ha habido asignacion. Vea NId.aceptar(this):
				tabSimb.ponAsignado(pent[i].obtId().obtId(), true);
			}
			
			// check descriptions
			for ( int i = 0; i < dent.length; i++ ) {
				// Vea que la descripción sea para un nombre de entrada:
				String idd = dent[i].obtId().obtId();
				boolean ok = false;
				for ( int j = 0; j < pent.length; j++ ) {
					String idp = pent[j].obtId().obtId();
					if ( idd.equals(idp) ) {
						ok = true;
						break;
					}
				}
				if ( !ok ) {	
					throw new ChequeadorException(
						dent[i].obtId(),
						Str.get("error.1_unexpected_description", idd)
					);
				}
			}
			
			////////////////////////////////////////////////////////////////
			// Primero completamos todas las entradas (ver arriba) pero
			// tambien las  salidas antes de las pre/poscondiciones.

			// Enlazar parámetros de salida:
			for ( int i = 0; i < psal.length; i++ ) {
				if ( psal[i].esConstante() ) {
					throw new ChequeadorException(
						psal[i].obtId(),
						Str.get("error.output_cannot_be_constant")
					);
				}
				if ( psal[i].tieneInicializacion() ) {
					throw new ChequeadorException(
						psal[i].obtId(),
						Str.get("error.1_invalid_param_init", psal[i].obtId())
					);
				}

				psal[i].aceptar(this);

				tabSimb.ponAsignado(psal[i].obtId().obtId(), false);
			}
			
			if ( dsal.length > 0 && psal.length > 0 ) {
				// Vea que la descripción correspondiente sea para el nombre:
				if ( !dsal[0].obtId().obtId().equals(psal[0].obtId().obtId()) ) {	
					throw new ChequeadorException(
						dsal[0].obtId(),
						Str.get("error.1_expected_output_description", psal[0].obtId())
					);
				}
			}

			
			
			// Notese que en lo anterior la variable de salida se deja con estado
			// de asignacion falso para prevenir que en la PRE se utilice:
				
			// Chequear precondicion:
			NAfirmacion pre = n.obtPrecondicion();
			if ( pre == null ) {
				if ( pent.length > 0 && requiredPrePost ) {
					throw new ChequeadorException(
						new Ubicacion(pent),
						Str.get("error.expected_input_precond")
					);
				}
			}
			else {
				pre.aceptar(this);
			}


			// Chequear poscondicion:
			
			// Primero, se ponen salidas como asignadas:
			for ( int i = 0; i < psal.length; i++ ) {
				tabSimb.ponAsignado(psal[i].obtId().obtId(), true);
			}
			
			NAfirmacion pos = n.obtPoscondicion();
			if ( pos == null ) {
				if ( psal.length > 0 && requiredPrePost ) {
					throw new ChequeadorException(
						new Ubicacion(psal),
						Str.get("error.expected_output_postcond")
					);
				}
			}
			else {
				pos.aceptar(this);
			}
		}
		catch (IdIndefinidoException ex) {
			if ( permitirIdIndefinido ) {
				numIdIndefinidos++;
			}
			else {
				throw ex;
			}
		}
		finally {
			if ( marca_tabla >= 0 )
				tabSimb.irAMarca(marca_tabla);

		}

		if ( miinterf == null ) {
			n.ponNombreFuente(nombreFuente);
			_agregarEspecificacion(n);
			_guardarCompilado(n);
		}
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Chequea una expresión "this".
	 */
	public void visitar(NEste n)
	throws VisitanteException
	{
		if ( claseActual == null ) {
			throw new ChequeadorException(n,
				Str.get("error.1_invalid_place_for", Str.get("this"))
			);
		}

		n.ponTipo(Tipo.clase(claseActual.obtNombreCompleto()));
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un fuente completo.
	 */
	public void visitar(NFuente n)
	throws VisitanteException
	{
		for ( int pasada = 1; pasada <= 2; pasada++ ) {
			reiniciar();

			numIdIndefinidos = 0;

			NPaquete paquete = n.obtPaquete();
			if ( paquete != null ) {
				paquete.aceptar(this);
			}
			else if ( expectedPackageName != null ) {
				if ( expectedPackageName.length() > 0 ) {
					throw new ChequeadorException(
						n,
						Str.get("error.1_expected_package", expectedPackageName)
					);
				}
			}


			NUtiliza[] autz = (NUtiliza[]) n.obtUtilizas();
			if ( autz != null ) {
				for (int i = 0; i < autz.length; i++) {
					autz[i].aceptar(this);
				}
			}

			enAfirmacion = false;

			Nodo[] nodos = (Nodo[]) n.obtUnidades();

			// se permite el error IdIndefinidoException si es la primera pasada:
			permitirIdIndefinido = pasada == 1;

			for (int i = 0; i < nodos.length; i++ ) {
				unidadActual = null;
				interfaceActual = null;
				claseActual = null;
				nodos[i].aceptar(this);

				Util._assert(
					tabSimb.obtNumEntradas() == 0,
					"empty symbol table: after compiling " +nodos[i]+ "\n"
				);
			}
			//System.out.println(pasada+ " : " +numIdIndefinidos);

			if ( numIdIndefinidos == 0 ) {
				break;
			}
		}
	}

	//////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo NId.
	 * Primero revisa que no sea una variable semantica por fuera
	 * de una afirmacion. 
	 *
	 * Se trata primero de resolver con respecto a la tabla de simbolos
	 * y luego por el ambiente.
	 */
	public void visitar(NId n)
	throws VisitanteException
	{
		TId id = n.obtId();
		if ( !enAfirmacion
		&&	 Util.esVarSemantica(id) ) {
			throw new ChequeadorException(id,
				Str.get("error.1_invalid_place_for", id)
			);
		}

		if ( enInvocacion ) {
			if ( _resolverIdEnTablaSimbolos(n) 
			||   _resolverInvocacionIdEnAmbiente(n) ) {
				return;		// OK
			}

			// problema:
			throw new IdIndefinidoException(id,
				Str.get("error.1_undefined_algorithm", id)
			);
		}
		else {
			if ( _resolverIdEnTablaSimbolos(n)
			||   _resolverIdEnAmbiente(n) ) {
				return;		// OK
			}

			// problema:
			throw new IdIndefinidoException(id,
				Str.get("error.1_id_undefined", id)
			);
		}

	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NIgual n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.booleano);

		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();

		if ( !enAfirmacion )
		{
			e.aceptar(this);
			f.aceptar(this);
			Tipo e_tipo = e.obtTipo();
			Tipo f_tipo = f.obtTipo();

			_chequearIgualdad(
				n,
				n.obtOperador(), e_tipo, f_tipo
			);
			return;
		}

		//{ enAfirmacion }

		// Manejo para posibles variables semánticas:

		TId id_e = Util.obtVarSemantica(e);
		TId id_f = Util.obtVarSemantica(f);

		if ( id_e == null && id_f == null )
		{
			//{ No hay variables semánticas }

			// Haga lo normal:
			e.aceptar(this);
			f.aceptar(this);
			Tipo e_tipo = e.obtTipo();
			Tipo f_tipo = f.obtTipo();

			_chequearIgualdad(
				n,
				n.obtOperador(), e_tipo, f_tipo
			);
			return;	// listo
		}


		//{ Hay variables semánticas }

		ChequeadorException se1 = null, se2 = null;

		int mal = 2;	// asuma mal las dos expresiones
		try
		{
			e.aceptar(this);
			mal--;
		}
		catch(ChequeadorException sex)
		{
			se1 = sex;
		}
		try
		{
			f.aceptar(this);
			mal--;
		}
		catch(ChequeadorException sex)
		{
			se2 = sex;
		}

		if ( mal == 2 )
		{
			// ambas expresiones malas.
			// Lance la primera exception:
			throw se1;
		}


		if ( mal == 0 )
		{
			// ambas expresiones buenas.
			// Haga lo normal:

			Tipo e_tipo = e.obtTipo();
			Tipo f_tipo = f.obtTipo();

			_chequearIgualdad(
				n,
				n.obtOperador(), e_tipo, f_tipo
			);
			return;	// listo
		}

		//{ mal == 1 }
		// Es decir, { se1 != null XOR se2 != null }
		// Trate de declarar una variable semantica

		if ( se1 != null )
		{
			//{ e mal && f bien }
			// e debe ser variable semántica:
			if ( id_e == null )
				throw se1;		// no lo es

			//{ id_e != null }

			// Instancie (o sea, declare) id_e:
			EntradaTabla et = new EntradaTabla(id_e.obtId(), f.obtTipo());
			// y póngalo como asignado:
			et.ponAsignado(true);
			_insertarEnTablaSimbolos(et, id_e);
		}
		else //{ se2 != null }
		{
			//{ e bien && f mal }
			// f debe ser variable semántica:
			if ( id_f == null )
				throw se2;		// no lo es

			//{ id_f != null }

			// Instancie (o sea, declare) id_f:
			EntradaTabla et = new EntradaTabla(id_f.obtId(), e.obtTipo());
			// y póngalo como asignado:
			et.ponAsignado(true);
			_insertarEnTablaSimbolos(et, id_f);
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NImplicacion n)
	throws VisitanteException
	{
		_visitarBinBooleano(n);
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NInvocacion n)
	throws VisitanteException
	{
		NExpresion expr = n.obtExpresion();
		NExpresion[] args = n.obtArgumentos();

		try {
			enInvocacion = expr instanceof NSubId ||
			               expr instanceof NId || expr instanceof NNombre;
			expr.aceptar(this);
		}
		finally {
			enInvocacion = false;
		}

		Tipo expr_tipo = expr.obtTipo();
		if ( !expr_tipo.esInvocable() ) {
			throw new ChequeadorException(
				expr,
				Str.get("error.1_not_callable_type", expr_tipo)
			);
		}

		NEspecificacion nespec = null;
		boolean es_operacion = false;

		if ( expr_tipo instanceof TipoEspecificacion ) {
			TipoEspecificacion te = (TipoEspecificacion) expr_tipo;
			String[] nom_espec = te.obtNombreEspecificacion();
			TipoInterface ti = te.obtInterface();
			if ( ti == null ) {
				nespec = _obtEspecificacionParaNombre(nom_espec);
			}
			else {
				try {
					nespec = ti.obtOperacion(nom_espec[0]);
					es_operacion = true;
				}
				catch(ClaseNoEncontradaException ex) {
					throw new ChequeadorException(expr,
						Str.get("error.1_class_not_found", ex.obtNombre())
					);
				}
			}
		}
		else {
			// NO DEBERIA SUCEDER (expr_tipo esInvocable!)
			throw new RuntimeException(
				"Internal error: Not callable: " +expr_tipo.getClass()
			);
		}

		if ( nespec == null ) {
			throw new ChequeadorException(expr,
				Str.get("error.unknown_spec")
			);
		}

		NDeclaracion[] nespec_pent = nespec.obtParametrosEntrada();
		if ( nespec_pent.length != args.length ) {
			throw new ChequeadorException(
				expr,
				Str.get("error.1_expected_arguments", ""+nespec_pent.length)
			);
		}

		for ( int i = 0; i < args.length; i++ ) {
			NExpresion a = args[i];
			a.aceptar(this);
			Tipo tipo_dado = a.obtTipo();
			Tipo tesperado = nespec_pent[i].obtTipo();
			_chequearAsignabilidad(a, tesperado, tipo_dado, a);
		}

		NDeclaracion[] nespec_psal = nespec.obtParametrosSalida();
		if ( nespec_psal.length == 1 ) {
			n.ponTipo(nespec_psal[0].obtTipo());
		}
		else if ( nespec_psal.length == 0 ) {
			n.ponTipo(Tipo.unit);
		}
		else {
			// no va a suceder.
			throw new RuntimeException("Multiple output not implemented");
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NLiteralBooleano n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.booleano);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NLiteralCadena n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.cadena);
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NLiteralCaracter n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.caracter);
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un literal entero.
	 * Es posible que el literal, aunque es entero, no pueda tomarse como
	 * un entero de Loro (que es el mismo de Java).
	 */
	public void visitar(NLiteralEntero n)
	throws VisitanteException
	{
		Object valor = n.obtValor();
		if ( valor instanceof NumberFormatException ) {
			throw new ChequeadorException(
				n,
				Str.get("error.cannot_represent_integer")
			);
		}
		n.ponTipo(Tipo.entero);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NLiteralNulo n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.nulo);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NLiteralReal n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.real);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NMas n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();

		e.aceptar(this);
		f.aceptar(this);

		Tipo e_tipo = e.obtTipo();
		Tipo f_tipo = f.obtTipo();

		// Si alguna de las expresiones e o f es Unit,
		// hay error
		if ( e_tipo.esUnit()
		||   f_tipo.esUnit() )
		{
			_operadorBinNoDefinido(
				n,
				n.obtOperador(), e_tipo, f_tipo
			);
		}

		// Si alguna de las expresiones e ó f es cadena,
		// se toma como concatenación.
		if ( e_tipo.esCadena()
		||   f_tipo.esCadena() )
		{
			n.ponTipo(Tipo.cadena);
			return;
		}

		// Aquí continúa el código que se tenía previamente:
		if ( !e_tipo.esNumerico()
		||   !f_tipo.esNumerico() )
		{
			_operadorBinNoDefinido(
				n,
				n.obtOperador(), e_tipo, f_tipo
			);
		}

		if ( e_tipo.esReal() || f_tipo.esReal() )
			n.ponTipo(Tipo.real);
		else
			n.ponTipo(Tipo.entero);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NMayor n)
	throws VisitanteException
	{
		_visitarComparacion(n);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NMayorIgual n)
	throws VisitanteException
	{
		_visitarComparacion(n);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NMenor n)
	throws VisitanteException
	{
		_visitarComparacion(n);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NMenorIgual n)
	throws VisitanteException
	{
		_visitarComparacion(n);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NMenos n)
	throws VisitanteException
	{
		_visitarBinNumerico(n);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NMientras n)
	throws VisitanteException
	{
		TId etq = n.obtEtiqueta();
		String label = _chequearDefinicionEtiqueta(etq);

		NExpresion e = n.obtCondicion();
		e.aceptar(this);
		Tipo e_tipo = e.obtTipo();
		if ( !e_tipo.esBooleano() ) {
			throw new ChequeadorException(
				e,
				Str.get("error.not_boolean_expression")
			);
		}

		int marca = tabSimb.marcar();
		labels.push(label);
		try {
			visitarAcciones(n.obtAcciones());
		}
		finally {
			tabSimb.irAMarca(marca);
			labels.pop();
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NMod n)
	throws VisitanteException
	{
		_visitarBinEntero(n);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NNeg n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		e.aceptar(this);
		Tipo e_tipo = e.obtTipo();

		if ( !e_tipo.esNumerico() ) {
			_operadorUnNoDefinido(
				e,
				n.obtOperador(), e_tipo
			);
		}

		n.ponTipo(e_tipo);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NNo n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		e.aceptar(this);
		Tipo e_tipo = e.obtTipo();

		if ( !e_tipo.esBooleano() ) {
			_operadorUnNoDefinido(
				e,
				n.obtOperador(), e_tipo
			);
		}

		n.ponTipo(e_tipo);
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NNoBit n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		e.aceptar(this);
		Tipo e_tipo = e.obtTipo();

		if ( !e_tipo.esEntero() ) {
			_operadorUnNoDefinido(
				e,
				n.obtOperador(), e_tipo
			);
		}

		n.ponTipo(e_tipo);
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Se revisa que no haya variables semanticas.
	 * Si no se esta en una invocacion, hay error de posible uso de
	 * una unidad como un valor.
	 * Si se esta en una invocacion, se obtiene el algoritmo correspondiente
	 * al nombre.
	 */
	public void visitar(NNombre n)
	throws VisitanteException
	{
		TNombre nom = n.obtNombre();
		TId[] ids = nom.obtIds();
		if ( !enAfirmacion ) {
			for (int i = 0; i < ids.length; i++) {
				if ( Util.esVarSemantica(ids[i]) ) {
					throw new ChequeadorException(ids[i],
						Str.get("error.1_invalid_place_for", ids[i])
					);
				}
			}
		}

		if ( enInvocacion ) {
			NAlgoritmo alg = _obtAlgoritmoParaNombre(nom);
			if ( alg == null )
			{
				throw new ChequeadorException(n,
					Str.get("error.1_undefined_algorithm", nom.obtCadena())
				);
			}

			Tipo tipo = Tipo.especificacion(alg.obtNombreEspecificacion());
			n.ponTipo(tipo);
		}
		else
		{
			// 2003-03-09
			// permitimos que se trate de un algoritmo:
			NAlgoritmo alg = _obtAlgoritmoParaNombre(nom);
			if ( alg != null ) {
				n.ponTipo(Tipo.especificacion(alg.obtNombreEspecificacion()));
				return;
			}
			
			NClase clase = _obtClaseParaNombre(nom);
			if ( clase != null ) {
				throw new ChequeadorException(n,
					Str.get("error.1_cannot_use_class_as_value", nom.obtCadena())
				);
			}
			
			//
			// PENDIENTE
			// A continuación podría venir un _obtObjetoParaNombre(nom) (aún no
			// implementado) para cuando se complemente manejo de objetos.
			// 2002-06-04
			//
			
			// ahora sólo queda reportar error:
			throw new ChequeadorException(n,
				Str.get("error.1_cannot_use_unit_as_value", nom.obtCadena())
			);
		}
	}
	
	////////////////////////////////////////////////////////////////
	/** Visits a NQualifiedName */
	public void visitar(NQualifiedName n)
	throws VisitanteException
	{
		String what = n.getWhat();
		TNombre nom = n.obtNombre();
		TId[] ids = nom.obtIds();
		if ( !enAfirmacion ) {
			for (int i = 0; i < ids.length; i++) {
				if ( Util.esVarSemantica(ids[i]) ) {
					throw new ChequeadorException(ids[i],
						Str.get("error.1_invalid_place_for", ids[i])
					);
				}
			}
		}

		if ( what.equals("algorithm") ) {
			NAlgoritmo alg = _obtAlgoritmoParaNombre(nom);
			if ( alg == null ) {
				throw new ChequeadorException(n,
					Str.get("error.1_undefined_algorithm", nom.obtCadena())
				);
			}
			Tipo tipo = Tipo.especificacion(alg.obtNombreEspecificacion());
			n.ponTipo(tipo);
		}
		else if ( what.equals("object") ) {
			throw new ChequeadorException(n,
				"search for object: not yet implemented"
			);
		}
		else {
			throw new RuntimeException(
				"EXPECTED: algorithm or object"
			);
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NO n)
	throws VisitanteException
	{
		_visitarBinBooleano(n);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NOArit n)
	throws VisitanteException
	{
		_visitarBinEnteroBooleano(n);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NOExc n)
	throws VisitanteException
	{
		_visitarBinEnteroBooleano(n);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NPaquete n)
	throws VisitanteException
	{
		if ( expectedPackageName != null ) {
			String pkgname = n.obtNPaquete().obtCadena();
			if ( expectedPackageName.length() == 0 ) {
				throw new ChequeadorException(
					n,
					Str.get("error.unexpected_package")
				);
			}
			else if ( ! expectedPackageName.equals(pkgname) ) {
				throw new ChequeadorException(
					n,
					Str.get("error.1_expected_package", expectedPackageName)
				);
			}
		}
		TNombre tnombre = n.obtNPaquete();
		TId[] tids = tnombre.obtIds();
		for ( int i = 0; i < tids.length; i++ ) {
			if ( Util.esVarSemantica(tids[i]) ) {
				throw new ChequeadorException(
					tnombre, 
					Str.get("error.1_invalid_place_for", tids[i])
				);
			}
		}
		paqueteActual = tnombre.obtCadenas();
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NForEach n)
	throws VisitanteException
	{
		TId etq = n.obtEtiqueta();
		String label = _chequearDefinicionEtiqueta(etq);
		TId id = n.obtId();
		NDeclaracion dec = n.obtDeclaracion();
		
		NExpresion in = n.obtExpresionEn();
		in.aceptar(this);
		Tipo in_tipo = in.obtTipo();
		if ( !in_tipo.esArreglo()
		&&   !in_tipo.esCadena() )
		{
			throw new ChequeadorException(
				in,
				Str.get("error.expected_array_or_string")
			);
		}

		int marca = tabSimb.marcar();	// marcar nuevo ambito
		labels.push(label);

		try
		{
			Tipo var_tipo;
			if ( dec == null )		// o sea, id != null.
			{
				EntradaTabla et = tabSimb.buscar(id.obtId());
				if ( et == null )
				{
					throw new ChequeadorException(
						id,
						Str.get("error.1_id_undefined", id)
					);
				}
				var_tipo = et.obtTipo();
				tabSimb.ponAsignado(id.obtId(), true);
			}
			else
			{
				dec.aceptar(this);
				var_tipo = dec.obtTipo();
				TId dec_id = dec.obtId();
				tabSimb.ponAsignado(dec_id.obtId(), true);
			}
			
			Tipo elem_tipo = in_tipo.obtTipoElemento();
			IUbicable u = id;
			if ( u == null )
				u = dec;
			_chequearAsignabilidad(u, var_tipo, elem_tipo, null);
	
			Nodo[] acciones = n.obtAcciones();
			visitarAcciones(acciones);
		}
		finally
		{
			tabSimb.irAMarca(marca);
			labels.pop();
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NPara n)
	throws VisitanteException
	{
		TId etq = n.obtEtiqueta();
		String label = _chequearDefinicionEtiqueta(etq);
		TId id = n.obtId();
		NDeclaracion dec = n.obtDeclaracion();
		if ( dec == null )		// o sea, id != null.
		{
			EntradaTabla et = tabSimb.buscar(id.obtId());
			if ( et == null )
			{
				throw new ChequeadorException(
					id,
					Str.get("error.1_id_undefined", id)
				);
			}
			if ( !et.obtTipo().esEntero() )
			{
				throw new ChequeadorException(
					id,
					Str.get("error.1_id_invalid_type", id)
				);
			}
		}
		// else: dec != null. Declaración interna.  Ver más abajo.

		NExpresion desde = n.obtExpresionDesde();
		desde.aceptar(this);
		Tipo desde_tipo = desde.obtTipo();
		if ( !desde_tipo.esEntero() ) {
			throw new ChequeadorException(
				desde,
				Str.get("error.1_expected_type", Str.get("integer"))
			);
		}

		NExpresion paso = n.obtExpresionPaso();
		if ( paso != null ) {
			paso.aceptar(this);
			Tipo paso_tipo = paso.obtTipo();
			if ( !desde_tipo.igual(paso_tipo) ) {
				throw new ChequeadorException(
					paso,
					Str.get("error.1_expected_type", Str.get("integer"))
				);
			}
		}

		NExpresion hasta = n.obtExpresionHasta();
		hasta.aceptar(this);
		Tipo hasta_tipo = hasta.obtTipo();
		if ( !desde_tipo.igual(hasta_tipo) ) {
			throw new ChequeadorException(
				hasta,
				Str.get("error.1_expected_type", Str.get("integer"))
			);
		}

		int marca = tabSimb.marcar();	// marcar nuevo ambito
		labels.push(label);

		try {
			if ( dec != null ) {		// Declaración interna
				dec.aceptar(this);
				Tipo dec_tipo = dec.obtTipo();
				TId dec_id = dec.obtId();
				if ( !dec_tipo.esEntero() ) {
					throw new ChequeadorException(
						dec_id,
						Str.get("error.1_expected_type", Str.get("integer"))
					);
				}
				tabSimb.ponAsignado(dec_id.obtId(), true);
			}
			else {
				tabSimb.ponAsignado(id.obtId(), true);
			}
	
			Nodo[] acciones = n.obtAcciones();
			visitarAcciones(acciones);
		}
		finally {
			tabSimb.irAMarca(marca);
			labels.pop();
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NPlus n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresion();
		e.aceptar(this);
		Tipo e_tipo = e.obtTipo();

		if ( !e_tipo.esNumerico() )
		{
			_operadorUnNoDefinido(
				e,
				n.obtOperador(), e_tipo
			);
		}

		n.ponTipo(e_tipo);
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NPor n)
	throws VisitanteException
	{
		_visitarBinNumerico(n);
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NRepita n)
	throws VisitanteException
	{
		TId etq = n.obtEtiqueta();
		String label = _chequearDefinicionEtiqueta(etq);
		int marca = tabSimb.marcar();
		labels.push(label);
		try {
			Nodo[] acciones = n.obtAcciones();
			visitarAcciones(acciones);
	
			NExpresion e = n.obtCondicion();
			e.aceptar(this);
			Tipo e_tipo = e.obtTipo();
			if ( !e_tipo.esBooleano() ) {
				throw new ChequeadorException(
					e,
					Str.get("error.not_boolean_expression")
				);
			}
		}
		finally {
			tabSimb.irAMarca(marca);
			labels.pop();
		}
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Se chequea que unidadActual sea NAlgoritmo.
	 */
	public void visitar(NRetorne n)
	throws VisitanteException
	{
		if ( !(unidadActual instanceof NAlgoritmo) ) {
			throw new ChequeadorException(n,
				Str.get("error.1_invalid_place_for", Str.get("return"))
			);
		}

		NAlgoritmo p = (NAlgoritmo) unidadActual;

		NDeclaracion[] p_psal = p.obtParametrosSalida();
		int numExprEsperadas = p_psal.length;

		TId p_id = p.obtId();
		NExpresion[] expresiones = n.obtExpresiones();
		if ( expresiones.length != numExprEsperadas ) {
			if ( numExprEsperadas == 0 ) {
				throw new ChequeadorException(
					n,
					Str.get("error.unexpected_return_value")
				);
			}
			else {
				throw new ChequeadorException(
					n,
					Str.get("error.expected_return_value")
				);
			}
		}

		for ( int i = 0; i < expresiones.length; i++ )
		{
			NExpresion e = expresiones[i];
			e.aceptar(this);
			Tipo e_tipo = e.obtTipo();
			NDeclaracion d = p_psal[i];
			Tipo d_tipo = d.obtTipo();

			// revise asignabilidad:
			_chequearAsignabilidad(e, d_tipo, e_tipo, e);

			TId d_id = d.obtId();
			if ( ! tabSimb.obtAsignado(d_id.obtId()) )
			{
				// Se pone como asignado:
				tabSimb.ponAsignado(d_id.obtId(), true);
			}
		}

	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 *
	 */
	public void visitar(NLance n)
	throws VisitanteException
	{
		n.obtExpresion().aceptar(this);
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	public void visitar(NIntente n)
	throws VisitanteException
	{
		Nodo[] acciones = n.obtAcciones();
		NAtrape[] cc = n.obtAtrapes();
		NAtrape f = n.obtSiempre();
		
		if ( cc.length == 0 && f == null ) {
			throw new ChequeadorException(
				n,
				Str.get("error.try_with_no_catch_finally")
			);
		}
		
		int marca = tabSimb.marcar();
		try {
			visitarAcciones(acciones);
		}
		finally {
			tabSimb.irAMarca(marca);
		}
		
		visitarAcciones(cc);
		if ( f != null )
			f.aceptar(this);
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 *
	 */
	public void visitar(NAtrape n)
	throws VisitanteException
	{
		int marca = tabSimb.marcar();
		try
		{
			NDeclaracion d = n.obtDeclaracion();
			if ( d != null )
			{
				d.aceptar(this);
				tabSimb.ponAsignado(d.obtId().obtId(), true);		
			}
			visitarAcciones(n.obtAcciones());
		}
		finally
		{
			tabSimb.irAMarca(marca);
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NSubId n)
	throws VisitanteException
	{
		TId id = n.obtId();
		NExpresion e = n.obtExpresion();
		e.aceptar(this);
		Tipo e_tipo = e.obtTipo();

		if ( e_tipo.esNulo() ) {
			throw new ChequeadorException(
				e,
				Str.get("error.1_invalid_place_for", Str.get("null"))
			);
		}
		else if ( e_tipo.esInterface() ) {
			if ( ! enInvocacion ) {
				throw new ChequeadorException(
					id,
					Str.get("error.cannot_access_attrs_through_interface")
				);
			}
			TipoInterface ti = (TipoInterface) e_tipo;
			NEspecificacion oper;
			try {
				oper = ti.obtOperacion(id.obtId());
			}
			catch(ClaseNoEncontradaException ex) {
				throw new ChequeadorException(
					id,
					Str.get("error.1_class_not_found", ex.obtNombre())
				);
			}
			
			if ( oper == null ) {
				throw new ChequeadorException(
					id,
					Str.get("error.2_no_operation_in_interface", ti, id)
				);
			}

			n.ponTipo(Tipo.especificacion(ti, id.obtId()));
		}
		else if ( e_tipo.esClase() ) {
			NClase clase;
			if ( e instanceof NEste ) {
				clase = claseActual;
			}
			else {
				TipoClase tipoClase = (TipoClase) e_tipo;
				String[] nombre = tipoClase.obtNombreConPaquete();
				String snombre = Util.obtStringRuta(nombre);
				clase = mu.obtClase(snombre);
			}
			
			Tipo tipo = _obtTipoAtributo(n, clase, id.obtId());
			if ( tipo == null ) {
				if ( enInvocacion ) {
					tipo = _obtTipoMetodo(n, clase, id.obtId());
				}
			}
			
			if ( tipo == null ) {
				throw new ChequeadorException(
					id,
					Str.get("error.2_no_member", e_tipo, id)
				);
			}
			
			n.ponTipo(tipo);
		}
		// e puede ser un arreglo o cadena:
		else if ( e_tipo.esArreglo() || e_tipo.esCadena() ) {
			String oper = id.obtId();
			if ( oper.equals(Str.get("length"))
			||   oper.equals(Str.get("inf"))
			||   oper.equals(Str.get("sup")) ) {
				n.ponTipo(Tipo.entero);
				return;
			}
			else {
				throw new ChequeadorException(
					id,
					Str.get("error.1_invalid_operation", oper)
				);
			}
		}
		else
		{
			throw new ChequeadorException(
				e,
				Str.get("error.1_cannot_be_dereferenced", e_tipo)
			);
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NSubindexacion n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();

		e.aceptar(this);
		Tipo e_tipo = e.obtTipo();

		if ( !e_tipo.esArreglo()
		&&   !e_tipo.esCadena() )
		{
			throw new ChequeadorException(
				e,
				Str.get("error.error.expected_array_or_string")
			);
		}


		f.aceptar(this);
		Tipo f_tipo = f.obtTipo();
		if ( !f_tipo.esEntero()  )
		{
			throw new ChequeadorException(
				f,
				Str.get("error.integer_expression_expected")
			);
		}

		n.ponTipo(e_tipo.obtTipoElemento());
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NTermine n)
	throws VisitanteException
	{
		TId etq = n.obtEtiqueta();
		_chequearEtiqueta(true, etq, n);
		NExpresion e = n.obtExpresion();
		if ( e != null )
		{
			e.aceptar(this);
			Tipo e_tipo = e.obtTipo();
			if ( !e_tipo.esBooleano() )
			{
				throw new ChequeadorException(
					e,
					Str.get("error.not_boolean_expression")
				);
			}
		}
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NUtiliza n)
	throws VisitanteException
	{
		String q = n.obtQue();
		TNombre nom = n.obtNombre();
		String s = nom.obtCadena();
		NUnidad unidad;

		if ( q.equals("specification") ) {
			unidad = mu.obtEspecificacion(s);
		}
		else if ( q.equals("algorithm") ) {
			unidad = mu.obtAlgoritmo(s);
		}
		else {  // q.equals("class") 
			unidad = mu.obtClase(s);
		}

		if ( unidad == null ) {
			throw new ChequeadorException(n,
				Str.get("error.2_what_not_found", q, s)
			);
		}

		TId[] ids = nom.obtIds();
		String simple = ids[ids.length -1].obtId();
		String elem = (String) utiliza.get(q+ ":" +simple);
		if ( elem != null ) {
			String comodin;
			if ( q.equals("specification") )
				comodin = Str.get("specificacions");
			else if ( q.equals("algorithm") )
				comodin = Str.get("algorithms");
			else // q.equals("class")
				comodin = Str.get("classes");

			throw new ChequeadorException(n,
				Str.get("error.2_uses_same_name", comodin, simple)
			);
		}

		// anote este elemento utilizado:
		utiliza.put(q+ ":" +simple, s);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NY n)
	throws VisitanteException
	{
		_visitarBinBooleano(n);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Chequea un nodo.
	 */
	public void visitar(NYArit n)
	throws VisitanteException
	{
		_visitarBinEnteroBooleano(n);
	}

	/////////////////////////////////////////////////////////////////////////
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

	/////////////////////////////////////////////////////////////////////////
	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 */
	public void visitar(NTipoBooleano n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.booleano);
	}

	/////////////////////////////////////////////////////////////////////////
	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 */
	public void visitar(NTipoCadena n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.cadena);
	}

	/////////////////////////////////////////////////////////////////////////
	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 */
	public void visitar(NTipoCaracter n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.caracter);
	}

	/////////////////////////////////////////////////////////////////////////
	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 */
	public void visitar(NTipoClase n)
	throws VisitanteException
	{
		TNombre nombre = n.obtTNombre();

		Util._assert(nombre != null, "class name must be non-null");
		
		NClase clase = _obtClaseParaNombre(nombre);
		if ( clase == null ) {
			throw new IdIndefinidoException(
				n,
				Str.get("error.1_class_not_found", nombre)
			);
		}
		Tipo tipo = Tipo.clase(clase.obtNombreCompleto());
		n.ponTipo(tipo);
	}

	/////////////////////////////////////////////////////////////////////////
	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 */
	public void visitar(NTipoEntero n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.entero);
	}

	/////////////////////////////////////////////////////////////////////////
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

		String[] anombre = null;

		NEspecificacion espec = null;
		if ( nombre != null )
		{
			espec = _obtEspecificacionParaNombre(nombre);
			if ( espec == null ) {
				String s = nombre.obtCadena();
				throw new IdIndefinidoException(
					n,
					Str.get("error.1_spec_not_found", s)
				);
			}

			anombre = espec.obtNombreCompleto();
		}

		Tipo tipo = Tipo.especificacion(anombre);
		n.ponTipo(tipo);
	}

	/////////////////////////////////////////////////////////////////////////
	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 */
	public void visitar(NTipoReal n)
	throws VisitanteException
	{
		n.ponTipo(Tipo.real);
	}

	//////////////////////////////////////////////////////////////////////////
	/**
	 * Chequea una expresión 'implementa'.
	 * Actualmente (2002-09-23) tiene en cuenta:
	 * <pre>
	 *    algun_algoritmo 'implementa'  alguna_especificacion
	 * </pre>
	 */
	public void visitar(NImplementa n)
	throws VisitanteException
	{
		// chequee la expresion:
		NExpresion e = n.obtExpresion();
		e.aceptar(this);
		Tipo e_tipo = e.obtTipo();
		
		// chequee el tipo revisado:
		NTipo ntipoRevisado = n.obtNTipoRevisado();
		ntipoRevisado.aceptar(this);
		Tipo tipoRevisado = ntipoRevisado.obtTipo();


		if ( e_tipo.esNulo() ) {
			throw new ChequeadorException(
				e,
				Str.get("null_reference")
			);
		}

		if ( !e_tipo.esAlgoritmo() ) {
			throw new ChequeadorException(
				e,
				Str.get("error.not_an_algorithm")
			);
		}

		if ( !tipoRevisado.esAlgoritmo() ) {
			throw new ChequeadorException(
				n.obtNTipoRevisado(),
				Str.get("error.not_a_spec")
			);
		}
		
		n.ponTipo(Tipo.booleano);
		return;		
		
		///////////  código anterior parcial para clases/métodos/etc:
		/*
		if ( true )
		{
			throw new ChequeadorException(
				n,
				"Operation 'implements' not yet implemented for objects/classes."
			);
		}
		

		// tipo revisado debe ser interface:
		if ( !tipoRevisado.esInterface() )
		{
			throw new ChequeadorException
			(
				n.obtNTipoRevisado(),
				"El tipo " +tipoRevisado+ " no es interface y por lo tanto no se puede\n"+
				"verificar si algun objeto lo implementa."
			);
		}

		if ( false ) // siempre se permite sin importar la interface en particular.
		{
			// se deja el siguiente codigo en caso de cambiar de opinion ;-)
			
			// Note: en las dos direcciones:
			boolean ok = _aKindOf(n, (TipoInterface)tipoRevisado, (TipoInterface)e_tipo)
					  || _aKindOf(n, (TipoInterface)e_tipo, (TipoInterface)tipoRevisado)
			;

			if ( !ok )
			{
				throw new ChequeadorException(
					e,
					"El objeto '" +e_tipo+ "' no implementa '" +tipoRevisado+ "'"
				);
			}
		}

		n.ponTipo(Tipo.booleano);
		*/
	}

	/////////////////////////////////////////////////////////////////////////
	/**
	 * Actualiza el tipo del nodo-tipo visitado.
	 * NOTA: Implementación INCOMPLETA.
	 */
	public void visitar(NTipoInterface n)
	throws VisitanteException
	{
		TNombre nombre = n.obtTNombre();

		NInterface interf = _obtInterfaceParaNombre(nombre);
		if ( interf == null ) {
			throw new IdIndefinidoException(
				n,
				Str.get("error.1_interface_not_found", nombre)
			);
		}
		Tipo tipo = Tipo.interface_(interf.obtNombreCompleto());
		n.ponTipo(tipo);
	}

	////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para visitar en orden una lista de acciones.
	 */
	public void visitarAcciones(Nodo[] nodos)
	throws VisitanteException
	{
		for ( int i = 0; i < nodos.length; i++ ) {
			visitarAccion(nodos[i]);
		}
	}

	////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para visitar una acción.
	 * Ya que una expresión puede ser una acción, aquí se revisa que 
	 * dicha expresión sea permitida. Las expresiones permitidas
	 * como acciones son:
	 * <ul>
	 * <li> NAsignacion
	 * <li> NInvocacion
	 * <li> NCrearArreglo
	 * <li> NCrearObjeto
	 * <li> NCondicion
	 * </ul>
	 * En general cualquier expresión puede tener efectos secundarios que
	 * deberían respetarse. Sin embargo, sólo se permite que la posibilidad
	 * de este efecto secundario sea a través de una de las posibilidades
	 * indicadas.
	 * Si la expresión no es permitida, se genera un error estilo:
	 *   "Esta acción no tiene ningún efecto"
	 *
	 * @since 0.8pre1
	 */
	public void visitarAccion(Nodo n)
	throws VisitanteException
	{
		n.aceptar(this);
		
		if ( n instanceof NExpresion )
		{
			if ( n instanceof NAsignacion
			||   n instanceof NInvocacion
			||   n instanceof NCrearArreglo
			||   n instanceof NCrearObjeto
			||   n instanceof NCondicion ) {
				// OK
			}
			else {
				throw new ChequeadorException(
					n,
					Str.get("error.not_a_statement")
				);
			}
		}
	}
}
