Loro README

	pendientes:
	
		- Definir concepto de "orden de búsqueda"
		  Quizá OroLoaderManager podría encargarse de todo este asunto
		  (incluyendo el coreOroLoader y el dirGuardarOroLoader)
		  El orden siempre comenzaría con:
			1) coreOroLoader
			2) dirGuardarOroLoader
			3) directorios
			4) extensiones
		  pero falta definirse completamente.
		  
		- Compilación "integral" de un directorio al estilo de como el Entorno
		  compila un proyecto completo.

NOTAS
	
	- Si un elemento dentro de un paquete foo necesita referenciar un
	  elemento baz que está en el paquete anónimo, debe hacer un 
	  'utiliza xxx baz;' correspondiente. Es decir, el paquete anónimo
	  NO es visible automáticamente desde otros paquetes.
	  (Como contraste, en Java no es posible acceder al paquete anónimo
	  desde un paquete nombrado ni siquiera con un 'import' --el import 
	  no acepta un nombre simple.)


////////////////////////////////////////////////////////////////////////////
2003-02-10 Version 0.7.7

Ejecución paso-a-paso

	- EjecutorPP.java: Se hace _obtSenalPP() tanto en _pushEvent()
	  como en _popEvent().

////////////////////////////////////////////////////////////////////////////
2003-02-09 Version 0.7.7

Ejecución paso-a-paso

	- Nuevo visitante Seguidor que extiende LoroEjecutor con el fin
	  de proporcionar notificaciones al entrar-a y salir-de cada nodo
	  visitado. Ahora EjecutorTerminable extiende Seguidor.
	  EjecutorPP (que extiende EjecutorTerminable) desde luego también
	  hace uso de este esquema.
	  
	- En DerivadorJavaCC.derivarFuente() corregida la asociación de 
	  código fuente para cada unidad. (Antes se ponía sólo el fragmento
	  propio de la unidad, pero debe ponerse todo el código fuente
	  de donde proviene.)
	  
	- ControlPP.setActive(boolean active) no estaba llamando notifyAll().
	  Corregido. (Este bug hacia congelar la ejecución cuando se
	  invocaba ejecutor.resume().)
	
////////////////////////////////////////////////////////////////////////////
2003-02-06 Version 0.7.7

Ejecución paso-a-paso

	- Mejoramiento general.
	
////////////////////////////////////////////////////////////////////////////
2003-02-05 Version 0.7.7

Ejecución paso-a-paso

	- En EjecutorPP atención de eventos asociados a la pila de ejecucion:
	  _pushEvent, _popEvent (llamados desde la superclase LoroEjecutorBase).

	- Más argumentos para IObservadorPP.ver:
		IObservadorPP.ver(IUbicable u, ISymbolTable symbTab, String src);
		
	- Aun falta sincronizar todo el asunto, por ejemplo revisar qué nodos
	  no se "visitan" directamente y por lo tanto se requiere algún
	  mecanismo para notificar su proceso...
	
////////////////////////////////////////////////////////////////////////////
2003-02-04 Version 0.7.7

Ejecución paso-a-paso

	- El constructor de InterpreteImpl recibe ahora un IObservadorPP obspp
	  para crear un EjecutorPP. Si el parámetro es nulo, se crea entonces
	  un EjecutorTerminable.
	  
	- El servicio Loro.crearInterprete ahora recibe a su vez el observador
	  para seguimiento paso-a-paso, de tal manera que se delega en el cliente
	  la decisión de si quiere una ejecución con o sin seguimiento paso-a-paso, 
	  dependiendo de si suministra un observador o no (null).
	  

////////////////////////////////////////////////////////////////////////////
2002-12-05 Version 0.7.7

	- Diseño preliminar de ejecución paso-a-paso.
	  	ControlPP
		EjecutorPP
		IObservadorPP
	  Compilación bien, pero se deshabilita por ahora. 
	  En el constructor de InterpreteImpl basta con cambiar una condición
	  (ver nota allí) para habilitar de nuevo las pruebas de paso-a-paso.
		
	
////////////////////////////////////////////////////////////////////////////
2002-11-27 Version 0.7.7

	- TablaSimbolos: Quitado el manejo basado en stack de marcas. Este manejo
	  se hace ahora sólo en el estilo:
	  	int marca = tabSimb.marcar();
		try ...
		finally { tabSimb.irAMarca(marca); }
	  Esto da más claridad para garantizar el debido empilamiento con base
	  en el implícito empilamiento de la ejecución Java.
	  
	- Nuevo palabra reservada "interfaz", que es la palabra correcta
	  según la RAE.  Por ahora se deja también como sinónimo "interface".
	
Algunos avances en manejo de métodos, pero aún incompleto.

	- Manejo de "éste" como expresión.  Ver LoroIParser.jj.
	- Nuevo nodo NEste.
	- Actualizados varios fuentes (visitantes, ConstructorArbol, ...)
	- NAlgoritmo fue modificado, así que deja de haber compatibidad binaria.
	
	- Ahora todo acceso a los atributos de una clase se debe hacer a través
	  de "éste", tanto en constructores como en métodos. Anteriormente las
	  declaraciones de los atributos seguían vigentes durante las visita
	  tanto a contructores como a métodos. Un efecto de esto es que ahora
	  es posible declarar parámetros repitiendo nombres de atributos.
	  Este cambio parece interesante por disciplina para enseñanza pero
	  hay que someterlo a más consideración.
	  
	- Nueva operación LAmbiente.obtEste() para permitirle a código Java
	  acceder al objeto en implementación de métodos. Su implementación, como
	  otros, está en LoroEjecutor.
	  Pendiente acceso a métodos desde la interfaz en loro.ijava.
	
	- Siguen más pruebas para decidir hasta donde dejar este manejo para
	  una primera versión aunque no completa.  
	  
////////////////////////////////////////////////////////////////////////////
2002-11-21 Version 0.7.6

Nuevo manejo de excepciones

	- Nuevas palabras reservadas y sintaxis correspondiente para:
		lance, intente, atrape, siempre
	  Ver LoroIParser.jj. 
	  
	  La sintaxis para "atrapar" es:
		"intente"
			acciones
			(
				"atrape" "(" declaracion ")"
					acciones
				[ "fin" "atrape" ]
			)*
			[
				"siempre"
					acciones
			]
		"fin" "intente"
	  
	  y para "lanzar":
	  	"lance" expresion
	  
	- Nuevos nodos para el árbol: NLance, NIntente, NAtrape.
	- Nueva ControlLanceException.
	- Actualizados: ConstructorArbol, IVisitante, VisitanteProfundidad, Chequeador, 
	  LoroEjecutor, ControlException, InterpreteImpl.
	  
	Descripción
		En esta primera versión (digamos, exploratoria) no se incluye la declaración de
		si un proceso lanza o no excepciones. Además, se permite el lanzamiento de
		cualquier tipo de expresión, incluso primitivos.
	
	
	
	  
////////////////////////////////////////////////////////////////////////////
2002-11-18 Version 0.7.5

	- Nuevo Loro.expandExtensionFile(File file)
	  
	- Nuevo Loro._expandExtensions() que expande cada archivo paths_dir/xxx.lar
	  en paths_dir/xxx/ en caso que paths_dir/xxx/ no exista.
	  La idea es sólo referenciar subdirectorios de búsqueda bajo paths_dir
	  (y no *.lar). En la inicialización de _crearManejadorUnidades() sólo se 
	  mantienen para búsqueda los *.lar encontrados bajo ext_dir/.

	- En Loro._crearManejadorUnidades() se entonces el siguiente preparativo
	  relacionado con los archivos *.lar y directorios de búsqueda:
	  
	    - Se invoca primero _expandExtensions()
		
	  	- Se tienen en cuenta sólo los directorios encontrados bajo el directorio
		  indicado en configurar().
		  
		- Se agregan los *.lar encontrados bajo ext_dir pero estos van después en
		  la lista de búsqueda para que tengan menos prioridad.
		  
		- NOTA: los posibles archivos *.jar (java) se siguen buscando sólo
		  bajo ext_dir.  PENDIENTE unificar este manejo.
		
	  
////////////////////////////////////////////////////////////////////////////
2002-11-17 Version 0.7.5

	- Ajustes menores a OroLoaderManager.java y Loro.java
	
	- Nuevo ManejadorUnidades.addExtensionToPath(File file)
	- Nuevo Loro.addExtensionToPath(File file)
	
////////////////////////////////////////////////////////////////////////////
2002-11-07 Version 0.7.4

	- Nueva operación IOroLoader.getFilenames() para obtener lista completa
	  de los nombres de los elementos del cargador.
	  Actualizados: CoreOroLoader, DirectoryOroLoader, ZipFileOroLoader.
	  
	- Nuevos métodos en loro.util.Util para copiar elementos de una extensión
	  a un directorio o a otra extensión (.lar).
	  
	- Actualizado build-ext.html: ahora se utiliza <zip> a cambio de <jar>
	  (El MANIFEST.MF no se necesita).
	
////////////////////////////////////////////////////////////////////////////
2002-10-30 Version 0.7.4

	- En loro.util.Util:
		- Nuevo método similar a getFilenames, pero recibiendo también
		  un filename filter: getFilenames(String, boolean, FilenameFilter)
		- Nuevos métodos para agregar entradas a un archivo zip:
			copyDirectoryToZip(File, ZipOutputStream, FilenameFilter)
			copyFileToZip(String, String, ZipOutputStream)
	
////////////////////////////////////////////////////////////////////////////
2002-10-25 Version 0.7.4

	- Modificado InterpreteImpl para permitir reader y/o writer nulos.
	
	- Modificada gramatica para aceptar coma "," al final de la lista de
	  expresiones en definición de arreglo enumerado.
		
////////////////////////////////////////////////////////////////////////////
2002-10-22 Version 0.7.4
	
	- Bug 621638 corregido: No se hacía asociación simple/compuesto para
	  el nombre de la superclase (extiende) en la definición de una clase.
	  Esto provocaba errores de "(super)clase no encontrada" tanto en
	  compilación como en ejecución.
	  Ver: Chequeador.visitar(NClase n).
	  
	- En declaración de atributo de clase ahora es opcional el ":" antes
	  de la cadena de documentación. Ver LoroIParser.jj

	- Permitida ahora una lista de acciones como entrada para interpretación
	  interactiva (antes sólo una acción era permitida). Ej:
	    $ cad: cadena := "hola "; cad := cad + "mundo"; escribir(cad); cad
	  que es equivalente a:
	    $ cad: cadena := "blah blah";
		$ cad = cad + "mundo";
	    $ escribir(cad);
	    $ cad
	  SALVO que sólo se muestra el valor de la última acción si aplica.
	  El ";" es obligado como separador pero es opcional al final de la lista.
	  Ver:
		LoroIParser.jj
		DerivadorJavaCC.java
		IDerivador.java
		InterpreteImpl.java
		
	- Loro.crearInterprete ahora recibe también un parámetro que indica si se
	  crea una nueva tabla de símbolos o bien se comparte una común.
	  
////////////////////////////////////////////////////////////////////////////
2002-10-09 Version 0.7.3

	- Agregada posibilidad de implementar algoritmos utilizando BeanShell.
	  (Se utiliza ahora bsh-core-1.2b6.jar)

		- El identificador para la implementación es "bsh".
		
		- Nuevo LoroEjecutor.ejecutarAlgoritmoBsh().
			
		- Agregado elemento léxico IMPL:  {%texto%} para info o código
		  en implementación usando otro lenguaje, con el fin de hacer
		  más clara la escritura del código (no tener que "escapar" las
		  comillas dobles, por ejemplo).  (Las comillas dobles se
		  siguen aceptando.)
		  
		- Pendiente hacer suficientes pruebas.
		  
		
	- Agregado Loro.getAutomaticPackageName() para facilitar posibles 
	  controles asociados a los clientes del núcleo.
	
////////////////////////////////////////////////////////////////////////////
2002-10-07 Version 0.7.2

	- Movido DerivacionException al paquete loro. (Antes en loro.derivacion.)
	
	- Bug corregido:  No conversión en retorno de valor en algoritmo.
	  Solución:
	  ControlRetorneException ahora incluye las expresiones como tales
	  para poder acceder a sus tipos en LoroEjecutor.visitar(NAlgoritmo),
	  fragmento catch(ControlRetorneException) y hacer la conversión de
	  tipo correspondiente.
	
////////////////////////////////////////////////////////////////////////////
2002-10-06 Version 0.7.2

	- CompiladorImpl.compileAlgorithm ahora tiene en cuenta que
	  expected_specname sea posiblemente el nombre simple de una especificación
	  en el mismo paquete del algoritmo o bien en el paquete automático.

	- Nuevo Loro.getClass(String qname).
	
	- Loro.get{Specification,Algorithm,Class}(String qname) agregan posibilidad
	  que el nombre sea en el paquete automático, en el caso que no se haya
	  encontrado la unidad con el nombre pasado directamente (verificando
	  primero que el argumento sea un nombre simple).
	  
	- Modificado LoroIParser.jj:
	
		- para permitir también una cadena de documentación en el contenido de
		  una afirmación. Ajustados visitantes Chequeador y LoroEjecutor. 
		  La idea es que se utilicen cadenas de documentación en vez de literales 
		  cadena para afirmaciones que se aceptan inmediatamente. Por compatibilidad,
		  las cadenas literales se siguen aceptando por ahora.
		  También se actualizaron ConstructorArbol y NAfirmacion para esto.
		  
		- Ahora se acepta sólo una comilla sencilla (') al final de un ID para
		  fines de indicar variables semánticas. Esto para evitar controles adicionales
		  innecesariamente complicados para resolver posible confusión con cadenas de
		  documentación (que utilizan las comillas sencillas como delimitadores).
	
////////////////////////////////////////////////////////////////////////////
2002-10-03 Version 0.7.2

	- Ajustes a interface IOroLoader para permitir a los clientes acceder
	  a los *nombres* de las unidades a pesar posiblemente de que las 
	  unidades compiladas como tales no puedan accederse (por ejemplo,
	  por problemas de compatibilidad).

////////////////////////////////////////////////////////////////////////////
2002-10-02 Version 0.7.2

	- Chequeador: Ahora se hace _borrarCompilado(n) en chequeo de unidades
	  antes de proceder con la compilación como tal. Esta llamada se hace
	  justo después de n.ponPaquete(paqueteActual), que es cuando se sabe 
	  el nombre completo de la unidad para saber qué archivo borrar.
	  Esto evita los problemas de confusión cuando una compilación exitosa
	  es seguida de una con errores. Anteriormente el archivo compilado
	  "sobrevivía", por lo que podía cargarse la unidad "exitosamente"
	  a pesar de haber fallado una posterior compilación.
	  
Modificaciones para permitir que múltiples intérpretes compartan una misma
tabla de símbolos:

	- Loro.crearInterprete ahora maneja una misma tabla de símbolos para
	  todos los intérpretes creados.
	- TablaSimbolos.irAMarca modificado: ahora tiene en cuenta el tamaño
	  actual de la tabla antes de empezar a sacar marcas de la pila.
	  Esto fue necesario para el siguiente cambio.
	- ChequeadorBase.{<init>,reiniciar}() modificados de tal manera que
	  se tiene en cuenta ahora el tamaño (marca) original de la tabla
	  de símbolos suministrada para efectos de reiniciar el chequeador.
	  Antes se hacía simplemente tabSimb.reiniciar().

  (NOTA: Ningún cambio fue necesario en este sentido para efectos de ejecución.)
	  
////////////////////////////////////////////////////////////////////////////
2002-10-01 Version 0.7.2

	- Algunos ajustes generales en Chequeador/LoroEjecutor sobre manejo de 
	  ámbitos de declaraciones de variables y etiquetas en iteraciones,
	  necesarios para prevenir posibles anomalías principalmente a nivel de
	  interpretación interactiva.

////////////////////////////////////////////////////////////////////////////
2002-09-26 Version 0.7.2

	- Nuevos Loro.{getNumArguments,hasReturnValue}(IUnidad u).
	
	- Modificados UtilEjecucion._pedirArgumentosParaAlgoritmo y 
	  LoroEjecutor.ejecutarAlgoritmo para mejorar un poco el despliegue de 
	  los mensajes al usuario.
	
////////////////////////////////////////////////////////////////////////////
2002-09-25 Version 0.7.2

	- Ajustado ParseException.getMessage() para mejorar el mensaje de error
	  cuando se trata del caso de marca de guía:  {{ bla bla bla }}
	  
	- Nuevo Loro.getAlgorithm(String qname)
	  
////////////////////////////////////////////////////////////////////////////
2002-09-23 Version 0.7.2

	- Operador 'implementa' habilitado ahora pero sólo para revisar si una
	  expresión es un algoritmo que implementa una cierta especificación. (*)
	  Esta operación estaba en mora de incluirse; sobre todo cuando se tienen
	  variables de tipo algoritmo genérico.
	  
	  Un ejemplo:
	  	alg: algoritmo := fiboIter;
		...
		si alg implementa algoritmo para fibo entonces
			f := (alg como algoritmo para fibo)(20);
		...
		
	  Ver Chequeador.visitar(NImplementa), LoroEjecutor.visitar(NImplementa)
	  
	  
	  (*) Sigue pendiente en cuanto a clases/interfaces.
	
////////////////////////////////////////////////////////////////////////////
2002-09-18 Version 0.7.2

	- Se chequea ahora que el nombre de un paquete no contenga una variable
	  semántica. Actualizada la implementación de ICompilador.derivarNombre()
	  y Chequador.visitar(NPaquete).
	  También ICompilador.derivarId() no permite que sea variable semántica.
	
////////////////////////////////////////////////////////////////////////////
2002-09-17 Version 0.7.2

	- Nuevo Loro.getSpecification(String qname)
	
////////////////////////////////////////////////////////////////////////////
2002-09-16 Version 0.7.2

	- Nuevos ICompilador.{derivarId,derivarNombre}() para facilitar validación 
	  sintáctica de identificadores (id <EOF>) y nombres (id1::id2::id3 <EOF>).
	  
	- NDeclaracion.toString() y NDeclDesc.toString() incluyen ahora 'constante'
	  si es del caso.
	  
	- Eliminado ICompilador.compileUnit(); ahora se tienen los servicios
	  concretos: compileSpecification, compileAlgorithm, compileClass.
	  Ver implementación en CompiladorImpl.java.
	
////////////////////////////////////////////////////////////////////////////
2002-09-09 Version 0.7.2

	- Bug corregido. 'continue' dentro de 'para' no estaba actualizando
	  variable de control.
	  
	- Modificado LAmbiente.obtAlgoritmo(nombre): ahora no se declara con 
	  throws LException. Más bien simplementa retornará null si el algoritmo 
	  no se encuentra.
	  (La implementación es LoroEjecutor.obtAlgoritmo(nombre).)

////////////////////////////////////////////////////////////////////////////
2002-08-31 Version 0.7.1

	- Control completo de posible recursión al obtener versión cadena de un
	  objeto. Ver UtilValor. Ej:
		arr : [] Objeto;
		obj : Objeto;
		arr := ["hola", "mundo'];
		obj := arr;
		arr[1] := obj;
	Si se imprime obj (o arr, pues son el mismo objeto):
		["hola",[!]]
	el símbolo [!] indica que se trata de un arreglo que ya se se está procesando 
	(o ya se procesó). Para objetos que son miembros de otros objetos, se utiliza
	el símbolo {!}.
	
////////////////////////////////////////////////////////////////////////////
2002-08-28 Version 0.7.1

	- No hay cambios; sólo puesta la version 0.7.1
	
////////////////////////////////////////////////////////////////////////////
2002-08-26

	- LoroIParser.jj actualizado para incluir nuevos elementos léxicos:
		 TEXT_DOC: ''texto''
		 para cadenas de documentación. Usado en la producción tdoc().
		 En tdoc() todavía se acepta <LITERAL_CADENA> por compatibilidad.
	  
		 GUIDE: {{texto}}
		 Este elemento no se usa en ninguna parte de la sintaxis, así que se
		 provocará un error sintáctico en donde se encuentre.
		 Es un simple mecanismo como ayuda para plantillas de código.
		 
////////////////////////////////////////////////////////////////////////////
2002-08-25 (hacia 0.8.x)

	- Inicialización del núcleo es ahora:
		Loro.configurar(ext_dir, paths_dir);
	  
	  De ext_dir se toman todos sus archivos de extensión (*.lar, *.jar) 
	  de la manera en que se ha venido haciendo.
	  
	  De paths_dir su toman todos sus subdirectorios para incluirlos en
	  la ruta de búsqueda.
	  
	  El parámetro anterior doc_dir se ha quitado y está pendiente completar
	  revisión de manejo de tal directorio desde otras interfaces como
	  IDocumentado (que parece estar debidamente preparado...)
	  
	- Agregado Loro.addDirectoryToPath(File dir).


////////////////////////////////////////////////////////////////////////////
2002-08-24 (hacia 0.8.x)

	- Quitado parámetro 'oro_dir' para Loro.configurar. 
	  El mecanismo para lograr el efecto de indicar el directorio de destino
	  para guardar compilados es:
		ICompilador compilador = Loro.obtCompilador();
		compilador.ponDirectorioDestino(oro_dir);
		
	  Compilación *completa* OK (incluso proyectos clientes).
		
	CONTINUAR EN ESTA MISMA LINEA:
		- Quitar doc_dir y pasar esto a IDocumentador
		- En cuanto a dir_ext, definir una operacion completa para establecer
		  en general la "ruta de búsqueda"  !!!!!! URGENTE
	  
	
	- Ahora se revisan las expresiones que se usan como acciones:
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
	 Ver Chequeador.visitarAccion(Nodo).
	 En particular, esto controla el caso muy común de intentar hacer una
	 asignación (:=) con el operador de igualdad (=). Antes no se reportaba
	 nada!
	
	
	- Bug corregido: obj.xxx() generaba error de invocación sobre objeto nulo.
	  Este error se introdujo en los cambios preliminares para incluir manejo
	  de métodos en donde tal invocacion se tomaba inmediatamente como
	  refiriendo a un método, ignorando que podría ser un atributo que ha
	  sido declarado como algoritmo.
	  La situación general y la solución se describen a continuación.
	
	- Considérese la siguiente declaración de una clase:
	
		clase UnaClase
			...
			xxx: algoritmo para ...
			
			método xxx()...
			...
		fin clase
		
	  Cómo proceder si se encuentra la siguiente invocación (donde obj es
	  instancia de UnaClase):
	  
		obj.xxx()
		
	  Es xxx un "método" como tal? o es un atributo declarado como "algoritmo"?
	  Véase LoroEjecutor.visitar(NSubId n). Allí se intenta siempre primero
	  resolver xxx como un atributo; si no lo es, entonces se procede a
	  resolver xxx como un método pero sólo si se está en una invocación.
	  Pendiente. (Una idea inicial es simplemente hacer que los nombres
	  de métodos y de atributos pertenezcan al mismo espacio de nombres, en
	  donde no sería posible la definición de UnaClase como está arriba.)
	  
////////////////////////////////////////////////////////////////////////////
2002-08-20

	- Se agregó atributo "millis" a una unidad compilada.

////////////////////////////////////////////////////////////////////////////
2002-08-12 (hacia 0.8.x)

	- Agregada posibilidad de compilar directamente una unidad.
	  Ver: ICompilador, ChequeadorBase, CompiladorImpl
	  Estrategia: 
	  	- Control a nivel semántico.
		- Nuevo método setExpectedPackageName(String pkgname) para
		  establecer el paquete que debe aparecer en el código fuente.
		- CompiladorImpl.compileUnit revisa que sólo se defina UNA unidad.
	
////////////////////////////////////////////////////////////////////////////
2002-08-11 (hacia 0.8.x)

	- Manejo de fuentes asociados a las unidades:
		IOroLoader.getUnitSource
		
	- Reorganización de programas en Loro:
		- El núcleo se conforma ahora sólo del paquete loroI::sistema en la
		  extensión especial loroI.lar (que sigue almacenándose junto con el
		  .jar del núcleo).
		- El paquete loroI::mat pasa a ser como extensión regular mat.lar
		- El paquete loroI::entsal pasa a ser como extensión regular entsal.lar
	  Ahora los clientes del núcleo requerirán además del propio .jar con las
	  clases Java del núcleo (loro-XXXX.jar), los paquetes de extensión.
	  En build.xml esto se establecido así:
	  	${dist}/
		       /loro-XXXX.jar
			   /ext/*
	  El cliente LoroEDI ya está ajustado a esto.
		  
////////////////////////////////////////////////////////////////////////////
2002-08-08 (hacia 0.8.x)

	- Rediseño del manejo de unidades a nivel de cargadores.
	  Nueva interface IOroLoader con implementaciones:
	  	CoreOroLoader        para unidades del núcleo
		DirectoryOroLoader   para un directorio
		ZipFileOroLoader     para extensiones
	- ManejadorUnidades: esta clase se ha reescrito en lo concerniente a
	  la búsqueda de unidades compiladas de acuerdo al esquema indicado.
	- Nueva clase OroLoaderManager que maneja la lista de IOroLoader's
	  asociados a zip-files.
	  
	- ant test -> OK
	  
	
////////////////////////////////////////////////////////////////////////////
2002-08-01 (hacia 0.8.x)

	- Un primer ensayo para obtener las extensiones del núcleo, incluyendo
	  también el lar de apoyo.
	
	- PENDIENTE: Separar paquetes que pueden quedar como extensiones
	  normales (loroI::mat, loroI::entsal).
	
////////////////////////////////////////////////////////////////////////////
2002-07-30 (0.7.0)

	- Bug corregido: No se estaba permitiendo:
		clase A ...
		...
		obj : Objeto;
		...
		obj := crear A;
		obj como A       <- error de compilación
	  Ver TipoClase.esConvertibleA

	- Nuevos:  
		interface IExtension
		método Loro.getExtensions()
	  Esto permite obtener información sobre las extensiones.
	  
////////////////////////////////////////////////////////////////////////////
2002-07-23

	- BUG corregido:
		LoroEjecutorBase._convertirValor NO estaba revisando el caso 'null',
		el cual debe retornarse inmediatamente tal cual.

	- Hechas algunas adiciones necesarias para el desarrollo del nuevo entorno 
	  integrado (por publicarse en versión 0.8.x)
	
////////////////////////////////////////////////////////////////////////////
2002-06-05 (0.7s2)

	- La ant-task loroc ahora acepta también el parámetro "save" para indicar si
	  se desean guardar o no las unidades compiladas en disco.

	- save="false" sobre test/valid/ hace fallar algunos de los fuentes 
	  que tienen interdependencias, que era lo de esperarse.
	  Pero puso en evidencia la situación que se comenta más abajo.
	  
	- Separados los programas bajo test/valid/ en dos subdirectorios:
	
	  	test/valid/indep/
			cada fuente debe compilar bien por sí solo. Se pone save="false"
			
		test/valid/interdep/
			Los fuentes tiene algún interdependencia. Se pone save="true"
			
	  Actualizados: directorio test/valid/ y build-test.xml

	- save="false" delató la siguiente situación:
		Algunos fuentes que se saben "independientes" como:
			Ver: test/valid/indep/clases/forward.loro
			     test/valid/indep/analisintantico.loro
		NO están compilando con la opción save="false" !
		Aparentemente el mecanismo de caches del manejador de unidades no
		está funcionando como debería !!!   PENDIENTE revisión
		POR AHORA: se mueven estas clases a test/valid/interdep/ mientras
		se ataca este asunto exhaustivamente. (Nótese que externamente sólo 
		se trata de un asunto de eficiencia en la compilación.)
			
- - - - - - -

	- Bug detectado y corregido:
	     Una definición de clase A recursiva (con un miembro de clase A), con un
		 constructor asignando al miembro: producía error de compilación asociado 
		 a la NO resolución del tipo recursivo.
	  Solución: En ChequeadorBase._chequearAsignabilidad se revisa el caso en que
	  los tipos en cuestión estén relacionados con la unidad actual en compilación.

	- Corregido UtilEjecucion._pedirArgumentosParaAlgoritmo: desplegaba mal 
	  las cadenas de diálogo para pedir los valores.

	- Nueva clase loro.compilacion.Ubicacion (implementa IUbicable) para ofrecer
	  más opciones de ubicación para errores. Usado ahora en Chequeador para
	  obtener la ubicación que cubre el arreglo de entradas (o descripciones)
	  en constructores y especificaciones. 
	  
	- Complementado Util.quote para desplegar comillas internas según sea el
	  quote (' o "). Bien.
		
	- En clase loro.Rango quitado método obtPos y movido al paquete más apropiado
	  loro.parsers.javacc (en la clase ContructorArbol), ya que su implementación
	  tiene dependencias en el modo en que opera JavaCC. La construcción de un 
	  Rango requiere ahora la indicación de todos sus atributos (explícitamente
	  o a través de otros Rangos).
	  
	- Algunas veces se presentaba todavía una mala ubicación de un rango.
	  Revisando de nuevo en JavaCC encontré que SimpleCharStream.UpdateLineColumn()
	  es quien hace un ajuste asociado al tabulador '\t':
	    ...
		case '\t' :
		   column--;
		   column += (8 - (column & 07));
		   break;
		...
	  Decidí comentar este fragmento y simplificar el cálculo en el método
	  ContructorArbol.obtPos() para tampoco considerar el '\t' como especial.
	  El resultado es que ahora ya se calcula siempre bien el rango!
	  Tener cuidado en un futuro ante cambios que se hagan en las clases
	  generadas del JavaCC, y tener esto del '\t' también presente para otras posibles
	  herramientas de derivación que se lleguen a utilizar.
	  (Por qué Javacc hace un manejo espacial del \t ?)
	  
	- En Loro.obtEstrategiaAlgoritmo corregido NPE al acceder a la estrategia de
	  un algoritmo que no es implementado el Loro ya que estos no incluyen cadena 
	  para  tal fin.  

	Test completo, OK.
	  
////////////////////////////////////////////////////////////////////////////
2002-06-04 (0.7s2)

	- En LoroCompilador.main corregido el valor por defecto para la opción -d
	  que establece el directorio de destino para los compilados: ahora sí 
	  es el directorio actual "." (como se venía documentando).
	  (Antes se dejaba "" provocando algunos problemas de hallazgo de unidades.)
	  
	- Actualizado InterpreteInteractivo.main para recibir parámetros en el
	  mismo estilo de LoroCompilador.	  
	  
	CONTINUANDO CON TESTING
	
	- Revisadas y corregidas la implementaciones de:
		Chequeador.visitar(NId)
		Chequeador.visitar(NNombre)
		ChequeadorBase._resolverIdEnAmbiente(NId n)
	  para evitar:
	  	- Acceder a atributos a través de los identificadores de las clases
		    (sea nombres simples o compuestos);
		- _resolverIdEnAmbiente(NId n) sólo intenta resolver el id como un
		  algoritmo.

	- Cambiado Chequeador.visitar(NId) para siempre primero intentar por la
	  tabla de símbolos y luego por el ambiente.
	  La tabla de símbolos es el ámbito más inmediato. (Antes se intentaba
	  primero el ambiente cuando se trataba de una invocación.)
	  Una caso ilustrativo: ver test/invalid/id-iguales.loro
	  PENDIENTE: El test completo actual es OK bajo este nuevo esquema PERO
	  estar pendiente ante nuevos ejemplos.
	  
////////////////////////////////////////////////////////////////////////////
2002-06-02 (0.7s2)

**Complementación del mecanismo para testing**

	- ICompilador ahora ofrece también un servicio de "anti-compilación"
	  anticompilarListaArchivos(...) orientado a apoyar tareas de pruebas del
	  sistema con respecto a programas inválidos. 
	  Anti-compilar significa compilar en espera de algún error de compilación
	  (aunque en esta primera versión aún no se puede indicar cuál error
	  específico debería generarse).
	  Esto se ha habilitado a través de la ant-task pero no desde el compilador 
	  de línea de comandos (loro.tools.LoroCompilador) pues no se hace tan
	  necesario ahora.
	  
	- Ahora se puede disponer de más de una sesión con el núcleo Loro durante la
	  misma ejecución de la máquina virtual de Java. Cambios en: ManejadorUnidades,
	  Loro, ChequeadorBase, Tipos (principalmente control sobre la única instancia
	  de manejador de unidades que es compartida como atributo static en algunas
	  clases).
	  Esto permite en particular flexibilizar la invocación de las ant-tasks de
	  compilación y anticompilación principalmente para el testing.
	  
	- Actualizados LoroCompilerTask.java, build.xml y build-test.xml.
	
	CONTINUAR CON TESTING
	  
////////////////////////////////////////////////////////////////////////////
2002-06-01 (0.7s2)

	- Actualizados build*.xml para prueba de fuentes Loro inválidos.
	  
	- Revisión general del código asociado a mecanismo para interface/objetos
	  con el ánimo de reportar errores de "No implementado aún" mientras se
	  hace un plan de implementación completa para estos nuevos elementos.
	  Chequeador genera este error en los siguientes casos:
	  	- visitar(NInterface).
		- visitar(NClase) cuando se trata de definición de objeto.
		- visitar(NImplementa).
		- visitar(NTipoInterface).

////////////////////////////////////////////////////////////////////////////
2002-05-26 (0.7s2)  (en VAJ, y actualizado disco)

	- Pruebas adicionales para mecanismo interface/objetos:
	  Ver error en invocacion de operacion en interface-clase.loro.
	  
	- Hay problemas cuando el nombre de una variable coincide con el
	  nombre de una unidad:
	   $ foo: interace IFoo := crear Foo();
	   $ foo
	   clase Foo      <-- "foo" no deberia resolverse en la clase Foo !
	  Pendiente arreglar esto [CORREGIDO 2002-06-04]
	  
////////////////////////////////////////////////////////////////////////////
2002-05-25 (0.7s2)  (en VAJ, y actualizado disco)

	- Nuevas clases TipoInterface, NImplementa.
	  La gramática incluye ahora:  expr() "implementa" ntipo()
	  pero en ntipo() se ha dejado una sintaxis provisional para interfaces:
	  	"interface" tnombre()
	  mientras se complementa en una versión futura el nuevo manejo de objetos.
	
	- Renombrado TipoAlgoritmo por TipoEspecificacion.
	  TipoEspecificacion es más adecuado: una especificación establece un tipo,
	  y va más acorde con el nuevo TipoInterface.
	  
	- Renombrado ObjetoClase por Objeto.  Objeto es más adecuado.
	  Nuevo método: boolean Objeto.implementa(TipoInterface ti)
	  pero pendiente su implementación :-)
	  Pendiente también mejorar la representación de Objeto (enlace paterno,
	  mapeo de atributos/métodos propios, etc.).
	  
	- En Chequeador.visitar(NImplementa) sólo se verifica que los argumentos
	  correspondan a tipos interface. La verificación completa se deja para
	  tiempo de ejecución: LoroEjecutor.visitar(NImplementa).
	  Maneejo completo aplazado para una versión futura.
	  
	- Restringida la visibilidad de todos los contructores de tipos (loro.tipo.*)
	  y manejados los siguientes tipos como verdaderos "singletons" (incluyendo 
	  control como serializables--readResolve): 
	  	TipoBooleano, TipoCaracter, TipoEntero, TipoReal
		TipoCadena, TipoNulo, TipoUnit
	  
	  En general, el acceso/creación de todos los tipos se hace a través de
	  métodos "static" en loro.tipo.Tipo. Este mejor diseño permite ya
	  la siguiente optimización: 
	  	- implementar t.igual(s) como t == s. 
	  que ya se está haciendo con los tipos "singleton" mencionados arriba. 
	  Queda pendiente (aunque no es prioritario) mejorar el manejo para
	  TipoArreglo y (subclases de) TipoUnidad.
	  
////////////////////////////////////////////////////////////////////////////
2002-05-23 (0.7s2)

	- Actualizados NInterface/NClase y sus elementos internos
	  métodos/operaciones) en cuanto a asociación necesaria en creación/lectura.
	  
	- Actualizado Chequeador: ahora no se guardan operaciones/métodos por separado
	  si pertenecen a interfaces/clases dadas.
	  
////////////////////////////////////////////////////////////////////////////
2002-05-21 (0.7s2)

	- Actualizados build*.xml (encoding="iso-8859-1").
	
////////////////////////////////////////////////////////////////////////////
2002-05-20 (0.7s2)
2002-05-19

	REVISION DEL SISTEMA DE TIPOS
	
	- Eliminados atributos 'nodos' en la jerarquía de tipos. Esto ha corregido
	  finalmente las referencias mutuas en el árbol de derivación.
	- Entre otros efectos positivos, esto también acelera la compilación.
	  
	- 'ant test' -- OK
		
///////////////////////////////////////////////////////////////////////////
2002-05-18 (0.7s2)

	- Bug corregido: NPE al construir nodo para "continúe".
	- Nueva clase ChequeadorBase que le sirve de base al visitante Chequeador.
	- Nueva clase LoroEjecutorBase que le sirve de base al visitante LoroEjecutor.
	- En InterpreteImpl.reiniciar agregada invocación:
		ui.iniciarAsociacionesSimpleCompuesto();
	  con el ánimo de eliminar comandos previos "utiliza". Faltan pruebas.
	
	- Puesto "transient" atributo 'clase' en NConstructor, y agregado método 
	  readObject() en NClase para actualizar sus constructores con respecto
	  a dicho atributo. Esto evita referencias mutuas en serializable.
	
////////////////////////////////////////////////////////////////////////////
2002-05-07

	- Complementada gramática para permitir "clase" u "objeto" intercambiablemente.
	  Sin embargo, para la definición de "objeto" se genera ahora un error del 
	  estilo "Definición de objeto no implementado aún".
		
	- Actualizado parcialmente VisitanteLoroDoc.
	
////////////////////////////////////////////////////////////////////////////
2002-05-05

**Adiciones preliminares para incluir manejo de interfaces y métodos**
Ejercicio realizado no necesariamente para hacer una implementación completa
en este sentido, pero sí para tener una primera aproximación que haga más
claro cómo proceder en una futura versión.

	- Complementada gramática para:
		- aceptar "interfaces"
		- permitir alternativas:
			- "especificación" | "operación" --> para especificacion()
			-  "algoritmo" | "método" --> para algoritmo()
		- lista de interfaces para una clase, y metodos
			
	- Una "interface" es una lista de operaciones (especificaciones).
	  Pendiente:
		- Ajustar chequeo de una especificación dentro de una interface:
		  	- para no guardar en disco aisladamente;
			- para revisar repetición de nombre en la misma interface;
		- Generar documentación (ahora solamente se muestra descripción
		  pero no se recorre la lista de operaciones).
		  
	- Pendiente para clases:
		- Ajustar chequeo de un algoritmo (método) dentro de una clase:
		  	- para no guardar en disco aisladamente;
			- para revisar repetición de nombre en la misma clase;
		- Chequear que se implementen las operaciones de las interfaces
		- Completar revisión de métodos (que son algoritmos)
		- Completar documentación
	
////////////////////////////////////////////////////////////////////////////
2002-04-27

	- Bug corregido: En chequeo no se revisaban asignaciones a nuevos
	  atributos automáticos para arreglos (longitud, inf, sup).
	  Ver _esConstanteExpresion(NExpresion), visitar(NAsignacion)
	  en Chequeador.
	
	- Mejorado reporte de errores de ejecución asociados a subindizaciones.
	  Ver _obtValorDeArreglo, _ponValorAArreglo en LoroEjecutor.
	  
////////////////////////////////////////////////////////////////////////////
2002-04-25 (0.7s1)

	- Nuevo servicio del núcleo: Loro.verificarConfiguracion(), que se apoya
	  a su vez en nuevo servicio ManejadorUnidades.verificarUnidadesCompiladas().
	  
	- El ManejadorUnidades utiliza ahora el siguiente formato para el
	  almacenamiento de una unidad compilada en disco:
	  
		(1) String version         -- versión actual del núcleo
		(2) String nombreFuente    -- nombre completo del fuente
		(3) String nombreCompleto  -- nombre completo de la unidad
		(4) NUnidad unidad         -- la unidad como tal.
	  
	  Una siguiente versión de Loro podrá utilizar el dato (2) para intentar una 
	  recompilación automática (aunque es posible que el fuente correspondiente
	  ya no se encuentre o no tenga nada que ver con la unidad).
	  Este es una solución simple que permite un control aceptable de los problemas
	  de compatibilidad bajo el esquema actual de almacenamiento de unidades.
	  Sin embargo, en un futuro, este esquema podrá ser totalmente distinto (sobre
	  todo buscando eliminar la base sobre serialización). 
	  
	
////////////////////////////////////////////////////////////////////////////
2002-04-23 (0.7s0)

	- Inicio de control de versiones en unidades compiladas:
		ManejadorUnidades
		NUnidad
	  Esquema preliminar:
		- Se asocia a cada unidad una versión de compilación (que coincide ahora
		  con la version del núcleo utilizado para hacer la compilación).
		- En el archivo compilado se guarda un encabezado de datos (de tipo básico)
		  antes de la propia unidad. El primer dato del encabezado es (y será) la versión
		  de compilación. En la actual versión 0.7s0 se tienen también:
		  el nombre del fuente y el nombre completo de la unidad.
		- Agregar un servicio al ManejadorUnidades (y eventualmente a la interface
		  ICargadorUnidades) para chequear unidades existentes (en archivos .oro
		  y ZIP), de tal manera que se pueda advertir al usuario en caso de problemas
		  de versión y/o efectuar un intento de compilación automática de los
		  fuentes asociados, como ayuda para la actualización.

////////////////////////////////////////////////////////////////////////////
2002-04-11 (0.7r9)

	- Agregada operación ICompilador.compilarListaArchivos (y su correspondiente
	  implementación en CompiladorImpl).
	  loro.tools.LoroCompilador ahora utiliza este servicio.
	  
	  La idea es preparar terreno para facilitar la compilación de múltiples
	  archivos fuentes (por ejemplo, bajo directorios completos).
	  Esto facilitará la implementación de "proyectos" por parte de un cliente
	  (como LoroEDI).
	  
	- Nueva interface loro.ICargadorUnidades que abstrae algunas de las
	  operaciones del ManejadorUnidades para ponerlas a disposición del
	  cliente. Se obtiene con loro.Loro.obtCargadorUnidades().
	  La idea es que el cliente tenga el mecanismo para navegar por las
	  unidades compiladas disponibles (por jerarquía de paquetes por ejemplo).
	  Actualmente, LoroEDI utiliza la jerarquía de archivos generada por
	  el documentador para crear su despliegue jerárquico; la idea es canalizar
	  este manejo directamente sobre las unidades, no sólo para visualizar
	  la documentación, sino para otras operaciones como ejecución, edición
	  sobre fuente asociado, etc.
	  Pendiente: apenas en inicio.
	
////////////////////////////////////////////////////////////////////////////
2002-04-06

	- Nuevas palabras clave "objeto", "operación" y "método" (preparando terreno
	  para unificación para POO).
	  
	- Agregado proceso en loroI::mat para elevar una base a una potencia real:
	    potr(base: real, p: real) -> res: real
		
	- Bug corregido: Se permitía acceder a un atributo a través del identificador 
	  de la clase.
	  
	- Bug corregido: Se permitia: Clase es_instancia_de Clase 
	  
	- Bug corregido: Posible compilación parcial de prototipo de una especificación
	  por error en el cuerpo de condiciones (pre/pos): Por ejemplo, cuando en la
	  PRE se invoca un algoritmo indefinido, la variable de salida queda sin procesar.
	  En una primera pasada, el error se permite (lo que va de acuerdo con la estrategia
	  para permitir "pendientes" dentro de un mismo fuente). Pero si más adelante 
	  se compila un algoritmo para esta especificación, sucede que la variable de
	  salida de la especificación no tiene asociado ningún tipo (es nulo). Así que
	  se genera error de "tipos distintos".
	  Corregido: Ver Chequeador.visitar(NEspecificacion).
	  
	- Bug corregido: De nuevo se muestra posición [lin,col] para ubicación en mensajes
	  de error. Ver Rango.

	- Bug corregido:	  
	  Situación:
	    Sea e una especificación;
		Sea a un algoritmo para e;
		No existe algoritmo e para e (del mismo nombre);

	  El siguiento código compila bien:
	  	e : algoritmo para e := a;
	  	e(...);
		
	  pero provoca error en ejecución "invocación de una especificación".
	  Corregido: Ver LoroEjecutor.visitar(NId n)
	  
	- Bug detectado similar:      [ver 2002-06-04]
	  Situación:
	  	Similar al anterior cuando SI existe un algoritmo de nombre e pero
		para una especificación distinta de e. Esto produce conflictos.
	  PENDIENTE.
	  
	- Agregado loro.tools.InterpreteInteractivo, que opera sobre i/o estándares.
	  Se tomó de base el interprete del LoroEDI. 
	  Acepta argumentos: -ext, -oro, -ayuda
	  Pendiente depurarse: ideas: quiza definir un filter-output general para
	  manejar mecanismo de prefijos, de tal manera que el servicio se preste
	  desde aqui (casi) completo incluso para el LoroEDI. [baja prioridad]
	  
	- Bug corregido:
	  Sucedia:
		obj: UnaClase := crear UnaClase(...);
		obj como cadena
		! Expresion de tipo 'UnaClase' no es convertible al tipo 'cadena'
	  Correccion: TipoClase.esConvertibleA(Tipo t) ahora permite tambien
	  que t sea cadena. Notese que aun falta definir operaciones relacionadas
	  con jerarquia de clases y luego con interfaces.
	  
	- Pendiente: control de mayusculas/minusculas: Por ejemplo, suponiendo
	  que exite la clase A:     [corregido 2002-06-04]
		$ a
		= clase A            -- No deberia resolverse asi.
		$ a: A               -- ok
		$ a                  
		! Variable 'a' no tiene asignacion   -- ok: primero se mira tabla simbolos.
	  Ver comentario en ManejadorUnidades._cargarUnidadDeDirectorio().
		
	
////////////////////////////////////////////////////////////////////////////
2002-03-27 (0.7r8)

	- Nueva palabra clave "como" para conversion de tipo:	
			expresion "como" ntipo
	   YA no se acepta la sintaxis:
			ntipo "(" expresion ")"
	  
	- Aumentada gramatica para aceptar expresiones arreglo, ej:
		[1,3,6,2]
	  El arreglo debe tener por lo menos una expresion.; de esta
	  manera se puede deducir el tipo. Siempre tendra subindexacion desde 0.
	  Si se requiere un arreglo vacio, el programador debera usar el mecanismo 
	  "crear" explicitamente, por ej:
	  	crear [0] entero
		
	  Todo el soporte esta listo.
	  
	- Corregidos diversos bugs sobre conversion de valores.
	
	- Bug encontrado:    [corregido según testing 2002-06-04]
		$ escribir como entero
		!  Expresion de tipo 'algoritmo:null' no es convertible al tipo 'entero'
		Notese el null. Ver TipoAlgoritmo.java
		Pendiente correccion!
	
	
////////////////////////////////////////////////////////////////////////////
2002-03-21 (0.7r7)

	- Actualizado build.xml para generar el nucleo con el nombre 
	  loro-${VERSION}.jar (en esta fecha esto es loro-0.7r7.jar) y no
	  simplemente loro.jar

////////////////////////////////////////////////////////////////////////////
2002-03-19

	- Aumentada gramatica para admitir elementos "si_no_si" opcionales en una
	  decision:
	  	"si" expresion "entonces" acciones
		( "si_no_si" expresion "entonces" acciones )*          <- nuevo
		[ "si_no" acciones ]
		"fin" "si"
	  Todo el soporte esta listo.
		
	- Ahora son opcionales la palabras "descripcion" y "estrategia"
	  que van antes de la cadena correspondiente.
	- Ahora la palabra antes de la cadena de descripcion de un algoritmo puede
	  ser tambien "descripcion" ("estrategia" todavia se admite pero quiza
	  se elimine en un futuro --"descripcion" es apropiada para el efecto.).

////////////////////////////////////////////////////////////////////////////
2002-03-14

	- Nueva clase loro.util.UtilValor, encargada de factorizar algunos
	  servicios asociados a obtener versiones cadena de los valores de Loro.
	  
	- Mejorada la version cadena de la TablaSimbolos (para fines del interprete 
	  interactivo).
	  
	- TipoClase.toString se simplifica a solo el nombre de la clase, por ejemplo
	  foo::Baz (ya no ``instancia de clase foo::Baz'').
	
////////////////////////////////////////////////////////////////////////////
2002-03-13

	- Por completitud con ``crear[5..10] ...'', se agregaron 'operaciones'
	  sobre arreglos con el mecanismo punto:
		arreglo.inf        obtiene el indice inferior del arreglo
		arreglo.sup        obtiene el indice superior del arreglo
		arreglo.longitud   obtiene la longitud del arreglo
	  Tambien se hizo para las cadenas aunque para estas siempre:
	    x.inf = 0  y  x.sup = x.longitud - 1
	  
	- En LoroEjecutor se agrego un limite (longitudVerObjeto) para la version 
	  cadena de los arreglos, similar a como se ha hecho con objetos.
	  PENDIENTE: Permitir cambiar los valores de nivelVerObjeto y longitudVerObjeto,
	  digamos como preferencias y/o a traves de metacomandos en el II.
		
	- Bug corregido: Se producia error de ejecucion si un algoritmo retornaba
	  'nulo' diciendo que no habia asignacion a la variable de salida. 
	  Faltaba utilizar el servicio tabSimb.obtAsignado para saber concretamente
	  si hubo o no asignacion en LoroEjecutor.visitar(NAlgoritmo).
	
	- Bug corregido: Se producia error de compilacion si un algoritmo retornaba
	  'nulo' para una variable de salida tipo cadena.
	  Se utiliza ahora _chequearAsignabilidad() como para asignacion.
	
////////////////////////////////////////////////////////////////////////////
2002-03-10

	- OJO: Definir modelo de resolucion de referencias en unidades compiladas,
	  como los tipos. (Ahora, NTipo contiene campo Tipo y TipoClase
	  tiene campo NClase; esto provoca autoreferencias.) Esto tiene relacion 
	  directa con la estrategia "serializable" utilizada para guardar las
	  unidades: el sintoma es que las unidades se vuelven mas y mas grandes
	  a medida se recompilan!
	  
	
////////////////////////////////////////////////////////////////////////////
2002-03-07

	- Actualizado LoroCompilador: ahora es completamente un cliente del
	  sistema (solo hace import loro.*).
	  Ofrece un metodo separado compilar() con el servicio completo tanto 
	  para su propio main() como para la "ant task".
	  (Pendiente: hacer actualizacion similar para LoroDocumentador y
	  las otras herramientas en loro.tools)
	  
	- Actualizado loro.misc.ant.LoroCompilerTask: ahora incluye argumento
	  para indicar directorio de extensiones (no probado aun), e invoca el 
	  nuevo metodo LoroCompilador.compilar().
	  
	  **Pendiente:   [Disponible 2002-06-02] 
	  Completar terminacion Loro.cerrar() para corregir por
	  ejemplo lo siguiente: la tarea loroc puede invocarse SOLA una vez 
	  dentro de un "build" porque una segunda vez provoca excepciones asociadas 
	  a inicializacions estaticas del nucleo.

	- Incluidas pruebas de compilacion de fuentes en Loro: build-test.xml,
	  que usa la anttask "loroc". El subdirectorio test/ contiene
	  diversos fuentes para pruebas generales. En particular, todos los
	  fuentes bajo el subdirectorio test/valid/ deben compilar
	  exitosamente, tarea que se realiza con ``ant test-valid''.
	  
	  La estructura bajo test/ es:
	  	test/valid/      -- Deben compilar exitosamente
		                    (En test/valid/fixed/ se ponen fuentes que antes delataban
							bugs del sistema.)
		test/invalid/    -- NO deben compilar exitosamente.
		test/bugs/       -- Actualmente con problemas para Loro.
		test/ext/        -- Mecanismo de extension (implica Java--prueba "a mano").
		test/pending/    -- Con posibles nuevas caracteristicas.
		
	  Pendiente:     [Disponible 2002-06-04] 
	  Automatizar el chequeo de test/invalid/
	
////////////////////////////////////////////////////////////////////////////
2002-03-06

	- Varias pruebas de compilacion/ejecucion (despues del cambio de integrar el
	  recurso de apoyo) han sido OK.
	  Seguir haciendo pruebas (enfocadas principalmente hacia el ManejadorUnidades
	  para verificar normalizacion de todos los nombres en los caches al estilo "::").
	  
	- Ahora solo basta con actualizar build.properties sobre la version
	  del sistema para que se refleje en todas partes.
	  
	- En el build.xml se hace primero un loro-sin-apoyo.jar que involucra toda
	  la compilacion de fuentes excepto los .loro de apoyo, y luego el
	  propio loro.lar que incluye ahora el recurso de apoyo. OK.
	
////////////////////////////////////////////////////////////////////////////
2002-03-05

	- Ahora loroI.lar se maneja como un recurso dentro del mismo loro.jar
	  (ya no hay que indicarlo externamente).
	  
	- En la inicializacion del ManejadorUnidades se cargan *todas* las unidades 
	  de apoyo en el cacheZip. Funciona OK pero tenerlo presente para cuando
	  el tamano de este apoyo se vuelva considerable en un futuro (pendiente).
	  
////////////////////////////////////////////////////////////////////////////	  
2002-03-02   (Creado este archivo)
	NOTA
		Lo que venia llamandose integralmente como proyecto "Loro", ahora se
		ha dividido en el nucleo como tal, que continuara llamandose "Loro", 
		y el Entorno de Desarrollo Integrado, que ahora se llama LoroEDI.
		Las notas de desarrollo DEVNOTES.txt continuaran a cargo de LoroEDI
		hasta que se se actualice adecuadamente la informacion. (Luego
		Loro tendra su propio DEVNOTES).
	
	- La construccion del nucleo de Loro (este proyecto) ha sido completamente
	  revisada: ahora esta mejor configurado (y configurable) y se hace un
	  control completo de dependencias.
	  Vea build*
